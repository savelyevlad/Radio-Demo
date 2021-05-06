package com.savelyevlad.radiodemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.savelyevlad.radiodemo.ads.AdsRunner;
import com.savelyevlad.radiodemo.fragments.FragmentAdsList;
import com.savelyevlad.radiodemo.fragments.FragmentMain;
import com.savelyevlad.radiodemo.fragments.FragmentStationList;
import com.savelyevlad.radiodemo.services.PlayerService;
import com.savelyevlad.radiodemo.tools.PlayingStatus;
import com.savelyevlad.radiodemo.tools.StationList;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance = null;

    private BroadcastReceiver broadcastReceiver;
    private boolean isPlaying = false;

    private FragmentStationList fragmentStationList = new FragmentStationList();
    private FragmentMain fragmentMain = new FragmentMain();
    private FragmentAdsList fragmentAdsList = new FragmentAdsList(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
                        if (getIsPlaying()) {
                            stop();
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(broadcastReceiver, filter);

        startPlayerService();

        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, fragmentMain).commit();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(), PlayerService.class));
        unregisterReceiver(broadcastReceiver);
        PlayerService.getInstance().removeNotification();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!getFragmentMain().isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, getFragmentMain()).commit();
        }
        else if(!getFragmentAdsList().isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, getFragmentMain()).commit();
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    public void setIsPlaying(boolean status) {
        isPlaying = status;
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void play() {
        AdsRunner.initialize(this);
        setIsPlaying(true);
        Intent servicePlayIntent = new Intent(this, PlayerService.class);
        servicePlayIntent.putExtra("playStop", "play");
        startService(servicePlayIntent);
        AdsRunner.start();
        PlayerService.getInstance().buildNotification(PlayingStatus.PLAYING);
    }

    public void stop() {
        setIsPlaying(false);
        Intent serviceStopIntent = new Intent(this, PlayerService.class);
        serviceStopIntent.putExtra("playStop", "stop");
        startService(serviceStopIntent);
        AdsRunner.stop();
        PlayerService.getInstance().buildNotification(PlayingStatus.PAUSED);
    }

    public void next() {
        stop();
        StationList.inc();
        play();
        if (fragmentStationList.getRadioStationAdapter() != null) {
            fragmentStationList.getRadioStationAdapter().notifyDataSetChanged();
        }
        PlayerService.getInstance().buildNotification(PlayingStatus.PLAYING);
    }

    public void previous() {
        stop();
        StationList.dec();
        play();
        if (fragmentStationList.getRadioStationAdapter() != null) {
            fragmentStationList.getRadioStationAdapter().notifyDataSetChanged();
        }
        PlayerService.getInstance().buildNotification(PlayingStatus.PLAYING);
    }

    private void startPlayerService() {
        Intent serviceStopIntent = new Intent(this, PlayerService.class);
        serviceStopIntent.putExtra("playStop", "first run");
        startService(serviceStopIntent);
    }

    public FragmentStationList getFragmentStationList() {
        return fragmentStationList;
    }

    public FragmentMain getFragmentMain() {
        return fragmentMain;
    }

    public FragmentAdsList getFragmentAdsList() {
        return fragmentAdsList;
    }

    public static MainActivity getInstance() {
        return instance;
    }
}