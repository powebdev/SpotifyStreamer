package com.example.po.spotifystreamer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.po.spotifystreamer.data.MusicContract;
import com.squareup.picasso.Picasso;

public class ArtistAdapter extends CursorAdapter {
    public ArtistAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_artist, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int idx_artist_image = cursor.getColumnIndex(MusicContract.ArtistEntry.COLUMN_ARTIST_IMAGE);
        int idx_artist_name = cursor.getColumnIndex(MusicContract.ArtistEntry.COLUMN_ARTIST_NAME);

        ImageView imageView = (ImageView) view.findViewById(R.id.list_item_artist_imageview);
        if (cursor.getString(idx_artist_image).equals("default")) {
            imageView.setImageResource(R.drawable.artist);
            imageView.setTag("default");
        } else {
            Picasso.with(context).load(cursor.getString(idx_artist_image)).into(imageView);
            imageView.setTag(cursor.getString(idx_artist_image));
        }


        TextView artistNameView = (TextView) view.findViewById(R.id.list_item_artist_textview);
        artistNameView.setText(cursor.getString(idx_artist_name));
    }
}
