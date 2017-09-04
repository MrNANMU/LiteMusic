package com.dasong.zmusic.model.control;

import android.content.Context;
import android.media.MediaPlayer;

import com.dasong.zmusic.model.bean.Music;

import java.util.List;

/**
 * Created by dason on 2017/8/26 0026.
 */

public class MusicManager {

    private Context context;
    private MediaPlayer player;
    private List<Music> list;


    public MusicManager(Context context, MediaPlayer player, List<Music> list){
        this.context = context;
        this.player = player;
        this.list = list;
    }

    public void next(){}

    public void last(){

    }

    public void pause(){
        this.player.pause();
    }


}
