package com.example.po.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
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
//import android.content.Loader;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment implements SeekBar.OnSeekBarChangeListener,View.OnClickListener{
    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();

    private PlayerService mPlayerService;
    private boolean mBound = false;

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

    private int mTrackCurrentPosition;

    public PlayerActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart(){
        super.onStart();
        Intent intent = new Intent(getActivity(), PlayerService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);


        mArtistNameView = (TextView) rootView.findViewById(R.id.frag_player_artist_textview);
        mAlbumNameView = (TextView) rootView.findViewById(R.id.frag_player_album_textview);
        mAlbumArtView = (ImageView) rootView.findViewById(R.id.frag_player_album_imageview);
        mTrackNameView = (TextView) rootView.findViewById(R.id.frag_player_track_textview);
        mLoadingWheel = (ProgressBar) rootView.findViewById(R.id.frag_player_loading_wheel);
        mCurrentPositionView = (TextView) rootView.findViewById(R.id.frag_player_current_textview);
        mDurationView = (TextView) rootView.findViewById(R.id.frag_player_duration_textview);
        mTrackSeekBar = (SeekBar) rootView.findViewById(R.id.frag_player_track_seekbar);
        mPreviousTrackButton = (ImageButton) rootView.findViewById(R.id.frag_player_previous_button);
        mNextTrackButton = (ImageButton) rootView.findViewById(R.id.frag_player_next_button);
        mPausePlayButton = (ImageButton) rootView.findViewById(R.id.frag_player_play_pause_button);

        mTrackSeekBar.setOnSeekBarChangeListener(this);
        mPreviousTrackButton.setOnClickListener(this);
        mNextTrackButton.setOnClickListener(this);
        mPausePlayButton.setOnClickListener(this);

        mPausePlayButton.setEnabled(false);
        mLoadingWheel.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.frag_player_previous_button:
                if(mBound){
                    if(mPlayerService.previousTrack()){
                        mPausePlayButton.setEnabled(false);
                        mLoadingWheel.setVisibility(View.VISIBLE);
                        updateUi();
                        mPausePlayButton.setEnabled(true);
                        mLoadingWheel.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.frag_player_next_button:
                if(mBound){
                    if(mPlayerService.nextTrack()){
                        mPausePlayButton.setEnabled(false);
                        mLoadingWheel.setVisibility(View.VISIBLE);
                        updateUi();
                        mPausePlayButton.setEnabled(true);
                        mLoadingWheel.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.frag_player_play_pause_button:
                if(mPlayerService.getPlayerState()){
                    pauseTrack();
                }else{
                    playTrack();
                }
                break;
        }
    }

    public void playTrack(){
        timeUpdateHandler.postDelayed(UpdateSongTime, 100);
        mPausePlayButton.setImageResource(android.R.drawable.ic_media_pause);
        mPlayerService.playTrack();
    }

    public void pauseTrack(){
        mPausePlayButton.setImageResource(android.R.drawable.ic_media_play);
        mPlayerService.pauseTrack();
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            mTrackCurrentPosition = mPlayerService.getCurrentPosition();
            mCurrentPositionView.setText(HelperFunction.timeFormatter(mTrackCurrentPosition));
            mTrackSeekBar.setProgress(mTrackCurrentPosition);
            timeUpdateHandler.postDelayed(this, 40);

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

        mPlayerService.seekToPosition(mTrackCurrentPosition);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(LOG_TAG, "in onServiceConnected");
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) service;
            mPlayerService = binder.getService();
            mBound = true;
            updateUi();
            mPausePlayButton.setEnabled(true);
            mLoadingWheel.setVisibility(View.INVISIBLE);
            mPlayerService.playTrack();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;

        }
    };

    private void updateUi(){
        Bundle trackInfo = mPlayerService.getTrackInfo();
        mArtistNameView.setText(trackInfo.getString("TRACK_INFO_ARTIST"));
        mAlbumNameView.setText(trackInfo.getString("TRACK_INFO_ALBUM"));
        mTrackNameView.setText(trackInfo.getString("TRACK_INFO_NAME"));
        mDurationView.setText(HelperFunction.timeFormatter(trackInfo.getInt("TRACK_INFO_DURATION")));
    }
}

//        catch(IllegalArgumentException e){
//        catch(SecurityException e){
//        catch(IllegalStateException e){
//        catch(IOException e){