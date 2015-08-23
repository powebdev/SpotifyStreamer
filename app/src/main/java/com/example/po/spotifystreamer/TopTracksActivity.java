package com.example.po.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class TopTracksActivity extends AppCompatActivity implements TopTracksFragment.Callback{
    private static final String LOG_TAG = TopTracksActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);
//        Intent intent = this.getIntent();
//        Bundle extraInfos = intent.getExtras();
//        String artistName = extraInfos.getString("EXTRA_ARTIST_NAME");
        Intent intent = this.getIntent();
        if(intent != null && intent.hasExtra("EXTRA_ARTIST_NAME")){
            String artistName = intent.getExtras().getString("EXTRA_ARTIST_NAME");
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null){
                actionBar.setSubtitle(artistName);
            }
        }

        TopTracksFragment ttf = new TopTracksFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.top_track_fragment_container, ttf).commit();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTopTrackSelected(int trackPosition, String artistName, String albumName, String trackName, String albumArtSmall, String albumArtLarge) {
//        FragmentManager fm = getSupportFragmentManager();
//        PlayerFragment pf = new PlayerFragment();
//        Bundle arguments = new Bundle();
//        arguments.putInt("ARGS_TRACK_POSITION", trackPosition);
//        pf.setArguments(arguments);
//        FragmentTransaction ft = fm.beginTransaction();
//
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        ft.replace(R.id.top_track_fragment_container, pf).commit();
        Log.d(LOG_TAG, artistName + albumName + trackName);
        Intent playerIntent = new Intent(this, PlayerActivity.class);
        playerIntent.putExtra("EXTRA_TRACK_POSITION", trackPosition);
        playerIntent.putExtra("EXTRA_ARTIST_NAME", artistName);
        playerIntent.putExtra("EXTRA_ALBUM_NAME", albumName);
        playerIntent.putExtra("EXTRA_TRACK_NAME", trackName);
        playerIntent.putExtra("EXTRA_ART_SMALL", albumArtSmall);
        playerIntent.putExtra("EXTRA_ART_LARGE", albumArtLarge);
        startActivity(playerIntent);
    }


}
