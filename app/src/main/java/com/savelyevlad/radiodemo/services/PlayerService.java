package com.savelyevlad.radiodemo.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;
import com.savelyevlad.radiodemo.ads.AdsRunner;
import com.savelyevlad.radiodemo.tools.PlayingStatus;
import com.savelyevlad.radiodemo.tools.StationList;

public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private static PlayerService instance;

    private static final String CHANNEL_ID = "KEKLELKEK";
    private static final int NOTIFICATION_ID = 228;

    public static final String ACTION_PAUSE = "com.savelyevlad.ACTION_PAUSE";
    public static final String ACTION_PLAY = "com.savelyevlad.ACTION_PLAY";
    public static final String ACTION_NEXT = "com.savelyevlad.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.savelyevlad.ACTION_PREVIOUS";

//    private WifiManager.WifiLock mWiFiLock;
//    private PowerManager.WakeLock mWakeLock;
    private AudioManager audioManager;
    private MediaSource mediaSource;
    private SimpleExoPlayer player;

    @Override
    public void onCreate() {
        Log.i("PlayerService", "onCreate");
        audioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        createNotificationChannel();
        instance = this;
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

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);

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
        }
    }

    private boolean requestFocus() {
        return (audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    private void play() {
        player.setForegroundMode(true);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        buildNotification(PlayingStatus.PLAYING);
    }

    private void stop() {
        if(player != null) {
            player.setPlayWhenReady(false);
            player.stop();
        }
    }

    private void initPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext());
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(getApplicationContext(), getString(R.string.app_name)));
        mediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(StationList.getPlayingStation()));
    }


    // added for notifications
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "getString(R.string.channel_name)";
            String description = "getString(R.string.channel_description)";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void buildNotification(PlayingStatus status) {

        Log.i("PlayerService", "buildNotification");

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
        Intent notificationIntent = new Intent(this, PlayerService.class);

        int icon;
        if(status == PlayingStatus.PLAYING) {
            icon = android.R.drawable.ic_media_pause;
            notificationIntent.setAction(ACTION_PAUSE);
        }
        else {
            icon = android.R.drawable.ic_media_play;
            notificationIntent.setAction(ACTION_PLAY);
        }

        PendingIntent notificationPendingIntent =
                PendingIntent.getService(this, 1, notificationIntent, 0);
        String nowPlaying = MainActivity.getInstance().getFragmentMain().getNowPlayingData();

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setShowWhen(false)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .setColor(getResources().getColor(R.color.design_default_color_on_primary))
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setContentTitle(nowPlaying == null ? "Radio is playing" : nowPlaying)
                .setContentIntent(notificationPendingIntent)
                .addAction(android.R.drawable.ic_media_previous, "previous", playAction(0))
                .addAction(icon, "pause", notificationPendingIntent)
                .addAction(android.R.drawable.ic_media_next, "next", playAction(1));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public PendingIntent playAction(int actionNumber) {
        Log.i("PlayerService", "playbackAction: " + actionNumber);
        Intent notificationIntent;
        switch (actionNumber) {
            case 0:
                // previous
                notificationIntent = new Intent(this, PlayerService.class);
                notificationIntent.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, 1, notificationIntent, 0);
            case 1:
                // next
                notificationIntent = new Intent(this, PlayerService.class);
                notificationIntent.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, 1, notificationIntent, 0);
        }
        return null;
    }

    public void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void handleIncomingActions(Intent playbackAction) {

        if (playbackAction == null || playbackAction.getAction() == null) {
            return;
        }

        Log.i("PlayerService", "handleIncomingActions: ");

        String action = playbackAction.getAction();

        switch (action) {
            case ACTION_PAUSE:
                Log.i("PlayerService", "handleIncomingActions: PAUSE");
                MainActivity.getInstance().stop();
                break;
            case ACTION_PLAY:
                Log.i("PlayerService", "handleIncomingActions: PLAY");
                MainActivity.getInstance().play();
                break;
            case ACTION_NEXT:
                Log.i("PlayerService", "handleIncomingActions: NEXT");
                MainActivity.getInstance().next();
                break;
            case ACTION_PREVIOUS:
                Log.i("PlayerService", "handleIncomingActions: PREVIOUS");
                MainActivity.getInstance().previous();
                break;
        }
    }

    public static PlayerService getInstance() {
        return instance;
    }
}