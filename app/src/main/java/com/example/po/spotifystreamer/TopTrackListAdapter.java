package com.example.po.spotifystreamer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Po on 7/5/2015.
 */
public class TopTrackListAdapter extends ArrayAdapter<TopTrackInfo> {

    public TopTrackListAdapter(Activity context, List<TopTrackInfo> topTrackInfos){
        super(context, 0, topTrackInfos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        TopTrackInfo topTrackInfo = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_top_track, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_top_track_imageview);
        imageView.setImageResource(R.drawable.record);

        TextView trackNameView = (TextView) convertView.findViewById(R.id.list_item_top_track_name_textview);
        trackNameView.setText(topTrackInfo.name);

        TextView albumNameView = (TextView) convertView.findViewById(R.id.list_item_top_track_album_textview);
        albumNameView.setText(topTrackInfo.album);

        return convertView;
    }

    public TopTrackInfo[] getValues(){
        TopTrackInfo[] tTopTrackInfo = new TopTrackInfo[getCount()];
        for(int i = 0; i < getCount(); i++){
            tTopTrackInfo[i] = getItem(i);
        }
        return tTopTrackInfo;
    }
}

