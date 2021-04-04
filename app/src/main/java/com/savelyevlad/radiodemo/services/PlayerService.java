package com.savelyevlad.radiodemo.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.savelyevlad.radiodemo.R;
import com.savelyevlad.radiodemo.ads.AdsRunner;
import com.savelyevlad.radiodemo.tools.StationList;

public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {

//    private WifiManager.WifiLock mWiFiLock;
//    private PowerManager.WakeLock mWakeLock;
    private AudioManager audioManager;
    private MediaSource mediaSource;
    private SimpleExoPlayer player;

    @Override
    public void onCreate() {
        audioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("playStop");
        if (input != null && input.equals("play")) {
            if (requestFocus()) {
                initPlayer();
                AdsRunner.setRadioPlayer(player);
                play();
            }
        } else if(input != null && input.equals("stop")) {
            stop();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            stop();
            System.exit(0);
        }
    }

    private boolean requestFocus() {
        return (audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    private void play() {
        player.setForegroundMode(true);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    private void stop() {
        player.setPlayWhenReady(false);
        player.stop();
    }

    private void initPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext());
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(getApplicationContext(), getString(R.string.app_name)));
        mediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(StationList.getPlayingStation()));
    }

}
