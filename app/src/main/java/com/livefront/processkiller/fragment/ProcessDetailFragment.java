package com.livefront.processkiller.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStatsManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.livefront.processkiller.R;
import com.livefront.processkiller.adapter.ProcessDetailAdapter;
import com.livefront.processkiller.adapter.ProcessDetailAdapter.OnProcessDetailClickListener;
import com.livefront.processkiller.model.ProcessDetail;
import com.livefront.processkiller.task.ProcessDetailTask;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Displays a list of packages with recent activity for a time period defined by the given
 * {@link com.livefront.processkiller.fragment.ProcessDetailFragment.ProcessType}
 */
public class ProcessDetailFragment extends Fragment {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            ProcessType.ALL,
            ProcessType.LAST_DAY,
            ProcessType.LAST_HOUR,
            ProcessType.RECENT})
    public @interface ProcessType {
        int ALL = 1;
        int LAST_DAY = 2;
        int LAST_HOUR = 3;
        int RECENT = 4;
    }

    private static final String ARG_PROCESS_TYPE = "argProcessType";

    private static final String[] IGNORED_SYSTEM_PACKAGES = new String[] {
            "android",
            "com.android.systemui"
    };

    private static Comparator<ProcessDetail> ALPHABETIC_COMPARATOR = new Comparator<ProcessDetail>() {
        @Override
        public int compare(ProcessDetail lhs, ProcessDetail rhs) {
            return lhs.getApplicationName().compareTo(rhs.getApplicationName());
        }
    };

    private static Comparator<ProcessDetail> TIMESTAMP_COMPARATOR = new Comparator<ProcessDetail>() {
        @Override
        public int compare(ProcessDetail lhs, ProcessDetail rhs) {
            return Long.compare(
                    rhs.getLastUsedTimestamp(),
                    lhs.getLastUsedTimestamp());
        }
    };

    @ProcessType
    private int mProcessType;
    private ActivityManager mActivityManager;
    private List<ProcessDetail> mProcessDetails = new ArrayList<>();
    private ProcessDetailAdapter mAdapter;
    @Nullable
    private ProcessDetailTask mProcessDetailTask;
    private PackageManager mPackageManager;
    private List<String> mIgnoredPackages = new ArrayList<>();
    private UsageStatsManager mUsageStatsManager;
    private Views mViews;

    static class Views {
        RecyclerView recyclerView;
        Views(View root) {
            recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        }
    }

    public static ProcessDetailFragment newInstance(@ProcessType int processType) {
        ProcessDetailFragment fragment = new ProcessDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PROCESS_TYPE, processType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ResourceType
        mProcessType = getArguments().getInt(ARG_PROCESS_TYPE, ProcessType.RECENT);
        mActivityManager = (ActivityManager) getActivity()
                .getApplicationContext()
                .getSystemService(Activity.ACTIVITY_SERVICE);
        mPackageManager = getActivity()
                .getApplicationContext()
                .getPackageManager();
        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getApplicationContext()
                .getSystemService(Activity.USAGE_STATS_SERVICE);

        // Add this package and some system packages to list of packages to ignore
        mIgnoredPackages.add(getActivity().getPackageName());
        mIgnoredPackages.addAll(Arrays.asList(IGNORED_SYSTEM_PACKAGES));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_process, container, false);
        mViews = new Views(root);
        setupRecyclerView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        // We want the data to be as fresh as possible so we'll always clear and reload it when
        // resuming.
        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViews = null;
        if (mProcessDetailTask != null) {
            mProcessDetailTask.cancel(true);
            mProcessDetailTask = null;
        }
    }

    private void refreshData() {
        long start;
        long now = Calendar.getInstance().getTimeInMillis();
        switch (mProcessType) {
            case ProcessType.LAST_DAY:
                start = now - TimeUnit.DAYS.toMillis(1);
                break;
            case ProcessType.LAST_HOUR:
                start = now - TimeUnit.HOURS.toMillis(1);
                break;
            case ProcessType.RECENT:
            default:
                // Within last 10 minutes
                start = now - TimeUnit.MINUTES.toMillis(10);
                break;
        }

        if (mProcessDetailTask != null) {
            mProcessDetailTask.cancel(true);
        }
        mProcessDetailTask = new ProcessDetailTask(
                mPackageManager,
                mUsageStatsManager,
                start,
                now,
                new ProcessDetailTask.OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(@NonNull List<ProcessDetail> processDetails) {
                        if (getView() == null) {
                            return;
                        }
                        mProcessDetailTask = null;

                        // Filter out ignored packages
                        mProcessDetails.clear();
                        for (ProcessDetail processDetail : processDetails) {
                            if (mIgnoredPackages.contains(processDetail.getPackageName())) {
                                continue;
                            }

                            mProcessDetails.add(processDetail);
                        }

                        // Sort the data depending on the process type and update the adapter
                        Collections.sort(
                                mProcessDetails,
                                mProcessType == ProcessType.RECENT ?
                                        TIMESTAMP_COMPARATOR :
                                        ALPHABETIC_COMPARATOR);
                        mAdapter.notifyDataSetChanged();
                    }
                }
        );
        mProcessDetailTask.execute();
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false);
        mAdapter = new ProcessDetailAdapter(mProcessDetails);
        mViews.recyclerView.setAdapter(mAdapter);
        mViews.recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter.setOnProcessDetailClickListener(new OnProcessDetailClickListener() {
            @Override
            public void onProcessDetailClick(@NonNull final ProcessDetail processDetail) {
                mActivityManager.killBackgroundProcesses(processDetail.getPackageName());
                Snackbar.make(
                        mViews.recyclerView,
                        R.string.snackbar_process_killed_message,
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }
}
