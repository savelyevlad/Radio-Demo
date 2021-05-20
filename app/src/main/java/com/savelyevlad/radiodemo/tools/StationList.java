package com.savelyevlad.radiodemo.tools;

import com.savelyevlad.radiodemo.adapters.RadioStation;
import com.savelyevlad.radiodemo.adapters.RadioStationAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StationList {

    private static int nowPlayingId = -1;

    private static final ArrayList<String> stations = StorageUtil.getStringArrayList("station_list");

    private static final ArrayList<String> stationsNames = StorageUtil.getStringArrayList("station_list_names");

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

    private static ArrayList<RadioStation> radioStations = new ArrayList<>();

    public static void updateRadioStations() {
        radioStations.clear();
        for (int i = 0; i < stations.size(); ++i) {
            radioStations.add(new RadioStation(stationsNames.get(i), stations.get(i)));
        }
    }

    public static void removeStation(int i) {
        if (i == nowPlayingId) {
            nowPlayingId = -1;
        }
        else if(i < nowPlayingId) {
            --nowPlayingId;
        }
        stationsNames.remove(i);
        stations.remove(i);
        StorageUtil.putStringArrayList("station_list", stations);
        StorageUtil.putStringArrayList("station_list_names", stationsNames);
    }

    public static void addStation(String stationUrl, String stationName) {
        StationList.getStations().add(stationUrl);
        StationList.getStationsNames().add(stationName);
        StorageUtil.putStringArrayList("station_list", stations);
        StorageUtil.putStringArrayList("station_list_names", stationsNames);
    }

    public static ArrayList<RadioStation> getRadioStationsArrayList() {
        return radioStations;
    }

    public static List<String> getStations() {
        return stations;
    }

    public static List<String> getStationsNames() {
        return stationsNames;
    }
}
