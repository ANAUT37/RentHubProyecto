package com.example.renthubpablo.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.model.AppHandler;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.view.MainNoSession;
import com.example.renthubpablo.view.MainSessioned;
import com.example.renthubpablo.view.NoAppStatus;

public class MainActivity extends AppCompatActivity {
    //USUARIO DEFAULT PRUEBAS
    //correo: pablo@gmail.com
    //password: pablo
    private static final AppHandler appHandler=new AppHandler();
    private static AppHandler.OnCheckAppSettingsListener onCheckAppSettingsListener;
    private static boolean appStatus;

    TextView rent;
    Animation bounce_anim;

    private static UsuarioHandler.OnLoginListener onLoginListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_fragment);
        onCheckAppSettingsListener =setOnCheckAppSettingsListener(onCheckAppSettingsListener);
        onLoginListener=setOnLoginListener(onLoginListener);
        appStatus();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(appStatus){
                    if(!checkSession()){
                        startActivity(new Intent(getApplicationContext(), MainNoSession.class));
                        finish();
                    }
                }else{
                    //APP FUERA DE SERVICIO
                    Intent intent = new Intent(getApplicationContext(), NoAppStatus.class);
                    intent.putExtra("errorMessage", Constants.NO_APP_STATUS_ERROR_MESSAGE);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);

    }

    private boolean checkSession(){
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String correo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");
        String password=prefs.getString(Constants.SHARED_PREFERENCES_PASSWORD,"");
        if(correo.length()>0&&password.length()>0){
            UsuarioHandler usuarioHandler = new UsuarioHandler();
            usuarioHandler.loginUser(correo,password,onLoginListener);
            return true;
        }else{
            return false;
        }
    }
    private boolean appStatus(){
        appHandler.checkAppSettings(Constants.CAMPO_APP_STATUS,onCheckAppSettingsListener);
        return true;
    }
    private AppHandler.OnCheckAppSettingsListener setOnCheckAppSettingsListener(AppHandler.OnCheckAppSettingsListener listener){
        listener=new AppHandler.OnCheckAppSettingsListener() {
            @Override
            public void onCheckSuccess(boolean result) {
                appStatus=result;
            }

            @Override
            public void onCheckError(String error) {
                System.out.println("OCURRIO: "+error);
            }
        };
        return listener;
    }
    private UsuarioHandler.OnLoginListener setOnLoginListener(UsuarioHandler.OnLoginListener listener) {
        listener= new UsuarioHandler.OnLoginListener() {
            @Override
            public void onLoginSuccess() {
                startActivity(new Intent(getApplicationContext(), MainSessioned.class));
                finish();
            }

            @Override
            public void onPasswordIncorrect() {
            }

            @Override
            public void onUserNotFound() {
            }

            @Override
            public void onLoginError() {
            }
        };
        return listener;
    }
}