package com.dasong.zmusic.model.msg;

/**
 * Created by dason on 2017/9/6 0006.
 * 歌单、专辑列表、歌手列表的点击msg
 */

public class OnShowPageMsg {

    public static final int SELECT_FRAGMENT = 0;
    public static final int ALBUM_FRAGMENT = 1;
    public static final int ARTIST_FRAGMENT = 2;
    public static final int MUSICLIST_FRAGMENT = 3;

    public int whichPage;
    public String whichItem;

    public OnShowPageMsg(){}

    public OnShowPageMsg(int whichPage,String whichItem){
        this.whichPage = whichPage;
        this.whichItem = whichItem;
    }

    public OnShowPageMsg setWhichPage(int whichPage){
        this.whichPage = whichPage;
        return this;
    }

    public OnShowPageMsg setWhichItem(String whichItem){
        this.whichItem = whichItem;
        return this;
    }

}
