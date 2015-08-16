package com.example.po.spotifystreamer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.po.spotifystreamer.data.MusicContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment implements View.OnClickListener{
    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();

    private static final boolean PLAYER_STATE_PLAYING = true;
    private static final boolean PLAYER_STATE_PAUSED = false;

    private Cursor mTrackListCursor;

    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String sortOrder = MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY + " DESC";
//        mTrackListCursor = getActivity().getContentResolver().query(MusicContract.TopTrackEntry.CONTENT_URI, null, null, null, sortOrder);

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        TextView artistName = (TextView) rootView.findViewById(R.id.frag_player_artist_textview);
        TextView albumName = (TextView) rootView.findViewById(R.id.frag_player_album_textview);
        ImageView albumArt = (ImageView) rootView.findViewById(R.id.frag_player_album_imageview);
        TextView trackName = (TextView) rootView.findViewById(R.id.frag_player_track_textview);

        ImageButton previousTrack = (ImageButton) rootView.findViewById(R.id.frag_player_previous_button);
        previousTrack.setOnClickListener(this);

        ImageButton nextTrack = (ImageButton) rootView.findViewById(R.id.frag_player_next_button);
        nextTrack.setOnClickListener(this);

        Intent intent = getActivity().getIntent();
        if(intent != null){
            Bundle extrasInfo = intent.getExtras();
            int trackPosition = extrasInfo.getInt("EXTRA_TRACK_POSITION");
//            String uriString = extrasInfo.getString("EXTRA_TRACK_URI");
//            Uri trackUri = Uri.parse(uriString);
            mTrackListCursor = getActivity().getContentResolver().query(MusicContract.TopTrackEntry.CONTENT_URI, null, null, null, sortOrder);
            if(mTrackListCursor.moveToPosition(trackPosition)){
                int inx_track_artist = mTrackListCursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ARTIST_KEY);
                int inx_track_album = mTrackListCursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_KEY);
                int inx_track_name = mTrackListCursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_NAME);
                int inx_album_art = mTrackListCursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_LARGE);
                int inx_track_url = mTrackListCursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_PREVIEW_URL);

                artistName.setText(mTrackListCursor.getString(inx_track_artist));
                albumName.setText(mTrackListCursor.getString(inx_track_album));
                trackName.setText(mTrackListCursor.getString(inx_track_name));
            }
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
        }
    }

    public void moveToNextTrack(){
        if(!mTrackListCursor.isLast()){
            mTrackListCursor.moveToNext();
        }
        else{
            mTrackListCursor.moveToLast();
        }
        Log.d(LOG_TAG, "Moved to next track. Cursor at position " + mTrackListCursor.getPosition());
    }

    public void moveToPreviousTrack(){
        if(!mTrackListCursor.isFirst()){
            mTrackListCursor.moveToPrevious();
        }
        else{
            mTrackListCursor.moveToFirst();
        }
        Log.d(LOG_TAG, "Moved to previous track. Cursor at position " + mTrackListCursor.getPosition());
    }
}
