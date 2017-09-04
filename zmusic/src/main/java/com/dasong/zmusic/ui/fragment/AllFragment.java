package com.dasong.zmusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.adapter.AllSongRecAdapter;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.msg.OnItemClickMsg;
import com.dasong.zmusic.model.msg.OnMusicStartMsg;
import com.dasong.zmusic.ui.activity.MainActivity;
import com.dasong.zmusic.ui.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by dason on 2017/8/19 0019.
 */

public class AllFragment extends BaseFragment {

    private View view;
    private RecyclerView asfm_all;
    private List<Music> datas;
    private AllSongRecAdapter adapter;
    private LinearLayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_allsong,container,false);
        EventBus.getDefault().register(this);
        this.initList(this.view,savedInstanceState);
        return this.view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("ZLogcat","onSaveInstanceState:position->"+this.adapter.isCheckedPosition);
        outState.putInt("ONCLICK_POSITION",this.adapter.isCheckedPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initList(View view,Bundle bundle){
        this.datas = ((MainActivity)this.getActivity()).musicList;
        this.asfm_all = this.findView(view,R.id.allsong_list);
        this.manager = new LinearLayoutManager(this.getActivity());
        this.manager.setOrientation(LinearLayoutManager.VERTICAL);
        this.adapter = new AllSongRecAdapter(this.datas,R.layout.item_song);
        this.asfm_all.setLayoutManager(this.manager);
        this.asfm_all.setAdapter(this.adapter);
        this.adapter.setOnItemClickListener(new AllSongRecAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("ZLogcat","AllFragment.OnItemClickListener is run...");
                if(AllFragment.this.adapter.isCheckedPosition != position){
                    View oldView = AllFragment.this.asfm_all.getChildAt(AllFragment.this.adapter.isCheckedPosition);
                    if(oldView != null){
                        AllSongRecAdapter.AllsongHolder holder =  (AllSongRecAdapter.AllsongHolder) AllFragment.this.asfm_all.getChildViewHolder(oldView);
                        TextView olditem_name = holder.itemView.findViewById(R.id.item_name);
                        TextView olditem_art_ablum = holder.itemView.findViewById(R.id.item_art_album);
                        olditem_name.setTextColor(v.getContext().getResources().getColor(R.color.textColor));
                        olditem_art_ablum.setTextColor(v.getContext().getResources().getColor(R.color.textColor));
                    }
                    TextView item_name = v.findViewById(R.id.item_name);
                    TextView item_art_ablum = v.findViewById(R.id.item_art_album);
                    item_name.setTextColor(v.getContext().getResources().getColor(R.color.colorPrimary));
                    item_art_ablum.setTextColor(v.getContext().getResources().getColor(R.color.colorPrimary));
                    AllFragment.this.adapter.isCheckedPosition = position;
                    Log.d("ZLogcat","OnClick->"+position);
                    OnItemClickMsg msg = new OnItemClickMsg();
                    msg.setPosition(position);
                    EventBus.getDefault().post(msg);
                }
            }
        });
        if(bundle != null){
            Log.d("ZLogcat","bundle is not null,position is "+bundle.getInt("ONCLICK_POSITION"));
            this.adapter.isCheckedPosition = bundle.getInt("ONCLICK_POSITION");
            View newView = this.asfm_all.getChildAt(this.adapter.isCheckedPosition);
            if(newView != null){
                AllSongRecAdapter.AllsongHolder newHolder =  (AllSongRecAdapter.AllsongHolder) this.asfm_all.getChildViewHolder(newView);
                TextView item_name = newHolder.itemView.findViewById(R.id.item_name);
                TextView item_art_ablum = newHolder.itemView.findViewById(R.id.item_art_album);
                item_name.setTextColor(this.getContext().getResources().getColor(R.color.colorPrimary));
                item_art_ablum.setTextColor(this.getContext().getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void changeHighlightedItem(OnMusicStartMsg msg){
        View oldView = this.asfm_all.getChildAt(this.adapter.isCheckedPosition);
        if(oldView != null){
            AllSongRecAdapter.AllsongHolder oldHolder =  (AllSongRecAdapter.AllsongHolder) this.asfm_all.getChildViewHolder(oldView);
            TextView olditem_name = oldHolder.itemView.findViewById(R.id.item_name);
            TextView olditem_art_ablum = oldHolder.itemView.findViewById(R.id.item_art_album);
            olditem_name.setTextColor(this.getContext().getResources().getColor(R.color.textColor));
            olditem_art_ablum.setTextColor(this.getContext().getResources().getColor(R.color.textColor));
        }
        View newView = this.asfm_all.getChildAt(msg.position);
        if(newView != null){
            AllSongRecAdapter.AllsongHolder newHolder =  (AllSongRecAdapter.AllsongHolder) this.asfm_all.getChildViewHolder(newView);
            TextView item_name = newHolder.itemView.findViewById(R.id.item_name);
            TextView item_art_ablum = newHolder.itemView.findViewById(R.id.item_art_album);
            item_name.setTextColor(this.getContext().getResources().getColor(R.color.colorPrimary));
            item_art_ablum.setTextColor(this.getContext().getResources().getColor(R.color.colorPrimary));
        }
        AllFragment.this.adapter.isCheckedPosition = msg.position;
    }
}
