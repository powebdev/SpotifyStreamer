package com.example.po.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Collection of various methods used in the app.
 */
public class HelperFunction {

    /**
     * this method search for image with the desired size
     *
     * @param imageList     list of Image objects to search through
     * @param sizeWanted    the size of the image to search for
     * @param atLeastOrMost whether the image should be at least(true) sizeWanted or at most(false)
     * @return              the URL for the image with the desired size, return String "default" if no image is found
     */
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

    /**
     * this method indicates whether network connectivity exists
     *
     * @param context   The context to use. Usually your Application or Activity object.
     * @return          true if network connectivity exists, false otherwise.
     */
    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * this method formats and returns the time user provided
     *
     * @param mSec  time needed to be formatted, in milliseconds
     * @return      String representing time in the format of mm:ss
     */
    public static String timeFormatter(int mSec) {
        long second = (mSec / 1000) % 60;
        long minute = mSec / (1000 * 60);
        return String.format("%d:%02d", minute, second);
    }

    /**
     * this method returns an intent to be used for shareIntent
     *
     * @param trackExtUrl   the URL links to the shared music web page
     * @return              return the intent to be used for ShareIntent
     */
    public static Intent createShareMusicIntent(String trackExtUrl) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out what I am listening to right now: " + trackExtUrl);
        return shareIntent;
    }

    /**
     * this method shows the proper toast message depends on the context
     *
     * @param toastMsg  xml string id used to display proper message
     * @param context   The context to use. Usually your Application or Activity object.
     * @return          a toast to be shown
     */
    public static Toast showToast(int toastMsg, Context context) {
        return Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT);
    }
}
