package com.example.po.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mArtistAdapter;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mArtistAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_artist,
                        R.id.list_item_artist_textview,
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        EditText searchText = (EditText) rootView.findViewById(R.id.search_artist);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchForArtist(v.getText().toString());
                    SearchForArtistTask searchTask = new SearchForArtistTask();
                    searchTask.execute(v.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(mArtistAdapter);
        return rootView;
    }

    private void searchForArtist(String output){
        Context context = getActivity();
        CharSequence text = output;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public class SearchForArtistTask extends AsyncTask<String, Void, ArtistsPager>{
        private final String LOG_TAG = SearchForArtistTask.class.getSimpleName();

        @Override
        protected ArtistsPager doInBackground(String... artistName){
            if(artistName.length == 0){
                return null;
            }
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            ArtistsPager searchResults = spotifyService.searchArtists(artistName[0]);

            return searchResults;

        }

        @Override
        protected void onPostExecute(ArtistsPager results){
            if(results != null){
                mArtistAdapter.clear();
                for(int i = 0; i < results.artists.items.size(); i++){
                    Artist artist = results.artists.items.get(i);
                    mArtistAdapter.add(artist.name);
                }
            }
        }
    }

}
