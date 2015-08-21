package com.example.po.spotifystreamer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MusicProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MusicDBHelper mMusicDbOpenHelper;

    static final int ARTISTS = 100;
    static final int SINGLE_ARTIST = 101;
    static final int TOP_TRACKS = 200;
    static final int SINGLE_TRACK = 201;

    @Override
    public boolean onCreate(){

        mMusicDbOpenHelper = new MusicDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri){

        final int match = sUriMatcher.match(uri);

        switch (match){
            case ARTISTS:
                return MusicContract.ArtistEntry.CONTENT_TYPE;
            case SINGLE_ARTIST:
                return MusicContract.ArtistEntry.CONTENT_ITEM_TYPE;
            case TOP_TRACKS:
                return MusicContract.TopTrackEntry.CONTENT_TYPE;
            case SINGLE_TRACK:
                return MusicContract.TopTrackEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){

        Cursor returnCursor;
        switch(sUriMatcher.match(uri)){
            case ARTISTS:{
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
            case SINGLE_ARTIST: {
                String idSelection = MusicContract.ArtistEntry.TABLE_NAME + "." + MusicContract.ArtistEntry._ID + " = ? ";
                String id = uri.getLastPathSegment();

                returnCursor = mMusicDbOpenHelper.getReadableDatabase().query(
                        MusicContract.ArtistEntry.TABLE_NAME,
                        projection,
                        idSelection,
                        new String[]{id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TOP_TRACKS:{
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
            case SINGLE_TRACK:{
                String idSelection = MusicContract.TopTrackEntry.TABLE_NAME + "." + MusicContract.TopTrackEntry._ID + " = ? ";
                String id = uri.getLastPathSegment();

                returnCursor = mMusicDbOpenHelper.getReadableDatabase().query(
                        MusicContract.TopTrackEntry.TABLE_NAME,
                        projection,
                        idSelection,
                        new String[]{id},
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
            case ARTISTS:{
                long _id = db.insert(MusicContract.ArtistEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MusicContract.ArtistEntry.buildArtistUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TOP_TRACKS:{
                long _id = db.insert(MusicContract.TopTrackEntry.TABLE_NAME, null, values);
                if(_id >0)
                    returnUri = MusicContract.TopTrackEntry.buildTopTrackUri(_id);
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
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mMusicDbOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case ARTISTS:
                db.beginTransaction();
                returnCount = 0;
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
            case TOP_TRACKS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MusicContract.TopTrackEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
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
            case ARTISTS:
                rowsDeleted = db.delete(MusicContract.ArtistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOP_TRACKS:
                rowsDeleted = db.delete(MusicContract.TopTrackEntry.TABLE_NAME, selection, selectionArgs);
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
            case ARTISTS:
                rowsUpdated = db.update(MusicContract.ArtistEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TOP_TRACKS:
                rowsUpdated = db.update(MusicContract.TopTrackEntry.TABLE_NAME, values, selection, selectionArgs);
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

        musicUriMatcher.addURI(authority, MusicContract.PATH_ARTIST, ARTISTS);
        musicUriMatcher.addURI(authority, MusicContract.PATH_ARTIST + "/#", SINGLE_ARTIST);
        musicUriMatcher.addURI(authority, MusicContract.PATH_TOP_TRACK, TOP_TRACKS);
        musicUriMatcher.addURI(authority, MusicContract.PATH_TOP_TRACK + "/#", SINGLE_TRACK);

        return musicUriMatcher;
    }
}
