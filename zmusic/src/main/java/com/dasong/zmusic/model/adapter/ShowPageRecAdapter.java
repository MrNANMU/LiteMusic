package com.dasong.zmusic.model.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.ui.base.BaseFragment;

import java.util.List;

/**
 * Created by dason on 2017/9/6 0006.
 */

public class ShowPageRecAdapter extends BaseRecAdapter {

    private List<Music> list;

    public class ShowPageRecHolder extends BaseRecViewHolder{

        TextView item_name;
        TextView item_art_album;

        public ShowPageRecHolder(View itemView) {
            super(itemView);
            item_name = this.findView(itemView, R.id.item_name);
            item_art_album = this.findView(itemView,R.id.item_art_album);
        }
    }

    public ShowPageRecAdapter(List<Music> list,int layoutId) {
        super(layoutId);
        this.list = list;
    }

    @Override
    protected <T extends BaseRecViewHolder> T createHolder(View view) {
        return (T)new ShowPageRecHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        ShowPageRecHolder mHolder = (ShowPageRecHolder)holder;
        mHolder.item_name.setText(this.list.get(position).getName());
        mHolder.item_art_album.setText(this.list.get(position).getArtist()+"-"+this.list.get(position).getAlbum());
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
