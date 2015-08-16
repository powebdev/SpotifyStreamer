package com.example.po.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Po on 8/13/2015.
 */
public class HelperFunction {

    public static int findProperImage(Artist artist) {
        int foundImage;
        if (artist.images.size() == 0) {
            foundImage = -1;
        } else if (artist.images.size() == 1) {
            foundImage = 0;
        } else {
            int foundTwoHundred = 0;
            while (foundTwoHundred < artist.images.size()) {
                if (artist.images.get(foundTwoHundred).height == 200) {
                    return foundTwoHundred;
                }
                foundTwoHundred++;
            }
            foundImage = foundTwoHundred - 1;
        }
        return foundImage;
    }

    public static boolean hasConnection(Activity activity){
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(
                Context.CONNECTIVITY_SERVICE);


        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean hasConnection = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return hasConnection;
    }
}
