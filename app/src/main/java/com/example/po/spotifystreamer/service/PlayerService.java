package com.example.po.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener{

    public interface PlayerCallbackListener{
        void playerPrepared(int trackDuration);
        void playbackCompleted();
    }

    public void setPlayerCallbackListener(PlayerCallbackListener listener){
        mPlayerCallbackListener = listener;
    }

    private static final String LOG_TAG = PlayerService.class.getSimpleName();
    private String mTrackUrl;
    private MediaPlayer mPlayer;
    private PlayerCallbackListener mPlayerCallbackListener = null;
    /**
     * Created by Po on 8/18/2015.
     */


    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "in onCreate");
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTrackUrl = intent.getStringExtra("EXTRA_TRACK_URL");
        mPlayer.reset();
        try{
            mPlayer.setDataSource(mTrackUrl);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mPlayer.prepareAsync();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mPlayer.stop();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();

        super.onTaskRemoved(rootIntent);
    }

    private final IBinder mBinder = new PlayerBinder();

    public class PlayerBinder extends Binder {

        public PlayerService getService() {
            return PlayerService.this;
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mPlayerCallbackListener.playerPrepared(mPlayer.getDuration());
        mPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayerCallbackListener.playbackCompleted();
//        stopSelf();
    }

    public void resumeTrack(){
        mPlayer.start();
    }

    public void pauseTrack(){
        mPlayer.pause();
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    public int getCurrentTrackPosition(){
        return mPlayer.getCurrentPosition();
    }

    public void seekTo(int position){
        mPlayer.seekTo(position);
    }

}
