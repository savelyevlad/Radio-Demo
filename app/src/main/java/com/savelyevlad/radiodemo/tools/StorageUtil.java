package com.savelyevlad.radiodemo.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class StorageUtil {

    private static final String STORAGE = " com.savelyevlad.musicplayer.STORAGE";
    private static SharedPreferences preferences;
    private static Gson gson = new Gson();

    public static void init(Context context) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        if(getInt("ads_time") == -1) {
            putInt("ads_time", 60);
        }

        if(getIntegerArrayList("ads_order") == null) {
            Integer[] kek = new Integer[] {0, 1, 2};
            ArrayList<Integer> adsOrder = new ArrayList<>(Arrays.asList(kek));
            putIntegerArrayList("ads_order",  adsOrder);
        }
    }

    public static void putIntegerArrayList(String key, ArrayList<Integer> list) {
        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public static ArrayList<Integer> getIntegerArrayList(String key) {
        String json = preferences.getString(key, null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        return gson.fromJson(json, type);
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
