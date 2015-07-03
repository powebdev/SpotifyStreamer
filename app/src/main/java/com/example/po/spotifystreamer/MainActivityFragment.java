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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
        //Construct the data source
        ArrayList<ArtistInfo> arrayOfArtists = new ArrayList<ArtistInfo>();
        //Create the adapter to convert the array to views
        artistListAdapter = new ArtistListAdapter(getActivity(), arrayOfArtists);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        EditText searchText = (EditText) rootView.findViewById(R.id.search_artist);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SearchForArtistTask searchTask = new SearchForArtistTask();
                    searchTask.execute(v.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(artistListAdapter);
        return rootView;
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
                artistListAdapter.clear();
                for(int i = 0; i < results.artists.items.size(); i++){
                    Artist artist = results.artists.items.get(i);
                    if(artist.images.isEmpty()){
                        ArtistInfo newArtist = new ArtistInfo(artist.name,"default");
                        artistListAdapter.add(newArtist);
                    }
                    else{
                        ArtistInfo newArtist = new ArtistInfo(artist.name,artist.images.get(artist.images.size()-1).url);
                        artistListAdapter.add(newArtist);
                    }


                }
            }
        }
    }

    public class ArtistInfo{
        public String name;
        public String imageLink;

        public ArtistInfo(String name, String imageLink){
            this.name = name;
            this.imageLink = imageLink;
        }
    }

    public class ArtistListAdapter extends ArrayAdapter<ArtistInfo>{
        public ArtistListAdapter(Context context, ArrayList<ArtistInfo> artists){
            super(context, 0, artists);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Get the data item for this position
            ArtistInfo artists = getItem(position);
            //Check if an existing view is being reused, otherwise inflate the view
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
            }
            //Lookup view for data population
            ImageView artistImage = (ImageView) convertView.findViewById(R.id.list_item_artist_imageview);
            TextView artistName = (TextView) convertView.findViewById(R.id.list_item_artist_textview);
            //Populate the data into the template view using the data object

            if(artists.imageLink.equals("default")){
                Picasso.with(getContext()).load(R.drawable.artist).into(artistImage);
            }
            else{
                Picasso.with(getContext()).load(artists.imageLink).into(artistImage);
            }

            artistName.setText(artists.name);
            //Return the completed view to render on screen
            return convertView;
        }
    }
}
