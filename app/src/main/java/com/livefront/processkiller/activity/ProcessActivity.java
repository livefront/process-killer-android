package com.livefront.processkiller.activity;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.livefront.processkiller.R;
import com.livefront.processkiller.fragment.ProcessDetailFragment;
import com.livefront.processkiller.fragment.ProcessDetailFragment.ProcessType;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays tabs showing packages with recent activity over several different time ranges.
 */
public class ProcessActivity extends AppCompatActivity {

    private Views mViews;

    static class Views {
        TabLayout tabLayout;
        Toolbar toolbar;
        ViewPager viewPager;
        Views(View root) {
            tabLayout = (TabLayout) root.findViewById(R.id.tabs);
            toolbar = (Toolbar) root.findViewById(R.id.toolbar);
            viewPager = (ViewPager) root.findViewById(R.id.view_pager);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        mViews = new Views(findViewById(R.id.main_content));
        setSupportActionBar(mViews.toolbar);
        setupViewPagerAndTabs();
        checkPermissions();
    }

    private void checkPermissions() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.enable_usage_stats_title)
                .setMessage(getString(R.string.enable_usage_stats_message))
                .setPositiveButton(R.string.enable_usage_stats_ok_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                            }
                        })
                .setCancelable(false)
                .create()
                .show();
    }

    private void setupViewPagerAndTabs() {
        List<TabItem> tabItems = new ArrayList<>();
        tabItems.add(
                new TabItem(
                        ProcessDetailFragment.newInstance(ProcessType.RECENT),
                        getString(R.string.process_type_recent)));
        tabItems.add(
                new TabItem(
                        ProcessDetailFragment.newInstance(ProcessType.LAST_DAY),
                        getString(R.string.process_type_last_day)));
        mViews.viewPager.setAdapter(
                new TabPagerAdapter(
                        getSupportFragmentManager(),
                        tabItems));
        mViews.tabLayout.setupWithViewPager(mViews.viewPager);
    }

    public static class TabItem {

        private Fragment mFragment;
        private String mLabel;

        public TabItem(@NonNull Fragment fragment,
                       @NonNull String label) {
            mFragment = fragment;
            mLabel = label;
        }

        public Fragment getFragment() {
            return mFragment;
        }

        public String getLabel() {
            return mLabel;
        }

    }

    public static class TabPagerAdapter extends FragmentPagerAdapter {

        private List<TabItem> mTabItems;

        public TabPagerAdapter(@NonNull FragmentManager fm,
                               @NonNull List<TabItem> tabItems) {
            super(fm);
            mTabItems = tabItems;
        }

        @Override
        public Fragment getItem(int position) {
            return mTabItems.get(position).getFragment();
        }

        @Override
        public int getCount() {
            return mTabItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabItems.get(position).getLabel();
        }

    }

}
