package com.example.renthubpablo.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.renthubpablo.R;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.resources.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainNoSession extends AppCompatActivity {
    private static UsuarioHandler.OnLoginListener onLoginListener;
    private static UsuarioHandler.OnTokenListener onTokenListener;
    private static UsuarioHandler usuarioHandler= new UsuarioHandler();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference().child(Constants.CARPETA_FOTO_APP);
    private int indexAcordeon = 0;
    private Button registrar,submitLogin;
    private String correo, password,newToken;
    private EditText etUsername, etPassword;
    private static List<Bitmap> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_no_session);
        getImagesAcordeon();
        onLoginListener=setOnLoginListener(onLoginListener);
        submitLogin=findViewById(R.id.bttnLogin);
        etUsername=findViewById(R.id.etUser);
        etPassword=findViewById(R.id.etPassword);

        askNotificationPermission();

        onTokenListener=setOnTokenListener(onTokenListener);

        submitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsuarioHandler usuarioHandler = new UsuarioHandler();
               if(etUsername.getText().toString().length()>0){
                   if(etPassword.getText().toString().length()>0){
                       try {
                           Usuario usuario= new Usuario();
                           //correo=usuario.encrypt(etUsername.getText().toString().trim());
                           //password=usuario.hashPassword(etPassword.getText().toString().trim());
                           correo=etUsername.getText().toString().trim();
                           password=usuario.hashPassword(etPassword.getText().toString().trim());
                           usuarioHandler.loginUser(correo,password,onLoginListener);
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }else{
                       etPassword.setError(Constants.CAMPO_VACIO);
                   }
               }else{
                   etUsername.setError(Constants.CAMPO_VACIO);
               }
            }
        });

        registrar=findViewById(R.id.bttnRegister);
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
        actualizarAcordeon();
    }
    private List<Bitmap> getImagesAcordeon(){
        ImageView imageView = findViewById(R.id.ivFotosAcordeon);

        for(int i=1;i<7;i++){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.CARPETA_FOTO_APP+
                    "stock_accordeon_0"+i+Constants.CARPETA_FOTO_EXTENSION);
            try {
                File localFile = File.createTempFile("tempfile",Constants.CARPETA_FOTO_EXTENSION);
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        int imageViewWidth = imageView.getWidth();
                        int imageViewHeight = imageView.getHeight();

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, imageViewWidth, imageViewHeight, true);
                        items.add(scaledBitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                    }
                });
            } catch (IOException e) {
            }
        }

        return items;
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                } else {
                }
            });

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
    private void actualizarAcordeon() {
        ProgressBar progressBar = findViewById(R.id.acordeonProgressBar);
        ImageView imageView = findViewById(R.id.ivFotosAcordeon);
        Handler mHandler = new Handler();
        if(items.size()==0){
            progressBar.setVisibility(View.VISIBLE);
        }
        if(items.size()>0){
            progressBar.setVisibility(View.GONE);
            if(indexAcordeon==items.size()){
                indexAcordeon = 0;
            }

            imageView.setImageBitmap(items.get(indexAcordeon));
            indexAcordeon++;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                actualizarAcordeon();
            }
        }, 4000);
    }
    public UsuarioHandler.OnLoginListener setOnLoginListener(UsuarioHandler.OnLoginListener listener) {
       listener= new UsuarioHandler.OnLoginListener() {

           @Override
           public void onLoginSuccess() {
               SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
               SharedPreferences.Editor editor = sharedPreferences.edit();
               editor.putString(Constants.SHARED_PREFERENCES_CORREO, correo);
               editor.putString(Constants.SHARED_PREFERENCES_PASSWORD,password);

               usuarioHandler.createUserToken(onTokenListener);
               Handler handler=new Handler();
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       usuarioHandler.setToken(correo,newToken);
                   }
               },500);
               editor.putString(Constants.SHARED_PREFERENCES_TOKEN,newToken);
               editor.apply();
               startActivity(new Intent(getApplicationContext(),MainSessioned.class));
               finish();
           }

           @Override
           public void onPasswordIncorrect() {
               etPassword.setError(Constants.WRONG_PASSWORD);
           }

           @Override
           public void onUserNotFound() {
               etUsername.setError(Constants.USER_NOT_EXIST);
           }

           @Override
           public void onLoginError() {

           }
       };
       return listener;
    }
    public UsuarioHandler.OnTokenListener setOnTokenListener(UsuarioHandler.OnTokenListener listener){
        listener=new UsuarioHandler.OnTokenListener() {
            @Override
            public void onCreateTokenSuccess(String token) {
                newToken=token;
            }
        };
        return listener;
    }

}