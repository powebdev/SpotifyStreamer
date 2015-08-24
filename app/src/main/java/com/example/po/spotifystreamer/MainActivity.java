package com.example.po.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements PlayerFragment.Callback, ArtistsFragment.Callback, TopTracksFragment.Callback {
    private static final String TOPTRACKFRAG_TAG = "TTFTAG";
    private boolean mTwoPane;
    private ShareActionProvider mShareActionProvider;
    private PlayerFragment pf = new PlayerFragment();
    private boolean mServiceStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.top_track_fragment_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        if (savedInstanceState != null) {
            mServiceStarted = savedInstanceState.getBoolean("SAVED_SERVICE_STARTED");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("SAVED_SERVICE_STARTED", mServiceStarted);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
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
            FragmentManager fm = getSupportFragmentManager();
            Bundle arguments = new Bundle();
            arguments.clear();
            arguments.putBoolean("ARGS_NEW_TRACK", false);
            pf.setArguments(arguments);
            pf.show(fm, "playerDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArtistSelected(String artistName) {
        if (mTwoPane) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(artistName);
            }

            TopTracksFragment ttf = new TopTracksFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.top_track_fragment_container, ttf, TOPTRACKFRAG_TAG).commit();

        } else {
            Intent topTrackIntent = new Intent(this, TopTracksActivity.class);
            Bundle infoStrings = new Bundle();
            infoStrings.putString("EXTRA_ARTIST_NAME", artistName);
            topTrackIntent.putExtras(infoStrings);
            startActivity(topTrackIntent);
        }
    }

    @Override
    public void onTopTrackSelected(int trackPosition, String artistName, String albumName, String trackName, String trackExtUrl, String albumArtSmall, String albumArtLarge, int trackDuration) {
        FragmentManager fm = getSupportFragmentManager();
        Bundle arguments = new Bundle();
        arguments.clear();
        arguments.putBoolean("ARGS_NEW_TRACK", true);
        arguments.putInt("ARGS_TRACK_POSITION", trackPosition);
        arguments.putString("ARGS_ARTIST_NAME", artistName);
        arguments.putString("ARGS_ALBUM_NAME", albumName);
        arguments.putString("ARGS_TRACK_NAME", trackName);
        arguments.putString("ARGS_TRACK_EXT_URL", trackExtUrl);
        arguments.putInt("ARGS_TRACK_DURATION", trackDuration);
        arguments.putString("ARGS_ART_SMALL", albumArtSmall);
        arguments.putString("ARGS_ART_LARGE", albumArtLarge);
        pf.setArguments(arguments);
        mServiceStarted = true;
        pf.show(fm, "playerDialog");
    }

    @Override
    public void newShareIntent(String newUrl) {
        TopTracksFragment ttf = (TopTracksFragment) getSupportFragmentManager().findFragmentByTag(TOPTRACKFRAG_TAG);
        mShareActionProvider = ttf.getShareActionProvider();
        mShareActionProvider.setShareIntent(HelperFunction.createShareMusicIntent(newUrl));
    }
}
