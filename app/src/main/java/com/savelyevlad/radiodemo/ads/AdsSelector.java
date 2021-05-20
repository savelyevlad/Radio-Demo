package com.savelyevlad.radiodemo.ads;

import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;
import com.savelyevlad.radiodemo.tools.StorageUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class AdsSelector {

    private static MainActivity mainActivity;
    private static ArrayList<String> ads = new ArrayList<>();
    private static Random random = new Random();

    public static ArrayList<String> getAds() {
        return ads;
    }

    public static void setAds(ArrayList<String> ads) {
        AdsSelector.ads = ads;
    }

    public static void init(MainActivity mainActivity) {
        AdsSelector.mainActivity = mainActivity;
        for (Field field : R.raw.class.getFields()) {
            ads.add(field.getName());
        }
        // TODO: more than 3
        ArrayList<Integer> adsOrder = StorageUtil.getIntegerArrayList("ads_order");
        ads = new ArrayList<>(Arrays.asList(
                ads.get(adsOrder.get(0)),
                ads.get(adsOrder.get(1)),
                ads.get(adsOrder.get(2))
                ));
    }

    public static void swap(int i, int j) {
//        Collections.swap(ads, i, j);
        StorageUtil.putIntegerArrayList("ads_order", getOrder());
    }

    private static ArrayList<Integer> getOrder() {
        return new ArrayList<>(Arrays.asList(
                ads.get(0).charAt(ads.get(0).length() - 1) - '0' - 1,
                ads.get(1).charAt(ads.get(1).length() - 1) - '0' - 1,
                ads.get(2).charAt(ads.get(2).length() - 1) - '0' - 1
        ));
    }

    public static int select() {
        double r = random.nextDouble();
        if(r <= 0.7) {      /// 70%
            return mainActivity.getResources().getIdentifier(ads.get(0), "raw", mainActivity.getPackageName());
        }
        else if(r <= 0.9) { /// 20%
            return mainActivity.getResources().getIdentifier(ads.get(1), "raw", mainActivity.getPackageName());
        }
        /// 10%
        return mainActivity.getResources().getIdentifier(ads.get(2), "raw", mainActivity.getPackageName());
    }
}
