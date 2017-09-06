package com.dasong.zmusic.model.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dason on 2017/8/21 0021.
 */

abstract public class BaseRecAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    public int layoutId;
    public int isCheckedPosition = -1;
    protected OnItemClickListener onItemClickListener;

    public static interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public BaseRecAdapter(int layoutId){
        this.layoutId = layoutId;
    }

    /**
     * 对onClick的默认实现，子类覆写时可以直接调用，如有需要则自己覆写onClick()
     * @param view
     */
    protected void defaultOnClick(View view) {
        if(this.onItemClickListener != null){
            this.onItemClickListener.onItemClick(view,(int)view.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.layoutId,parent,false);
        view.setOnClickListener(this);
        return this.createHolder(view);
    }

    abstract protected <T extends BaseRecViewHolder> T createHolder(View view);
}
