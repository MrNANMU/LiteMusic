package com.dasong.zmusic.model.listener;

import com.dasong.zmusic.model.bean.Music;

import java.util.List;

/**
 * Created by dason on 2017/9/5 0005.
 */

public interface MusicSelectListener {

    public void onSuccess(Music music);
    public void onSuccess(List<Music> musicList);
    public void onError(Exception e);

}
