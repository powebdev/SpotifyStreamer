package com.example.po.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.po.spotifystreamer.service.PlayerService;
import com.squareup.picasso.Picasso;

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
    private String mAlbumArtUrl;
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

        if(savedInstanceState == null){
            mLoadingWheel.setVisibility(View.VISIBLE);
            mPausePlayButton.setEnabled(false);
            mPausePlayButton.setImageResource(android.R.drawable.ic_media_pause);
            Log.d(LOG_TAG, "savedInstanceState is null");
            int trackNumber;
            if(getResources().getBoolean(R.bool.twopanes_layout)){
                Bundle argsBundle = getArguments();
                trackNumber = argsBundle.getInt("ARGS_TRACK_POSITION", 0);
                mArtistNameView.setText(argsBundle.getString("ARGS_ARTIST_NAME"));
                mAlbumNameView.setText(argsBundle.getString("ARGS_ALBUM_NAME"));
                mTrackNameView.setText(argsBundle.getString("ARGS_TRACK_NAME"));
                mAlbumArtUrl = argsBundle.getString("ARGS_ART_LARGE");
                if(mAlbumArtUrl.equals("default")){
                    mAlbumArtView.setImageResource((R.drawable.record));
                }else{
                    Picasso.with(getActivity()).load(mAlbumArtUrl).into(mAlbumArtView);
                }

            }else{
                Intent intent = getActivity().getIntent();
                trackNumber = intent.getIntExtra("EXTRA_TRACK_POSITION", 0);
                mArtistNameView.setText(intent.getStringExtra("EXTRA_ARTIST_NAME"));
                mAlbumNameView.setText(intent.getStringExtra("EXTRA_ALBUM_NAME"));
                mTrackNameView.setText(intent.getStringExtra("EXTRA_TRACK_NAME"));
                mAlbumArtUrl = intent.getStringExtra("EXTRA_ART_LARGE");
                if(mAlbumArtUrl.equals("default")){
                    mAlbumArtView.setImageResource((R.drawable.record));
                }else{
                    Picasso.with(getActivity()).load(mAlbumArtUrl).into(mAlbumArtView);
                }
            }

            startServiceIntent = new Intent(getActivity(), PlayerService.class);
            startServiceIntent.putExtra("EXTRA_TRACK_NUMBER", trackNumber);
            getActivity().startService(startServiceIntent);


        } else{
            Log.d(LOG_TAG, "savedInstanceState is not null");
            mTrackDuration = savedInstanceState.getInt("SAVED_TRACK_DURATION");
            mPlayerStatePlaying = savedInstanceState.getBoolean("SAVED_PLAYER_STATE_PLAYING");
            mArtistNameView.setText(savedInstanceState.getString("SAVED_ARTIST_NAME"));
            mAlbumNameView.setText(savedInstanceState.getString("SAVED_ALBUM_NAME"));
            mTrackNameView.setText(savedInstanceState.getString("SAVED_TRACK_NAME"));
            mAlbumArtUrl = savedInstanceState.getString("SAVED_ART_LARGE");
            if(mAlbumArtUrl.equals("default")){
                mAlbumArtView.setImageResource((R.drawable.record));
            }else{
                Picasso.with(getActivity()).load(mAlbumArtUrl).into(mAlbumArtView);
            }
            mDurationView.setText(HelperFunction.timeFormatter(mTrackDuration));
            mTrackSeekBar.setMax(mTrackDuration);
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

        mTrackSeekBar.setOnSeekBarChangeListener(this);
        mPreviousTrackButton.setOnClickListener(this);
        mNextTrackButton.setOnClickListener(this);
        mPausePlayButton.setOnClickListener(this);
        timeUpdateHandler.postDelayed(UpdateSongTime, 100);

        return rootView;

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
        outState.putInt("SAVED_TRACK_DURATION", mTrackDuration);
        outState.putBoolean("SAVED_PLAYER_STATE_PLAYING", mPlayerService.isPlaying());
        outState.putString("SAVED_ARTIST_NAME", (String) mArtistNameView.getText());
        outState.putString("SAVED_ALBUM_NAME", (String)mAlbumNameView.getText());
        outState.putString("SAVED_TRACK_NAME", (String)mTrackNameView.getText());
        outState.putString("SAVED_ART_LARGE", mAlbumArtUrl);
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
                public void playerLoading() {
                    mTrackSeekBar.setProgress(0);
                    mLoadingWheel.setVisibility(View.VISIBLE);
                    mPausePlayButton.setEnabled(false);
                    mPausePlayButton.setImageResource(android.R.drawable.ic_media_pause);
                }

                @Override
                public void trackInfoReady(Bundle trackInfo) {
                    mArtistNameView.setText(trackInfo.getString("INFO_ARTIST_NAME"));
                    mAlbumNameView.setText(trackInfo.getString("INFO_ALBUM_NAME"));
                    mTrackNameView.setText(trackInfo.getString("INFO_TRACK_NAME"));
                    mAlbumArtUrl = trackInfo.getString("INFO_ART_LARGE");
                    if(mAlbumArtUrl.equals("default")){
                        mAlbumArtView.setImageResource((R.drawable.record));
                    }else{
                        Picasso.with(getActivity()).load(mAlbumArtUrl).into(mAlbumArtView);
                    }

                }

                @Override
                public void trackDurationReady(int duration) {
                    mTrackDuration = duration;
                    mTrackSeekBar.setMax(mTrackDuration);
                    mDurationView.setText(HelperFunction.timeFormatter(mTrackDuration));
                    mLoadingWheel.setVisibility(View.INVISIBLE);
                    mPausePlayButton.setEnabled(true);
                    mPausePlayButton.setImageResource(android.R.drawable.ic_media_pause);
                }

                @Override
                public void isPlaying() {
                    mPlayerStatePlaying = true;
                    mPausePlayButton.setImageResource(android.R.drawable.ic_media_pause);
                }

                @Override
                public void isPaused() {
                    mPlayerStatePlaying = false;
                    mPausePlayButton.setImageResource(android.R.drawable.ic_media_play);
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
                    mPlayerService.previousTrack();
                }
                break;
            case R.id.frag_player_next_button:
                if(mBound){
                    mPlayerService.nextTrack();
                }
                break;
        }
    }
}