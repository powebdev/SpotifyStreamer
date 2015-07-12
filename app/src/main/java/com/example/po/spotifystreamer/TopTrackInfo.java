package com.example.po.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class stores the information from the artist top tracks search results
 * The custom ArrayAdapter uses this information to populate the top tracks listview
 * Created by Po on 7/5/2015.
 */
public class TopTrackInfo implements Parcelable {

    String name;        //track name
    String artistName;  //name of the artist of the track
    String album;       //name of the album the track belongs to
    String imageLink;   //url for the album art for which the album the track belongs to

    public TopTrackInfo(String name, String artistName, String album, String imageLink){
        this.name = name;
        this.artistName = artistName;
        this.album = album;
        this.imageLink = imageLink;
    }

    public TopTrackInfo(Parcel source){
        this.name = source.readString();
        this.artistName = source.readString();
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
        dest.writeString(artistName);
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
