package com.dasong.zmusic.utils.onlythis;

import android.media.MediaPlayer;
import android.print.PageRange;

/**
 * Created by dason on 2017/8/6 0006.
 */

public class MediaPlayerInstall {

    private MediaPlayerInstall(){

    }

    public static MediaPlayer create(){
        return Single.INSTANCE.getInstall();
    }

    private static enum Single{

        INSTANCE;
        private MediaPlayer install;

        private Single(){
            install = new MediaPlayer();
        }

        public MediaPlayer getInstall(){
            return this.install;
        }
    }
}
