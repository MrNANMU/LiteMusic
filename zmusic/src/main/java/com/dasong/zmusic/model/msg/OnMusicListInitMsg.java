package com.dasong.zmusic.model.msg;

import com.dasong.zmusic.model.bean.Music;

import java.util.List;

/**
 * Created by dason on 2017/8/30 0030.
 */

public class OnMusicListInitMsg {

    public List<Music> musicList;
    public OnMusicListInitMsg(List<Music> musicList){
        this.musicList = musicList;
    }
}
