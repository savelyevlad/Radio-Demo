package com.savelyevlad.radiodemo.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class StorageUtil {

    private static final String STORAGE = " com.savelyevlad.musicplayer.STORAGE";
    private static SharedPreferences preferences;
    private static Gson gson = new Gson();

    public static void init(Context context) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        if(getInt("ads_time") == -1) {
            putInt("ads_time", 60);
        }
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key) {
        return preferences.getInt(key, -1); //return -1 if no data found
    }

    private StorageUtil() { }
}
