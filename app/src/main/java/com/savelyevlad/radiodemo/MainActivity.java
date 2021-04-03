package com.savelyevlad.radiodemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.savelyevlad.radiodemo.services.PlayerService;
import com.savelyevlad.radiodemo.tools.NetworkTools;
import com.savelyevlad.radiodemo.tools.StationList;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private boolean isPlaying = false;

    private FragmentStationList fragmentStationList = new FragmentStationList();
    private FragmentMain fragmentMain = new FragmentMain();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, fragmentMain).commit();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(), PlayerService.class));
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    public void setIsPlaying(boolean status) {
        isPlaying = status;
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void play() {
        setIsPlaying(true);
        Intent servicePlayIntent = new Intent(this, PlayerService.class);
        servicePlayIntent.putExtra("playStop", "play");
        startService(servicePlayIntent);
    }

    public void stop() {
        setIsPlaying(false);
        Intent serviceStopIntent = new Intent(this, PlayerService.class);
        serviceStopIntent.putExtra("playStop", "stop");
        startService(serviceStopIntent);
        StationList.setNowPlayingId(-1);
    }

    public FragmentStationList getFragmentStationList() {
        return fragmentStationList;
    }

    public FragmentMain getFragmentMain() {
        return fragmentMain;
    }
}