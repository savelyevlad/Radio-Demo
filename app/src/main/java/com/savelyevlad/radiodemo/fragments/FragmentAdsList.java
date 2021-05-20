package com.savelyevlad.radiodemo.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.savelyevlad.radiodemo.ads.AdsSelector;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ads_list, container, false);

        buttonBack = rootView.findViewById(R.id.buttonReturnBack);
        seekBar = rootView.findViewById(R.id.seekBar_time);
        textView = rootView.findViewById(R.id.textView_time);
        recyclerView = rootView.findViewById(R.id.recyclerAdsList);

        seekBar.setProgress(adsLength / 60 - 1);
        textView.setText(adsLength / 60 + "");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ++progress;
                adsLength = progress * 60;
                AdsRunner.setSecondsBetweenAds(progress * 60);
                textView.setText(adsLength / 60 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        buttonBack.setOnClickListener((view) -> {
            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, mainActivity.getFragmentMain()).commit();
        });

        adsAdapter = new AdsAdapter(mainActivity, AdsSelector.getAds());
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.setAdapter(adsAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                                                            ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                adsAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                Log.e("FragmentAdsList:", "moving item");
                return true;
            }
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) { }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return rootView;
    }
}