package com.example.po.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private TopTrackListAdapter topTrackListAdapter;
    private String artistId;
    private Toast noResultsToast;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Create or reload the adapter to convert the array to views
        if(savedInstanceState != null){
            TopTrackInfo[] savedStateValues = (TopTrackInfo[]) savedInstanceState.getParcelableArray("topTrackKey");
            if(savedStateValues != null){
                topTrackListAdapter = new TopTrackListAdapter(getActivity(), new ArrayList<TopTrackInfo>(Arrays.asList(savedStateValues)));
            }
        }
        else{
            topTrackListAdapter = new TopTrackListAdapter(getActivity(), new ArrayList<TopTrackInfo>());
        }


        View  rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
            fetchTracksResults(artistId);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_top_track);
            listView.setAdapter(topTrackListAdapter);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        super.onSaveInstanceState(savedState);

        TopTrackInfo[] stateValuesToSave = topTrackListAdapter.getValues();
        savedState.putParcelableArray("topTrackKey", stateValuesToSave);

    }
    public class QuerySpotifyTopTracksTask extends AsyncTask<String, Void, ArrayList<TopTrackInfo>> {

        @Override
        protected ArrayList<TopTrackInfo> doInBackground(String... artistId){

            if(artistId.length == 0){
                return null;
            }
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            Map availableMarket = new HashMap();
            availableMarket.put("country", "US");
            Artist artist = spotifyService.getArtist(artistId[0]);
            Tracks topTracks = spotifyService.getArtistTopTrack(artistId[0], availableMarket);

            if(topTracks != null && topTracks.tracks.size() > 0){
                ArrayList<TopTrackInfo> topTrackInfos = new ArrayList<>();
                for(int i = 0; i < topTracks.tracks.size(); i++){
                    Track track = topTracks.tracks.get(i);

                    int imagePos = findProperImage(track);
                    if (imagePos == -1) {
                        topTrackInfos.add(new TopTrackInfo(track.name, artist.name, track.album.name, "default"));
                    } else {
                        topTrackInfos.add(new TopTrackInfo(track.name, artist.name, track.album.name, track.album.images.get(0).url));
                    }


                }
                return topTrackInfos;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<TopTrackInfo> results){
            if(results != null){
                topTrackListAdapter.clear();
                topTrackListAdapter.addAll(results);
            }
            else{
                if(noResultsToast != null){
                    noResultsToast.cancel();
                }
                noResultsToast.makeText(getActivity(), R.string.no_tracks_result_text, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method uses an AsyncTask to query the Spotify API for an artist top tracks search
     * @param searchString the artist to search for
     */
    public void fetchTracksResults(String searchString){
        QuerySpotifyTopTracksTask searchTopTrackTask = new QuerySpotifyTopTracksTask();
        searchTopTrackTask.execute(searchString);
    }

    /**
     * This method find the url of the image with proper size, in this case with height of 640 px
     * @param track the track to search the image for
     * @return an integer which indicates the array position of the found image
     */
    public int findProperImage(Track track){
        int foundImage;
        if(track.album.images.size() == 0){
            foundImage = -1;
        }
        else if(track.album.images.size() == 1){
            foundImage = 0;
        }
        else{
            int foundSixForty = 0;
            while(foundSixForty < track.album.images.size()){
                if(track.album.images.get(foundSixForty).height == 640){
                    return foundSixForty;
                }
                foundSixForty++;
            }
            foundImage = foundSixForty - 1;
        }
        return foundImage;
    }

}
