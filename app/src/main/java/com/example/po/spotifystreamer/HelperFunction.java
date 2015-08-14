package com.example.po.spotifystreamer;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Po on 8/13/2015.
 */
public class HelperFunction {

    public int findProperImage(Artist artist) {
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
}
