package com.dasong.zmusic.model.bean;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.dasong.zmusic.utils.BitMapUtil;

import java.io.Serializable;

/**
 * Created by dason on 2017/8/21 0021.
 */

public class Music{

    private static final long serialVersionUID = 20170821L;

    private long id;
    private String name;
    private String artist;
    private String lyric;
    private long time;
    private Uri uri;
    private String album;
    private byte[] cover;
    private long album_id;
    private int position;
    private boolean isLocal;

    public Music(){}

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getArtist() {
        return artist;
    }

    public String getLyric() {
        return lyric;
    }

    public long getTime() {
        return time;
    }

    public Uri getUri() {
        return uri;
    }

    public String getAlbum(){
        return album;
    }

    public long getAlbum_id(){
        return album_id;
    }

    public Bitmap getCover(int width,int height) {
        return BitMapUtil.compress(cover,width,height);
    }

    public int getPosition(){
        return position;
    }

    public boolean isLocal(){
        return isLocal;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setAlbum(String album){
        this.album = album;
    }

    public void setAlbum_id(long album_id){
        this.album_id = album_id;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public void setLocal(boolean isLocal){
        this.isLocal = isLocal;
    }

    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeLong(this.id);
        out.writeString(this.name);
        out.writeString(this.artist);
        out.writeString(this.lyric);
        out.writeLong(this.time);
        out.writeParcelable(this.uri,0);
        out.writeString(this.album);
        out.writeByteArray(this.cover);
        out.writeLong(this.album_id);
        out.writeInt(this.position);
        if(isLocal){
            out.writeInt();
        }

    }

    private Music(Parcel in){
        in.readLong();
        in.readString();
        in.readString();
        in.readString();
        in.readLong();
        in.readParcelable(Thread.currentThread().getContextClassLoader());
        in.readString();
        in.readByteArray(this.cover);
        in.readLong();
        in.readInt();
    }

    public static final Parcelable.Creator<Music> CREATOR = new Parcelable.Creator<Music>(){

        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int i) {
            return new Music[i];
        }
    };*/
}
