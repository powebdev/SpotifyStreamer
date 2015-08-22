package com.example.po.spotifystreamer;

import android.support.v4.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import com.example.po.spotifystreamer.service.PlayerService;
//import android.content.Loader;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends DialogFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{
    private static final String LOG_TAG = PlayerFragment.class.getSimpleName();

    private boolean seekBarTouched = false;
    private PlayerService mPlayerService;
    private Intent startServiceIntent;
    private Intent bindServiceIntent;
    private boolean mBound = false;
    private Cursor mSongList;
    private String mCurrentTrackUrl;
    private boolean mPlayerStatePlaying = false;

    private Handler timeUpdateHandler = new Handler();

    private TextView mArtistNameView;
    private TextView mAlbumNameView;
    private ImageView mAlbumArtView;
    private TextView mTrackNameView;
    private TextView mCurrentPositionView;
    private TextView mDurationView;

    private SeekBar mTrackSeekBar;
    private ImageButton mPreviousTrackButton;
    private ImageButton mNextTrackButton;
    private ImageButton mPausePlayButton;
    private ProgressBar mLoadingWheel;

    private int mTrackDuration;

    public PlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCV");

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        mArtistNameView = (TextView) rootView.findViewById(R.id.frag_player_artist_textview);
        mAlbumNameView = (TextView) rootView.findViewById(R.id.frag_player_album_textview);
        mAlbumArtView = (ImageView) rootView.findViewById(R.id.frag_player_album_imageview);
        mTrackNameView = (TextView) rootView.findViewById(R.id.frag_player_track_textview);
        mLoadingWheel = (ProgressBar) rootView.findViewById(R.id.frag_player_loading_wheel);
        mTrackSeekBar = (SeekBar) rootView.findViewById(R.id.frag_player_track_seekbar);
        mCurrentPositionView = (TextView) rootView.findViewById(R.id.frag_player_current_textview);
        mDurationView = (TextView) rootView.findViewById(R.id.frag_player_duration_textview);
        mPreviousTrackButton = (ImageButton) rootView.findViewById(R.id.frag_player_previous_button);
        mNextTrackButton = (ImageButton) rootView.findViewById(R.id.frag_player_next_button);
        mPausePlayButton = (ImageButton) rootView.findViewById(R.id.frag_player_play_pause_button);

        String sortOrder = MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY + " DESC";
        mSongList = getActivity().getContentResolver().query(MusicContract.TopTrackEntry.CONTENT_URI, null, null, null, sortOrder);
        if(savedInstanceState == null){
            mLoadingWheel.setVisibility(View.VISIBLE);
            mPausePlayButton.setEnabled(false);
            mPausePlayButton.setImageResource(android.R.drawable.ic_media_pause);
            Log.d(LOG_TAG, "savedInstanceState is null");
            int trackNumber;
            if(getResources().getBoolean(R.bool.twopanes_layout)){
                Bundle argsBundle = getArguments();
                trackNumber = argsBundle.getInt("ARGS_TRACK_POSITION", 0);
            }else{
                Intent intent = getActivity().getIntent();
                trackNumber = intent.getIntExtra("EXTRA_TRACK_POSITION", 0);
            }
//            Bundle argsBundle = getArguments();
//            trackNumber = argsBundle.getInt("ARGS_TRACK_POSITION", 0);

            mSongList.moveToPosition(trackNumber);
            int idx_track_url = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_PREVIEW_URL);

            startServiceIntent = new Intent(getActivity(), PlayerService.class);
            startServiceIntent.putExtra("EXTRA_TRACK_URL", mSongList.getString(idx_track_url));
            getActivity().startService(startServiceIntent);


        } else{
            Log.d(LOG_TAG, "savedInstanceState is not null");
            mSongList.moveToPosition(savedInstanceState.getInt("SAVED_TRACK_POSITION"));
            mTrackDuration = savedInstanceState.getInt("SAVED_TRACK_DURATION");
            mPlayerStatePlaying = savedInstanceState.getBoolean("SAVED_PLAYER_STATE_PLAYING");
            mDurationView.setText(HelperFunction.timeFormatter(mTrackDuration));
            mTrackSeekBar.setMax(mTrackDuration);
            timeUpdateHandler.postDelayed(UpdateSongTime, 100);
            mLoadingWheel.setVisibility(View.INVISIBLE);
            mPausePlayButton.setEnabled(true);
            if(mPlayerStatePlaying){
                mPausePlayButton.setImageResource(android.R.drawable.ic_media_pause);
            }else{
                mPausePlayButton.setImageResource(android.R.drawable.ic_media_play);
            }

        }

        bindServiceIntent = new Intent(getActivity(), PlayerService.class);
        getActivity().bindService(bindServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

        updateUi();

        mTrackSeekBar.setOnSeekBarChangeListener(this);
        mPreviousTrackButton.setOnClickListener(this);
        mNextTrackButton.setOnClickListener(this);
        mPausePlayButton.setOnClickListener(this);

        return rootView;

    }

    private void updateUi() {
        int idx_track_artist = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ARTIST_KEY);
        int idx_track_album = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_KEY);
        int idx_track_name = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_NAME);

        mArtistNameView.setText(mSongList.getString(idx_track_artist));
        mAlbumNameView.setText(mSongList.getString(idx_track_album));
        mTrackNameView.setText(mSongList.getString(idx_track_name));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onAC");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("SAVED_TRACK_POSITION", mSongList.getPosition());
        outState.putInt("SAVED_TRACK_DURATION", mTrackDuration);
        outState.putBoolean("SAVED_PLAYER_STATE_PLAYING", mPlayerService.isPlaying());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, "onDestroyView");
        getActivity().unbindService(mConnection);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "onDetach");
        super.onDetach();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) service;
            mPlayerService = binder.getService();
            mPlayerService.setPlayerCallbackListener(new PlayerService.PlayerCallbackListener() {
                @Override
                public void playerPrepared(int trackDuration) {
                    mTrackDuration = trackDuration;
                    mTrackSeekBar.setMax(mTrackDuration);

                    mTrackSeekBar.setProgress(0);

                    mCurrentPositionView.setText(HelperFunction.timeFormatter(0));
                    mDurationView.setText(HelperFunction.timeFormatter(trackDuration));
                    timeUpdateHandler.postDelayed(UpdateSongTime, 100);
                    mLoadingWheel.setVisibility(View.INVISIBLE);
                    mPausePlayButton.setEnabled(true);
                    mPausePlayButton.setImageResource(android.R.drawable.ic_media_pause);
                    mPlayerStatePlaying = true;
                }

                @Override
                public void playbackCompleted() {
                    mPausePlayButton.setImageResource(android.R.drawable.ic_media_play);
                    mPlayerStatePlaying = false;

                }
            });
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
//            mTrackCurrentPosition = mPlayerService.getCurrentTrackPosition();
            if(!seekBarTouched && mPlayerService != null){
                mCurrentPositionView.setText(HelperFunction.timeFormatter(mPlayerService.getCurrentTrackPosition()));
                mTrackSeekBar.setProgress(mPlayerService.getCurrentTrackPosition());
            }
            timeUpdateHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        mCurrentPositionView.setText(HelperFunction.timeFormatter(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        seekBarTouched = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mCurrentPositionView.setText(HelperFunction.timeFormatter(seekBar.getProgress()));
        mPlayerService.seekTo(seekBar.getProgress());
        seekBarTouched = false;

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.frag_player_play_pause_button:
                if(mBound){
                    if(mPlayerStatePlaying){
                        ((ImageButton) v).setImageResource(android.R.drawable.ic_media_play);
                        mPlayerService.pauseTrack();
                        mPlayerStatePlaying = false;
                    }else{
                        ((ImageButton) v).setImageResource(android.R.drawable.ic_media_pause);
                        mPlayerService.resumeTrack();
                        mPlayerStatePlaying = true;
                    }
                }
                break;
            case R.id.frag_player_previous_button:
                if(mBound){
                    if(!mSongList.isFirst()){
                        mSongList.moveToPrevious();
                        skipTrack();
                        updateUi();
                    }else{
                        mPlayerService.seekTo(0);
                    }
                }
                break;
            case R.id.frag_player_next_button:
                if(mBound){
                    if(!mSongList.isLast()){
                        mSongList.moveToNext();
                        skipTrack();
                        updateUi();
                    }else{
                        mPlayerService.seekTo(0);
                    }
                }
                break;
        }
    }

    private void skipTrack() {
        int idx_track_url = mSongList.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_PREVIEW_URL);
        startServiceIntent = new Intent(getActivity(), PlayerService.class);
        startServiceIntent.putExtra("EXTRA_TRACK_URL", mSongList.getString(idx_track_url));
        mPlayerStatePlaying = false;
        mPausePlayButton.setImageResource(android.R.drawable.ic_media_play);
        mLoadingWheel.setVisibility(View.VISIBLE);
        getActivity().startService(startServiceIntent);
    }
}