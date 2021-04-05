package com.savelyevlad.radiodemo.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.savelyevlad.radiodemo.ads.AdsRunner;
import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;
import com.savelyevlad.radiodemo.tools.NetworkTools;
import com.savelyevlad.radiodemo.tools.StationList;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class FragmentMain extends Fragment {

    private MainActivity mainActivity = null;

    private FloatingActionButton buttonTurnOnOff = null;
    private FloatingActionButton buttonMenu = null;
    private TextView nowIsPlaying;

    public FragmentMain() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) inflater.getContext();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        AdsRunner.setFragmentMain(rootView);

        buttonTurnOnOff = rootView.findViewById(R.id.buttonTurnOnOff);
        buttonTurnOnOff.setOnClickListener((view) -> {
            if (NetworkTools.isNetworkAvailable(mainActivity)) {
                if (mainActivity.getIsPlaying()) {
                    mainActivity.stop();
                    AdsRunner.stop();
                }
                else {
                    mainActivity.play();
                    AdsRunner.start();
                }
            } else {
                Toast.makeText(mainActivity.getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
            }
        });

        buttonMenu = rootView.findViewById(R.id.buttonMenu);
        buttonMenu.setOnClickListener((view) -> {
            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, mainActivity.getFragmentStationList()).commit();
        });
        nowIsPlaying = rootView.findViewById(R.id.textFmRadioInfo);

        loadNowPlaying();

        return rootView;
    }

    private String nowPlayingData = null;

    private void loadNowPlaying() {
        new CountDownTimer(1000_000_000_000_000_000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                reloadShoutCastInfo();
            }

            @Override
            public void onFinish() {
                //
            }
        }.start();
    }

    private void reloadShoutCastInfo() {
        if (NetworkTools.isNetworkAvailable(mainActivity)) {
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            if(StationList.getPlayingStation() == null) {
                return null;
            }
            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(StationList.getPlayingStation());
            nowPlayingData = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ICY_METADATA).replaceAll("StreamTitle", "").replaceAll("[=,';]+", "");
            mmr.release();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(StationList.getPlayingStation() == null) {
                return;
            }
            if(nowIsPlaying == null) {
                return;
            }
            nowIsPlaying.setText(nowPlayingData);
        }
    }
}