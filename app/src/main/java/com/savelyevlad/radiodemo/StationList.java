package com.savelyevlad.radiodemo;

import java.util.Arrays;
import java.util.List;

public class StationList {

    private static int nowPlayingId = -1;

    private static final List<String> stations = Arrays.asList(
//            "android.resource://com.savelyevlad.radiodemo/raw/ads1.mp3",
            "http://198.58.98.83:8258/stream",
            "http://198.58.98.83:8258/stream",
            "http://aska.ru-hoster.com:8053/autodj");

    public static String get(int id) {
        return stations.get(id);
    }

    public static int getNowPlayingId() {
        return nowPlayingId;
    }

    public static void setNowPlayingId(int nowPlayingId) {
        StationList.nowPlayingId = nowPlayingId;
    }

    public static String getPlayingStation() {
        return stations.get(nowPlayingId);
    }
}
