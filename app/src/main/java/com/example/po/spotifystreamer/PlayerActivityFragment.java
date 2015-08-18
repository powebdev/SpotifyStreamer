package com.example.po.spotifystreamer;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.po.spotifystreamer.data.MusicContract;

import java.io.IOException;
//http://stackoverflow.com/questions/26266774/dealing-with-androids-mediaplayer-while-rotating-screen-pressing-home-button-o
//import android.content.Loader;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment implements SeekBar.OnSeekBarChangeListener,View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();
    private static final int TRACK_LOADER = 2;
    private static final boolean PLAYER_STATE_PLAYING = true;
    private static final boolean PLAYER_STATE_PAUSED = false;

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private Handler timeUpdateHandler = new Handler();
    private boolean mPlayerPlaying;
    private TextView mArtistNameView;
    private TextView mAlbumNameView;
    private ImageView mAlbumArtView;
    private TextView mTrackNameView;
    private SeekBar mTrackSeekBar;
    private ImageButton mPreviousTrackButton;
    private ImageButton mNextTrackButton;
    private ImageButton mPausePlayButton;
    private ProgressBar mLoadingWheel;
    private TextView mCurrentPositionView;
    private TextView mDurationView;

    private String mTrackUri;
    private int mTrackNumber;
    private int mTrackDuration;
    private int mTrackCurrentPosition;

    public PlayerActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(TRACK_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPlayerPlaying = PLAYER_STATE_PAUSED;
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        mArtistNameView = (TextView) rootView.findViewById(R.id.frag_player_artist_textview);
        mAlbumNameView = (TextView) rootView.findViewById(R.id.frag_player_album_textview);
        mAlbumArtView = (ImageView) rootView.findViewById(R.id.frag_player_album_imageview);
        mTrackNameView = (TextView) rootView.findViewById(R.id.frag_player_track_textview);
        mLoadingWheel = (ProgressBar) rootView.findViewById(R.id.frag_player_loading_wheel);
        mCurrentPositionView = (TextView) rootView.findViewById(R.id.frag_player_current_textview);
        mDurationView = (TextView) rootView.findViewById(R.id.frag_player_duration_textview);

        mTrackSeekBar = (SeekBar) rootView.findViewById(R.id.frag_player_track_seekbar);
        mTrackSeekBar.setOnSeekBarChangeListener(this);

        mPreviousTrackButton = (ImageButton) rootView.findViewById(R.id.frag_player_previous_button);
        mPreviousTrackButton.setOnClickListener(this);

        mNextTrackButton = (ImageButton) rootView.findViewById(R.id.frag_player_next_button);
        mNextTrackButton.setOnClickListener(this);

        mPausePlayButton = (ImageButton) rootView.findViewById(R.id.frag_player_play_pause_button);
        mPausePlayButton.setOnClickListener(this);


        Intent intent = getActivity().getIntent();
        if(intent != null) {
            Bundle extrasInfo = intent.getExtras();
            mTrackNumber = extrasInfo.getInt("EXTRA_TRACK_POSITION");
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.frag_player_previous_button:
                moveToPreviousTrack();
                break;
            case R.id.frag_player_next_button:
                moveToNextTrack();
                break;
            case R.id.frag_player_play_pause_button:
                if(!mMediaPlayer.isPlaying()){
                    playTrack();
                }
                else if(mMediaPlayer.isPlaying()){
                    pauseTrack();
                }
                break;
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        mMediaPlayer.stop();
    }

    public void moveToNextTrack(){
        mTrackNumber++;
        mMediaPlayer.stop();
        getLoaderManager().restartLoader(TRACK_LOADER, null, this);
    }

    public void moveToPreviousTrack(){
        mTrackNumber--;
        mMediaPlayer.stop();
        getLoaderManager().restartLoader(TRACK_LOADER, null, this);
    }

    public void playTrack(){
        mPlayerPlaying = PLAYER_STATE_PLAYING;
        mPausePlayButton.setImageResource(android.R.drawable.ic_media_pause);
        mMediaPlayer.start();
    }

    public void pauseTrack(){
        mPlayerPlaying = PLAYER_STATE_PAUSED;
        mPausePlayButton.setImageResource(android.R.drawable.ic_media_play);
        mMediaPlayer.pause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY + " DESC";
        Uri topTrackUri = MusicContract.TopTrackEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                topTrackUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(mTrackNumber < 0){
            mTrackNumber = 0;
        }
        if(mTrackNumber > data.getCount() - 1){
            mTrackNumber = data.getCount() - 1;
        }

        data.moveToPosition(mTrackNumber);
        int inx_artist_name_col = data.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ARTIST_KEY);
        int inx_album_name_col = data.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_KEY);
        int inx_track_name_col = data.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_NAME);
        int inx_track_url_col = data.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_PREVIEW_URL);

        String artistName = data.getString(inx_artist_name_col);
        String albumName = data.getString(inx_album_name_col);
        String trackName = data.getString(inx_track_name_col);
        mTrackUri = data.getString(inx_track_url_col);

        mArtistNameView.setText(artistName);
        mAlbumNameView.setText(albumName);
        mTrackNameView.setText(trackName);

        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.d(LOG_TAG, "url is " + mTrackUri);
            mMediaPlayer.setDataSource(mTrackUri);
            mPausePlayButton.setEnabled(false);
            mLoadingWheel.setVisibility(View.VISIBLE);
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
        }
        catch(SecurityException e){
            e.printStackTrace();
        }
        catch(IllegalStateException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mTrackDuration = mMediaPlayer.getDuration();
                mTrackSeekBar.setMax(mTrackDuration);
                mTrackCurrentPosition = mMediaPlayer.getCurrentPosition();
                mPausePlayButton.setEnabled(true);
                mLoadingWheel.setVisibility(View.INVISIBLE);
                mDurationView.setText(HelperFunction.timeFormatter(mTrackDuration));
                mCurrentPositionView.setText(HelperFunction.timeFormatter(mTrackCurrentPosition));
                mTrackSeekBar.setProgress(mTrackCurrentPosition);
                playTrack();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            mTrackCurrentPosition = mMediaPlayer.getCurrentPosition();

        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mTrackCurrentPosition = progress;
        mCurrentPositionView.setText(HelperFunction.timeFormatter(mTrackCurrentPosition));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        mMediaPlayer.seekTo(mTrackCurrentPosition);

    }
}
