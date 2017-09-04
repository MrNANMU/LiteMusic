package com.dasong.zmusic.model.msg;

/**
 * Created by dason on 2017/8/27 0027.
 */

public class OnItemClickMsg {

    public int position;
    public final boolean isPlaying = true;
    public void setPosition(int position){
        this.position = position;
    }
    public int getPosition(){
        return this.position;
    }


}
