package com.savelyevlad.radiodemo.ads;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;
import com.savelyevlad.radiodemo.tools.StorageUtil;

public class AdsRunner {

    private static int SECONDS_BETWEEN_ADS = StorageUtil.getInt("ads_time");
    private static int secondsToAds = SECONDS_BETWEEN_ADS;

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

        new CountDownTimer(1000_000_000_000_000_000L, 1000) {

            boolean firstRun = true;

            @Override
            public void onTick(long millisUntilFinished) {

                if(firstRun) {
                    firstRun = false;
                    return;
                }

                TextView textView = fragmentMain.findViewById(R.id.text_ads_time);
                textView.setText(minutes(secondsToAds) + ":" + seconds(secondsToAds % 60));

                if(adsIsPlaying || !isRunning) {
                    return;
                }

                if(--secondsToAds > 0) {
                    return;
                }
                secondsToAds = SECONDS_BETWEEN_ADS;
                adsIsPlaying = true;
                playAds();
            }

            @Override
            public void onFinish() {
                //
            }
        }.start();
    }

    private static String minutes(int seconds) {
        if(seconds / 60 < 10) {
            return "0" + seconds / 60;
        }
        return seconds / 60 + "";
    }

    private static String seconds(int seconds) {
        if(seconds % 60 < 10) {
            return "0" + seconds % 60;
        }
        return seconds % 60 + "";
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
        MediaPlayer mp = MediaPlayer.create(mainActivity, AdsSelector.select());
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

    public static void setSecondsBetweenAds(int sec) {
        SECONDS_BETWEEN_ADS = sec;
        if(!isRunning) {
            secondsToAds = sec;
        }
    }

    private AdsRunner() { }
}
