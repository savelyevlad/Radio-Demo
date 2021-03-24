package com.savelyevlad.radiodemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class MainActivity extends AppCompatActivity {

    private String STREAMING_URL = null;
    private boolean isPlaying = false;
    private TextView nowIsPlaying;
    private Button[] buttons = new Button[3];
    private Button buttonStop;
    private BroadcastReceiver broadcastReceiver;
    private String nowPlayingData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("kek", getResources().openRawResource(R.raw.ads1).toString());

        buttons[0] = findViewById(R.id.button1);
        buttons[1] = findViewById(R.id.button2);
        buttons[2] = findViewById(R.id.button3);
        buttonStop = findViewById(R.id.buttonStop);
        nowIsPlaying = findViewById(R.id.nowIsPlaying);

        Log.e("kek", getResources().getResourcePackageName(R.raw.ads1));

        buttons[0].setOnClickListener(buttonsStationsOnClickListener);
        buttons[1].setOnClickListener(buttonsStationsOnClickListener);
        buttons[2].setOnClickListener(buttonsStationsOnClickListener);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
                        if (getIsPlaying()) {
                            stop();
                        }
                        System.exit(0);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(broadcastReceiver, filter);
        loadNowPlaying();

        buttonStop.setOnClickListener((v) -> {
            if(getIsPlaying()) {
                stop();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    View.OnClickListener buttonsStationsOnClickListener = v -> {
        String oldUrl;
        switch (v.getId()) {
            case R.id.button1:
                oldUrl = STREAMING_URL;
                STREAMING_URL = StationList.get(0);
//                StationList.setNowPlayingId(0);
                if(STREAMING_URL.equals(oldUrl)) {
                    break;
                }
                simulateClick(0);
                break;
            case R.id.button2:
                oldUrl = STREAMING_URL;
                STREAMING_URL = StationList.get(1);
//                StationList.setNowPlayingId(1);
                if(STREAMING_URL.equals(oldUrl)) {
                    break;
                }
                simulateClick(1);
                break;
            case R.id.button3:
                oldUrl = STREAMING_URL;
                STREAMING_URL = StationList.get(2);
//                StationList.setNowPlayingId(2);
                if(STREAMING_URL.equals(oldUrl)) {
                    break;
                }
                simulateClick(2);
                break;
        }
    };

    private void simulateClick(int playingStation) {
        if (isNetworkAvailable()) {
            if (getIsPlaying()) {
                stop();
            }
            StationList.setNowPlayingId(playingStation);
            play();
        } else {
            Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
        }
    }

    private void loadNowPlaying() {
        Thread t = new Thread() {
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(() -> reloadShoutCastInfo());
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ignored) {
                }
            }
        };
        t.start();
    }

    private void reloadShoutCastInfo() {
        if (isNetworkAvailable()) {
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            if(STREAMING_URL == null) {
                return null;
            }
            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(STREAMING_URL);
            nowPlayingData = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ICY_METADATA).replaceAll("StreamTitle", "").replaceAll("[=,';]+", "");
            mmr.release();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(STREAMING_URL == null) {
                return;
            }
            nowIsPlaying.setText(nowPlayingData);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void setIsPlaying(boolean status) {
        isPlaying = status;
    }

    private boolean getIsPlaying() {
        return isPlaying;
    }

    private void play() {
        setIsPlaying(true);
        Intent servicePlayIntent = new Intent(this, PlayerService.class);
        servicePlayIntent.putExtra("playStop", "play");
        startService(servicePlayIntent);
    }

    private void stop() {
        setIsPlaying(false);
        Intent serviceStopIntent = new Intent(this, PlayerService.class);
        serviceStopIntent.putExtra("playStop", "stop");
        startService(serviceStopIntent);
        STREAMING_URL = null;
        StationList.setNowPlayingId(-1);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(), PlayerService.class));
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}