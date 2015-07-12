package com.example.po.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class stores the information from the artist name search results
 * The custom ArrayAdapter uses this information to populate the artist listview
 * Created by Po on 7/3/2015.
 */
public class ArtistInfo implements Parcelable{

    String name;        //artist name
    String spotifyId;   //spotify ID for the artist
    String imageLink;   //url for the artist image

    public ArtistInfo(String artistName, String artistSpotifyId, String artistImageLink){
        this.name = artistName;
        this.spotifyId = artistSpotifyId;
        this.imageLink = artistImageLink;
    }

    public ArtistInfo(Parcel source){
        this.name = source.readString();
        this.spotifyId = source.readString();
        this.imageLink = source.readString();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(name);
        dest.writeString(spotifyId);
        dest.writeString(imageLink);
    }

    public class MyCreator implements Parcelable.Creator<ArtistInfo>{
        public ArtistInfo createFromParcel(Parcel source){
            return new ArtistInfo(source);
        }

        public ArtistInfo[] newArray(int size){
            return new ArtistInfo[size];
        }

    }
}
