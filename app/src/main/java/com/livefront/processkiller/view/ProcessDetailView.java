package com.livefront.processkiller.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.livefront.processkiller.R;
import com.livefront.processkiller.model.ProcessDetail;

/**
 * A simple container class for displaying the details of a recently run package
 */
public class ProcessDetailView extends FrameLayout {

    private Views mViews;

    static class Views {
        ImageView icon;
        TextView applicationName;
        TextView packageName;
        Views(View root) {
            icon = (ImageView) root.findViewById(R.id.icon);
            applicationName = (TextView) root.findViewById(R.id.application_name);
            packageName = (TextView) root.findViewById(R.id.package_name);
        }
    }

    public ProcessDetailView(Context context) {
        super(context);
        init();
    }

    public ProcessDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProcessDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_process_detail, this);
        mViews = new Views(this);
    }

    public void setData(@NonNull ProcessDetail processDetail) {
        mViews.applicationName.setText(processDetail.getApplicationName());
        mViews.packageName.setText(processDetail.getPackageName());
        mViews.icon.setImageDrawable(processDetail.getIcon());
    }

}
