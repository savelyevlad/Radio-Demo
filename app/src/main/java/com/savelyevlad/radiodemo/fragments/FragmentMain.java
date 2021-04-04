package com.savelyevlad.radiodemo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.savelyevlad.radiodemo.ads.AdsRunner;
import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;
import com.savelyevlad.radiodemo.tools.NetworkTools;

public class FragmentMain extends Fragment {

    private MainActivity mainActivity = null;

    private FloatingActionButton buttonTurnOnOff = null;
    private FloatingActionButton buttonMenu = null;

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

        return rootView;
    }
}