package com.dasong.zmusic.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dasong.zmusic.R;

import com.dasong.zmusic.background.service.MusicService;
import com.dasong.zmusic.model.adapter.MyPagerAdapter;
import com.dasong.zmusic.model.adapter.ShowPageRecAdapter;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.config.ConfigXML;
import com.dasong.zmusic.model.config.PlayModel;
import com.dasong.zmusic.model.config.UserConfig;
import com.dasong.zmusic.model.msg.OnItemClickMsg;
import com.dasong.zmusic.model.msg.OnMusicModelChangeMsg;
import com.dasong.zmusic.model.msg.OnMusicStartMsg;
import com.dasong.zmusic.model.msg.OnMusicStateChangedMsg;
import com.dasong.zmusic.model.msg.OnNextMusicMsg;
import com.dasong.zmusic.model.msg.OnSeekBarTouchMsg;
import com.dasong.zmusic.model.msg.OnShowPageMsg;
import com.dasong.zmusic.model.msg.OnUpdateSeekMsg;
import com.dasong.zmusic.ui.base.BaseActivity;
import com.dasong.zmusic.ui.base.BaseFragment;
import com.dasong.zmusic.ui.fragment.AlbumFragment;
import com.dasong.zmusic.ui.fragment.AllFragment;
import com.dasong.zmusic.ui.fragment.ArtistFragment;
import com.dasong.zmusic.ui.fragment.MusicListFragment;
import com.dasong.zmusic.utils.onlythis.MusicFinder;
import com.dasong.zmusic.utils.universal.PixUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static boolean isPlaying = false;
    private static String APPACTION_PLAY = "com.dasong.zmusic.PLAY";
    private static String APPACTION_PAUSE = "com.dasong.zmusic.PAUSE";

    private SharedPreferences config;

    private FragmentManager showPageManager = this.getSupportFragmentManager();
    private FragmentTransaction showPageTransaction;

    private Toolbar bar_main_title;
    private Button btn_main_find;
    private RelativeLayout player_console;
    private TextView player_console_name,player_console_lyric,player_console_artist,player_now,player_long;
    private ImageView player_cover,player_console_cover;
    private FloatingActionButton player_playbtn;
    private ImageButton player_model,player_last,player_play,player_next,player_list;
    private SeekBar player_seek;
    private TabLayout bar_tab;
    private ViewPager main_pager;
    private MyPagerAdapter adapter;
    private AlbumFragment albumFragment;
    private MusicListFragment musicListFragment;
    private AllFragment allFragment;
    private ArtistFragment artistFragment;
    private List<BaseFragment> fragments;
    private List<String> titles;
    private ShowPageRecAdapter showPageAdapter;
    public Fragment main_showFrag,main_showPage;

    public List<Music> musicList;

    private MusicService.MusicBinder musicBinder;
    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MainActivity.this.musicBinder = (MusicService.MusicBinder)iBinder;
            MainActivity.this.musicBinder.setMusicList(MainActivity.this.musicList);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        this.checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    //接受了权限才能
                    this.musicList = MusicFinder.getAll(this);
                    this.initMain();
                    this.setSupportActionBar(this.bar_main_title);
                    this.startMusicService();
                    this.createConfig();
                    this.initConsole();
                    this.initShowPager();
                }else{
                    Toast.makeText(this,"需要权限搜索本地音乐，即将退出",Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1500);
                        MainActivity.this.finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("ZLogcat","MainActivity.onDestroy");
        Music music = this.musicBinder.getPlayingMusic();
        Log.d("ZLogcat","OD:Music.id->"+music.getId()+",Music.name->"+music.getName());
        this.config = this.getSharedPreferences(ConfigXML.USER_CONFIG,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = this.config.edit();
        editor.putLong(UserConfig.LASTPLAYMUSICID,this.musicBinder.getPlayingMusic().getId());
        editor.commit();
        //this.unbindService(this.musicServiceConnection);
        //this.stopService(new Intent(this,MusicService.class));
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    private void checkPermissions(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
            Log.d("ZLogcat","申请权限");
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        } else{
          Log.d("ZLogcat","已有权限，直接运行");
            this.musicList = MusicFinder.getAll(this);
            this.initMain();
            this.setSupportActionBar(this.bar_main_title);
            this.startMusicService();
            this.createConfig();
            this.initConsole();
            this.initShowPager();
        }
    }

    private void startMusicService(){
        Intent intent = new Intent(this, MusicService.class);
        this.bindService(intent,musicServiceConnection,BIND_AUTO_CREATE);
        this.startService(new Intent(this, MusicService.class));
    }

    private void createConfig(){
        this.config = this.getSharedPreferences(ConfigXML.USER_CONFIG, Context.MODE_PRIVATE);
        if(config.getInt(UserConfig.FIRSTLOAD,0) == 0){
            SharedPreferences.Editor editor = config.edit();
            editor.putInt(UserConfig.FIRSTLOAD,1);
            editor.putInt(UserConfig.PLAYMODEL, PlayModel.LOOP);
            editor.commit();
        }
    }

    //初始化主页四个ViewPager和Fragment
    private void initMain(){
        this.btn_main_find = this.findView(R.id.btn_main_find);
        this.btn_main_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.showPageManager = MainActivity.this.getSupportFragmentManager();
                MainActivity.this.showPageTransaction = MainActivity.this.showPageManager.beginTransaction();
                MainActivity.this.showPageTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                MainActivity.this.showPageTransaction.show(MainActivity.this.main_showFrag);
                MainActivity.this.showPageTransaction.addToBackStack(null);
                MainActivity.this.showPageTransaction.commit();
            }
        });
        this.bar_tab = this.findView(R.id.bar_tab);
        this.main_pager = this.findView(R.id.main_pager);
        this.albumFragment = new AlbumFragment();
        this.allFragment = new AllFragment();
        this.artistFragment = new ArtistFragment();
        this.musicListFragment = new MusicListFragment();
        this.fragments = new ArrayList<BaseFragment>();
        this.fragments.add(this.allFragment);
        this.fragments.add(this.musicListFragment);
        this.fragments.add(this.albumFragment);
        this.fragments.add(this.artistFragment);
        this.titles = new ArrayList<String>();
        this.titles.add("单曲");
        this.titles.add("歌单");
        this.titles.add("专辑");
        this.titles.add("歌手");
        int i=0;
        while(i++ < 4){
            this.bar_tab.addTab(this.bar_tab.newTab());
        }
        this.adapter = new MyPagerAdapter(this.getSupportFragmentManager(),this.fragments,this.titles);
        this.main_pager.setAdapter(this.adapter);
        this.main_pager.setOffscreenPageLimit(4);
        this.bar_tab.setupWithViewPager(this.main_pager);
    }

    //初始化展示页的View
    private void initShowPager(){
        this.main_showFrag = this.showPageManager.findFragmentById(R.id.main_showFrag);
        this.main_showPage = this.showPageManager.findFragmentById(R.id.main_showPage);
        this.showPageTransaction = this.showPageManager.beginTransaction();
        this.showPageTransaction.hide(this.main_showFrag);
        this.showPageTransaction.hide(this.main_showPage);
        this.showPageTransaction.commit();
    }

    //初始上拉播放界面和首页悬浮按钮
    private void initConsole(){
        this.player_console = this.findView(R.id.player_console);
        this.player_cover = this.findView(R.id.player_cover);
        this.player_console_cover = this.findView(R.id.player_console_cover);
        this.player_console_artist = this.findView(R.id.player_console_artist);
        this.player_console_lyric = this.findView(R.id.player_console_lyric);
        this.player_console_name = this.findView(R.id.player_console_name);
        this.player_seek = this.findView(R.id.player_seek);
        this.player_now = this.findView(R.id.player_now);
        this.player_long = this.findView(R.id.player_long);
        this.player_playbtn = this.findView(R.id.player_playbtn);
        this.player_model = this.findView(R.id.player_model);
        this.player_last = this.findView(R.id.player_last);
        this.player_play = this.findView(R.id.player_play);
        this.player_next = this.findView(R.id.player_next);
        this.player_list = this.findView(R.id.player_list);
        this.player_playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.this.isPlaying){
                    MainActivity.this.musicBinder.pause();
                    MainActivity.this.isPlaying = false;
                    OnMusicStartMsg msg = new OnMusicStartMsg();
                    msg.position = MainActivity.this.musicBinder.getPlayingMusicPosition();
                    EventBus.getDefault().post(msg);
                }else{
                    MainActivity.this.musicBinder.pause();
                    MainActivity.this.isPlaying = true;
                    OnMusicStartMsg msg = new OnMusicStartMsg();
                    msg.position = MainActivity.this.musicBinder.getPlayingMusicPosition();
                    EventBus.getDefault().post(msg);
                }
            }
        });

        this.initMusicInfo();
        this.consoleEvent();
        this.seekBarEvent();
        switch (this.config.getInt(UserConfig.PLAYMODEL,PlayModel.UNKNOUN)){
            case PlayModel.LOOP :
                this.player_model.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_loopd));
                break;
            case PlayModel.RANDOM :
                this.player_model.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_randomd));
                break;
            case PlayModel.SINGLE :
                this.player_model.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_singled));
                break;
            default :
                this.player_model.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_loopd));
                break;
        }
        this.consoleButtonEvent();
    }

    private void initMusicInfo(){
        this.config = this.getSharedPreferences(ConfigXML.USER_CONFIG,Context.MODE_PRIVATE);
        long lastPlayedMusicNameId = this.config.getLong(UserConfig.LASTPLAYMUSICID,this.musicList.get(0).getId());
        Music music = MusicFinder.selectMusicById(this,lastPlayedMusicNameId);
        if(music != null){
            this.player_console_name.setText(music.getName());
            this.player_console_artist.setText(music.getArtist());
            this.player_console_lyric.setText("");
            this.player_console_cover.setImageBitmap(music.getCover(PixUtils.dp2px(this,56),PixUtils.dp2px(this,56)));
            this.player_cover.setImageBitmap(music.getCover(PixUtils.dp2px(this,360),PixUtils.dp2px(this,360)));
            this.player_seek.setMax((int)music.getTime());
            this.player_long.setText(new SimpleDateFormat("mm:ss").format(new Date(music.getTime())));

        }else{
            this.player_console_name.setText("");
            this.player_console_artist.setText("");
            this.player_console_lyric.setText("");
            this.player_console_cover.setImageDrawable(this.getResources().getDrawable(R.drawable.cover_logo));
            this.player_cover.setImageDrawable(this.getResources().getDrawable(R.drawable.cover_logo));
            this.player_long.setText("00:00");
        }
    }

    //设置上拉音乐界面
    private void consoleEvent(){
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(this.findView(R.id.player_layout));
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                switch (i){
                    case BottomSheetBehavior.STATE_COLLAPSED ://默认折叠状态
                        //Log.d("ZLogcat","折叠状态");
                        MainActivity.this.player_console.setVisibility(View.VISIBLE);
                        MainActivity.this.player_playbtn.setVisibility(View.VISIBLE);
                        MainActivity.this.player_cover.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING  ://过度状态
                       // Log.d("ZLogcat","过度状态");
                        MainActivity.this.player_playbtn.setVisibility(View.GONE);
                        MainActivity.this.player_console.setVisibility(View.GONE);
                        MainActivity.this.player_cover.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING ://视图从脱离手指自由滑动到最终停下的这一小段时间
                        //Log.d("ZLogcat","视图从脱离手指自由滑动到最终停下的这一小段时间");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED ://完全展开的状态
                        //Log.d("ZLogcat","完全展开");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN ://默认不起用
                        //Log.d("ZLogcat","默认不起用");
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View view, float v) {
                //Log.d("ZLogcat","X->"+view.getX()+",Y->"+view.getY()+",offset->"+v);
                if(v < 0){
                    view.setTranslationY(0);
                }
            }
        });
    }

    //上拉界面的按钮监听
    private void consoleButtonEvent(){
        ConsoleBtnListener listener = new ConsoleBtnListener();
        this.player_model.setOnClickListener(listener);
        this.player_play.setOnClickListener(listener);
        this.player_last.setOnClickListener(listener);
        this.player_next.setOnClickListener(listener);
    }

    private void seekBarEvent(){
        this.player_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                long progress = (long)seekBar.getProgress();
                MainActivity.this.player_now.setText(new SimpleDateFormat("mm:ss").format(new Date(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                OnSeekBarTouchMsg msg = new OnSeekBarTouchMsg();
                msg.progress = seekBar.getProgress();
                EventBus.getDefault().post(msg);
            }
        });
    }


    //上拉界面按钮监听器
    class ConsoleBtnListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            ImageButton btn = (ImageButton)view;
            switch (view.getId()){
                case R.id.player_model :
                    int model = MainActivity.this.config.getInt(UserConfig.PLAYMODEL,PlayModel.UNKNOUN);
                    SharedPreferences.Editor editor = MainActivity.this.config.edit();
                    OnMusicModelChangeMsg msg;
                    switch (model){
                        case PlayModel.LOOP :{
                            btn.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.icon_singled));
                            editor.putInt(UserConfig.PLAYMODEL,PlayModel.SINGLE);
                            editor.commit();
                            msg = new OnMusicModelChangeMsg();
                            msg.model = PlayModel.SINGLE;
                            EventBus.getDefault().post(msg);
                            Toast.makeText(MainActivity.this,"已切换到单曲循环",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case PlayModel.SINGLE :{
                            btn.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.icon_randomd));
                            editor.putInt(UserConfig.PLAYMODEL,PlayModel.RANDOM);
                            editor.commit();
                            Log.d("ZLogcat","MainActivity_361:PlayMode->"+PlayModel.RANDOM);
                            msg = new OnMusicModelChangeMsg();
                            msg.model = PlayModel.RANDOM;
                            EventBus.getDefault().post(msg);
                            Toast.makeText(MainActivity.this,"已切换到随机播放",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case PlayModel.RANDOM :{
                            btn.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.icon_loopd));
                            editor.putInt(UserConfig.PLAYMODEL,PlayModel.LOOP);
                            editor.commit();
                            msg = new OnMusicModelChangeMsg();
                            msg.model = PlayModel.LOOP;
                            EventBus.getDefault().post(msg);
                            Toast.makeText(MainActivity.this,"已切换到列表循环",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        default :
                            break;
                    }
                    break;
                case R.id.player_play :
                    if(MainActivity.isPlaying){
                        MainActivity.this.musicBinder.pause();
                        MainActivity.this.isPlaying = false;
                        OnMusicStartMsg msg1 = new OnMusicStartMsg();
                        msg1.position = MainActivity.this.musicBinder.getPlayingMusicPosition();
                        EventBus.getDefault().post(msg1);
                    }else{
                        MainActivity.this.musicBinder.pause();
                        MainActivity.this.isPlaying = true;
                        OnMusicStartMsg msg1 = new OnMusicStartMsg();
                        msg1.position = MainActivity.this.musicBinder.getPlayingMusicPosition();
                        EventBus.getDefault().post(msg1);
                    }
                    break;
                case R.id.player_last :
                    MainActivity.this.musicBinder.lastMusic();
                    break;
                case R.id.player_next :
                    MainActivity.this.musicBinder.nextMusic();
                    break;
            }
        }
    }

    public void showMusicPage(int pageTag){
        switch (pageTag){
            case 1:
                //
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateViews(OnNextMusicMsg msg){
        Music m = msg.nextMusic;
        //底栏小封面
        this.player_console_cover.setImageBitmap(m.getCover(PixUtils.dp2px(this,56),PixUtils.dp2px(this,56)));
        //底栏歌曲名
        this.player_console_name.setText(m.getName());
        //底栏歌词
        this.player_console_lyric.setText("");
        //底栏歌手名
        this.player_console_artist.setText(m.getArtist());
        //上拉大封面
        this.player_cover.setImageBitmap(m.getCover(PixUtils.dp2px(this,360),PixUtils.dp2px(this,360)));
        //进度条
        this.player_seek.setMax((int)m.getTime());
        //歌曲总长
        this.player_long.setText(new SimpleDateFormat("mm:ss").format(new Date(m.getTime())));
        //歌曲进度
        this.player_now.setText("00:00");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updapteSeek(OnUpdateSeekMsg msg){
        this.player_seek.setProgress(msg.progress);
        this.player_now.setText(new SimpleDateFormat("mm:ss").format(new Date(msg.progress)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeFbtnState(OnItemClickMsg msg){
        this.isPlaying = msg.isPlaying;
        Music m = this.musicBinder.getMusicByPosition(msg.getPosition());
        //底栏小封面
        this.player_console_cover.setImageBitmap(m.getCover(PixUtils.dp2px(this,56),PixUtils.dp2px(this,56)));
        //底栏歌曲名
        this.player_console_name.setText(m.getName());
        //底栏歌词
        this.player_console_lyric.setText("");
        //底栏歌手名
        this.player_console_artist.setText(m.getArtist());
        //上拉大封面
        this.player_cover.setImageBitmap(m.getCover(PixUtils.dp2px(this,360),PixUtils.dp2px(this,360)));
        //进度条
        this.player_seek.setMax((int)m.getTime());
        //悬浮按钮图标
        this.player_playbtn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pause));
        //上拉按钮图标
        this.player_play.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_paused));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMusicStateChangedListener(OnMusicStateChangedMsg msg){
        this.isPlaying = msg.isPlaying;
        if(this.isPlaying){
            //悬浮按钮图标
            this.player_playbtn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pause));
            //上拉按钮图标
            this.player_play.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_paused));
        }else{
            //悬浮按钮图标
            this.player_playbtn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_play));
            //上拉按钮图标
            this.player_play.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_playd));
        }
    }

    ///*
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowPageListener(OnShowPageMsg msg){
        this.showPageTransaction = this.showPageManager.beginTransaction();
        this.showPageTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        this.showPageTransaction.show(this.main_showPage);
        this.showPageTransaction.addToBackStack(null);
        this.showPageTransaction.commit();
    }//*/

}
