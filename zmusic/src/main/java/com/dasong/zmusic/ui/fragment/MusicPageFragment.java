package com.dasong.zmusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.msg.OnShowPageMsg;
import com.dasong.zmusic.ui.base.BaseFragment;
import com.dasong.zmusic.ui.base.ShowFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dason on 2017/9/10 0010.
 */

public class MusicPageFragment extends BaseFragment{

    private ImageView show_coverpic;
    private RecyclerView show_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        this.view = inflater.inflate(R.layout.fragment_musicpage,container,false);
        return this.view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View view){
        this.show_coverpic = this.findView(view,R.id.show_coverpic);
        this.show_list = this.findView(view,R.id.show_list);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowPageListener(OnShowPageMsg msg){
        switch (msg.whichPage){
            case OnShowPageMsg.ALBUM_FRAGMENT:
                Toast.makeText(this.getContext(),"专辑页",Toast.LENGTH_SHORT).show();
                break;
            case OnShowPageMsg.ARTIST_FRAGMENT:
                Toast.makeText(this.getContext(),"歌手页",Toast.LENGTH_SHORT).show();
                break;
            case OnShowPageMsg.MUSICLIST_FRAGMENT:
                Toast.makeText(this.getContext(),"歌单页",Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
