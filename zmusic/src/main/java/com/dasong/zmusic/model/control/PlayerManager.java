package com.dasong.zmusic.model.control;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

import com.dasong.zmusic.background.service.MusicService;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.config.ConfigXML;
import com.dasong.zmusic.model.config.PlayMode;
import com.dasong.zmusic.model.config.UserConfig;
import com.dasong.zmusic.model.msg.OnItemClickMsg;
import com.dasong.zmusic.model.msg.OnMusicModelChangeMsg;
import com.dasong.zmusic.model.msg.OnMusicStartMsg;
import com.dasong.zmusic.model.msg.OnMusicStateChangedMsg;
import com.dasong.zmusic.model.msg.OnNextMusicMsg;
import com.dasong.zmusic.model.msg.OnSeekBarTouchMsg;
import com.dasong.zmusic.model.msg.OnUpdateSeekMsg;
import com.dasong.zmusic.utils.onlythis.MediaPlayerInstall;
import com.dasong.zmusic.utils.onlythis.MusicFinder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dason on 2017/8/26 0026.
 */

public class PlayerManager {

    private static PlayerManager instance;

    private final static List<Music> LIST = new ArrayList<Music>();
    private final static List<Music> LASTLIST = new ArrayList<Music>();
    private Music isPlayingMusic;

    private Service context;
    private MediaPlayer player = MediaPlayerInstall.create();
    private SharedPreferences config;

    //状态量
    private int playMode = PlayMode.UNKNOUN;  //放模式
    private int position;                     //当前位置
    private int listSize;                     //当前播放列表总长
    private boolean isPrepare = false;        //当前歌曲是否处于prepare状态


        private PlayerManager(Service context,List<Music> list){
        this.context = context;
        LIST.addAll(list);
    }

    public static synchronized PlayerManager create(Service context,List<Music> list){
        if(instance == null){
            instance = new PlayerManager(context,list);
        }
        return instance;
    }

    public void open(){
        this.config = this.context.getSharedPreferences(ConfigXML.USER_CONFIG, Context.MODE_PRIVATE);
        this.isPlayingMusic = MusicFinder.selectMusicById(this.context,this.config.getLong(UserConfig.LASTPLAYMUSICID,LIST.get(0).getId()));
        this.position = this.isPlayingMusic.getPosition();
        this.playMode = this.config.getInt(UserConfig.PLAYMODEL, PlayMode.UNKNOUN);
        this.register();
    }

    public void close(){
        this.unregister();
        if(this.isPlaying()){
            this.reset();
            //this.player.release();
            //添加此句会导致下次启动的时候报错，原因未知
        }else{
            this.stop();
            this.reset();
            //this.player.release();
            //添加此句会导致下次启动的时候报错，原因未知
        }
    }

    public void next(){
        OnMusicStartMsg msg;
        OnNextMusicMsg next;
        switch (this.playMode){
            case PlayMode.LOOP:
                this.player.reset();
                if(this.position + 1 == LIST.size()){
                    this.position = 0;
                    this.isPlayingMusic = LIST.get(this.position);
                    msg = new OnMusicStartMsg();
                    msg.position = 0;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = PlayerManager.this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }else{
                    this.position ++;
                    this.isPlayingMusic = LIST.get(this.position);
                    msg = new OnMusicStartMsg();
                    msg.position = PlayerManager.this.position;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = PlayerManager.this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }
                this.playInLoopMode();
                break;
            case PlayMode.RANDOM:
                this.player.reset();
                Random random = new Random();
                int newPosition = random.nextInt(LIST.size());
                while(this.position == newPosition){
                    newPosition = random.nextInt(LIST.size());
                }
                this.position = newPosition;
                this.isPlayingMusic = LIST.get(this.position);
                msg = new OnMusicStartMsg();
                msg.position = newPosition;
                EventBus.getDefault().post(msg);
                next = new OnNextMusicMsg();
                next.nextMusic = this.isPlayingMusic;
                EventBus.getDefault().post(next);
                this.playInRandomMode();
                break;
            case PlayMode.SINGLE:
                this.player.reset();
                if(this.position + 1 == LIST.size()){
                    this.position = 0;
                    this.isPlayingMusic = LIST.get(this.position);
                    msg = new OnMusicStartMsg();
                    msg.position = 0;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = PlayerManager.this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }else{
                    this.position ++;
                    this.isPlayingMusic = LIST.get(this.position);
                    msg = new OnMusicStartMsg();
                    msg.position = PlayerManager.this.position;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = PlayerManager.this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }
                this.playInSingleMode();
                break;
            case PlayMode.UNKNOUN:
                this.playMode = PlayMode.LOOP;
                this.next();
                break;
            default:
                this.playMode = PlayMode.LOOP;
                this.next();
                break;
        }
    }

    public void last(){
        OnMusicStartMsg msg;
        OnNextMusicMsg next;
        switch (this.playMode){
            case PlayMode.LOOP:
                this.player.reset();
                if(this.position == 0){
                    this.position = this.LIST.size() - 1;
                    this.isPlayingMusic = this.LIST.get(this.position);
                    msg = new OnMusicStartMsg();
                    msg.position = this.position;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }else{
                    this.position --;
                    this.isPlayingMusic = this.LIST.get(this.position);
                    msg = new OnMusicStartMsg();
                    msg.position = this.position;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }
                this.playInLoopMode();
                break;
            case PlayMode.RANDOM:
                //随机播放模式下上一首也是随机出来的，并没有一个顺序之说
                this.next();
                break;
            case PlayMode.SINGLE:
                this.player.reset();
                if(this.position == 0){
                    this.position = LIST.size() - 1;
                    this.isPlayingMusic = LIST.get(this.position);
                    msg = new OnMusicStartMsg();
                    msg.position = this.position;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }else{
                    this.position --;
                    this.isPlayingMusic = LIST.get(this.position);
                    msg = new OnMusicStartMsg();
                    msg.position = this.position;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }
                this.playInSingleMode();
                break;
            case PlayMode.UNKNOUN:
                this.playMode = PlayMode.LOOP;
                this.next();
                break;
            default:
                this.playMode = PlayMode.LOOP;
                this.next();
                break;
        }
    }

    public void pause(){
        if(this.player.isPlaying()) {
            this.player.pause();
            EventBus.getDefault().post(new OnMusicStateChangedMsg(false));
        }else{
            if(this.isPrepare){
                this.player.start();
            }else{
                this.startPlayingByMode();
            }
            EventBus.getDefault().post(new OnMusicStateChangedMsg(true));
        }
    }

    public void reset(){
        this.player.reset();
    }

    public void stop(){
        this.player.stop();
    }

    public void release(){
        this.player.release();
    }

    public boolean isPlaying(){
        return this.player.isPlaying();
    }

    public void unregister(){
        EventBus.getDefault().unregister(this);
    }

    public void register(){
        EventBus.getDefault().register(this);
    }

    public void setMusicList(List<Music> list){
        LASTLIST.removeAll(LASTLIST);
        LASTLIST.addAll(LIST);
        LIST.removeAll(LIST);
        LIST.addAll(list);
        this.position = 0;
        this.listSize = LIST.size();
    }

    public void setPlayMode(int mode){
        this.playMode = mode;
    }

    public int getPlayMode(){
        return this.playMode;
    }

    public List<Music> getPlayingMusicList(){
        return LIST;
    }

    public List<Music> getLastPlayingMusicList(){
        return LASTLIST;
    }

    public Music getPlayingMusic(){
        return LIST.get(this.position);
    }

    public int getPosition(){
        return this.position;
    }

    //单曲循环
    private void playInSingleMode(){
        try {
            final Timer timer = new Timer();
            this.player.setDataSource(this.context,this.isPlayingMusic.getUri());
            this.player.prepareAsync();
            this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    PlayerManager.this.isPrepare = true;
                    mediaPlayer.start();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(mediaPlayer.isPlaying()){
                                OnUpdateSeekMsg msg = new OnUpdateSeekMsg();
                                msg.progress = mediaPlayer.getCurrentPosition();
                                EventBus.getDefault().post(msg);
                            }
                        }
                    },1000,1000);
                }
            });
            this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    timer.cancel();
                    mediaPlayer.reset();
                    PlayerManager.this.playInSingleMode();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //列表循环
    private void playInLoopMode(){
        try {
            final Timer timer = new Timer();
            this.player.setDataSource(this.context,this.isPlayingMusic.getUri());
            this.player.prepareAsync();
            this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    PlayerManager.this.isPrepare = true;
                    mediaPlayer.start();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(mediaPlayer.isPlaying()){
                                OnUpdateSeekMsg msg = new OnUpdateSeekMsg();
                                msg.progress = mediaPlayer.getCurrentPosition();
                                EventBus.getDefault().post(msg);
                            }
                        }
                    },1000,1000);
                }
            });
            this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    timer.cancel();
                    PlayerManager.this.next();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //随机模式
    private void playInRandomMode(){
        try {
            Log.d("ZLogcat","RandomMode.Position:"+this.position);
            final Timer timer = new Timer();
            this.player.setDataSource(this.context,this.isPlayingMusic.getUri());
            this.player.prepareAsync();
            this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    PlayerManager.this.isPrepare = true;
                    mediaPlayer.start();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(mediaPlayer.isPlaying()){
                                OnUpdateSeekMsg msg = new OnUpdateSeekMsg();
                                msg.progress = mediaPlayer.getCurrentPosition();
                                EventBus.getDefault().post(msg);
                            }
                        }
                    },1000,1000);
                }
            });
            this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    timer.cancel();
                    PlayerManager.this.next();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startPlayingByMode(){
        switch (this.playMode){
            case PlayMode.LOOP :
                this.playInLoopMode();
                break;
            case PlayMode.RANDOM :
                this.playInRandomMode();
                break;
            case PlayMode.SINGLE :
                this.playInSingleMode();
                break;
            case PlayMode.UNKNOUN :
                this.playInLoopMode();
                break;
            default :
                this.playMode = PlayMode.UNKNOUN;
                this.startPlayingByMode();
                break;
        }
    }

    /**
     * 点击播放列表的时候会触发此事件，并播放指定的音乐
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void playByClickItem(OnItemClickMsg msg){
        if(this.player.isPlaying()){
            this.player.stop();
        }
        this.player.reset();
        int clickPosition = msg.getPosition();
        this.position = clickPosition;
        this.isPlayingMusic = LIST.get(clickPosition);
        this.startPlayingByMode();
    }

    /**
     * 切换播放模式时触发
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void setPlayModel(OnMusicModelChangeMsg msg){
        this.playMode = msg.model;
        if(this.player.isPlaying()){
            this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(PlayerManager.this.playMode != PlayMode.SINGLE){
                        PlayerManager.this.next();
                    }else{
                        mediaPlayer.reset();
                        PlayerManager.this.playInSingleMode();
                    }

                }
            });
        }else{
            this.startPlayingByMode();
        }
    }

    /**
     * 拖动控制条时的事件，三种状态：
     * 1.正在播放时拖动
     * 2.mediaplayer没有Prepare时拖动
     * 3.歌曲处于暂停状态的时候拖动
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void changProess(final OnSeekBarTouchMsg msg){
        if(this.player.isPlaying()){
            this.pause();
            this.player.seekTo(msg.progress);
            this.player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }else{
            if(!this.isPrepare){
                //未开始播放任何一首歌
                try {
                    final Timer timer = new Timer();
                    this.player.setDataSource(this.context,this.isPlayingMusic.getUri());
                    this.player.prepareAsync();
                    this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            PlayerManager.this.isPrepare = true;
                            //mediaPlayer.start();
                            mediaPlayer.seekTo(msg.progress);
                            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                                @Override
                                public void onSeekComplete(final MediaPlayer mediaPlayer) {
                                    if(mediaPlayer.isPlaying()){
                                        mediaPlayer.pause();
                                    }
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if(mediaPlayer.isPlaying()){
                                                OnUpdateSeekMsg msg = new OnUpdateSeekMsg();
                                                msg.progress = mediaPlayer.getCurrentPosition();
                                                EventBus.getDefault().post(msg);
                                            }
                                        }
                                    },1000,1000);
                                }
                            });

                        }
                    });

                    this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.reset();
                            timer.cancel();
                            PlayerManager.this.startPlayingByMode();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                if(this.isPlaying()){
                }
                this.player.seekTo(msg.progress);
                this.player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        if(mediaPlayer.isPlaying()){
                        }
                    }
                });
            }
        }
    }

}
