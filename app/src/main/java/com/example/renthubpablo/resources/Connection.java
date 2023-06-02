package com.example.renthubpablo.resources;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renthubpablo.view.MainSessioned;

public class Connection extends BroadcastReceiver {
    //GESTIONA QUE EL USUARIO SE ENCUENTRE CONECTADO A INTERNET
    private SetConnectionError setConnectionError;

    public Connection(SetConnectionError setConnectionError) {
        this.setConnectionError=setConnectionError;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MainSessioned mainSessioned=new MainSessioned();

        boolean isConnected = isConnectedToInternet(context);
        if (isConnected) {
            setConnectionError.setErrorVisible(false);
        } else {
            setConnectionError.setErrorVisible(true);
        }
    }

    public boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    public interface SetConnectionError{
        void setErrorVisible(boolean errorVisible);
    }
}
