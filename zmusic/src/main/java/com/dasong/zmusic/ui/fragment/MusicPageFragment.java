package com.dasong.zmusic.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.adapter.ShowPageRecAdapter;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.msg.OnShowPageMsg;
import com.dasong.zmusic.ui.base.BaseActivity;
import com.dasong.zmusic.ui.base.BaseFragment;
import com.dasong.zmusic.utils.onlythis.MusicFinder;
import com.dasong.zmusic.utils.onlythis.MusicOrder;
import com.dasong.zmusic.utils.universal.PixUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dason on 2017/9/10 0010.
 */

public class MusicPageFragment <ACTIVITY extends BaseActivity> extends BaseFragment{

    private Context activity;
    private static int imgX;
    private static int imgY;

    private Map<String,List<Music>> albumList = new HashMap<String,List<Music>>();
    private Map<String,List<Music>> artList = new HashMap<String,List<Music>>();
    private List<Music> musicList = new ArrayList<Music>();

    private ImageView show_coverpic;
    private RecyclerView show_list;
    private ShowPageRecAdapter adapter;
    private LinearLayoutManager manager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        this.view = inflater.inflate(R.layout.fragment_musicpage,container,false);
        this.initView(this.view);
        return this.view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (ACTIVITY)context;
        this.imgX = PixUtils.dp2px(this.activity,360);
        this.imgY = PixUtils.dp2px(this.activity,160);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View view){
        this.show_coverpic = this.findView(view,R.id.show_coverpic);
        this.show_list = this.findView(view,R.id.show_list);
        this.manager = new LinearLayoutManager(this.activity);
        this.manager.setOrientation(LinearLayoutManager.VERTICAL);
        this.adapter = new ShowPageRecAdapter(this.musicList,R.layout.item_song);
        this.show_list.setLayoutManager(this.manager);
        this.show_list.setAdapter(this.adapter);
    }

    private void getData(int page,int position,String name){
        this.albumList.clear();
        this.artList.clear();
        if(this.musicList != null){
            this.musicList.removeAll(this.musicList);
        }
        switch (page){
            case OnShowPageMsg.ALBUM_FRAGMENT:
                Map<String,List<Music>> map = MusicOrder.byAlbum(MusicFinder.getAll(this.activity));
                this.albumList.putAll(map);
                this.musicList = this.albumList.get(name);
                break;
            case OnShowPageMsg.ARTIST_FRAGMENT:
                this.artList = MusicOrder.byArtist(MusicFinder.getAll(this.activity));
                this.musicList = this.artList.get(name);
                break;
            default:break;
        }
    }

    private void resetPage(){
        Log.d("ZLog","resetPage");
        this.show_list.removeAllViews();
        this.adapter.notifyDataSetChanged();
        this.show_coverpic.setImageBitmap(this.musicList.get(0).getCover(this.imgX,this.imgY));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowPageListener(OnShowPageMsg msg){
        Log.d("ZLogcat",msg.whichPage+","+msg.whichPosition+","+msg.whichItem+"");
        this.getData(msg.whichPage,msg.whichPosition,msg.whichItem);
        this.resetPage();
    }
}
