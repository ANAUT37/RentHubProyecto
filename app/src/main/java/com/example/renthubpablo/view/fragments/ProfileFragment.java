package com.example.renthubpablo.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.view.SettingsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    static String mcorreo="";
    static boolean perfilPropio=true;
    private static UsuarioHandler.OnReadListener onReadListener;
    private static ProfileAnunciosFragment PROFILE_ANUNCIOS_FRAGMENT=new ProfileAnunciosFragment();
    private static ProfileValoracionesFragment PROFILE_VALORACIONES_FRAGMENT;
    TextView nombre,biografia,ubicacion,telefono,genero,edad,correo,pronombres;
    ImageButton ajustes;
    CircleImageView pfp;
    ConstraintLayout infoPersonal;

    public ProfileFragment() {
    }
    public static ProfileFragment newInstance(boolean perfilPropio, String email) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean("perfil_propio", perfilPropio);
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
            mcorreo="";
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        nombre = view.findViewById(R.id.tvNombre);
        biografia = view.findViewById(R.id.tvBiografia);
        ubicacion = view.findViewById(R.id.tvUbicacion);
        telefono = view.findViewById(R.id.tvTelefono);
        genero = view.findViewById(R.id.tvGenero);
        edad = view.findViewById(R.id.tvEdad);
        correo = view.findViewById(R.id.tvCorreo);
        pfp = view.findViewById(R.id.ivPfpProfile);
        ajustes = view.findViewById(R.id.bttnSettings);
        infoPersonal=view.findViewById(R.id.consPrivateInfo);
        pronombres=view.findViewById(R.id.tvPronombres);


        onReadListener=setOnReadListener(onReadListener);
        try {
            String searchCorreo=mcorreo;
            if(searchCorreo==""){
                SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                searchCorreo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");
            }
            UsuarioHandler usuarioHandler = new UsuarioHandler();
            usuarioHandler.readUserData(searchCorreo, onReadListener, "");
        } catch (Exception e) {
            System.out.println("OCURRIO: PF 2"+e.getMessage());
        }
        if(!perfilPropio){
            ajustes.setVisibility(View.GONE);
            infoPersonal.setVisibility(View.GONE);
        }else{
            ajustes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), SettingsActivity.class));
                }
            });
        }
        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFragment(PROFILE_ANUNCIOS_FRAGMENT);
            }
        },300);

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomNavigationView navigationView = view.findViewById(R.id.profile_navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId()==R.id.anunciosFragment){
                PROFILE_ANUNCIOS_FRAGMENT= ProfileAnunciosFragment.newInstance(perfilPropio,mcorreo);
                loadFragment(PROFILE_ANUNCIOS_FRAGMENT);
            }else if(item.getItemId()==R.id.valoracionesFragment){
                PROFILE_VALORACIONES_FRAGMENT = ProfileValoracionesFragment.newInstance(perfilPropio, mcorreo);
                loadFragment(PROFILE_VALORACIONES_FRAGMENT);
            }
            return true;
        }
    };
    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        //AÑADIR TRANSICIONES
        transaction.replace(R.id.frame_profile,fragment);
        transaction.commit();

    }
    private UsuarioHandler.OnReadListener setOnReadListener(UsuarioHandler.OnReadListener onReadListener) {
        onReadListener=new UsuarioHandler.OnReadListener() {
            @Override
            public void onReadSuccess(Usuario usuario) {
                nombre.setText(usuario.getNombre()+" "+usuario.getApellido());
                biografia.setText(usuario.getDescripcion());
                ubicacion.setText("");
                telefono.setText(usuario.getPhone());
                genero.setText(usuario.getGenero());
                edad.setText(usuario.getDate());
                correo.setText(usuario.getEmail());
                pronombres.setText(usuario.getPronombres());

                StorageReference storageReference = FirebaseStorage.getInstance().getReference("pfp/"+usuario.getPfp());

                try {
                    File localFile = File.createTempFile("tempfile",".jpg");
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            pfp.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pfp.setImageResource(R.drawable.user_no_pic);
                        }
                    });
                } catch (IOException e) {
                    System.out.println("OCURRIO: "+e.getMessage());
                }


            }

            @Override
            public void onReadError(String error) {
                System.out.println("OCURRIO: "+error);
            }
        };
        return onReadListener;
    }


}