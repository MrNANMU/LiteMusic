package com.dasong.zmusic.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.adapter.BaseRecAdapter;
import com.dasong.zmusic.model.adapter.ResultRecAdapter;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.listener.MusicSelectListener;
import com.dasong.zmusic.ui.base.BaseActivity;
import com.dasong.zmusic.utils.onlythis.MusicFinder;

import java.util.List;

/**
 * Created by dason on 2017/8/26 0026.
 */

public class SelelctActivity extends BaseActivity {

    private List<Music> musicList;

    private RecyclerView result_list;
    private Button resule_findbtn;
    private EditText result_edit;
    private TextView resule_none;
    private ResultRecAdapter adapter;
    private LinearLayoutManager manager;
    private String editText = "";
    private MusicSelectListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        //EventBus.getDefault().register(this);
        this.initView();
    }

    @Override
    protected void onDestroy() {
        //EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initView(){
        this.result_list = this.findView(R.id.result_list);
        this.resule_findbtn = this.findView(R.id.result_findbtn);
        this.result_edit = this.findView(R.id.result_edit);
        this.resule_none = this.findView(R.id.resule_none);
        this.manager = new LinearLayoutManager(this);
        this.manager.setOrientation(LinearLayoutManager.VERTICAL);
        this.result_list.setLayoutManager(this.manager);
        this.clickEvent();
    }

    private void clickEvent(){
        this.resule_findbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = SelelctActivity.this.result_edit.getText().toString();
                SelelctActivity.this.show(name);
            }
        });
        this.result_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelelctActivity.this.resule_none.setVisibility(View.INVISIBLE);
                if(SelelctActivity.this.adapter != null){
                    SelelctActivity.this.result_list.removeAllViews();
                    SelelctActivity.this.musicList.removeAll(SelelctActivity.this.musicList);
                    SelelctActivity.this.adapter.notifyDataSetChanged();
                }
            }
        });
    }
    private void show(String name){
        this.musicList = MusicFinder.selectMusicByName(this,name);
        Log.d("ZLogcat","SelectActivity.show:List.size()->"+this.musicList.size());
        if(this.musicList.size() == 0){
            this.resule_none.setVisibility(View.VISIBLE);
        }else{
            this.adapter = new ResultRecAdapter(this,this.musicList,R.layout.item_result);
            this.result_list.setAdapter(this.adapter);
            this.adapter.setOnItemClickListener(new BaseRecAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    TextView item_resule_name = v.findViewById(R.id.item_resule_name);
                    TextView item_resule_art_album = v.findViewById(R.id.item_resule_art_album);
                    item_resule_name.setTextColor(SelelctActivity.this.getResources().getColor(R.color.colorPrimary));
                    item_resule_art_album.setTextColor(SelelctActivity.this.getResources().getColor(R.color.colorPrimary));
                }
            });
        }
    }

}
