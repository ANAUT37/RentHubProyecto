package com.example.renthubpablo.view.adapter;

import com.example.renthubpablo.controller.Anuncio;
import com.google.maps.errors.ApiException;

import java.io.IOException;

public interface SelectedListenerPrediction {
    void onItemClicked(String string) throws IOException, InterruptedException, ApiException;
}
