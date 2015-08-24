package com.example.po.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class TopTracksActivity extends AppCompatActivity implements TopTracksFragment.Callback {
    private boolean mServiceStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("EXTRA_ARTIST_NAME")) {
            String artistName = intent.getExtras().getString("EXTRA_ARTIST_NAME");
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(artistName);
            }
        }
        if (savedInstanceState != null) {
            mServiceStarted = savedInstanceState.getBoolean("SAVED_SERVICE_STARTED");
        }

        TopTracksFragment ttf = new TopTracksFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.top_track_fragment_container, ttf).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("SAVED_SERVICE_STARTED", mServiceStarted);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_open_player && mServiceStarted) {
            Intent playerIntent = new Intent(this, PlayerActivity.class);
            playerIntent.putExtra("EXTRA_NEW_TRACK", false);
            startActivity(playerIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTopTrackSelected(int trackPosition, String artistName, String albumName, String trackName, String trackExtUrl, String albumArtSmall, String albumArtLarge, int trackDuration) {
        Intent playerIntent = new Intent(this, PlayerActivity.class);
        playerIntent.putExtra("EXTRA_NEW_TRACK", true);
        playerIntent.putExtra("EXTRA_TRACK_POSITION", trackPosition);
        playerIntent.putExtra("EXTRA_ARTIST_NAME", artistName);
        playerIntent.putExtra("EXTRA_ALBUM_NAME", albumName);
        playerIntent.putExtra("EXTRA_TRACK_NAME", trackName);
        playerIntent.putExtra("EXTRA_TRACK_EXT_URL", trackExtUrl);
        playerIntent.putExtra("EXTRA_TRACK_DURATION", trackDuration);
        playerIntent.putExtra("EXTRA_ART_SMALL", albumArtSmall);
        playerIntent.putExtra("EXTRA_ART_LARGE", albumArtLarge);
        mServiceStarted = true;
        startActivity(playerIntent);
    }
}
