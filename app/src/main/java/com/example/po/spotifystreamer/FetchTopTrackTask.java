package com.example.po.spotifystreamer;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.po.spotifystreamer.data.MusicContract;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Po on 8/13/2015.
 */
public class FetchTopTrackTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchTopTrackTask.class.getSimpleName();
    private final Context mContext;

    public FetchTopTrackTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... artistInfo) {
        if (artistInfo.length == 0) {
            return null;
        }

        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        Map availableMarket = new HashMap();
        availableMarket.put("country", "US");
        Tracks topTracks = spotifyService.getArtistTopTrack(artistInfo[0], availableMarket);

        if (topTracks != null && topTracks.tracks.size() > 0) {
            Vector<ContentValues> cVVector = new Vector<ContentValues>(topTracks.tracks.size());

            for (int i = 0; i < topTracks.tracks.size(); i++) {
                Track track = topTracks.tracks.get(i);
                ContentValues trackValues = new ContentValues();
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_TRACK_NAME, track.name);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_TRACK_SPOTIFY_ID, track.id);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_TRACK_PREVIEW_URL, track.preview_url);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY, track.popularity);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_ARTIST_KEY, artistInfo[1]);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_ALBUM_KEY, track.album.name);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_LARGE, "default");
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_SMALL, "default");

                cVVector.add(trackValues);

                int inserted = 0;
                if(cVVector.size() > 0){
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    int deleted = mContext.getContentResolver().delete(MusicContract.TopTrackEntry.CONTENT_URI, null, null);
                    inserted = mContext.getContentResolver().bulkInsert(MusicContract.TopTrackEntry.CONTENT_URI, cvArray);
                }

                Log.d(LOG_TAG, "FetchTopTrackTask Complete. " + inserted + " Inserted");
//                int imagePos = findProperImage(track);
//                if (imagePos == -1) {
//                    topTrackInfos.add(new TopTrackInfo(track.name, artist.name, track.album.name, "default"));
//                } else {
//                    topTrackInfos.add(new TopTrackInfo(track.name, artist.name, track.album.name, track.album.images.get(0).url));
//                }
            }
        }
        return null;

    }
}
