package com.example.po.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Po on 8/2/2015.
 */
public class MusicDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SpotifyStreamer.db";

    public MusicDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase musicDatabase){

        final String SQL_CREATE_ARTIST_TABLE =
                "CREATE TABLE " + MusicContract.ArtistEntry.TABLE_NAME + " (" +
                        MusicContract.ArtistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MusicContract.ArtistEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL, " +
                        MusicContract.ArtistEntry.COLUMN_ARTIST_SPOTIFY_ID + " TEXT NOT NULL, " +
                        MusicContract.ArtistEntry.COLUMN_ARTIST_IMAGE + " TEXT NOT NULL, " +
                        " UNIQUE (" + MusicContract.ArtistEntry.COLUMN_ARTIST_SPOTIFY_ID + ") ON CONFLICT REPLACE);";

//        final String SQL_CREATE_ALBUM_TABLE =
//                "CREATE TABLE " + MusicContract.AlbumEntry.TABLE_NAME + " (" +
//                        MusicContract.AlbumEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                        MusicContract.AlbumEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
//                        MusicContract.AlbumEntry.COLUMN_ALBUM_SPOTIFY_ID + " TEXT NOT NULL, " +
//                        MusicContract.AlbumEntry.COLUMN_ALBUM_IMAGE + " TEXT NOT NULL, " +
//                        MusicContract.AlbumEntry.COLUMN_ALBUM_THUMBNAIL + " TEXT NOT NULL), " +
//                        " UNIQUE (" + MusicContract.AlbumEntry.COLUMN_ALBUM_SPOTIFY_ID + ") ON CONFLICT REPLACE);";
//
//        final String SQL_CREATE_TRACK_TABLE =
//                "CREATE TABLE " + MusicContract.TrackEntry.TABLE_NAME + " (" +
//                        MusicContract.TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                        MusicContract.TrackEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL, " +
//                        MusicContract.TrackEntry.COLUMN_TRACK_SPOTIFY_ID + " TEXT NOT NULL, " +
//                        MusicContract.TrackEntry.COLUMN_TRACK_PREVIEW_URL + " TEXT NOT NULL, " +
//                        " FOREIGN KEY (" + MusicContract.TrackEntry.COLUMN_ALBUM_KEY + ") REFERENCES " +
//                        MusicContract.AlbumEntry.TABLE_NAME + " (" + MusicContract.AlbumEntry._ID + "), " +
//                        " UNIQUE (" + MusicContract.TrackEntry.COLUMN_TRACK_SPOTIFY_ID + ") ON CONFLICT REPLACE);";

        musicDatabase.execSQL(SQL_CREATE_ARTIST_TABLE);
//        musicDatabase.execSQL(SQL_CREATE_ALBUM_TABLE);
//        musicDatabase.execSQL(SQL_CREATE_TRACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase musicDatabase, int oldVersion, int newVersion) {

        musicDatabase.execSQL("DROP TABLE IF EXISTS " + MusicContract.ArtistEntry.TABLE_NAME);
//        musicDatabase.execSQL("DROP TABLE IF EXISTS " + MusicContract.AlbumEntry.TABLE_NAME);
//        musicDatabase.execSQL("DROP TABLE IF EXISTS " + MusicContract.TrackEntry.TABLE_NAME);
        onCreate(musicDatabase);
    }

}
