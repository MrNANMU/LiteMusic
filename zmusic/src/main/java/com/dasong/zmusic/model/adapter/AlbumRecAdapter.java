package com.dasong.zmusic.model.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.utils.universal.PixUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dason on 2017/8/21 0021.
 */

public class AlbumRecAdapter extends BaseRecAdapter {

    private Context context;
    private Map<String,List<Music>> albumMap;
    private List<String> albumList;
    private List<Bitmap> covers = new ArrayList<Bitmap>();

    public AlbumRecAdapter(Context context,Map<String, List<Music>> albumMap, List<String> albumList, int layoutId) {
        super(layoutId);
        this.context = context;
        this.albumMap = albumMap;
        this.albumList = albumList;
    }

    @Override
    public void onClick(View view) {
        this.defaultOnClick(view);
    }

    public static class AlbumHoder extends BaseRecViewHolder{

        ImageView item_cover;
        TextView item_album;

        public AlbumHoder(View itemView) {
            super(itemView);
            item_cover = this.findView(itemView, R.id.item_cover);
            item_album = this.findView(itemView,R.id.item_album);
        }
    }

    @Override
    protected <T extends BaseRecViewHolder> T createHolder(View view) {
        return (T)new AlbumHoder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AlbumHoder albumHoder = (AlbumHoder)holder;
        albumHoder.itemView.setTag(position);
        String albumName = this.albumList.get(position);
        Music music = this.albumMap.get(albumName).get(0);
        Bitmap cover = music.getCover(PixUtils.dp2px(this.context,128),PixUtils.dp2px(this.context,128));
        //Log.d("ZLogcat","AlbumRecAdapter:albumName->"+albumName);
        if(albumName != null && cover != null){
            albumHoder.item_album.setText(albumName);
            albumHoder.item_cover.setImageBitmap(cover);
        }else{
            albumHoder.item_album.setText("默认专辑");
            albumHoder.item_cover.setImageResource(R.drawable.cover_logo);
        }


    }

    @Override
    public int getItemCount() {
        return this.albumList.size();
    }
}
