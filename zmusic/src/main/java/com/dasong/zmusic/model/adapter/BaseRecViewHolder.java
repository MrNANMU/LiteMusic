package com.dasong.zmusic.model.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by dason on 2017/8/31 0031.
 */

abstract public class BaseRecViewHolder extends RecyclerView.ViewHolder{

    public BaseRecViewHolder(View itemView) {
        super(itemView);
    }

    protected  <T extends View> T findView(View view,int id){
        return (T)view.findViewById(id);
    }
}
