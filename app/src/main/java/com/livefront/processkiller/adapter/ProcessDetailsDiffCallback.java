package com.livefront.processkiller.adapter;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.livefront.processkiller.model.ProcessDetail;

import java.util.List;

class ProcessDetailsDiffCallback extends DiffUtil.Callback {
    private List<ProcessDetail> mOldList;
    private List<ProcessDetail> mNewList;

    ProcessDetailsDiffCallback(
            @NonNull List<ProcessDetail> oldList,
            @NonNull List<ProcessDetail> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getPackageName()
                .equals(mNewList.get(newItemPosition).getPackageName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // We are intentionally comparing just the package name and not the whole object due to
        // properties of ProcessDetail never being equivalent (i.e. the icon Drawable) even when
        // the item contents should be the same.
        return mOldList.get(oldItemPosition).getPackageName()
                .equals(mNewList.get(newItemPosition).getPackageName());
    }
}
