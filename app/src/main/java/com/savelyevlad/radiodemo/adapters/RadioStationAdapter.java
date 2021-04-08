package com.savelyevlad.radiodemo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.savelyevlad.radiodemo.R;
import com.savelyevlad.radiodemo.tools.StationList;

import java.util.ArrayList;

public class RadioStationAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater lInflater;
    private ArrayList<RadioStation> objects;

    public RadioStationAdapter(Context context, ArrayList<RadioStation> objects) {
        this.context = context;
        this.objects = objects;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.station_list_item, parent, false);
        }
        TextView textView = view.findViewById(R.id.list_item);
        textView.setText(objects.get(position).getName());

        int curr = StationList.getNowPlayingId();

        if (position == curr) {
            view.findViewById(R.id.list_item).setBackgroundColor(Color.parseColor("#707070"));
        }
        else {
            view.findViewById(R.id.list_item).setBackgroundColor(0);
        }

        return view;
    }
}
