package com.example.renthubpablo.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.renthubpablo.R;


public class GarajeFragment extends Fragment {


    public GarajeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GarajeFragment newInstance(String param1, String param2) {
        GarajeFragment fragment = new GarajeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_garaje, container, false);
    }
}