package com.dasong.zmusic.model.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.bean.Music;

import java.util.List;

/**
 * Created by dason on 2017/8/21 0021.
 */

public class AllSongRecAdapter extends BaseRecAdapter{

    private List<Music> list;

    public static class AllsongHolder extends BaseRecViewHolder{

        RelativeLayout item_main;
        TextView item_name;
        TextView item_art_album;
        boolean isChecked = false;

        public AllsongHolder(View itemView) {
            super(itemView);
            item_name = this.findView(itemView,R.id.item_name);
            item_art_album = this.findView(itemView,R.id.item_art_album);
            item_main = this.findView(itemView,R.id.item_main);
        }
    }

    public AllSongRecAdapter(List<Music> list,int layoutId){
        super(layoutId);
        this.list = list;
        Log.d("ZLogcat","size:"+this.list.size());

    }

/*    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song,parent,false);
        view.setOnClickListener(this);
        AllsongHolder holder = new AllsongHolder(view);
        return holder;
    }*/

    @Override
    protected <T extends BaseRecViewHolder> T createHolder(View view) {
        return (T)new AllsongHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        ((AllsongHolder)holder).item_name.setText(this.list.get(position).getName());
        ((AllsongHolder)holder).item_art_album.setText(this.list.get(position).getArtist()+"-"+this.list.get(position).getAlbum());
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @Override
    public void onClick(View view) {
        this.defaultOnClick(view);
    }

}
