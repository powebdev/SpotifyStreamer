package com.example.po.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Po on 7/5/2015.
 */
public class TopTrackInfo implements Parcelable {

    String name;
    String album;
    String imageLink;

    public TopTrackInfo(String name, String album, String imageLink){
        this.name = name;
        this.album = album;
        this.imageLink = imageLink;
    }

    public TopTrackInfo(Parcel source){
        this.name = source.readString();
        this.album = source.readString();
        this.imageLink = source.readString();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(name);
        dest.writeString(album);
        dest.writeString(imageLink);
    }

    public class MyCreator implements Parcelable.Creator<TopTrackInfo>{
        public TopTrackInfo createFromParcel(Parcel source){
            return new TopTrackInfo(source);
        }

        public TopTrackInfo[] newArray(int size){
            return new TopTrackInfo[size];
        }

    }
}
