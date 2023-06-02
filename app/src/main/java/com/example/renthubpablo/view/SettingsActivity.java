package com.example.renthubpablo.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.MainActivity;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.resources.DialogUtils;
import com.example.renthubpablo.view.fragments.EditProfileFragment;
import com.example.renthubpablo.view.fragments.GestionarCuentaFragment;
import com.example.renthubpablo.view.fragments.NotificationsFragment;

public class SettingsActivity extends AppCompatActivity {
    private static final EditProfileFragment EDIT_PROFILE_FRAGMENT=new EditProfileFragment();
    private static final NotificationsFragment NOTIFICATIONS_FRAGMENT=new NotificationsFragment();
    private static final GestionarCuentaFragment GESTIONAR_CUENTA_FRAGMENT=new GestionarCuentaFragment();
    private static UsuarioHandler usuarioHandler= new UsuarioHandler();
    Button logout, editarPerfil, gestionarCuenta,notificaciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editarPerfil=findViewById(R.id.bttnSettEditPerfil);
        editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(EDIT_PROFILE_FRAGMENT);
            }
        });
        gestionarCuenta=findViewById(R.id.bttnSettGestionarCuenta);
        gestionarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(GESTIONAR_CUENTA_FRAGMENT);
            }
        });
        notificaciones=findViewById(R.id.bttnSettNotificaciones);
        notificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(NOTIFICATIONS_FRAGMENT);
            }
        });

    }
    public void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(android.R.id.content, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}