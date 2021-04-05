package com.savelyevlad.radiodemo.tools;

import com.savelyevlad.radiodemo.adapters.RadioStation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StationList {

    private static int nowPlayingId = -1;

    private static final List<String> stations = Arrays.asList(
            "http://us4.internet-radio.com:8258/",
            "http://us4.internet-radio.com:8266/",
            "http://us5.internet-radio.com:8267/",
            "http://aska.ru-hoster.com:8053/autodj",
            "http://64.20.39.8:8421/stream",
            "http://uk1.internet-radio.com:8004/",
            "http://198.178.123.17:10922/stream"
    );

    private static final List<String> stationsNames = Arrays.asList(
            "Classic Rock Florida HD",
            "Smooth Jazz Florida",
            "Classic Rock Radio HD",
            "LIFE CHILL MUSIC",
            "LO FLY Radio",
            "Pink Noise Radio",
            "San Franciscos 70s HITS!"
    );

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
        if (nowPlayingId == -1) {
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

    public static ArrayList<RadioStation> getRadioStationsArrayList() {
        ArrayList<RadioStation> res = new ArrayList<>();
        for (int i = 0; i < stations.size(); ++i) {
            res.add(new RadioStation(stationsNames.get(i), stations.get(i)));
        }
        return res;
    }
}
