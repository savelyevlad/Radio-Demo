package com.savelyevlad.radiodemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.savelyevlad.radiodemo.services.PlayerService;
import com.savelyevlad.radiodemo.tools.NetworkTools;
import com.savelyevlad.radiodemo.tools.StationList;

public class MainActivity extends AppCompatActivity {

    private boolean isPlaying = false;
    private Button buttonTurnOnOff = null;
    private Button buttonMenu = null;
    private BroadcastReceiver broadcastReceiver;

    private FragmentStationList fragmentStationList = new FragmentStationList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("kek", getResources().openRawResource(R.raw.ads1).toString());
        Log.e("kek", getResources().getResourcePackageName(R.raw.ads1));

        buttonTurnOnOff = findViewById(R.id.buttonTurnOnOff);
        buttonTurnOnOff.setOnClickListener((view) -> {
            if (NetworkTools.isNetworkAvailable(this)) {
                if (getIsPlaying()) {
                    stop();
                }
                play();
            } else {
                Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
            }
        });

        buttonMenu = findViewById(R.id.buttonMenu);
        buttonMenu.setOnClickListener((view) -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.firstLayout, fragmentStationList).commit();
        });

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
        StationList.setNowPlayingId(-1);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(), PlayerService.class));
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}