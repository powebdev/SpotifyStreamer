package com.example.po.spotifystreamer;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.po.spotifystreamer.data.MusicContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class FetchTopTrackTask extends AsyncTask<String, Void, Boolean> {
    private final Context mContext;

    public FetchTopTrackTask(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... artistInfo) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String countryCode = sharedPreferences.getString(mContext.getString(R.string.pref_country_codes_key), mContext.getString(R.string.pref_country_codes_us));
        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        Map availableMarket = new HashMap();
        availableMarket.put("country", countryCode);
        Tracks topTracks = spotifyService.getArtistTopTrack(artistInfo[0], availableMarket);
        mContext.getContentResolver().delete(MusicContract.TopTrackEntry.CONTENT_URI, null, null);

        if (topTracks != null && topTracks.tracks.size() > 0) {
            Vector<ContentValues> cVVector = new Vector<>(topTracks.tracks.size());
            for (int i = 0; i < topTracks.tracks.size(); i++) {
                Track track = topTracks.tracks.get(i);
                List<Image> imagesToSearch = track.album.images;
                ContentValues trackValues = new ContentValues();
                String[] tempStrs = track.external_urls.values().toArray(new String[0]);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_TRACK_NAME, track.name);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_TRACK_SPOTIFY_ID, track.id);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_TRACK_PREVIEW_URL, track.preview_url);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_TRACK_POPULARITY, track.popularity);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_TRACK_EXTERNAL_URL, tempStrs[0]);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_TRACK_DURATION, 30000);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_ARTIST_KEY, artistInfo[1]);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_ALBUM_KEY, track.album.name);
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_LARGE, HelperFunction.findProperImage(imagesToSearch, 500, true));
                trackValues.put(MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_SMALL, HelperFunction.findProperImage(imagesToSearch, 500, false));

                cVVector.add(trackValues);

                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    mContext.getContentResolver().bulkInsert(MusicContract.TopTrackEntry.CONTENT_URI, cvArray);
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
