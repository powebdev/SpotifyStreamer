package com.example.po.spotifystreamer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MusicContract {

    public static final String CONTENT_AUTHORITY = "com.example.po.spotifystreamer";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ARTIST = "artist";
    public static final String PATH_TOP_TRACK = "top_track";

    public static final class ArtistEntry implements BaseColumns {

        public static final String TABLE_NAME = "artist";

        public static final String COLUMN_ARTIST_NAME = "artist_name";
        public static final String COLUMN_ARTIST_SPOTIFY_ID = "artist_spotify_id";
        public static final String COLUMN_ARTIST_IMAGE = "artist_image";
        public static final String COLUMN_ARTIST_POPULARITY = "artist_popularity";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        public static Uri buildArtistUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TopTrackEntry implements BaseColumns {

        public static final String TABLE_NAME = "top_track";

        public static final String COLUMN_TRACK_NAME = "track_name";
        public static final String COLUMN_TRACK_SPOTIFY_ID = "track_spotify_id";
        public static final String COLUMN_TRACK_PREVIEW_URL = "track_url";
        public static final String COLUMN_TRACK_POPULARITY = "track_popularity";
        public static final String COLUMN_TRACK_EXTERNAL_URL = "track_ext_url";
        public static final String COLUMN_TRACK_DURATION = "track_duration";
        public static final String COLUMN_ARTIST_KEY = "artist_id";
        public static final String COLUMN_ALBUM_KEY = "album_id";
        public static final String COLUMN_ALBUM_ART_LARGE = "album_art_large";
        public static final String COLUMN_ALBUM_ART_SMALL = "album_art_small";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_TRACK).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_TRACK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_TRACK;

        public static Uri buildTopTrackUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
