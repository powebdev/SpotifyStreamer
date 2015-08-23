package com.example.po.spotifystreamer;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.po.spotifystreamer.data.MusicContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG =ArtistsFragment.class.getSimpleName();

    private static final int ARTIST_LOADER_ID = 0;
    private ArtistAdapter mArtistAdapter;
    public Toast noResultsToast;

    public interface Callback{
        void onArtistSelected(String artistName);
    }
    public ArtistsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(LOG_TAG, "App in onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "App in onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "App in onCV");
        mArtistAdapter = new ArtistAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);
        EditText searchText = (EditText) rootView.findViewById(R.id.search_artist);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (HelperFunction.hasConnection(getActivity())) {
                    String searchStr = v.getText().toString();
                    if(!searchStr.isEmpty()){
                        fetchArtistResults(searchStr);
                        handled = true;
                    }
                }
                else{
                    showToast(R.string.no_network_connection_text);
                    handled = false;
                }
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//
//
//                }

                return handled;
            }
        });
//

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(mArtistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(HelperFunction.hasConnection(getActivity())){

                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if(cursor != null){
                        int inx_artist_id = cursor.getColumnIndex(MusicContract.ArtistEntry.COLUMN_ARTIST_SPOTIFY_ID);
                        String artistId = cursor.getString(inx_artist_id);
                        int inx_artist_name = cursor.getColumnIndex(MusicContract.ArtistEntry.COLUMN_ARTIST_NAME);
                        String artistName = cursor.getString(inx_artist_name);
                        String[] artistInfo = {artistId, artistName};
                        fetchTracksResults(artistInfo);

                        ((Callback) getActivity()).onArtistSelected(artistName);
                    }
                }
                else{
                    showToast(R.string.no_network_connection_text);
                }

            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        Log.d(LOG_TAG, "App in onAC");
        getLoaderManager().initLoader(ARTIST_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "App in onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "App in onResume");
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        String sortOrder = MusicContract.ArtistEntry.COLUMN_ARTIST_POPULARITY + " DESC";
        Uri artistUri = MusicContract.ArtistEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                artistUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        mArtistAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader){
        mArtistAdapter.swapCursor(null);
    }

    /**
     * This method uses an AsyncTask to query the Spotify API for an artist name search
     * @param searchString the artist to search for
     */
    public void fetchArtistResults(String searchString){
        FetchArtistTask queryArtistTask = new FetchArtistTask(getActivity());
        queryArtistTask.execute(searchString);
    }

    public void fetchTracksResults(String searchString[]){
        FetchTopTrackTask queryTopTrackTask = new FetchTopTrackTask(getActivity());
        queryTopTrackTask.execute(searchString);
//        QuerySpotifyTopTracksTask searchTopTrackTask = new QuerySpotifyTopTracksTask();
//        searchTopTrackTask.execute(searchString);
    }

    /**
     * this method shows the proper toast message depends on the context
     * @param toastMsg xml string id used to display proper message
     */
    public void showToast(int toastMsg){
        if(noResultsToast != null){
            noResultsToast.cancel();
        }
        noResultsToast = Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT);
        noResultsToast.show();
    }
}
