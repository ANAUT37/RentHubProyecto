package com.example.renthubpablo.view.fragments;


import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Anuncio;
import com.example.renthubpablo.model.AnuncioHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.view.adapter.FeedSliderAdapter;
import com.example.renthubpablo.view.adapter.ImageSelectorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class EditAnuncioFragment extends Fragment {
    public static Context globalContext;
    private static AnuncioHandler.OnReadAnuncioListener onReadAnuncioListener;
    private static AnuncioHandler.OnGetImagenesInmuebleListener onGetImagenesInmuebleListener;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static AnuncioHandler anuncioHandler=new AnuncioHandler();
    private static ArrayList<Uri> selectedImages;
    private static ImageSelectorAdapter imageSelectorAdapter;
    private static RecyclerView recyclerViewImagenes;
    private static String id;
    private static String categoria;
    private Button add, remove;
    EditText titulo, descripcion,precio;

    public EditAnuncioFragment() {
    }
    public static EditAnuncioFragment newInstance(String param1, String param2) {
        EditAnuncioFragment fragment = new EditAnuncioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_PARAM1);
            categoria = getArguments().getString(ARG_PARAM2);
        }
        selectedImages=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_edit_anuncio, container, false);
        globalContext=view.getContext();
        titulo=view.findViewById(R.id.tvEditAnunTitulo);
        descripcion=view.findViewById(R.id.etEditAnunDescripcion);
        precio=view.findViewById(R.id.etEditAnuncioPrecio);
        recyclerViewImagenes=view.findViewById(R.id.rvEditAnunImages);

        imageSelectorAdapter = new ImageSelectorAdapter(globalContext, selectedImages);
        recyclerViewImagenes.setAdapter(imageSelectorAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(globalContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewImagenes.setLayoutManager(layoutManager);


        onGetImagenesInmuebleListener=setOnGetImagenesInmuebleListener(onGetImagenesInmuebleListener);
        onReadAnuncioListener=setOnReadAnuncioListener(onReadAnuncioListener);
        anuncioHandler.onReadAnuncios(id,categoria,onReadAnuncioListener);

        add=view.findViewById(R.id.bttnAddEditImage);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImages.size()<10){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE);
                }
            }
        });
        remove=view.findViewById(R.id.bttnRemoveEditImage);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImages.size()>0){
                    selectedImages.remove(selectedImages.size()-1);
                    imageSelectorAdapter = new ImageSelectorAdapter(globalContext, selectedImages);
                    recyclerViewImagenes.setAdapter(imageSelectorAdapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(globalContext, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewImagenes.setLayoutManager(layoutManager);
                    if(selectedImages.size()==0){
                        recyclerViewImagenes.setVisibility(View.GONE);
                    }
                }
            }
        });



        return view;
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            selectedImages.add(uri);
            imageSelectorAdapter.notifyDataSetChanged();
            recyclerViewImagenes.setVisibility(View.VISIBLE);
        }
    }



    private AnuncioHandler.OnReadAnuncioListener setOnReadAnuncioListener(AnuncioHandler.OnReadAnuncioListener onReadAnuncioListener) {
        onReadAnuncioListener=new AnuncioHandler.OnReadAnuncioListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onReadSucess(Anuncio anuncio) {
                titulo.setHint(anuncio.getTitulo());
                descripcion.setHint(anuncio.getDescripcion());
                precio.setHint(anuncio.getPrecio()+"€/Mes");

                selectedImages.clear();

                imageSelectorAdapter.notifyDataSetChanged();
                for(String name: anuncio.getImagen()){
                    System.out.println("OCURRIO: "+name);
                    anuncioHandler.getImagenesInmueble(name,onGetImagenesInmuebleListener);
                }
            }

            @Override
            public void onReadError(String error) {

            }
        };
        return onReadAnuncioListener;
    }

    private AnuncioHandler.OnGetImagenesInmuebleListener setOnGetImagenesInmuebleListener(AnuncioHandler.OnGetImagenesInmuebleListener onGetImagenesInmuebleListener) {
        onGetImagenesInmuebleListener=new AnuncioHandler.OnGetImagenesInmuebleListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onGetImagesSuccess(Uri uri) {
                if(!selectedImages.contains(uri)){
                    selectedImages.add(uri);
                    imageSelectorAdapter.notifyItemInserted(selectedImages.size()-1);
                }


            }

            @Override
            public void onGetImagesComplete() {

            }

            @Override
            public void onGetImagesError(String error) {

            }
        };
        return onGetImagenesInmuebleListener;
    }
}