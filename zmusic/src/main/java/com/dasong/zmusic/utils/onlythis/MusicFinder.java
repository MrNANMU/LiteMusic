package com.dasong.zmusic.utils.onlythis;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.dasong.zmusic.model.bean.Music;
import com.dasong.zmusic.model.listener.MusicSelectListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dason on 2017/8/21 0021.
 */

public class MusicFinder {

    private static long id;   //音乐id

    private static String title;//音乐标题

    private static String artist;//艺术家

    private static long duration;//时长

    private static long size;  //文件大小

    private static String url;  //文件路径

    private static String album; //唱片图片

    private static long album_id; //唱片图片ID

    private static int isMusic;//是否为音乐

    public static List<Music> data;

    public static final List<Music> localMusicList  = new ArrayList<Music>();;

    public static final List<Music> netMusicList = null;

    public static final List<Music> allMusicInThePhone = new ArrayList<Music>();

    private static ContentResolver resolver;

    public static List<Music> selectAll(Context context){
        if(allMusicInThePhone.size() == 0){
            resolver = context.getContentResolver();
            Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            int position = 0;
            for(int i=0; i < cursor.getCount(); i++){

                cursor.moveToNext();
                select(cursor);

                Music music;
                if(duration >60000 && isMusic == 1){
                    music = new Music();
                    music.setId(id);
                    music.setAlbum(album);
                    music.setArtist(artist);
                    music.setName(title);
                    music.setAlbum_id(album_id);
                    music.setUri(Uri.parse(url));
                    music.setTime(duration);
                    music.setPosition(i);
                    MediaMetadataRetriever m = new MediaMetadataRetriever();
                    m.setDataSource(context,music.getUri());
                    byte[] bytes = m.getEmbeddedPicture();
                    music.setCover(bytes);
                    music.setLocal(true);
                    allMusicInThePhone.add(music);
                    position++;
                }

            }
            cursor.close();
        }
        return allMusicInThePhone;
    }

    public static List<Music> selectAll(Context context,MusicSelectListener listener){
        if(allMusicInThePhone.size() == 0){
            resolver = context.getContentResolver();
            Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            int position = 0;
            for(int i=0; i < cursor.getCount(); i++){

                cursor.moveToNext();
                select(cursor);
                Music music;
                if(duration >60000 && isMusic == 1){
                    music = new Music();
                    music.setId(id);
                    music.setAlbum(album);
                    music.setArtist(artist);
                    music.setName(title);
                    music.setAlbum_id(album_id);
                    music.setUri(Uri.parse(url));
                    music.setTime(duration);
                    music.setPosition(i);
                    MediaMetadataRetriever m = new MediaMetadataRetriever();
                    m.setDataSource(context,music.getUri());
                    byte[] bytes = m.getEmbeddedPicture();
                    music.setCover(bytes);
                    music.setLocal(true);
                    allMusicInThePhone.add(music);
                    position++;
                }

            }
            cursor.close();
        }
        return allMusicInThePhone;
    }

    public static List<Music> selectMusics(Context context,String muiscsName){
        List<Music> list = new ArrayList<Music>();
        return list;
    }

    public static List<Music> selectMusicInPhone(Context context,String name){
        localMusicList.removeAll(localMusicList);
        ArrayList<Music> list = (ArrayList) selectAll(context);
        for(Music music:list){
            if(music.getName().equals(name)){
                localMusicList.add(music);
            }
        }
        return localMusicList;
    }

    public static List<Music> selectMusicByNet(Context context,String name){
        List<Music> list = new ArrayList<Music>();

        return list;
    }

    public static Music selectMusicById(Context context,long mId){

        resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Music music = null;
        int position = 0;
        Log.d("ZLogcat","游标:"+cursor.getCount());
        for(int i=0; i < cursor.getCount(); i++){

            cursor.moveToNext();
            select(cursor);
            if(id == mId && isMusic == 1){
                music = new Music();
                music.setId(id);
                music.setAlbum(album);
                music.setArtist(artist);
                music.setName(title);
                music.setAlbum_id(album_id);
                music.setUri(Uri.parse(url));
                music.setTime(duration);
                music.setPosition(position);
                MediaMetadataRetriever m = new MediaMetadataRetriever();
                m.setDataSource(context,music.getUri());
                byte[] bytes = m.getEmbeddedPicture();
                music.setCover(bytes);
                music.setLocal(true);
                break;
            }
            position ++;
        }
        cursor.close();
        return music;
    }

    public static List<Music> selectMusicByName(Context context, String name){
        List<Music> list = new ArrayList<Music>();
        list.addAll(selectMusicInPhone(context,name));
        list.addAll(selectMusicByNet(context,name));
        return list;
    }

    public static List<Music> selectMusicByName(Context context, String name, MusicSelectListener listener){
        List<Music> list = new ArrayList<Music>();
        list.addAll(selectMusicInPhone(context,name));
        list.addAll(selectMusicByNet(context,name));
        return list;
    }

    public static ArrayList<Music> getAll(Context context){
        if(data == null){
            Log.d("ZLogcat","MusicFinder.getAll->data==null");
            data = MusicFinder.selectAll(context);
            return (ArrayList<Music>)data;
        }else{
            Log.d("ZLogcat","MusicFinder.getAll->data!=null");
            return (ArrayList<Music>)data;
        }
    }

    public static void select(Cursor cursor){

        id = cursor.getLong(cursor
                .getColumnIndex(MediaStore.Audio.Media._ID));   //音乐id

        title = cursor.getString((cursor
                .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题

        artist = cursor.getString(cursor
                .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家

        duration = cursor.getLong(cursor
                .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长

        size = cursor.getLong(cursor
                .getColumnIndex(MediaStore.Audio.Media.SIZE));  //文件大小

        url = cursor.getString(cursor
                .getColumnIndex(MediaStore.Audio.Media.DATA));  //文件路径

        album = cursor.getString(cursor
                .getColumnIndex(MediaStore.Audio.Media.ALBUM)); //唱片图片

        album_id = cursor.getLong(cursor
                .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)); //唱片图片ID

        isMusic = cursor.getInt(cursor
                .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
    }

}
