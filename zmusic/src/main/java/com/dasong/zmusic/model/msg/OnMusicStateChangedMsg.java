package com.dasong.zmusic.model.msg;

/**
 * Created by dason on 2017/8/28 0028.
 */

public class OnMusicStateChangedMsg {

    public boolean isPlaying;
    public OnMusicStateChangedMsg(boolean isPlaying){
        this.isPlaying = isPlaying;
    }
}
