package com.dasong.zmusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.adapter.BaseRecAdapter;
import com.dasong.zmusic.model.adapter.ResultRecAdapter;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.listener.MusicSelectListener;
import com.dasong.zmusic.ui.base.BaseFragment;
import com.dasong.zmusic.utils.onlythis.MusicFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dason on 2017/9/6 0006.
 */

public class SelectFragment extends BaseFragment {

    private List<Music> musicList = new ArrayList<Music>();

    private RecyclerView result_list;
    private Button resule_findbtn;
    private EditText result_edit;
    private TextView resule_none;
    private ResultRecAdapter adapter;
    private LinearLayoutManager manager;
    private String editText = "";
    private MusicSelectListener listener;

    private boolean isChecked = false;

    ///*
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_select,container,false);
        this.initView(this.view);
        return this.view;
    }//*/


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.initView(this.view);
    }

    protected void initView(View view) {
        this.result_list = this.findView(view,R.id.frag_result_list);
        this.resule_findbtn = this.findView(view,R.id.frag_result_findbtn);
        this.result_edit = this.findView(view,R.id.frag_result_edit);
        this.resule_none = this.findView(view,R.id.frag_result_none);
        this.manager = new LinearLayoutManager(this.getActivity());
        this.manager.setOrientation(LinearLayoutManager.VERTICAL);
        this.result_list.setLayoutManager(this.manager);
        this.adapter = new ResultRecAdapter(this.getContext(),this.musicList,R.layout.item_result);
        this.result_list.setAdapter(this.adapter);
        this.clickEvent(this);
    }

    private void clickEvent(final SelectFragment fragment){
        this.resule_findbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = fragment.result_edit.getText().toString();
                fragment.show(name,fragment);
            }
        });
        this.result_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.resule_none.setVisibility(View.INVISIBLE);
                if(fragment.adapter != null){
                    fragment.result_list.removeAllViews();
                    fragment.musicList.removeAll(fragment.musicList);
                    fragment.adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void show(String name, final SelectFragment fragment){
        fragment.musicList.removeAll(fragment.musicList);
        this.musicList.addAll(MusicFinder.selectMusicByName(this.getContext(),name));
        Log.d("ZLogcat","SelectFragment.show:List.size()->"+this.musicList.size());
        if(this.musicList.size() == 0){
            fragment.result_list.removeAllViews();
            fragment.musicList.removeAll(fragment.musicList);
            this.adapter.notifyDataSetChanged();
            this.resule_none.setVisibility(View.VISIBLE);
        }else{
            this.adapter.notifyDataSetChanged();
            this.adapter.setOnItemClickListener(new BaseRecAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    TextView item_resule_name = v.findViewById(R.id.item_resule_name);
                    TextView item_resule_art_album = v.findViewById(R.id.item_resule_art_album);
                    if(fragment.isChecked){
                        item_resule_name.setTextColor(fragment.getResources().getColor(R.color.textColor));
                        item_resule_art_album.setTextColor(fragment.getResources().getColor(R.color.textColor));
                        fragment.isChecked = false;
                    }else{
                        item_resule_name.setTextColor(fragment.getResources().getColor(R.color.colorPrimary));
                        item_resule_art_album.setTextColor(fragment.getResources().getColor(R.color.colorPrimary));
                        fragment.isChecked = true;
                    }
                }
            });
        }
    }
}
