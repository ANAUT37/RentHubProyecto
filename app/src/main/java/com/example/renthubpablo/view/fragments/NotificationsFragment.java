package com.example.renthubpablo.view.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.renthubpablo.R;
import com.example.renthubpablo.resources.Constants;

public class NotificationsFragment extends Fragment {
    Button openSettings;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch pausarTodas, mensajes,valoraciones,favoritos,app;
    public NotificationsFragment() {
    }

    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notifications, container, false);
        sharedPreferences= getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NOTIFICACIONES, MODE_PRIVATE);
        editor= sharedPreferences.edit();
        pausarTodas=view.findViewById(R.id.swNotiPauseAll);
        mensajes=view.findViewById(R.id.swNotiMensajes);
        valoraciones=view.findViewById(R.id.swNotiValoraciones);
        favoritos=view.findViewById(R.id.swNotiFavoritos);
        app=view.findViewById(R.id.swNotiApp);
        pausarTodas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pausarTodas.isChecked()){
                    mensajes.setChecked(false);
                    mensajes.setEnabled(false);
                    valoraciones.setChecked(false);
                    valoraciones.setEnabled(false);
                    favoritos.setChecked(false);
                    favoritos.setEnabled(false);
                    app.setChecked(false);
                    app.setEnabled(false);
                }else if(!pausarTodas.isChecked()){
                    mensajes.setChecked(true);
                    mensajes.setEnabled(true);
                    valoraciones.setChecked(true);
                    valoraciones.setEnabled(true);
                    favoritos.setChecked(true);
                    favoritos.setEnabled(true);
                    app.setChecked(true);
                    app.setEnabled(true);
                }
            }
        });
        setInitialState();
        switchListeners();


        openSettings=view.findViewById(R.id.bttnNotiOpenSettings);
        openSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                startActivity(intent);
            }
        });
        return view;
    }

    private void switchListeners() {
        pausarTodas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString(Constants.NOTIFICACIONES_ALL, String.valueOf(true));editor.apply();
                }else {
                    editor.putString(Constants.NOTIFICACIONES_ALL, String.valueOf(false));editor.apply();
                }
            }
        });
        mensajes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString(Constants.NOTIFICACIONES_MENSAJES, String.valueOf(true));editor.apply();
                }else {
                    editor.putString(Constants.NOTIFICACIONES_MENSAJES, String.valueOf(false));editor.apply();
                }
            }
        });
        valoraciones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString(Constants.NOTIFICACIONES_VALORACIONES, String.valueOf(true));editor.apply();
                }else {
                    editor.putString(Constants.NOTIFICACIONES_VALORACIONES, String.valueOf(false));editor.apply();
                }
            }
        });
        favoritos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString(Constants.NOTIFICACIONES_FAVORITOS, String.valueOf(true));editor.apply();
                }else {
                    editor.putString(Constants.NOTIFICACIONES_FAVORITOS, String.valueOf(false));editor.apply();
                }
            }
        });
        app.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString(Constants.NOTIFICACIONES_APP, String.valueOf(true));editor.apply();
                }else {
                    editor.putString(Constants.NOTIFICACIONES_APP, String.valueOf(false));editor.apply();
                }
            }
        });
    }
    private void setInitialState(){
        if(sharedPreferences.getString(Constants.NOTIFICACIONES_ALL,"").equals("true")){
            pausarTodas.setChecked(true);
            pausarTodas.setEnabled(true);
            mensajes.setChecked(false);
            mensajes.setEnabled(false);
            valoraciones.setChecked(false);
            valoraciones.setEnabled(false);
            favoritos.setChecked(false);
            favoritos.setEnabled(false);
            app.setChecked(false);
            app.setEnabled(false);
        }
        if(sharedPreferences.getString(Constants.NOTIFICACIONES_MENSAJES,"").equals("false")&&!pausarTodas.isChecked()){
            mensajes.setEnabled(false);
            mensajes.setChecked(false);
        }
        if(sharedPreferences.getString(Constants.NOTIFICACIONES_FAVORITOS,"").equals("false")&&!pausarTodas.isChecked()){
            favoritos.setEnabled(false);
            favoritos.setChecked(false);
        }
        if(sharedPreferences.getString(Constants.NOTIFICACIONES_VALORACIONES,"").equals("false")&&!pausarTodas.isChecked()){
            valoraciones.setEnabled(false);
            valoraciones.setChecked(false);
        }
        if(sharedPreferences.getString(Constants.NOTIFICACIONES_APP,"").equals("false")&&!pausarTodas.isChecked()){
            app.setEnabled(false);
            app.setChecked(false);
        }

    }
}