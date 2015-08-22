package com.example.po.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements ArtistsFragment.Callback, TopTracksFragment.Callback{
    private static final String LOG_TAG =MainActivity.class.getSimpleName();
    private static final String TOPTRACKFRAG_TAG = "TTFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "App in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.top_track_fragment_container) != null){
            Log.d(LOG_TAG, "In two pane");
            mTwoPane = true;
        }else{
            Log.d(LOG_TAG, "In one pane");
            mTwoPane = false;
        }
    }

    @Override
    protected void onStart() {
        Log.d(LOG_TAG, "App in onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "App in onResume");

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArtistSelected(String artistName) {
        if(mTwoPane){
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(artistName);
            }

            TopTracksFragment ttf = new TopTracksFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.top_track_fragment_container, ttf, TOPTRACKFRAG_TAG).commit();

        }else{
            Intent topTrackIntent = new Intent(this, TopTracksActivity.class);
            Bundle infoStrings = new Bundle();
            infoStrings.putString("EXTRA_ARTIST_NAME", artistName);
            topTrackIntent.putExtras(infoStrings);
            startActivity(topTrackIntent);
        }
    }

    @Override
    public void onTopTrackSelected(int trackPosition) {
        FragmentManager fm = getSupportFragmentManager();
        PlayerFragment pf = new PlayerFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("ARGS_TRACK_POSITION", trackPosition);
        pf.setArguments(arguments);
        pf.show(fm, "playerDialog");
    }
}
