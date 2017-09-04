package com.dasong.zmusic.model.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dasong.zmusic.R;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by dason on 2017/8/20 0020.
 */

public class MyRecAdapter extends RecyclerView.Adapter {

    private List<String> datas;
    private int itemStyle;

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView item;

        public MyViewHolder(View itemView) {
            super(itemView);
            Log.d("ZLogcat","MyViewHolder is runing");
            item = this.findView(itemView, R.id.item_test);
            //item = (TextView)itemView.findViewById(R.id.item_test);
        }

        public <T extends View> T findView(View v,int id){
            return (T)v.findViewById(id);
        }
    }

    public MyRecAdapter(List<String> datas,int itemStyle){
        Log.d("ZLogcat","MyRecAdapter is runing,datas.size->" + datas.size());
        this.datas = datas;
        this.itemStyle = itemStyle;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("ZLogcat","Create ViewHolder");
        View v = LayoutInflater.from(parent.getContext()).inflate(this.itemStyle,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        String s = holder == null?"null":"not null";
        Log.d("ZLogcat","ViewHolder is " + s);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String data = this.datas.get(position);
        Log.d("ZLogcat","data->"+data);
        ((MyViewHolder)holder).item.setText(data);
    }

    @Override
    public int getItemCount() {
        Log.d("ZLogcat","data->"+this.datas.size());
        return this.datas.size();
    }

}
