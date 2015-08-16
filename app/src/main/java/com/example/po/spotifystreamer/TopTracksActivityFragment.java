package com.example.po.spotifystreamer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.po.spotifystreamer.data.MusicContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TOP_TRACK_LOADER_ID = 1;
    private  TopTrackAdapter mTopTrackAdapter;
    private String artistId;
    private Toast noResultsToast;

    public TopTracksActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(TOP_TRACK_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Create or reload the adapter to convert the array to views

        String sortOrder = MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY + " DESC";
        Cursor cur = getActivity().getContentResolver().query(MusicContract.TopTrackEntry.CONTENT_URI, null, null, null, sortOrder);
        mTopTrackAdapter = new TopTrackAdapter(getActivity(), cur, 0);

        View  rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
//        Intent intent = getActivity().getIntent();
//        if(intent != null && intent.hasExtra("EXTRA_ARTIST_ID")){
//            Bundle extraInfos = intent.getExtras();
//            artistId = extraInfos.getString("EXTRA_ARTIST_ID");
//            fetchTracksResults(artistId);
//        }
//        if(savedInstanceState != null){
//            TopTrackInfo[] savedStateValues = (TopTrackInfo[]) savedInstanceState.getParcelableArray("topTrackKey");
//            if(savedStateValues != null){
//                topTrackListAdapter = new TopTrackListAdapter(getActivity(), new ArrayList<TopTrackInfo>(Arrays.asList(savedStateValues)));
//            }
//        }
//        else{
//            topTrackListAdapter = new TopTrackListAdapter(getActivity(), new ArrayList<TopTrackInfo>());
//            Intent intent = getActivity().getIntent();
//            if(intent != null && intent.hasExtra("EXTRA_ARTIST_ID")){
//                Bundle extraInfos = intent.getExtras();
//                artistId = extraInfos.getString("EXTRA_ARTIST_ID");
//                fetchTracksResults(artistId);
//            }
//        }
        ListView listView = (ListView) rootView.findViewById(R.id.listview_top_track);
        listView.setAdapter(mTopTrackAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(HelperFunction.hasConnection(getActivity())){
                    Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);
                    Bundle playerInfoStrings = new Bundle();
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    if(cursor != null){
//                        int inx_track_table_id = cursor.getColumnIndex(MusicContract.TopTrackEntry._ID);
//                        int _trackId = cursor.getInt(inx_track_table_id);
//                        Uri trackUri = ContentUris.withAppendedId(MusicContract.TopTrackEntry.CONTENT_URI, _trackId);
//                        playerInfoStrings.putString("EXTRA_TRACK_URI", trackUri.toString());
                        playerInfoStrings.putInt("EXTRA_TRACK_POSITION", position);
                        playerIntent.putExtras(playerInfoStrings);
                        startActivity(playerIntent);
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        String sortOrder = MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY + " DESC";
        Uri topTrackUri = MusicContract.TopTrackEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                topTrackUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        mTopTrackAdapter.swapCursor(cursor);
    }

    @Override
    public  void onLoaderReset(Loader<Cursor> cursorLoader){
        mTopTrackAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        super.onSaveInstanceState(savedState);

//        TopTrackInfo[] stateValuesToSave = topTrackListAdapter.getValues();
//        savedState.putParcelableArray("topTrackKey", stateValuesToSave);

    }

    /**
     * This method uses an AsyncTask to query the Spotify API for an artist top tracks search
     * @param searchString the artist to search for
     */
//    public void fetchTracksResults(String searchString){
//        FetchTopTrackTask queryTask = new FetchTopTrackTask(getActivity());
//        queryTask.execute(searchString);
////        QuerySpotifyTopTracksTask searchTopTrackTask = new QuerySpotifyTopTracksTask();
////        searchTopTrackTask.execute(searchString);
//    }

    /**
     * This method find the url of the image with proper size, in this case with height of 640 px
     * @param track the track to search the image for
     * @return an integer which indicates the array position of the found image
     */
//    public int findProperImage(Track track){
//        int foundImage;
//        if(track.album.images.size() == 0){
//            foundImage = -1;
//        }
//        else if(track.album.images.size() == 1){
//            foundImage = 0;
//        }
//        else{
//            int foundSixForty = 0;
//            while(foundSixForty < track.album.images.size()){
//                if(track.album.images.get(foundSixForty).height == 640){
//                    return foundSixForty;
//                }
//                foundSixForty++;
//            }
//            foundImage = foundSixForty - 1;
//        }
//        return foundImage;
//    }
}
