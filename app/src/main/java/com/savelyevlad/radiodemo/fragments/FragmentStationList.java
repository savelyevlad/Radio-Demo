package com.savelyevlad.radiodemo.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;
import com.savelyevlad.radiodemo.adapters.RadioStation;
import com.savelyevlad.radiodemo.adapters.RadioStationAdapter;
import com.savelyevlad.radiodemo.tools.StationList;

public class FragmentStationList extends Fragment {

    private MainActivity mainActivity;
    private Button buttonReturnBack;

    private RadioStationAdapter radioStationAdapter;

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

        radioStationAdapter = new RadioStationAdapter(mainActivity, StationList.getRadioStationsArrayList());
        ListView listView = rootView.findViewById(R.id.list_of_stations);
        listView.setAdapter(radioStationAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if(StationList.getNowPlayingId() != position) {
                mainActivity.stop();
                StationList.setNowPlayingId(position);
                radioStationAdapter.notifyDataSetChanged();
                mainActivity.play();
            }
        });

        return rootView;
    }
}