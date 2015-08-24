package com.example.po.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

public class PlayerActivity extends AppCompatActivity implements PlayerFragment.Callback {
    private ShareActionProvider mShareActionProvider;
    private String mTrackExtUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if (savedInstanceState == null) {
            mTrackExtUrl = this.getIntent().getStringExtra("EXTRA_TRACK_EXT_URL");
            PlayerFragment pf = new PlayerFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.player_fragment_container, pf).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mTrackExtUrl = getIntent().getStringExtra("EXTRA_TRACK_EXT_URL");
        if (mTrackExtUrl != null) {
            mShareActionProvider.setShareIntent(HelperFunction.createShareMusicIntent(mTrackExtUrl));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void newShareIntent(String newUrl) {
        mShareActionProvider.setShareIntent(HelperFunction.createShareMusicIntent(newUrl));
    }
}
