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

/**
 * Created by Po on 8/11/2015.
 */
public class ArtistAdapter extends CursorAdapter{
    public ArtistAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_artist, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){

        ImageView imageView = (ImageView) view.findViewById(R.id.list_item_artist_imageview);
        imageView.setImageResource(R.drawable.artist);

        TextView artistNameView = (TextView) view.findViewById(R.id.list_item_artist_textview);
        int inx_artist_name = cursor.getColumnIndex(MusicContract.ArtistEntry.COLUMN_ARTIST_NAME);
        artistNameView.setText(cursor.getString(inx_artist_name));
    }
}
