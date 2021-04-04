package com.savelyevlad.radiodemo.ads;

import android.media.MediaPlayer;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;

public class AdsRunner {

    private static boolean isInitialized = false;
    private static boolean isRunning = false;

    private static MainActivity mainActivity = null;
    private static SimpleExoPlayer radioPlayer = null;
    private static Thread t = null;

    public static void initialize(MainActivity mainActivity) {

        if(isInitialized) {
            return;
        }

        AdsRunner.mainActivity = mainActivity;
        isInitialized = true;

        t = new Thread(()-> {
            try {
                while (true) {
                    if(isRunning) {
                        Thread.sleep(60 * 1000); // every minute
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
            return false;
        });
        mp.setOnCompletionListener(mp1 -> {
            mp1.stop();
            // make radioPlayer louder
            louder();
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

    public SimpleExoPlayer getRadioPlayer() {
        return radioPlayer;
    }

    public static void setRadioPlayer(SimpleExoPlayer radioPlayer) {
        AdsRunner.radioPlayer = radioPlayer;
//        isInitialized = (mainActivity != null && radioPlayer != null);
    }

    private AdsRunner() { }
}
