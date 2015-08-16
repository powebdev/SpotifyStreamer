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
 * Created by Po on 8/14/2015.
 */
public class TopTrackAdapter extends CursorAdapter{
    public TopTrackAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_top_track, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        ImageView imageView = (ImageView) view.findViewById(R.id.list_item_top_track_imageview);
        imageView.setImageResource((R.drawable.record));

        TextView trackNameView = (TextView) view.findViewById(R.id.list_item_top_track_name_textview);
        int inx_track_name = cursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_NAME);
        trackNameView.setText(cursor.getString(inx_track_name));

        TextView albumNameView = (TextView) view.findViewById(R.id.list_item_top_track_album_textview);
        int inx_album_name = cursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_KEY);
        albumNameView.setText(cursor.getString(inx_album_name));
    }
}
