package com.example.po.spotifystreamer;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.po.spotifystreamer.data.MusicContract;

import java.util.Vector;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Po on 8/11/2015.
 */
public class FetchResultTask extends AsyncTask<String, Void, Void>{

    private final String LOG_TAG = FetchResultTask.class.getSimpleName();
    private final Context mContext;

    public FetchResultTask(Context context){
        mContext = context;
    }

    private boolean DEBUG = true;

    @Override
    protected Void doInBackground(String... artistName){

            if(artistName.length == 0){
                return null;
            }
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            ArtistsPager artistSearchResults = spotifyService.searchArtists(artistName[0]);
            if (artistSearchResults != null && artistSearchResults.artists.items.size() > 0) {
                Vector<ContentValues> cVVector = new Vector<ContentValues>(artistSearchResults.artists.items.size());
//                ArrayList<ArtistInfo> artistInfos = new ArrayList<>();
                for (int i = 0; i < artistSearchResults.artists.items.size(); i++) {
                    Artist artist = artistSearchResults.artists.items.get(i);
                    ContentValues artistValues = new ContentValues();
                    artistValues.put(MusicContract.ArtistEntry.COLUMN_ARTIST_NAME, artist.name);
                    artistValues.put(MusicContract.ArtistEntry.COLUMN_ARTIST_SPOTIFY_ID, artist.id);
                    artistValues.put(MusicContract.ArtistEntry.COLUMN_ARTIST_IMAGE, "default");

                    cVVector.add(artistValues);

                    int inserted = 0;
                    if(cVVector.size() > 0){
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        inserted = mContext.getContentResolver().bulkInsert(MusicContract.ArtistEntry.CONTENT_URI, cvArray);
                    }

                    Log.d(LOG_TAG, "FetchResultTask Complete. " + inserted + " Inserted");

//                    int imagePos = findProperImage(artist);
//                    if (imagePos == -1) {
//                        artistInfos.add(new ArtistInfo(artist.name, artist.id, "default"));
//                    } else {
//                        artistInfos.add(new ArtistInfo(artist.name, artist.id, artist.images
//                                .get(imagePos).url));
//                    }
                }
            }
            return null;
    }

}
