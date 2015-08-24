package com.example.po.spotifystreamer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.po.spotifystreamer.R;
import com.example.po.spotifystreamer.data.MusicContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    public interface PlayerCallbackListener {
        void playerLoading();

        void trackInfoReady(Bundle trackInfo);

        void isPlaying();

        void isPaused();
    }

    public void setPlayerCallbackListener(PlayerCallbackListener listener) {
        mPlayerCallbackListener = listener;
    }

    private Target loadTarget;
    private static final String ACTION_PLAY = "com.example.po.spotifystreamer.service.ACTION_PLAY";
    private static final String ACTION_PAUSE = "com.example.po.spotifystreamer.service.ACTION_PAUSE";
    private static final String ACTION_NEXT = "com.example.po.spotifystreamer.service.ACTION_NEXT";
    private static final String ACTION_PREVIOUS = "com.example.po.spotifystreamer.service.ACTION_PREVIOUS";
    private String mTrackUrl;
    private MediaPlayer mPlayer;
    private Cursor mSongList;
    private String mArtistName, mAlbumName, mTrackName, mTrackExtUrl, mAlbumArtLarge, mAlbumArtSmall;
    private int mTrackDuration;
    private Bundle mTrackInfo = new Bundle();
    private PlayerCallbackListener mPlayerCallbackListener = null;
    private Notification.Builder mNotificationBuilder;
    NotificationManager mNotifyMgr;
    private int[] allControls = {0, 1, 2};

    PendingIntent mPendingPlayIntent;
    PendingIntent mPendingPauseIntent;
    PendingIntent mPendingNextTrackIntent;
    PendingIntent mPendingPreviousTrackIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        Intent playIntent = new Intent(ACTION_PLAY);
        Intent pauseIntent = new Intent(ACTION_PAUSE);
        Intent nextTrackIntent = new Intent(ACTION_NEXT);
        Intent previousTrackIntent = new Intent(ACTION_PREVIOUS);
        mPendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        mPendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        mPendingNextTrackIntent = PendingIntent.getService(this, 0, nextTrackIntent, 0);
        mPendingPreviousTrackIntent = PendingIntent.getService(this, 0, previousTrackIntent, 0);

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra("EXTRA_TRACK_NUMBER")) {
                String sortOrder = MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY + " DESC";
                mSongList = getContentResolver().query(MusicContract.TopTrackEntry.CONTENT_URI, null, null, null, sortOrder);
                int trackNumber = intent.getIntExtra("EXTRA_TRACK_NUMBER", 0);
                mSongList.moveToPosition(trackNumber);
                settingTrackInfo();
                refreshTrackInfoBundle();
                settingTrackUrl();
                settingPlayer();
            } else {
                String action = intent.getAction();
                switch (action) {
                    case ACTION_PLAY:
                        resumeTrack();
                        break;
                    case ACTION_PAUSE:
                        pauseTrack();
                        break;
                    case ACTION_NEXT:
                        nextTrack();
                        break;
                    case ACTION_PREVIOUS:
                        previousTrack();
                        break;
                }
            }

        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mPlayer.stop();
        mNotifyMgr.cancel(R.id.notification_id);
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
    public void onRebind(Intent intent) {
        refreshTrackInfoBundle();
        mPlayerCallbackListener.trackInfoReady(mTrackInfo);
        if (mPlayer.isPlaying()) {
            mPlayerCallbackListener.isPlaying();
        } else {
            mPlayerCallbackListener.isPaused();
        }
        super.onRebind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        showNotification(true);
        mPlayerCallbackListener.isPlaying();
        mPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextTrack();
    }

    public void resumeTrack() {
        showNotification(true);
        mPlayerCallbackListener.isPlaying();
        mPlayer.start();
    }

    public void pauseTrack() {
        showNotification(false);
        mPlayerCallbackListener.isPaused();
        mPlayer.pause();
    }

    public void nextTrack() {
        mPlayerCallbackListener.playerLoading();
        if (!mSongList.isLast()) {
            mSongList.moveToNext();

        } else {
            mSongList.moveToFirst();
        }
        settingTrackInfo();
        refreshTrackInfoBundle();
        mPlayerCallbackListener.trackInfoReady(mTrackInfo);
        showNotification(true);
        settingTrackUrl();
        settingPlayer();
    }

    public void previousTrack() {
        mPlayerCallbackListener.playerLoading();
        if (!mSongList.isFirst()) {
            mSongList.moveToPrevious();
        } else {
            mSongList.moveToLast();
        }
        settingTrackInfo();
        refreshTrackInfoBundle();
        mPlayerCallbackListener.trackInfoReady(mTrackInfo);
        showNotification(true);
        settingTrackUrl();
        settingPlayer();
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public int getCurrentTrackPosition() {
        return mPlayer.getCurrentPosition();
    }

    public void seekTo(int position) {
        mPlayer.seekTo(position);
    }

    public void refreshTrackInfoBundle() {
        mTrackInfo.clear();
        mTrackInfo.putString("INFO_ARTIST_NAME", mArtistName);
        mTrackInfo.putString("INFO_ALBUM_NAME", mAlbumName);
        mTrackInfo.putString("INFO_TRACK_NAME", mTrackName);
        mTrackInfo.putString("INFO_TRACK_EXT_URL", mTrackExtUrl);
        mTrackInfo.putInt("INFO_TRACK_DURATION", mTrackDuration);
        mTrackInfo.putString("INFO_ART_LARGE", mAlbumArtLarge);
        mTrackInfo.putString("INFO_ART_SMALL", mAlbumArtSmall);
    }

    private void settingTrackUrl() {
        int idx_track_url = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_PREVIEW_URL);
        mTrackUrl = mSongList.getString(idx_track_url);
    }

    private void settingTrackInfo() {
        int idx_track_artist = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ARTIST_KEY);
        int idx_track_album = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_KEY);
        int idx_track_name = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_NAME);
        int idx_track_ext_url = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_EXTERNAL_URL);
        int idx_track_duration = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_DURATION);
        int idx_album_art_large = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_LARGE);
        int idx_album_art_small = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_SMALL);

        mArtistName = mSongList.getString(idx_track_artist);
        mAlbumName = mSongList.getString(idx_track_album);
        mTrackName = mSongList.getString(idx_track_name);
        mTrackExtUrl = mSongList.getString(idx_track_ext_url);
        mTrackDuration = mSongList.getInt(idx_track_duration);
        mAlbumArtLarge = mSongList.getString(idx_album_art_large);
        mAlbumArtSmall = mSongList.getString(idx_album_art_small);
    }

    private void settingPlayer() {
        mPlayer.reset();
        try {
            mPlayer.setDataSource(mTrackUrl);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
            mPlayer.prepareAsync();
    }

    public void loadBitmap(String url) {
        if (loadTarget == null) loadTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mNotificationBuilder.setLargeIcon(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        Picasso.with(this).load(url).into(loadTarget);
    }

    public void showNotification(boolean playingNotPaused) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String displayNotificationsKey = this.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = sharedPreferences.getBoolean(displayNotificationsKey, Boolean.parseBoolean(this.getString(R.string.pref_enable_notifications_default)));
        if (displayNotifications) {
            mNotificationBuilder = new Notification.Builder(this)
                    .setContentTitle("Now Playing")
                    .setContentText(mTrackName);

            if (playingNotPaused) {
                mNotificationBuilder
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setOngoing(true)
                        .addAction(android.R.drawable.ic_media_previous, "", mPendingPreviousTrackIntent)
                        .addAction(android.R.drawable.ic_media_pause, "", mPendingPauseIntent)
                        .addAction(android.R.drawable.ic_media_next, "", mPendingNextTrackIntent);
            } else {
                mNotificationBuilder
                        .setSmallIcon(android.R.drawable.ic_media_pause)
                        .setOngoing(false)
                        .addAction(android.R.drawable.ic_media_previous, "", mPendingPreviousTrackIntent)
                        .addAction(android.R.drawable.ic_media_play, "", mPendingPlayIntent)
                        .addAction(android.R.drawable.ic_media_next, "", mPendingNextTrackIntent);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mNotificationBuilder.setShowWhen(false);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mNotificationBuilder
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setStyle(new Notification.MediaStyle().setShowActionsInCompactView(allControls));
            }

            loadBitmap(mAlbumArtSmall);
            mNotifyMgr.notify(R.id.notification_id, mNotificationBuilder.build());
        } else {
            try {
                mNotifyMgr.cancel(R.id.notification_id);
                Log.d("BOOM!", "Cancelled");
            } catch (NullPointerException e) {
                Log.d("no notes yet", "nothing to cancel");
            }
        }
    }
}