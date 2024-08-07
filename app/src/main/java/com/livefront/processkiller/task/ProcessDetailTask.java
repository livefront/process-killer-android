package com.livefront.processkiller.task;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import com.livefront.processkiller.model.ProcessDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Retrieves a list of {@link ProcessDetail} objects corresponding to packages that were in some
 * way active in the given time frame.
 */
public class ProcessDetailTask extends AsyncTask<Void, Void, List<ProcessDetail>>{

    private long mEndTime;
    private long mStartTime;
    private OnTaskCompleteListener mListener;
    private PackageManager mPackageManager;
    private UsageStatsManager mUsageStatsManager;

    public interface OnTaskCompleteListener {
        void onTaskComplete(@NonNull List<ProcessDetail> processDetails);
    }

    public ProcessDetailTask(@NonNull PackageManager packageManager,
                             @NonNull UsageStatsManager usageStatsManager,
                             long startTime,
                             long endTime,
                             @NonNull OnTaskCompleteListener listener) {
        mPackageManager = packageManager;
        mUsageStatsManager = usageStatsManager;
        mListener = listener;
        mStartTime = startTime;
        mEndTime = endTime;
    }

    @Override
    protected List<ProcessDetail> doInBackground(Void... params) {
        // Get list of any active packages during the given time frame
        List<ProcessDetail> processDetails = new ArrayList<>();
        Map<String, UsageStats> usageStatsList = mUsageStatsManager
                .queryAndAggregateUsageStats(0, mEndTime);
        for (String packageName : usageStatsList.keySet()) {
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = mPackageManager.getPackageInfo(packageName, 0).applicationInfo;
            } catch (PackageManager.NameNotFoundException e) {
                // Not much we can do without application info
                continue;
            }
            long lastUsedTimestamp = usageStatsList.get(packageName).getLastTimeUsed();
            if (lastUsedTimestamp < mStartTime) {
                // We only want packages active in the given range
                continue;
            }
            processDetails.add(
                    new ProcessDetail(
                            applicationInfo.packageName,
                            applicationInfo.loadLabel(mPackageManager).toString(),
                            applicationInfo.loadIcon(mPackageManager),
                            lastUsedTimestamp));
        }
        return processDetails;
    }

    @Override
    protected void onCancelled(List<ProcessDetail> processDetails) {
        super.onCancelled(processDetails);
        mPackageManager = null;
        mUsageStatsManager = null;
        mListener = null;
    }

    @Override
    protected void onPostExecute(List<ProcessDetail> processDetails) {
        if (mListener == null) {
            return;
        }
        mListener.onTaskComplete(processDetails);
    }
}
