package com.example.po.spotifystreamer;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.po.spotifystreamer.data.MusicContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int TOP_TRACK_LOADER_ID = 1;
    private TopTrackAdapter mTopTrackAdapter;
    private ShareActionProvider mShareActionProvider;
    private String mExtUrl;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    public Toast mShowToast;

    public interface Callback {
        void onTopTrackSelected(int trackPosition, String artistName, String albumName, String trackName, String trackExtUrl, String albumArtSmall, String albumArtLarge, int trackDuration);
    }

    public TopTracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.twopanes_layout)) {
            setHasOptionsMenu(true);
        } else {
            setHasOptionsMenu(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String sortOrder = MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY + " DESC";
        Cursor cur = getActivity().getContentResolver().query(MusicContract.TopTrackEntry.CONTENT_URI, null, null, null, sortOrder);
        mTopTrackAdapter = new TopTrackAdapter(getActivity(), cur, 0);

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_top_track);
        mListView.setAdapter(mTopTrackAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                if (HelperFunction.hasConnection(getActivity())) {

                    String artistName = (String) (view.findViewById(R.id.list_item_top_track_album_textview)).getTag(R.id.artist_name_id);
                    String albumName = (String) ((TextView) view.findViewById(R.id.list_item_top_track_album_textview)).getText();
                    String trackName = (String) ((TextView) view.findViewById(R.id.list_item_top_track_name_textview)).getText();
                    mExtUrl = (String) (view.findViewById(R.id.list_item_top_track_name_textview)).getTag(R.id.track_external_url_id);
                    int trackDuration = Integer.parseInt((String) (view.findViewById(R.id.list_item_top_track_name_textview)).getTag(R.id.track_duration_id));

                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(HelperFunction.createShareMusicIntent(mExtUrl));
                    }

                    String albumArtSmall = (String) (view.findViewById(R.id.list_item_top_track_imageview)).getTag(R.id.album_art_small_tag_id);
                    String albumArtLarge = (String) (view.findViewById(R.id.list_item_top_track_imageview)).getTag(R.id.album_art_large_tag_id);

                    ((Callback) getActivity()).onTopTrackSelected(position, artistName, albumName, trackName, mExtUrl, albumArtSmall, albumArtLarge, trackDuration);
                }else{
                    if(mShowToast != null){
                        mShowToast.cancel();
                    }
                    mShowToast = HelperFunction.showToast(R.string.no_network_connection_text, getActivity());
                    mShowToast.show();
                }
                mPosition = position;
            }
        });
        if(savedInstanceState != null && savedInstanceState.containsKey("SELECTED_KEY")) {
            mPosition = savedInstanceState.getInt("SELECTED_KEY");
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TOP_TRACK_LOADER_ID, null, this);
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
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mTopTrackAdapter.swapCursor(cursor);
        if(mPosition !=ListView.INVALID_POSITION){
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mTopTrackAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_top_track_fragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mExtUrl != null) {
            mShareActionProvider.setShareIntent(HelperFunction.createShareMusicIntent(mExtUrl));
        }
    }

    public ShareActionProvider getShareActionProvider() {
        return mShareActionProvider;
    }
}
