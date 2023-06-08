package com.kar.kfd.gov.kfdsurvey.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter<T>.AdapterHolderBase>{

    protected abstract AdapterHolderBase initUI(ViewGroup parent);

    protected abstract void initListeners(AdapterHolderBase holder, int position);

    public interface OnClickCallback {
        void onCallback(Object o);
    }

    protected Context mCtx;
    protected List<T> mList;
    protected OnClickCallback mOnClickCallback = null;
    protected RecyclerView mRv;

    protected BaseAdapter(Context ctx, List<T> list, RecyclerView rv, RecyclerView.LayoutManager lm) {
        this.mCtx = ctx;
        this.mList = list;
        mRv = rv;
        mRv.setLayoutManager(lm);
        mRv.setAdapter(this);
    }

    @NonNull
    @Override
    public AdapterHolderBase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return this.initUI(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHolderBase holder, int position) {
        this.initListeners(holder, position);
    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    public T getItem(int i) {
        return this.mList.get(i);
    }

    public void setResultList(List<T> objects) {
        this.mList = objects;
        notifyDataSetChanged();
    }

    public List<T> getmList() {
        return mList;
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        mOnClickCallback = onClickCallback;
    }


    protected class AdapterHolderBase extends RecyclerView.ViewHolder{

        public AdapterHolderBase(View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if (mOnClickCallback != null)
                    mOnClickCallback.onCallback(mList.get(getAdapterPosition()));
            });
        }


    }
}
