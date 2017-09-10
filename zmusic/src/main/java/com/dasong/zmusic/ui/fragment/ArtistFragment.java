package com.dasong.zmusic.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.adapter.ArtistRecAdapter;
import com.dasong.zmusic.model.adapter.BaseRecAdapter;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.msg.OnShowPageMsg;
import com.dasong.zmusic.ui.activity.MainActivity;
import com.dasong.zmusic.ui.base.BaseFragment;
import com.dasong.zmusic.utils.MusicOrder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by dason on 2017/8/19 0019.
 */

public class ArtistFragment extends BaseFragment {

    private MainActivity activity;
    private RecyclerView artRecView;
    private List<String> artList;
    private Map<String,List<Music>> artMap;
    private List<Music> musicList;
    private ArtistRecAdapter adapter;
    private LinearLayoutManager manager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_byartist,container,false);
        //EventBus.getDefault().register(this);
        this.initView();
        return view;
    }

    @Override
    public void onDestroy() {
        //EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 获取初始化数据
     */
    private void getData(){
        if(this.artList == null && this.artMap == null){
            this.artMap = MusicOrder.byArtist(this.activity.musicList);
            this.artList = MusicOrder.getArtist(this.activity.musicList);
        }
    }

    private void initView(){
        this.getData();
        this.artRecView = this.findView(this.view,R.id.art_list);
        this.adapter = new ArtistRecAdapter(this.artMap,this.artList,R.layout.item_art);
        this.manager = new LinearLayoutManager(this.activity);
        this.manager.setOrientation(LinearLayoutManager.VERTICAL);
        this.artRecView.setLayoutManager(this.manager);
        this.artRecView.setAdapter(this.adapter);
        this.adapter.setOnItemClickListener(new BaseRecAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                EventBus.getDefault().post(new OnShowPageMsg().setWhichPage(OnShowPageMsg.ARTIST_FRAGMENT));
            }
        });
    }


}
