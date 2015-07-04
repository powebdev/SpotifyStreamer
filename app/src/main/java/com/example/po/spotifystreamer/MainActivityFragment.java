package com.example.po.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArtistListAdapter artistListAdapter;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Create or reload the adapter to convert the array to views
        if(savedInstanceState != null){
            ArtistInfo[] savedStateValues = (ArtistInfo[]) savedInstanceState.getParcelableArray("artistKey");
            if(savedStateValues != null){
                artistListAdapter = new ArtistListAdapter(getActivity(), new ArrayList<ArtistInfo>(Arrays.asList(savedStateValues)));
            }
        }
        else{
            artistListAdapter = new ArtistListAdapter(getActivity(), new ArrayList<ArtistInfo>());
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        fetchArtistResults(rootView);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(artistListAdapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        super.onSaveInstanceState(savedState);

        ArtistInfo[] stateValuesToSave = artistListAdapter.getValues();
        savedState.putParcelableArray("artistKey", stateValuesToSave);

    }

    public class QuerySpotifyArtistTask extends AsyncTask<String, Void, ArrayList<ArtistInfo>>{

        @Override
        protected ArrayList<ArtistInfo> doInBackground(String... artistName){

            if(artistName.length == 0){
                return null;
            }
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            ArtistsPager artistSearchResults = spotifyService.searchArtists(artistName[0]);

            if(artistSearchResults != null){
                ArrayList<ArtistInfo> artistInfos = new ArrayList<>();
                for(int i = 0; i < artistSearchResults.artists.items.size(); i++){
                    Artist artist = artistSearchResults.artists.items.get(i);
                    //function here for figuring out the right image to load
                    int imagePos = findProperImage(artist);
                    if(imagePos == -1){
                        artistInfos.add(new ArtistInfo(artist.name, artist.id, "default"));
                    }
                    else{
                        artistInfos.add(new ArtistInfo(artist.name, artist.id, artist.images.get(imagePos).url));
                    }

                }
                return artistInfos;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ArtistInfo> results){
            if(results != null){
                artistListAdapter.clear();
                artistListAdapter.addAll(results);
            }
        }
    }

    public void fetchArtistResults(View rootView){

        EditText searchText = (EditText) rootView.findViewById(R.id.search_artist);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchStr = v.getText().toString();
                    if(!searchStr.isEmpty()){
                        QuerySpotifyArtistTask searchTask = new QuerySpotifyArtistTask();
                        searchTask.execute(v.getText().toString());
                        handled = true;
                        return handled;
                    }
                }
                return handled;
            }
        });
    }

    public int findProperImage(Artist artist){
        int foundImage;
        if(artist.images.size() == 0){
            foundImage = -1;
        }
        else if(artist.images.size() == 1){
            foundImage = 0;
        }
        else{
            int foundTwoHundred = 0;
            while(foundTwoHundred < artist.images.size()){
                if(artist.images.get(foundTwoHundred).height == 200){
                    return foundTwoHundred;
                }
                foundTwoHundred++;
            }
            foundImage = foundTwoHundred - 1;
        }
        return foundImage;
    }
}
