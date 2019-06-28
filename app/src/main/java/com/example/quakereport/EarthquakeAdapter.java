package com.example.quakereport;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_listview, parent, false);
        }
        Earthquake earthquake = getItem(position);

        TextView magText = listItemView.findViewById(R.id.mag);
        magText.setText("" + earthquake.getMag());
        switch ((int) Math.floor(earthquake.getMag())) {
            case 0:
            case 1:
                magText.setBackgroundResource(R.drawable.mag_circle1);
                break;
            case 2:
                magText.setBackgroundResource(R.drawable.mag_circle2);
                break;
            case 3:
                magText.setBackgroundResource(R.drawable.mag_circle3);
                break;
            case 4:
                magText.setBackgroundResource(R.drawable.mag_circle4);
                break;
            case 5:
                magText.setBackgroundResource(R.drawable.mag_circle5);
                break;
            case 6:
                magText.setBackgroundResource(R.drawable.mag_circle6);
                break;
            case 7:
                magText.setBackgroundResource(R.drawable.mag_circle7);
                break;
            case 8:
                magText.setBackgroundResource(R.drawable.mag_circle8);
                break;
            case 9:
                magText.setBackgroundResource(R.drawable.mag_circle9);
                break;
            case 10:
                magText.setBackgroundResource(R.drawable.mag_circle10);
                break;
        }


        TextView placeText = listItemView.findViewById(R.id.place);
        placeText.setText(earthquake.getPlace());

        TextView locationText = listItemView.findViewById(R.id.location);
        locationText.setText(earthquake.getLocation());

        TextView dateText = listItemView.findViewById(R.id.date);
        dateText.setText(earthquake.getDate());

        TextView timeText = listItemView.findViewById(R.id.time);
        timeText.setText(earthquake.getTime());

        return listItemView;
    }
}
