package com.dasong.zmusic.model.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.bean.Music;

import java.util.List;
import java.util.Map;

/**
 * Created by dason on 2017/8/31 0031.
 */

public class ArtistRecAdapter extends BaseRecAdapter {

    private Map<String,List<Music>> artMap;
    private List<String> artList;

    public static class ArtistHolder extends BaseRecViewHolder{

        TextView item_art;
        TextView item_number;

        public ArtistHolder(View itemView) {
            super(itemView);
            item_art = this.findView(itemView, R.id.item_art);
            item_number = this.findView(itemView,R.id.item_number);
        }
    }

    public ArtistRecAdapter(Map<String,List<Music>> artMap,List<String> artList,int layoutId) {
        super(layoutId);
        this.artList = artList;
        this.artMap = artMap;
    }

    @Override
    protected <T extends BaseRecViewHolder> T createHolder(View view) {
        return (T)new ArtistHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ArtistHolder artHolder = (ArtistHolder)holder;
        artHolder.itemView.setTag(position);
        artHolder.item_art.setText(this.artList.get(position));
        artHolder.item_number.setText(this.artMap.get(this.artList.get(position)).size()+"首");
    }

    @Override
    public int getItemCount() {
        return this.artList.size();
    }

    @Override
    public void onClick(View view) {
        this.defaultOnClick(view);
    }
}
