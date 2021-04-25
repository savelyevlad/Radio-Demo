package com.savelyevlad.radiodemo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savelyevlad.radiodemo.MainActivity;
import com.savelyevlad.radiodemo.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> objects;
    private MainActivity mainActivity;

    public AdsAdapter(MainActivity mainActivity, String[] o) {
        this.mainActivity = mainActivity;
        objects = new ArrayList<>(Arrays.asList(o));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.advertisement_list_item, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TextView) holder.itemView.findViewById(R.id.list_item)).setText(objects.get(position));
//        ((ViewHolder)holder).getTextView().setText(objects.get(position));
    }

    public void swap(int i, int j) {
        Collections.swap(objects, i, j);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
