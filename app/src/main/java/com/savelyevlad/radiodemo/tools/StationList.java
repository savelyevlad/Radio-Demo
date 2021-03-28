package com.savelyevlad.radiodemo.tools;

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
        if(nowPlayingId == -1) {
            nowPlayingId = 0;
            return stations.get(0);
        }
        return stations.get(nowPlayingId);
    }

    public static void inc() {
        ++nowPlayingId;
        if(nowPlayingId >= stations.size()) {
            nowPlayingId = 0;
        }
    }

    public static void dec() {
        --nowPlayingId;
        if(nowPlayingId <= -1) {
            nowPlayingId = stations.size() - 1;
        }
    }
}