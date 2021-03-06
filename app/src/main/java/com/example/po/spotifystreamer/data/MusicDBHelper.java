package com.example.po.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Po on 8/2/2015.
 * Manages a local database for spotifyAPI search results.
 */
public class MusicDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "SpotifyStreamer.db";

    public MusicDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase musicDatabase) {
        musicDatabase.execSQL("DROP TABLE IF EXISTS " + MusicContract.ArtistEntry.TABLE_NAME);
        musicDatabase.execSQL("DROP TABLE IF EXISTS " + MusicContract.TopTrackEntry.TABLE_NAME);

        final String SQL_CREATE_ARTIST_TABLE =
                "CREATE TABLE " + MusicContract.ArtistEntry.TABLE_NAME + " (" +
                        MusicContract.ArtistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MusicContract.ArtistEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL, " +
                        MusicContract.ArtistEntry.COLUMN_ARTIST_SPOTIFY_ID + " TEXT NOT NULL, " +
                        MusicContract.ArtistEntry.COLUMN_ARTIST_IMAGE + " TEXT NOT NULL, " +
                        MusicContract.ArtistEntry.COLUMN_ARTIST_POPULARITY + " INTEGER NOT NULL, " +
                        " UNIQUE (" + MusicContract.ArtistEntry.COLUMN_ARTIST_SPOTIFY_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TOP_TRACK_TABLE =
                "CREATE TABLE " + MusicContract.TopTrackEntry.TABLE_NAME + " (" +
                        MusicContract.TopTrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MusicContract.TopTrackEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL, " +
                        MusicContract.TopTrackEntry.COLUMN_TRACK_SPOTIFY_ID + " TEXT NOT NULL, " +
                        MusicContract.TopTrackEntry.COLUMN_TRACK_PREVIEW_URL + " TEXT NOT NULL, " +
                        MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY + " INTEGER NOT NULL, " +
                        MusicContract.TopTrackEntry.COLUMN_TRACK_EXTERNAL_URL + " TEXT, " +
                        MusicContract.TopTrackEntry.COLUMN_TRACK_DURATION + " INTEGER NOT NULL, " +
                        MusicContract.TopTrackEntry.COLUMN_ARTIST_KEY + " TEXT NOT NULL, " +
                        MusicContract.TopTrackEntry.COLUMN_ALBUM_KEY + " TEXT NOT NULL, " +
                        MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_LARGE + " TEXT NOT NULL, " +
                        MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_SMALL + " TEXT NOT NULL, " +
                        " UNIQUE (" + MusicContract.TopTrackEntry.COLUMN_TRACK_SPOTIFY_ID + ") ON CONFLICT REPLACE);";

        musicDatabase.execSQL(SQL_CREATE_ARTIST_TABLE);
        musicDatabase.execSQL(SQL_CREATE_TOP_TRACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase musicDatabase, int oldVersion, int newVersion) {
        musicDatabase.execSQL("DROP TABLE IF EXISTS " + MusicContract.ArtistEntry.TABLE_NAME);
        musicDatabase.execSQL("DROP TABLE IF EXISTS " + MusicContract.TopTrackEntry.TABLE_NAME);
        onCreate(musicDatabase);
    }

}
