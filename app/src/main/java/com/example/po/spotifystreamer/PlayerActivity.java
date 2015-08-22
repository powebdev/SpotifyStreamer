package com.example.po.spotifystreamer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PlayerActivity extends AppCompatActivity {
    private static final String LOG_TAG =PlayerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "App in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if(savedInstanceState == null){
            PlayerFragment pf = new PlayerFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.player_fragment_container, pf).commit();
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
    protected void onPause() {
        Log.d(LOG_TAG, "App in onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "App in onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "App in onDestroy");
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
