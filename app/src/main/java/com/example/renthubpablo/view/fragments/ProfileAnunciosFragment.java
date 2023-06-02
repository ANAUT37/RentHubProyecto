package com.example.renthubpablo.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Anuncio;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.model.AnuncioHandler;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.view.AnuncioViewExtended;
import com.example.renthubpablo.view.adapter.AnuncioMiniAdapter;
import com.example.renthubpablo.view.adapter.FeedItemAdapter;
import com.example.renthubpablo.view.adapter.SelectListenerFeed;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ProfileAnunciosFragment extends Fragment{
    private static UsuarioHandler usuarioHandler=new UsuarioHandler();
    private static AnuncioHandler anuncioHandler=new AnuncioHandler();
    private static ArrayList<Anuncio> anuncioArrayList;
    private static Map<String,String> idsArrayList=new HashMap<>();
    private static AnuncioHandler.OnReadAnuncioListener onReadAnuncioListener;
    private static UsuarioHandler.OnUserAnuncioListener onUserAnuncioListener;
    static boolean perfilPropio=true;
    private static String mcorreo;
    TextView noAnuncios;
    RecyclerView recyclerAnuncios;
    public ProfileAnunciosFragment() {

    }

    public static ProfileAnunciosFragment newInstance(boolean perfilPropio,String email) {
        ProfileAnunciosFragment fragment = new ProfileAnunciosFragment();
        Bundle args= new Bundle();
        args.putBoolean("perfil_propio",perfilPropio);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            perfilPropio=getArguments().getBoolean("perfil_propio");
            mcorreo=getArguments().getString("email");
        }else{
            perfilPropio=true;
            SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            mcorreo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_anuncios, container, false);

        if(perfilPropio){
            SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            mcorreo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");
        }
        noAnuncios=view.findViewById(R.id.noAnuncio);
        anuncioArrayList=new ArrayList<>();
        onUserAnuncioListener= setOnUserAnuncioListener(onUserAnuncioListener);
        onReadAnuncioListener=setOnReadAnuncioListener(onReadAnuncioListener);
        usuarioHandler.readUserAnuncios(mcorreo,onUserAnuncioListener);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(anuncioArrayList.size()==0){
                    noAnuncios.setVisibility(View.VISIBLE);
                }
                recyclerAnuncios=view.findViewById(R.id.rvAnunciosPerfil);
                AnuncioMiniAdapter anuncioMiniAdapter= new AnuncioMiniAdapter(getContext(), anuncioArrayList, new AnuncioMiniAdapter.OnMiniAnuncioListener() {
                    @Override
                    public void onItemClicked(Anuncio anuncio) {
                        Intent intent = new Intent(getContext(), AnuncioViewExtended.class);
                        intent.putExtra("id",anuncio.getIdAnuncio());
                        intent.putExtra("categoria",anuncio.getCategoria().toLowerCase(Locale.ROOT));
                        intent.putExtra("email",anuncio.getEmailEmisor());
                        startActivity(intent);
                    }
                });
                recyclerAnuncios.setAdapter(anuncioMiniAdapter);
                recyclerAnuncios.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        },300);



        return view;
    }

    private AnuncioHandler.OnReadAnuncioListener setOnReadAnuncioListener(AnuncioHandler.OnReadAnuncioListener onReadAnuncioListener) {
        onReadAnuncioListener=new AnuncioHandler.OnReadAnuncioListener() {
            @Override
            public void onReadSucess(Anuncio anuncio) {
                anuncioArrayList.add(anuncio);
                System.out.println("OCURRIO: "+anuncio.toString());
            }

            @Override
            public void onReadError(String error) {

            }
        };
        return onReadAnuncioListener;
    }

    private UsuarioHandler.OnUserAnuncioListener setOnUserAnuncioListener(UsuarioHandler.OnUserAnuncioListener onUserAnuncioListener) {
        onUserAnuncioListener=new UsuarioHandler.OnUserAnuncioListener() {
            @Override
            public void onUserAnuncioSuccess(Map<String, String> anunciosIds) {
                idsArrayList=anunciosIds;
                for(Map.Entry<String, String> entry : anunciosIds.entrySet()){
                    anuncioHandler.onReadAnuncios(entry.getKey(),entry.getValue(),onReadAnuncioListener);
                }
            }

            @Override
            public void onUserAnuncioError(String error) {

            }
        };
        return onUserAnuncioListener;
    }



}