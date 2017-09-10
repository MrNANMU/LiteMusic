package com.dasong.zmusic.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.adapter.AlbumRecAdapter;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.msg.OnShowPageMsg;
import com.dasong.zmusic.ui.activity.MainActivity;
import com.dasong.zmusic.ui.base.BaseFragment;
import com.dasong.zmusic.utils.MusicOrder;
import com.dasong.zmusic.utils.PixUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dason on 2017/8/19 0019.
 */

public class AlbumFragment extends BaseFragment {

    private RecyclerView albumRecView;
    private MainActivity activity;
    private List<String> albumList;
    private Map<String,List<Music>> albumMap;
    private List<Music> musicList;
    private AlbumRecAdapter adapter;
    private GridLayoutManager manager;

    private GridView albumGrid;
    private SimpleAdapter simpleAdapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("ZLogcat","AlbumFm is running");
        this.view = inflater.inflate(R.layout.fragment_album,container,false);
        this.initView();
        return view;
    }

    /**
     * 获取初始化数据
     */
    private void getData(){
        if(this.albumList == null && this.albumMap == null){
            this.albumMap = MusicOrder.byAlbum(this.activity.musicList);
            this.albumList = MusicOrder.getAlbumList(this.activity.musicList);
        }
    }

    /*private void initView(){
        this.getData();
        this.albumRecView = this.findView(this.view,R.id.album_list);
        this.adapter = new AlbumRecAdapter(this.activity,this.albumMap,this.albumList,R.layout.item_gird);
        this.manager = new GridLayoutManager(this.activity,2,GridLayoutManager.VERTICAL,false);
        this.manager.setOrientation(GridLayoutManager.VERTICAL);
        this.albumRecView.setLayoutManager(this.manager);
        //this.albumRecView.addItemDecoration(new GridDivider(this.activity,GridLayoutManager.VERTICAL));
        this.albumRecView.setAdapter(this.adapter);
    }*/

    private void initView(){
        this.getData();
        List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
        for(String album:this.albumList){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("item_cover",this.albumMap.get(album).get(0).getCover(PixUtils.dp2px(this.activity,128),PixUtils.dp2px(this.activity,128)));
            map.put("item_album",album);
            datas.add(map);
        }
        this.albumGrid = this.findView(this.view,R.id.album_grid);
        this.simpleAdapter = new SimpleAdapter(this.activity,datas,R.layout.item_gird,new String[]{"item_cover","item_album"},new int[]{R.id.item_cover,R.id.item_album});
        this.albumGrid.setAdapter(this.simpleAdapter);
        this.simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if(view instanceof ImageView && o instanceof Bitmap){
                    ((ImageView)view).setImageBitmap((Bitmap)o);
                    return true;
                }else{
                    return false;
                }
            }
        });
        this.albumGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventBus.getDefault().post(new OnShowPageMsg().setWhichPage(OnShowPageMsg.ALBUM_FRAGMENT));
            }
        });

    }//*/

}
