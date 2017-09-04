package com.dasong.zmusic.utils;

import android.graphics.Bitmap;

import com.dasong.zmusic.model.adapter.BaseRecAdapter;
import com.dasong.zmusic.model.bean.Music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dason on 2017/8/21 0021.
 */

public class MusicOrder {

    private static Map<String,List<Music>> artistMap;
    private static Map<String,List<Music>> albumMap;
    private static List<String> artistList;
    private static List<String> albumList;
    private static Map<String,Bitmap> coverList;

    public static Map<String,List<Music>> byArtist(List<Music> list){
        if(artistMap == null){
            artistMap = new ConcurrentHashMap<String,List<Music>>();
            for(Music music:list){
                String name = music.getArtist();
                if(name == null){
                    name = "未知歌手";
                }
                if(artistMap.get(name) == null){
                    List<Music> musicList = new ArrayList<Music>();
                    musicList.add(music);
                    artistMap.put(name,musicList);
                }else{
                    artistMap.get(name).add(music);
                }
            }
            return artistMap;
        }else{
            return artistMap;
        }

    }

    public static Map<String,List<Music>> byAlbum(List<Music> list){
        if(albumMap == null){
            albumMap = new ConcurrentHashMap<String,List<Music>>();
            for(Music music:list){
                String album = music.getAlbum();
                if(album == null){
                    album = "未知专辑";
                }
                if(albumMap.get(album) == null){
                    List<Music> albumList = new ArrayList<Music>();
                    albumList.add(music);
                    albumMap.put(album,albumList);
                }else{
                    albumMap.get(album).add(music);
                }
            }
            return albumMap;
        }else{
            return albumMap;
        }

    }

    public static List<String> getArtist(List<Music> list){
        if(artistList == null){
            artistList = new ArrayList<String>();
            if(artistMap == null){
                artistMap = MusicOrder.byArtist(list);
            }
            for(String key:artistMap.keySet()){
                artistList.add(key);
            }
            return artistList;
        }else{
            return artistList;
        }
    }

    public static List<String> getAlbumList(List<Music> list){
        if(albumList == null){
            albumList = new ArrayList<String>();
            if(albumMap == null){
                albumMap = MusicOrder.byAlbum(list);
            }
            for(String key:albumMap.keySet()){
                albumList.add(key);
            }
            return albumList;
        }else{
            return albumList;
        }
    }

    /*public static Map<String,Bitmap> getCoverList(List<Music> list){
        if(coverList == null){
            coverList = new HashMap<>();
            albumList = getAlbumList(list);
            albumMap = byAlbum(list);
            Music music;
            for(String album:albumList){
                Bitmap cover = albumMap.get(album).get(0).getCover(PixUtils.dp2px(128));
                coverList.put(album,cover);
            }
            return coverList;
        }else{
            return coverList;
        }
    }

    public static Map<String,Bitmap> getCoverList(Map<String,List<Music>> albumMap){
        if(coverList == null){
            coverList = new HashMap<String,Bitmap>();
        }
        for(String album:albumMap.keySet()){
            Music music = albumMap.get(album).get(0);
            coverList.put(album,music.getCover());
        }
        return coverList;
    }*/
}
