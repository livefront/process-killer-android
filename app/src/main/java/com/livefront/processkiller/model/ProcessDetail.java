package com.livefront.processkiller.model;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Holds data about a recently run process related to the given package.
 */
public class ProcessDetail {

    private long mTimestamp;
    private Drawable mIcon;
    private String mApplicationName;
    private String mPackageName;

    public ProcessDetail(@NonNull String packageName,
                         @NonNull String applicationName,
                         @NonNull Drawable icon,
                         long lastUsedTimestamp) {
        mPackageName = packageName;
        mApplicationName = applicationName;
        mIcon = icon;
        mTimestamp = lastUsedTimestamp;
    }

    public String getApplicationName() {
        return mApplicationName;
    }

    public long getLastUsedTimestamp() {
        return mTimestamp;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public String getPackageName() {
        return mPackageName;
    }

}
