package com.savelyevlad.radiodemo.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortItemView;
import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;
import com.savelyevlad.radiodemo.adapters.AdsAdapter;
import com.savelyevlad.radiodemo.ads.AdsRunner;

import java.util.List;

public class FragmentAdsList extends Fragment {

    private int adsLength = 60;

    private MainActivity mainActivity;
    private SeekBar seekBar;
    private Button buttonBack;
    private TextView textView;
    private RecyclerView recyclerView;

    private AdsAdapter adsAdapter;

    public FragmentAdsList(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ads_list, container, false);

        buttonBack = rootView.findViewById(R.id.buttonReturnBack);
        seekBar = rootView.findViewById(R.id.seekBar_time);
        textView = rootView.findViewById(R.id.textView_time);
        recyclerView = rootView.findViewById(R.id.recyclerAdsList);

        seekBar.setProgress(adsLength);
        textView.setText(adsLength + "");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adsLength = 30 + progress;
                AdsRunner.setSecondsBetweenAds(30 + progress);
                textView.setText(adsLength + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        buttonBack.setOnClickListener((view) -> {
            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, mainActivity.getFragmentMain()).commit();
        });

        adsAdapter = new AdsAdapter(mainActivity, new String[] {"ads1", "ads2", "kek", "shmek"});
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.setAdapter(adsAdapter);
        recyclerView.addItemDecoration(new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT) {
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        adsAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        Log.e("kek", "moving item");
                        return true;
                    }
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        // remove from adapter
                    }
                }));

        return rootView;
    }
}