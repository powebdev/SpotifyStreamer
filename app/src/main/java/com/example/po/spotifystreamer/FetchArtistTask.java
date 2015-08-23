package com.example.po.spotifystreamer;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.po.spotifystreamer.data.MusicContract;

import java.util.List;
import java.util.Vector;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;

public class FetchArtistTask extends AsyncTask<String, Void, Boolean> {

    private final String LOG_TAG = FetchArtistTask.class.getSimpleName();
    private final Context mContext;

    public FetchArtistTask(Context context) {
        mContext = context;
    }

    private boolean DEBUG = true;

    @Override
    protected Boolean doInBackground(String... artistName) {

        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        ArtistsPager artistSearchResults = spotifyService.searchArtists(artistName[0]);
        Log.d(LOG_TAG, "search resutls is null: " + (artistSearchResults == null));
        Log.d(LOG_TAG, "search resutls number is: " + artistSearchResults.artists.items.size());
        mContext.getContentResolver().delete(MusicContract.ArtistEntry.CONTENT_URI, null, null);

        if (artistSearchResults != null && artistSearchResults.artists.items.size() > 0) {
            Vector<ContentValues> cVVector = new Vector<>(artistSearchResults.artists.items.size());
            for (int i = 0; i < artistSearchResults.artists.items.size(); i++) {
                Artist artist = artistSearchResults.artists.items.get(i);
                List<Image> imagesToSearch = artist.images;
                ContentValues artistValues = new ContentValues();
                artistValues.put(MusicContract.ArtistEntry.COLUMN_ARTIST_NAME, artist.name);
                artistValues.put(MusicContract.ArtistEntry.COLUMN_ARTIST_SPOTIFY_ID, artist.id);
                artistValues.put(MusicContract.ArtistEntry.COLUMN_ARTIST_IMAGE, HelperFunction.findProperImage(imagesToSearch, 200, false));
                artistValues.put(MusicContract.ArtistEntry.COLUMN_ARTIST_POPULARITY, artist.popularity);

                cVVector.add(artistValues);

                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    mContext.getContentResolver().bulkInsert(MusicContract.ArtistEntry.CONTENT_URI, cvArray);
                }
            }
            return true;
        }else{
            return false;
        }

    }

    /**
     * This method find the url of the image with proper size, in this case with height of 200 px
     *
     * @param artist the artist whose image to search for
     * @return an integer which indicates the array position of the found image
     */

}
