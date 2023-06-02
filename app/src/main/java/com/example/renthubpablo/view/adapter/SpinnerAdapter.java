package com.example.renthubpablo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.renthubpablo.R;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public SpinnerAdapter(Context context, List<String> values){
        super(context, android.R.layout.simple_spinner_item, values);
        this.context = context;
        this.values = values;
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTextColor(ContextCompat.getColor(context, R.color.azulMarino));
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) LayoutInflater.from(context)
                .inflate(R.layout.spinner_layout, parent, false);
        view.setText(values.get(position));
        view.setTextColor(ContextCompat.getColor(context, R.color.azulMarino));
        return view;
    }
}
