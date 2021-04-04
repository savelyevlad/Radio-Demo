package com.savelyevlad.radiodemo.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;

public class FragmentStationList extends Fragment {

    private MainActivity mainActivity;
    private Button buttonReturnBack;

    public FragmentStationList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) inflater.getContext();
        View rootView = inflater.inflate(R.layout.fragment_station_list, container, false);
        buttonReturnBack = rootView.findViewById(R.id.buttonReturnBack);
        buttonReturnBack.setOnClickListener((view) -> {
            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, mainActivity.getFragmentMain()).commit();
        });
        return rootView;
    }
}