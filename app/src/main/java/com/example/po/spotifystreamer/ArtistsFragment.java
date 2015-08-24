package com.example.po.spotifystreamer;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

    private static final int ARTIST_LOADER_ID = 0;
    private ArtistAdapter mArtistAdapter;
    public Toast noResultsToast;
    private int mPosition = ListView.INVALID_POSITION;
    private ListView mListView;

    public interface Callback {
        void onArtistSelected(String artistName);
    }

    public ArtistsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mArtistAdapter = new ArtistAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);
        EditText searchText = (EditText) rootView.findViewById(R.id.search_artist);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (HelperFunction.hasConnection(getActivity())) {
                    String searchStr = v.getText().toString();
                    if (!searchStr.isEmpty()) {
                        fetchArtistResults(searchStr);
                        handled = true;
                    }
                } else {
                    showToast(R.string.no_network_connection_text);
                    handled = false;
                }
                return handled;
            }
        });

        mListView = (ListView) rootView.findViewById(R.id.listview_artist);
        mListView.setAdapter(mArtistAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                view.setSelected(true);
                if (HelperFunction.hasConnection(getActivity())) {

                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        int inx_artist_id = cursor.getColumnIndex(MusicContract.ArtistEntry.COLUMN_ARTIST_SPOTIFY_ID);
                        String artistId = cursor.getString(inx_artist_id);
                        int inx_artist_name = cursor.getColumnIndex(MusicContract.ArtistEntry.COLUMN_ARTIST_NAME);
                        String artistName = cursor.getString(inx_artist_name);
                        String[] artistInfo = {artistId, artistName};
                        fetchTracksResults(artistInfo);

                        ((Callback) getActivity()).onArtistSelected(artistName);
                    }
                } else {
                    showToast(R.string.no_network_connection_text);
                }
                mPosition = position;

            }
        });
        if(savedInstanceState != null && savedInstanceState.containsKey("SELECTED_KEY")){
            mPosition = savedInstanceState.getInt("SELECTED_KEY");
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ARTIST_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION){
            outState.putInt("SELECTED_KEY", mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mArtistAdapter.swapCursor(cursor);
        if(mPosition !=ListView.INVALID_POSITION){
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mArtistAdapter.swapCursor(null);
    }

    /**
     * This method uses an AsyncTask to query the Spotify API for an artist name search
     *
     * @param searchString the artist to search for
     */
    public void fetchArtistResults(String searchString) {
        FetchArtistTask queryArtistTask = new FetchArtistTask(getActivity());
        queryArtistTask.execute(searchString);
        try {
            if (!queryArtistTask.get()) {
                showToast(R.string.no_artists_result_text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchTracksResults(String searchString[]) {
        FetchTopTrackTask queryTopTrackTask = new FetchTopTrackTask(getActivity());
        queryTopTrackTask.execute(searchString);
        try {
            if (!queryTopTrackTask.get()) {
                showToast(R.string.no_tracks_result_text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this method shows the proper toast message depends on the context
     *
     * @param toastMsg xml string id used to display proper message
     */
    public void showToast(int toastMsg) {
        if (noResultsToast != null) {
            noResultsToast.cancel();
        }
        noResultsToast = Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT);
        noResultsToast.show();
    }
}
