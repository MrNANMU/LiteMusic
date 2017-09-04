package com.dasong.zmusic.background.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.config.AppAction;
import com.dasong.zmusic.model.config.ConfigXML;
import com.dasong.zmusic.model.config.PlayModel;
import com.dasong.zmusic.model.config.UserConfig;
import com.dasong.zmusic.model.msg.OnItemClickMsg;
import com.dasong.zmusic.model.msg.OnMusicStartMsg;
import com.dasong.zmusic.model.msg.OnMusicModelChangeMsg;
import com.dasong.zmusic.model.msg.OnMusicStateChangedMsg;
import com.dasong.zmusic.model.msg.OnNextMusicMsg;
import com.dasong.zmusic.model.msg.OnSeekBarTouchMsg;
import com.dasong.zmusic.model.msg.OnUpdateSeekMsg;
import com.dasong.zmusic.utils.MediaPlayerInstall;
import com.dasong.zmusic.ui.activity.MainActivity;
import com.dasong.zmusic.utils.MusicFinder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dason on 2017/8/6 0006.
 */

public class MusicService extends Service {

    public static final String NOTIFICATION_NEXT = "com.dasong.zmusic.NOTIFICATION_NEXT";
    public static final String NOTIFICATION_PLAY = "com.dasong.zmusic.NOTIFICATION_PLAY";
    public static final String NOTIFICATION_LAST = "com.dasong.zmusic.NOTIFICATION_LAST";
    public static final String INIT = "com.dasong.zmusic.INIT";

    private int playModel;

    private MediaPlayer player = MediaPlayerInstall.create();
    private boolean isPlaying = false;
    private boolean isPrepare = false;
    private Music isPlayingMusic;
    private int isPlayingPosition;

    private SharedPreferences config;
    private SharedPreferences.Editor editor;

    private RemoteViews lRm,bRm;
    private Notification musicControl;

    private List<Music> musicList;

    public class MusicBinder extends Binder{

        public void setMusicList(List<Music> musicList){
            MusicService.this.musicList = musicList;
        }

        public Music getPlayingMusic(){
            return MusicService.this.isPlayingMusic;
        }

        public void startMusic(){

        }

        public void stopMusic(){
            Toast.makeText(MusicService.this,"停止",Toast.LENGTH_SHORT).show();
        }

        public void pause(){
            MusicService.this.pause();
        }

        public void nextMusic(){
            MusicService.this.next();
        }

        public void lastMusic(){
            MusicService.this.last();
        }

        public boolean isPlaying(){
            return MusicService.this.player.isPlaying();
        }

        public int getPlayingMusicPosition(){
            return MusicService.this.isPlayingPosition;
        }

        public Music getMusicByPosition(int position){
            return MusicService.this.musicList.get(position);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        Log.d("ZLogcat","onCreate");
        this.initService();
        this.startMusicNotification();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.updateNotification(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.stopForeground(true);
        if(this.player.isPlaying()){
            this.player.reset();
            //this.player.release();
            //添加此句会导致下次启动的时候报错，原因未知
        }else{
            this.player.stop();
            this.player.reset();
            //this.player.release();
            //添加此句会导致下次启动的时候报错，原因未知
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initService(){
        this.musicList = MusicFinder.getAll(this);
        this.config = this.getSharedPreferences(ConfigXML.USER_CONFIG, Context.MODE_PRIVATE);
        this.isPlayingMusic = MusicFinder.selectMusicById(this,this.config.getLong(UserConfig.LASTPLAYMUSICID,this.musicList.get(0).getId()));
        this.isPlayingPosition = this.isPlayingMusic.getPosition();
        this.playModel = this.config.getInt(UserConfig.PLAYMODEL,PlayModel.UNKNOUN);
    }

    private void startMusicNotification(){
        this.lRm = new RemoteViews(this.getPackageName(),R.layout.remoteview_noticelittle);
        this.bRm = new RemoteViews(this.getPackageName(),R.layout.remoteview_notice);
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        this.musicControl = builder.setSmallIcon(R.drawable.icon_notyd)
                .setContentTitle("")
                .setContentText("")
                .setCustomBigContentView(this.bRm).setContent(this.lRm)
                .setContentIntent(pi)
                .build();
        this.setNotificationEvent();
        this.startForeground(1,this.musicControl);
    }

     private void setNotificationEvent(){
         Intent nextIntent = new Intent(this,MusicService.class);
         Intent lastIntent = new Intent(this,MusicService.class);
         Intent playIntent = new Intent(this,MusicService.class);
         nextIntent.setAction(this.NOTIFICATION_NEXT);
         lastIntent.setAction(this.NOTIFICATION_LAST);
         playIntent.setAction(this.NOTIFICATION_PLAY);
         PendingIntent nextPi = PendingIntent.getService(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
         PendingIntent lastPi = PendingIntent.getService(this,0,lastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
         PendingIntent playPi = PendingIntent.getService(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
         this.bRm.setOnClickPendingIntent(R.id.remote_play,playPi);
         this.lRm.setOnClickPendingIntent(R.id.remote_play_lite,playPi);
         this.bRm.setOnClickPendingIntent(R.id.remote_last,lastPi);
         this.lRm.setOnClickPendingIntent(R.id.remote_last_lite,lastPi);
         this.bRm.setOnClickPendingIntent(R.id.remote_next,nextPi);
         this.lRm.setOnClickPendingIntent(R.id.remote_next_lite,nextPi);
     }

     private void setNotificationPlayBtn(boolean isPlaying){
         this.lRm = new RemoteViews(this.getPackageName(),R.layout.remoteview_noticelittle);
         this.bRm = new RemoteViews(this.getPackageName(),R.layout.remoteview_notice);
         if(isPlaying){
             this.lRm.setImageViewResource(R.id.remote_play_lite,R.drawable.icon_pause);
             this.bRm.setImageViewResource(R.id.remote_play,R.drawable.icon_pause);
         }else{
             this.lRm.setImageViewResource(R.id.remote_play_lite,R.drawable.icon_play);
             this.bRm.setImageViewResource(R.id.remote_play,R.drawable.icon_play);
         }
         this.setNotificationEvent();
     }

     private void startMusic(){
         if(this.isPlaying){
             Toast.makeText(this,"暂停",Toast.LENGTH_SHORT).show();
             this.lRm.setImageViewResource(R.id.remote_play_lite,R.drawable.icon_play);
             this.bRm.setImageViewResource(R.id.remote_play,R.drawable.icon_play);
             NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
             manager.notify(1,this.musicControl);
             this.isPlaying = false;
         }else{
             Toast.makeText(this,"播放",Toast.LENGTH_SHORT).show();
             this.lRm.setImageViewResource(R.id.remote_play_lite,R.drawable.icon_pause);
             this.bRm.setImageViewResource(R.id.remote_play,R.drawable.icon_pause);
             NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
             manager.notify(1,this.musicControl);
             this.isPlaying = true;
         }
     }

     //单曲循环
     private void playInSingleMode(){
         try {
             final Timer timer = new Timer();
             this.player.setDataSource(this,this.isPlayingMusic.getUri());
             this.player.prepareAsync();
             this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                 @Override
                 public void onPrepared(final MediaPlayer mediaPlayer) {
                     MusicService.this.isPrepare = true;
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
                     MusicService.this.playInSingleMode();
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
             this.player.setDataSource(this,this.isPlayingMusic.getUri());
             this.player.prepareAsync();
             this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                 @Override
                 public void onPrepared(final MediaPlayer mediaPlayer) {
                     MusicService.this.isPrepare = true;
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
                     MusicService.this.next();
                 }
             });
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

     private void playInRandomMode(){
         try {
             Log.d("ZLogcat","RandomMode.Position:"+this.isPlayingPosition);
             final Timer timer = new Timer();
             this.player.setDataSource(this,this.isPlayingMusic.getUri());
             this.player.prepareAsync();
             this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                 @Override
                 public void onPrepared(final MediaPlayer mediaPlayer) {
                     MusicService.this.isPrepare = true;
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
                     MusicService.this.next();
                 }
             });
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

     private void updateNotification(Intent notiIntent){
         if(notiIntent != null){
             String action = notiIntent.getAction();
             if(action != null){
                 switch (action){
                     case AppAction.NOTIFICATION_LAST:
                         this.last();
                         break;
                     case AppAction.NOTIFICATION_NEXT:
                         this.next();
                         break;
                     case AppAction.NOTIFICATION_PLAY:
                         this.pause();
                         this.setNotificationPlayBtn(this.player.isPlaying());
                         break;
                     default:break;
                 }
             }
         }
     }

     protected void pause(){
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

    /**
     * 根据模式启动播放队列
     */
    private void startPlayingByMode(){
        switch (this.playModel){
            case PlayModel.LOOP :
                this.playInLoopMode();
                break;
            case PlayModel.RANDOM :
                this.playInRandomMode();
                break;
            case PlayModel.SINGLE :
                this.playInSingleMode();
                break;
            case PlayModel.UNKNOUN :
                this.playInLoopMode();
                break;
            default :
                this.playModel = PlayModel.UNKNOUN;
                this.startPlayingByMode();
                break;
        }
    }

    /**
     * 下一首，播放队列的循环播放(playInXXXMode)，Notification的下一首按钮，都使用这个方法
     */
    private void next(){
        OnMusicStartMsg msg;
        OnNextMusicMsg next;
        switch (this.playModel){
            case PlayModel.LOOP:
                this.player.reset();
                if(this.isPlayingPosition + 1 == this.musicList.size()){
                    this.isPlayingPosition = 0;
                    this.isPlayingMusic = this.musicList.get(this.isPlayingPosition);
                    msg = new OnMusicStartMsg();
                    msg.position = 0;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = MusicService.this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }else{
                    this.isPlayingPosition ++;
                    this.isPlayingMusic = this.musicList.get(this.isPlayingPosition);
                    msg = new OnMusicStartMsg();
                    msg.position = MusicService.this.isPlayingPosition;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = MusicService.this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }
                this.playInLoopMode();
                break;
            case PlayModel.RANDOM:
                this.player.reset();
                Random random = new Random();
                int newPosition = random.nextInt(this.musicList.size());
                while(this.isPlayingPosition == newPosition){
                    newPosition = random.nextInt(this.musicList.size());
                }
                this.isPlayingPosition = newPosition;
                this.isPlayingMusic = this.musicList.get(this.isPlayingPosition);
                msg = new OnMusicStartMsg();
                msg.position = newPosition;
                EventBus.getDefault().post(msg);
                next = new OnNextMusicMsg();
                next.nextMusic = this.isPlayingMusic;
                EventBus.getDefault().post(next);
                this.playInRandomMode();
                break;
            case PlayModel.SINGLE:
                this.player.reset();
                if(this.isPlayingPosition + 1 == this.musicList.size()){
                    this.isPlayingPosition = 0;
                    this.isPlayingMusic = this.musicList.get(this.isPlayingPosition);
                    msg = new OnMusicStartMsg();
                    msg.position = 0;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = MusicService.this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }else{
                    this.isPlayingPosition ++;
                    this.isPlayingMusic = this.musicList.get(this.isPlayingPosition);
                    msg = new OnMusicStartMsg();
                    msg.position = MusicService.this.isPlayingPosition;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = MusicService.this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }
                this.playInSingleMode();
                break;
            case PlayModel.UNKNOUN:
                this.playModel = PlayModel.LOOP;
                this.next();
                break;
            default:
                this.playModel = PlayModel.LOOP;
                this.next();
                break;
        }
    }

    private void last(){
        OnMusicStartMsg msg;
        OnNextMusicMsg next;
        switch (this.playModel){
            case PlayModel.LOOP:
                this.player.reset();
                if(this.isPlayingPosition == 0){
                    this.isPlayingPosition = this.musicList.size() - 1;
                    this.isPlayingMusic = this.musicList.get(this.isPlayingPosition);
                    msg = new OnMusicStartMsg();
                    msg.position = this.isPlayingPosition;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }else{
                    this.isPlayingPosition --;
                    this.isPlayingMusic = this.musicList.get(this.isPlayingPosition);
                    msg = new OnMusicStartMsg();
                    msg.position = this.isPlayingPosition;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }
                this.playInLoopMode();
                break;
            case PlayModel.RANDOM:
                //随机播放模式下上一首也是随机出来的，并没有一个顺序直说
                this.next();
                break;
            case PlayModel.SINGLE:
                this.player.reset();
                if(this.isPlayingPosition == 0){
                    this.isPlayingPosition = this.musicList.size() - 1;
                    this.isPlayingMusic = this.musicList.get(this.isPlayingPosition);
                    msg = new OnMusicStartMsg();
                    msg.position = this.isPlayingPosition;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }else{
                    this.isPlayingPosition --;
                    this.isPlayingMusic = this.musicList.get(this.isPlayingPosition);
                    msg = new OnMusicStartMsg();
                    msg.position = this.isPlayingPosition;
                    EventBus.getDefault().post(msg);
                    next = new OnNextMusicMsg();
                    next.nextMusic = this.isPlayingMusic;
                    EventBus.getDefault().post(next);
                }
                this.playInSingleMode();
                break;
            case PlayModel.UNKNOUN:
                this.playModel = PlayModel.LOOP;
                this.next();
                break;
            default:
                this.playModel = PlayModel.LOOP;
                this.next();
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
         this.isPlayingPosition = clickPosition;
         this.isPlayingMusic = this.musicList.get(clickPosition);
         this.startPlayingByMode();
     }

    /**
     * 切换播放模式时触发
     */
     @Subscribe(threadMode = ThreadMode.POSTING)
     public void setPlayModel(OnMusicModelChangeMsg msg){
         this.playModel = msg.model;
         if(this.player.isPlaying()){
             this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                 @Override
                 public void onCompletion(MediaPlayer mediaPlayer) {
                     if(MusicService.this.playModel != PlayModel.SINGLE){
                         MusicService.this.next();
                     }else{
                         mediaPlayer.reset();
                         MusicService.this.playInSingleMode();
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
                 Log.d("ZLogcat","未开始播放任何一首歌");
                 try {
                     final Timer timer = new Timer();
                     this.player.setDataSource(this,this.isPlayingMusic.getUri());
                     this.player.prepareAsync();
                     this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                         @Override
                         public void onPrepared(MediaPlayer mediaPlayer) {
                             MusicService.this.isPrepare = true;
                             //mediaPlayer.start();
                             mediaPlayer.seekTo(msg.progress);
                             mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                                 @Override
                                 public void onSeekComplete(final MediaPlayer mediaPlayer) {
                                     if(mediaPlayer.isPlaying()){
                                         mediaPlayer.pause();
                                         Log.d("ZLogcat","pause");
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
                             MusicService.this.startPlayingByMode();
                         }
                     });
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }else{
                 Log.d("ZLogcat","isPrepare");
                 if(this.player.isPlaying()){
                     Log.d("ZLogcat","isPlaying");
                 }
                 this.player.seekTo(msg.progress);
                 this.player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                     @Override
                     public void onSeekComplete(MediaPlayer mediaPlayer) {
                         if(mediaPlayer.isPlaying()){
                             Log.d("ZLogcat","isPlaying");
                         }
                     }
                 });
             }
         }
     }

}
