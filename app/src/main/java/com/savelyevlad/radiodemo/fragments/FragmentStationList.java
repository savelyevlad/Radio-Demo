package com.savelyevlad.radiodemo.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;
import com.savelyevlad.radiodemo.adapters.RadioStationAdapter;
import com.savelyevlad.radiodemo.tools.StationList;

public class FragmentStationList extends Fragment {

    private MainActivity mainActivity;
    private Button buttonReturnBack;
    private FloatingActionButton buttonAddStation;

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
        buttonAddStation = rootView.findViewById(R.id.button_adds_station);
        buttonAddStation.setOnClickListener((l) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setTitle("Add station");

            LayoutInflater li = LayoutInflater.from(mainActivity);
            View theView = li.inflate(R.layout.add_station, null);
            builder.setView(theView);

            builder.setPositiveButton("OK", (dialog, which) -> {
                StationList.getStations().add(((EditText) theView.findViewById(R.id.editTextUrl)).getText().toString());
                StationList.getStationsNames().add(((EditText) theView.findViewById(R.id.editTextName)).getText().toString());
                StationList.updateRadioStations();
                radioStationAdapter.notifyDataSetChanged();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> { });

            builder.show();
        });

        StationList.updateRadioStations();
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