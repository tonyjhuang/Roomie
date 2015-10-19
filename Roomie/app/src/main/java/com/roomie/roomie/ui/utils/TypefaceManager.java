package com.roomie.roomie.ui.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Hashtable;

/**
 * Created by tonyjhuang on 10/18/15.
 */
public class TypefaceManager {
    private static final Hashtable<String, Typeface> cache = new Hashtable<>();

    /**
     * Returns a default typeface if the passed in typeface path is not found
     */
    public static Typeface get(Context c, String assetPath) {
        if (assetPath == null || assetPath.equals("null") || assetPath.equals("null.ttf")) {
            return null;
        }
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),
                            assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    Log.e("TYPEFACEMANAGER", "Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }
}
