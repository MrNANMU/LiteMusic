package com.dasong.zmusic.model.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.listener.MusicSelectListener;
import com.dasong.zmusic.utils.PixUtils;

import java.util.List;

/**
 * Created by dason on 2017/9/4 0004.
 */

public class ResultRecAdapter extends BaseRecAdapter {

    private List<Music> musicList;
    private Context context;
    //private MusicSelectListener listener;

    public static class ResultHolder extends BaseRecViewHolder{

        TextView item_resule_name;
        TextView item_resule_art_album;
        ImageView item_resule_local;

        public ResultHolder(View itemView) {
            super(itemView);
            item_resule_name = this.findView(itemView, R.id.item_resule_name);
            item_resule_art_album = this.findView(itemView, R.id.item_resule_art_album);
            item_resule_local = this.findView(itemView,R.id.item_resule_local);
        }
    }


    public ResultRecAdapter(Context context,List<Music> musicList,int layoutId) {
        super(layoutId);
        this.context = context;
        this.musicList = musicList;
    }

    @Override
    protected <T extends BaseRecViewHolder> T createHolder(View view) {
        return (T)new ResultHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ResultHolder resultHolder = (ResultHolder)holder;
        resultHolder.itemView.setTag(position);
        Music music = this.musicList.get(position);
        resultHolder.item_resule_name.setText(music.getName());
        resultHolder.item_resule_art_album.setText(music.getArtist()+"-"+music.getAlbum());
        if(music.isLocal()){
            resultHolder.item_resule_local.setImageResource(R.drawable.icon_locald);
        }else{
            resultHolder.item_resule_local.setImageResource(R.drawable.icon_netd);
        }
    }

    @Override
    public int getItemCount() {
        return this.musicList.size();
    }

    @Override
    public void onClick(View view) {
        this.defaultOnClick(view);
    }
}
