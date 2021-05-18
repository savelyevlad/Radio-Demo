package com.savelyevlad.radiodemo.ads;

import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
