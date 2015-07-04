package com.example.po.spotifystreamer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Po on 7/3/2015.
 */
public class ArtistListAdapter extends ArrayAdapter<ArtistInfo> {
    private static final String LOG_TAG = ArtistListAdapter.class.getSimpleName();

    public ArtistListAdapter(Activity context, List<ArtistInfo> artistInfos){
        super(context, 0, artistInfos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ArtistInfo artistInfo = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_artist_imageview);
        if(artistInfo.imageLink.equals("default")){
            imageView.setImageResource(R.drawable.artist);
        }
        else{
            Picasso.with(getContext()).load(artistInfo.imageLink).into(imageView);
        }


        TextView artistNameView = (TextView) convertView.findViewById(R.id.list_item_artist_textview);
        artistNameView.setText(artistInfo.name);

        return convertView;
    }

    public ArtistInfo[] getValues(){
        ArtistInfo[] tArtistInfo = new ArtistInfo[getCount()];
        for(int i = 0; i < getCount(); i++){
            tArtistInfo[i] = getItem(i);
        }
        return tArtistInfo;
    }
}
