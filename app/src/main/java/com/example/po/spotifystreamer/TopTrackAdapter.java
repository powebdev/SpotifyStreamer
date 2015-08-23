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

public class TopTrackAdapter extends CursorAdapter{
    public TopTrackAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.list_item_top_track, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        int idx_album_image_small = cursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_SMALL);
        int idx_album_image_large = cursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_ART_LARGE);
        int idx_track_name = cursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_TRACK_NAME);
        int idx_album_name = cursor.getColumnIndex(MusicContract.TopTrackEntry.COLUMN_ALBUM_KEY);

        ImageView imageView = (ImageView) view.findViewById(R.id.list_item_top_track_imageview);

        if(cursor.getString(idx_album_image_small).equals("default")){
            imageView.setImageResource((R.drawable.record));
            imageView.setTag(R.id.album_art_small_tag_id, "default");
            imageView.setTag(R.id.album_art_large_tag_id, "default");
        }else{
            Picasso.with(context).load(cursor.getString(idx_album_image_small)).into(imageView);
            imageView.setTag(R.id.album_art_small_tag_id, cursor.getString(idx_album_image_small));
            imageView.setTag(R.id.album_art_large_tag_id, cursor.getString(idx_album_image_large));
        }

        TextView trackNameView = (TextView) view.findViewById(R.id.list_item_top_track_name_textview);
        trackNameView.setText(cursor.getString(idx_track_name));

        TextView albumNameView = (TextView) view.findViewById(R.id.list_item_top_track_album_textview);
        albumNameView.setText(cursor.getString(idx_album_name));
    }
}
