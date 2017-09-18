package com.dasong.zmusic.model.msg;

import com.dasong.zmusic.model.bean.Music;

/**
 * Created by dason on 2017/8/27 0027.
 * 该类为EventBus事件类，负责响应歌曲列表点击时Service切换播放的歌曲
 */

public class OnItemClickMsg {

    public final boolean isPlaying = true;

    public int position;
    public Music music;

    public OnItemClickMsg setPosition(int position){
        this.position = position;
        return this;
    }

    public OnItemClickMsg setMusic(Music mnusic){
        this.music = music;
        return this;
    }

    public int getPosition(){
        return this.position;
    }


}
