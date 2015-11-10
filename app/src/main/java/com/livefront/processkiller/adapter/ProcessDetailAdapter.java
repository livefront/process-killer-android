package com.livefront.processkiller.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.livefront.processkiller.R;
import com.livefront.processkiller.model.ProcessDetail;
import com.livefront.processkiller.view.ProcessDetailView;

import java.util.List;

public class ProcessDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProcessDetail> mProcessDetails;
    private OnProcessDetailClickListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            // Simply hold the item view
            super(itemView);
        }
    }

    public interface OnProcessDetailClickListener {
        void onProcessDetailClick(@NonNull ProcessDetail processDetail);
    }

    public ProcessDetailAdapter(@NonNull List<ProcessDetail> processDetails) {
        mProcessDetails = processDetails;
    }

    @Override
    public int getItemCount() {
        return mProcessDetails.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProcessDetailView processDetailView = (ProcessDetailView) holder.itemView;
        final ProcessDetail processDetail = mProcessDetails.get(position);
        processDetailView.setData(processDetail);
        processDetailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener == null) {
                    return;
                }
                mListener.onProcessDetailClick(processDetail);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.view_process_detail_inflatable,
                        parent,
                        false));
    }

    public void setOnProcessDetailClickListener(@Nullable OnProcessDetailClickListener listener) {
        mListener = listener;
    }

}
