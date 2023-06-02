package com.example.renthubpablo.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import com.example.renthubpablo.R;


public class ViviendaFragment extends Fragment {
    EditText tamanyo, dormitorios, banyos;
    Switch ascensor, garaje, trastero;
    public ViviendaFragment() {
        // Required empty public constructor
    }

    public static ViviendaFragment newInstance(String param1, String param2) {
        ViviendaFragment fragment = new ViviendaFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_vivienda, container, false);



        return view;
    }
}