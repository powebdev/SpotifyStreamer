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
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private static final String LOG_TAG = TopTracksActivityFragment.class.getSimpleName();
    private TopTrackListAdapter topTrackListAdapter;
    private String artistId;
    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        topTrackListAdapter = new TopTrackListAdapter(getActivity(), new ArrayList<TopTrackInfo>());
        View  rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            artistId = intent.getStringExtra(Intent.EXTRA_TEXT);

            QuerySpotifyTopTracksTask searchTopTrackTask = new QuerySpotifyTopTracksTask();
            searchTopTrackTask.execute(artistId);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_top_track);
            listView.setAdapter(topTrackListAdapter);
        }

        return rootView;
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
            Tracks topTracks = spotifyService.getArtistTopTrack(artistId[0], availableMarket);

            if(topTracks != null && topTracks.tracks.size() > 0){
                ArrayList<TopTrackInfo> topTrackInfos = new ArrayList<>();
                for(int i = 0; i < topTracks.tracks.size(); i++){
                    Track track = topTracks.tracks.get(i);
                    topTrackInfos.add(new TopTrackInfo(track.name, track.album.name, "default"));

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
                Toast.makeText(getActivity(), "No top tracks found", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
