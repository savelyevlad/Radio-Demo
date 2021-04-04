package com.savelyevlad.radiodemo.ads;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;

public class AdsRunner {

    private static final int SECONDS_BETWEEN_ADS = 60;
    private static int secondsToAds;

    private static boolean isInitialized = false;
    private static boolean isRunning = false;
    private static boolean adsIsPlaying = false;

    private static MainActivity mainActivity = null;
    private static SimpleExoPlayer radioPlayer = null;
    @SuppressLint("StaticFieldLeak")
    private static View fragmentMain = null;
    private static Thread t = null;

    @SuppressLint("SetTextI18n")
    public static void initialize(MainActivity mainActivity) {

        if(isInitialized) {
            return;
        }

        AdsRunner.mainActivity = mainActivity;
        isInitialized = true;

        t = new Thread(()-> {
            try {
                while (true) {
                    mainActivity.runOnUiThread(() -> {
                        TextView textView = fragmentMain.findViewById(R.id.textFmRadioInfo);
                        textView.setText(adsIsPlaying + "");
                    });
                    if(isRunning && !adsIsPlaying) {
                        for(int secondsToAds = SECONDS_BETWEEN_ADS; secondsToAds > 0; --secondsToAds) { // every minute
                            Thread.sleep(1000);
                            int finalI = secondsToAds;
                            mainActivity.runOnUiThread(() -> {
                               TextView textView = fragmentMain.findViewById(R.id.text_ads_time);
                               textView.setText(finalI + "");
                            });
                        }
                        // TODO: not working for some reason
//                        adsIsPlaying = true;
                        mainActivity.runOnUiThread(() -> {
                            playAds();
                        });
                    }
                }
            } catch (InterruptedException ignored) {
            }
        });
        t.start();
    }

    public static void start() {
        if (!isInitialized) {
            throw new AdsNotInitializedException();
        }
        isRunning = true;
    }

    public static void stop() {
        isRunning = false;
    }

    public static void playAds() {
        // make radioPlayer quieter
        quieter();
        // play ads
        MediaPlayer mp = MediaPlayer.create(mainActivity, R.raw.ads1);
        mp.setOnErrorListener((mp1, what, extra) -> {
            Toast.makeText(mainActivity.getApplicationContext(), "ads error", Toast.LENGTH_LONG).show();
            mp1.stop();
            louder();
            adsIsPlaying = false;
            return false;
        });
        mp.setOnCompletionListener(mp1 -> {
            mp1.stop();
            // make radioPlayer louder
            louder();
            adsIsPlaying = false;
        });
        mp.start();
    }

    private static void quieter() {
        while (radioPlayer.getVolume() > 0.1f) {
            radioPlayer.setVolume(radioPlayer.getVolume() - 0.1f);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void louder() {
        while (radioPlayer.getVolume() < 1) {
            radioPlayer.setVolume(radioPlayer.getVolume() + 0.1f);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isIsInitialized() {
        return isInitialized;
    }

    public static boolean isIsRunning() {
        return isRunning;
    }

    public static void setRadioPlayer(SimpleExoPlayer radioPlayer) {
        AdsRunner.radioPlayer = radioPlayer;
//        isInitialized = (mainActivity != null && radioPlayer != null);
    }

    public static void setFragmentMain(View fragmentMain) {
        AdsRunner.fragmentMain = fragmentMain;
    }

    private AdsRunner() { }
}
