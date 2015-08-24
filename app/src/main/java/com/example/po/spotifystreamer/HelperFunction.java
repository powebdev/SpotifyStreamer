package com.example.po.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Collection of various methods used in the app.
 */
public class HelperFunction {

    public static String findProperImage(List<Image> imageList, int sizeWanted, boolean atLeastOrMost) {
        String foundURL = "default";
        int foundImageSize;

        if (imageList.size() == 0) {
            Log.d("in findImage Method", "no image available");
            foundURL = "default";
        } else if (imageList.size() == 1) {
            Log.d("in findImage Method", "only one image");
            foundURL = imageList.get(0).url;
        } else {
            List<Integer> imageSizeList = new ArrayList<>();
            for (Image imageItem : imageList) {
                imageSizeList.add(imageItem.height);
            }
            Collections.sort(imageSizeList);
            int foundImagePosition = 0;
            boolean imageFound = false;
            if (atLeastOrMost) {
                Collections.reverse(imageSizeList);
                while (foundImagePosition < imageList.size() && !imageFound) {
                    if (sizeWanted < imageSizeList.get(foundImagePosition)) {
                        foundImagePosition++;
                    } else {
                        foundImagePosition--;
                        imageFound = true;
                    }
                }
            } else {
                while (foundImagePosition < imageList.size() && !imageFound) {
                    if (sizeWanted > imageSizeList.get(foundImagePosition)) {
                        foundImagePosition++;
                    } else {
                        foundImagePosition--;
                        imageFound = true;
                    }
                }
            }
            if (!imageFound || foundImagePosition < 0) {
                foundImageSize = imageSizeList.get(0);
            } else {
                foundImageSize = imageSizeList.get(foundImagePosition);
            }
            for (Image imageItem : imageList) {
                if (imageItem.height == foundImageSize) {
                    foundURL = imageItem.url;
                }
            }
        }
        return foundURL;
    }

    public static boolean hasConnection(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String timeFormatter(int mSec) {
        long second = (mSec / 1000) % 60;
        long minute = mSec / (1000 * 60);
        return String.format("%d:%02d", minute, second);
    }

    public static Intent createShareForecastIntent(String trackExtUrl) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out what I am listening to right now: " + trackExtUrl);
        return shareIntent;
    }
}
