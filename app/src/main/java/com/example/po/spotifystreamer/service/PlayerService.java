package com.example.po.spotifystreamer.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.po.spotifystreamer.data.MusicContract;

import java.io.IOException;

/**
 * Created by Po on 8/18/2015.
 */
public class PlayerService extends IntentService {
    private static final String LOG_TAG = PlayerService.class.getSimpleName();
    private final IBinder mBinder = new PlayerBinder();
    private static MediaPlayer sMediaPlayer;
    private Cursor mMediaCursor;
    private int mTrackNumber, mCurrentPosition, mTrackDuration;
    private String mArtistName, mAlbumName, mTrackName;
    private boolean mPlayerState = false;

    public class PlayerBinder extends Binder {
        public PlayerService getService(){
            return PlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        Log.d(LOG_TAG, "in onBind");
        return mBinder;
    }

    public PlayerService(){
        super("PlayerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null){
            sMediaPlayer = new MediaPlayer();
            String sortOrder = MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY + " DESC";
            mMediaCursor = getContentResolver().query(MusicContract.TopTrackEntry.CONTENT_URI, null, null, null, sortOrder);
            Bundle extrasInfo = intent.getExtras();
            mTrackNumber = extrasInfo.getInt("EXTRA_TRACK_POSITION");
            mMediaCursor.moveToPosition(mTrackNumber);

            getTrack();

        }
    }

    public Bundle getTrackInfo(){
        Bundle infoForPlayer = new Bundle();
        infoForPlayer.putString("TRACK_INFO_ARTIST",mArtistName);
        infoForPlayer.putString("TRACK_INFO_ALBUM",mAlbumName);
        infoForPlayer.putString("TRACK_INFO_NAME",mTrackName);
        infoForPlayer.putInt("TRACK_INFO_DURATION",mTrackDuration);

        return infoForPlayer;
    }

    public int getCurrentPosition(){
        return sMediaPlayer.getCurrentPosition();
    }

    public void playTrack(){
        sMediaPlayer.start();

    }

    public void pauseTrack(){
        sMediaPlayer.pause();

    }

    public boolean nextTrack(){
        if(!mMediaCursor.isLast()){
            mMediaCursor.moveToNext();
            getTrack();
            sMediaPlayer.start();
            return true;
        }
        sMediaPlayer.seekTo(0);
        return false;
    }

    public boolean previousTrack(){
        if(!mMediaCursor.isFirst()){
            mMediaCursor.moveToPrevious();
            getTrack();
            sMediaPlayer.start();
            return true;
        }
        sMediaPlayer.seekTo(0);
        return false;
    }

    public void seekToPosition(int position){
        sMediaPlayer.seekTo(position);
    }

    public boolean getPlayerState(){
        mPlayerState = sMediaPlayer.isPlaying();
        return mPlayerState;
    }

    private void getTrack(){
        int inx_artist_name_col = mMediaCursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ARTIST_KEY);
        int inx_album_name_col = mMediaCursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_KEY);
        int inx_track_name_col = mMediaCursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_NAME);

        mArtistName = mMediaCursor.getString(inx_artist_name_col);
        mAlbumName = mMediaCursor.getString(inx_album_name_col);
        mTrackName = mMediaCursor.getString(inx_track_name_col);

        int inx_track_url_col = mMediaCursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_PREVIEW_URL);
        try{
            sMediaPlayer.reset();
            sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            sMediaPlayer.setDataSource(mMediaCursor.getString(inx_track_url_col));
            sMediaPlayer.prepare();
            mTrackDuration = sMediaPlayer.getDuration();
            mCurrentPosition = sMediaPlayer.getCurrentPosition();

        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
