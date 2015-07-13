package com.example.po.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private Toast noResultsToast;

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
        EditText searchText = (EditText) rootView.findViewById(R.id.search_artist);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (hasConnection()) {
                        String searchStr = v.getText().toString();
                        if(!searchStr.isEmpty()){
                            fetchArtistResults(searchStr);
                            handled = true;
                            return handled;
                        }
                    }
                    else{
                        if(noResultsToast != null){
                            noResultsToast.cancel();
                        }
                        noResultsToast.makeText(getActivity(), R.string.no_network_connection_text, Toast.LENGTH_SHORT).show();
                        handled = false;
                        return handled;
                    }

                }

                return handled;
            }
        });
//

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(artistListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent topTrackIntent = new Intent(getActivity(), TopTracksActivity.class).putExtra(Intent.EXTRA_TEXT, view.getTag().toString());
                startActivity(topTrackIntent);
            }
        });
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
//            Tracks topTracks = spotifyService.getArtistTopTrack(artistSearchResults.artists.items.get(0).id);
//            topTracks.tracks.get(0).name;
            if (artistSearchResults != null && artistSearchResults.artists.items.size() > 0) {
                ArrayList<ArtistInfo> artistInfos = new ArrayList<>();
                for (int i = 0; i < artistSearchResults.artists.items.size(); i++) {
                    Artist artist = artistSearchResults.artists.items.get(i);
                    //function here for figuring out the right image to load
                    int imagePos = findProperImage(artist);
                    if (imagePos == -1) {
                        artistInfos.add(new ArtistInfo(artist.name, artist.id, "default"));
                    } else {
                        artistInfos.add(new ArtistInfo(artist.name, artist.id, artist.images.get(imagePos).url));
                    }
                }
                return artistInfos;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ArtistInfo> results){
            artistListAdapter.clear();
            if(results != null){
                artistListAdapter.addAll(results);
            }
            else{
                if(noResultsToast != null){
                    noResultsToast.cancel();
                }
                noResultsToast.makeText(getActivity(), R.string.no_artists_result_text, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method uses an AsyncTask to query the Spotify API for an artist name search
     * @param searchString the artist to search for
     */
    public void fetchArtistResults(String searchString){
        QuerySpotifyArtistTask searchArtistTask = new QuerySpotifyArtistTask();
        searchArtistTask.execute(searchString);
    }

    /**
     * This method find the url of the image with proper size, in this case with height of 200 px
     * @param artist the artist whose image to search for
     * @return an integer which indicates the array position of the found image
     */
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

    /**
     * this method check whether or not there is connection to the internet
     * @return true if there is connection and false if there is not
     */
    public boolean hasConnection(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean hasConnection = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return hasConnection;
    }
}
