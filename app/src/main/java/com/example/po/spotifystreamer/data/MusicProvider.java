package com.example.po.spotifystreamer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Po on 8/2/2015.
 */
public class MusicProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MusicDbHelper mMusicDbOpenHelper;

    static final int SEARCH_RESULT_ARTISTS = 100;
    static final int SEARCH_ONE_ARTIST = 101;
    static final int SEARCH_RESULT_TOP_TRACKS = 200;
    static final int SEARCH_ONE_TRACK = 201;

    @Override
    public boolean onCreate(){

        mMusicDbOpenHelper = new MusicDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri){

        final int match = sUriMatcher.match(uri);

        switch (match){
            case SEARCH_RESULT_ARTISTS:
                return MusicContract.ArtistEntry.CONTENT_TYPE;
            case SEARCH_ONE_ARTIST:
                return MusicContract.ArtistEntry.CONTENT_ITEM_TYPE;
            case SEARCH_RESULT_TOP_TRACKS:
                return MusicContract.TopTrackEntry.CONTENT_TYPE;
            case SEARCH_ONE_TRACK:
                return MusicContract.TopTrackEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){

        Cursor returnCursor;
        switch(sUriMatcher.match(uri)){
            case SEARCH_RESULT_ARTISTS:{
                returnCursor = mMusicDbOpenHelper.getReadableDatabase().query(
                        MusicContract.ArtistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SEARCH_RESULT_TOP_TRACKS:{
                returnCursor = mMusicDbOpenHelper.getReadableDatabase().query(
                        MusicContract.TopTrackEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){

        final SQLiteDatabase db = mMusicDbOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match){
            case SEARCH_RESULT_ARTISTS:{
                long _id = db.insert(MusicContract.ArtistEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MusicContract.ArtistEntry.buildArtistUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMusicDbOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SEARCH_RESULT_ARTISTS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MusicContract.ArtistEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){

        final SQLiteDatabase db = mMusicDbOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if(selection == null)
            selection = "1";

        switch(match){
            case SEARCH_RESULT_ARTISTS:
                rowsDeleted = db.delete(MusicContract.ArtistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        final SQLiteDatabase db = mMusicDbOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case SEARCH_RESULT_ARTISTS:
                rowsUpdated = db.update(MusicContract.ArtistEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher(){

        final UriMatcher musicUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MusicContract.CONTENT_AUTHORITY;

        musicUriMatcher.addURI(authority, MusicContract.PATH_ARTIST, SEARCH_RESULT_ARTISTS);
        musicUriMatcher.addURI(authority, MusicContract.PATH_ARTIST + "/#", SEARCH_ONE_ARTIST);
        musicUriMatcher.addURI(authority, MusicContract.PATH_TOP_TRACK, SEARCH_RESULT_TOP_TRACKS);
        musicUriMatcher.addURI(authority, MusicContract.PATH_TOP_TRACK + "/#", SEARCH_ONE_TRACK);

        return musicUriMatcher;
    }
}
