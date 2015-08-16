package com.example.po.spotifystreamer;

import android.content.Intent;
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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.po.spotifystreamer.data.MusicContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ARTIST_LOADER_ID = 0;
    private ArtistAdapter mArtistAdapter;
    public Toast noResultsToast;

    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(ARTIST_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Create or reload the adapter to convert the array to views
//        if(savedInstanceState != null){
//            ArtistInfo[] savedStateValues = (ArtistInfo[]) savedInstanceState
//                    .getParcelableArray("artistKey");
//            if(savedStateValues != null){
//                artistListAdapter = new ArtistListAdapter(getActivity(),
//                        new ArrayList<ArtistInfo>(Arrays.asList(savedStateValues)));
//            }
//        }
//        else{
//            artistListAdapter = new ArtistListAdapter(getActivity(), new ArrayList<ArtistInfo>());
//        }

        String sortOrder = MusicContract.ArtistEntry.COLUMN_ARTIST_POPULARITY + " DESC";
        Cursor cur = getActivity().getContentResolver().query(MusicContract.ArtistEntry.CONTENT_URI, null, null, null, sortOrder);
        mArtistAdapter = new ArtistAdapter(getActivity(), cur, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        EditText searchText = (EditText) rootView.findViewById(R.id.search_artist);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (HelperFunction.hasConnection(getActivity())) {
                        String searchStr = v.getText().toString();
                        if(!searchStr.isEmpty()){
                            fetchArtistResults(searchStr);
                            handled = true;
                            return handled;
                        }
                    }
                    else{
                        showToast(R.string.no_network_connection_text);
                        handled = false;
                        return handled;
                    }

                }

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
                    Intent topTrackIntent = new Intent(getActivity(), TopTracksActivity.class);
                    Bundle infoStrings = new Bundle();
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if(cursor != null){
                        int inx_artist_id = cursor.getColumnIndex(MusicContract.ArtistEntry.COLUMN_ARTIST_SPOTIFY_ID);
                        String artistId = cursor.getString(inx_artist_id);

                        int inx_artist_name = cursor.getColumnIndex(MusicContract.ArtistEntry.COLUMN_ARTIST_NAME);
                        String artistName = cursor.getString(inx_artist_name);
                        String[] artistInfo = {artistId, artistName};

                        fetchTracksResults(artistInfo);

                        infoStrings.putString("EXTRA_ARTIST_NAME", artistName);
                        topTrackIntent.putExtras(infoStrings);
                        startActivity(topTrackIntent);
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
