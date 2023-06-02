package com.example.renthubpablo.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renthubpablo.R;
import com.example.renthubpablo.resources.Connection;
import com.example.renthubpablo.resources.NotificationUtils;
import com.example.renthubpablo.view.fragments.ContratosFragment;
import com.example.renthubpablo.view.fragments.SearchFragment;
import com.example.renthubpablo.view.fragments.ProfileFragment;
import com.example.renthubpablo.view.fragments.ChatFragment;
import com.example.renthubpablo.view.fragments.UploadFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import javax.xml.transform.ErrorListener;

public class MainSessioned extends AppCompatActivity implements Connection.SetConnectionError {
    private Connection connectionHandler;
    TextView errorConexion;
    Fragment currentFragment;
    ProgressBar progressBar;
    private static final SearchFragment SEARCH_FRAGMENT = new SearchFragment();
    private static final ChatFragment CHAT_FRAGMENT = new ChatFragment();
    private static final UploadFragment UPLOAD_FRAGMENT = new UploadFragment();
    private static final ContratosFragment CONTRATOS_FRAGMENT = new ContratosFragment();
    private static final ProfileFragment PROFILE_FRAGMENT = new ProfileFragment();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sessioned);

        errorConexion=findViewById(R.id.tvErrorMessageNoConnection);
        connectionHandler= new Connection(this);
        if(connectionHandler.isConnectedToInternet(getApplicationContext())){
            errorConexion.setVisibility(View.GONE);
        }else{
            errorConexion.setVisibility(View.VISIBLE);
        }

        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        currentFragment=CHAT_FRAGMENT;
        loadFragment(SEARCH_FRAGMENT);
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Task<String> messaging = FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
               System.out.println("OCURRIO: TOKEN DEL DIS: "+s);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionHandler, intentFilter);
        boolean isConnected = isConnectedToInternet(this);
        updateConnectivityStatus(isConnected);
        if(connectionHandler.isConnectedToInternet(getApplicationContext())){
            errorConexion.setVisibility(View.GONE);
        }else{
            errorConexion.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectionHandler);
        //NotificationUtils notificationUtils= new NotificationUtils();
        //NotificationUtils.sendNotification(getApplicationContext());
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId()==R.id.searchFragment){
                progressBar.setVisibility(View.VISIBLE);
                loadFragment(SEARCH_FRAGMENT);
                return true;
            }else if(item.getItemId()==R.id.chatFragment){
                progressBar.setVisibility(View.VISIBLE);
                loadFragment(CHAT_FRAGMENT);
                return true;
            }else if(item.getItemId()==R.id.uploadFragment){
                progressBar.setVisibility(View.VISIBLE);
                loadFragment(UPLOAD_FRAGMENT);
                return true;
            }else if(item.getItemId()==R.id.contratosFragment){
                progressBar.setVisibility(View.VISIBLE);
                loadFragment(CONTRATOS_FRAGMENT);
                return true;
            }else if(item.getItemId()==R.id.profileFragment){
                progressBar.setVisibility(View.VISIBLE);
                loadFragment(PROFILE_FRAGMENT);
                return true;
            }else{
                progressBar.setVisibility(View.VISIBLE);
                loadFragment(PROFILE_FRAGMENT);
                return true;
            }
        }
    };

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //AÑADIR TRANSICIONES
        this.currentFragment=fragment;
        transaction.replace(R.id.frame_container,fragment);
        transaction.commit();
        progressBar.setVisibility(View.INVISIBLE);
    }
    public boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        if(connectionHandler.isConnectedToInternet(getApplicationContext())){
            errorConexion.setVisibility(View.GONE);
        }else{
            errorConexion.setVisibility(View.VISIBLE);
        }
        return false;
    }
    private void updateConnectivityStatus(boolean isConnected) {
        if (!isConnected) {
            errorConexion.setVisibility(View.VISIBLE);
        } else {
            errorConexion.setVisibility(View.GONE);
        }
        if(connectionHandler.isConnectedToInternet(getApplicationContext())){
            errorConexion.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Conectado a Internet", Toast.LENGTH_SHORT).show();
        }else{
            errorConexion.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Conexión perdida", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setErrorVisible(boolean errorVisible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Cambiar el texto del TextView
                if(errorVisible){
                    errorConexion.setVisibility(View.VISIBLE);
                }else{
                    errorConexion.setVisibility(View.GONE);
                }
            }
        });

    }
}