package com.example.po.spotifystreamer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Po on 8/2/2015.
 */
public class MusicContract {

    public static final String CONTENT_AUTHORITY = "com.example.po.spotifystreamer";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ARTIST = "artist";

    public static final class ArtistEntry implements BaseColumns{

        public static final String TABLE_NAME = "artist";

        public static final String COLUMN_ARTIST_NAME = "artist_name";
        public static final String COLUMN_ARTIST_SPOTIFY_ID = "artist_spotify_id";
        public static final String COLUMN_ARTIST_IMAGE = "artist_image";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        public static Uri buildArtistUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class AlbumEntry implements BaseColumns{

        public static final String TABLE_NAME = "album";

        public static final String COLUMN_ALBUM_NAME = "album_name";
        public static final String COLUMN_ALBUM_SPOTIFY_ID = "album_spotify_id";
        public static final String COLUMN_ALBUM_THUMBNAIL = "album_thumbnail";
        public static final String COLUMN_ALBUM_IMAGE = "album_image";
    }
    public static final class TrackEntry implements BaseColumns{

        public static final String TABLE_NAME = "track";

        public static final String COLUMN_TRACK_NAME = "track_name";
        public static final String COLUMN_TRACK_SPOTIFY_ID = "track_spotify_id";
        public static final String COLUMN_TRACK_PREVIEW_URL = "track_url";
        public static final String COLUMN_ARTIST_KEY = "artist_id";
        public static final String COLUMN_ALBUM_KEY = "album_id";
    }
}
