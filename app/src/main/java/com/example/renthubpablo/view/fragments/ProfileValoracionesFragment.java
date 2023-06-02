package com.example.renthubpablo.view.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CpuUsageInfo;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.controller.types.Valoracion;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.model.ValoracionHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.resources.FCMSend;
import com.example.renthubpablo.resources.NotificationUtils;
import com.example.renthubpablo.view.adapter.ValoracionesAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class ProfileValoracionesFragment extends Fragment {
    private static UsuarioHandler.OnTokenReadListener onTokenReadListener;
    static boolean perfilPropio=true;
    private static UsuarioHandler.OnReadListener onReadListener;
    private static ValoracionHandler.OnValoracionesRead onValoracionesRead;
    private static ArrayList<Valoracion> valoracionesArrayList = new ArrayList<>();
    RatingBar ratingBar;
    ConstraintLayout valoracionCons;
    EditText valoracionComentario;
    Button showCreateValoracion, sendValoracion;
    TextView noValoraciones;
    private static String currentUserName,sender,receiverToken,mcorreo,currentUserPfp;

    public ProfileValoracionesFragment() {

    }

    public static ProfileValoracionesFragment newInstance(boolean perfilPropio,String email) {
        ProfileValoracionesFragment fragment = new ProfileValoracionesFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile_valoraciones, container, false);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        sender=sharedPreferences.getString(Constants.SHARED_PREFERENCES_CORREO,"");
        if(perfilPropio){
            mcorreo=sharedPreferences.getString(Constants.SHARED_PREFERENCES_CORREO,"");
        }
        valoracionCons=view.findViewById(R.id.consNewValoracion);
        valoracionComentario=view.findViewById(R.id.etNewValoracionComentario);
        ratingBar=view.findViewById(R.id.rbNewValoracion);
        showCreateValoracion=view.findViewById(R.id.bttnShowNewValoracion);
        noValoraciones=view.findViewById(R.id.noValoraciones);
        sendValoracion=view.findViewById(R.id.bttnNewValoracionEnviar);
        onReadListener=setOnReadListener(onReadListener);
        onValoracionesRead=setOnValoracionesRead(onValoracionesRead);
        onTokenReadListener=setOnTokenReadListener(onTokenReadListener);
        ValoracionHandler valoracionHandler= new ValoracionHandler();
        valoracionHandler.getValoraciones(mcorreo,onValoracionesRead);
        UsuarioHandler usuarioHandler= new UsuarioHandler();
        try {
            usuarioHandler.readUserData(sender,onReadListener,"");
            if(!perfilPropio){
                usuarioHandler.getToken(mcorreo,onTokenReadListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showCreateValoracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valoracionCons.getVisibility()==View.VISIBLE){
                    valoracionCons.setVisibility(View.GONE);
                }else{
                    valoracionCons.setVisibility(View.VISIBLE);
                }
            }
        });

        if(perfilPropio){
            showCreateValoracion.setVisibility(View.GONE);
        }else{
            showCreateValoracion.setVisibility(View.VISIBLE);
        }
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if(valoracionesArrayList.isEmpty()){
                    noValoraciones.setVisibility(View.VISIBLE);
                }
                if(valoracionesArrayList.size()!=0){
                    noValoraciones.setVisibility(View.GONE);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerValoraciones);
                    ValoracionesAdapter valoracionesAdapter = new ValoracionesAdapter(getContext(),valoracionesArrayList);
                    recyclerView.setAdapter(valoracionesAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    for(Valoracion v: valoracionesArrayList){
                        if(v.getSender().equals(sender)){
                            showCreateValoracion.setEnabled(false);
                            showCreateValoracion.setTypeface(showCreateValoracion.getTypeface(), Typeface.ITALIC);
                            showCreateValoracion.setText(showCreateValoracion.getText()+" (Ya has opinado de estx usuarix)");
                            valoracionCons.setVisibility(View.GONE);
                        }
                    }
                    if(perfilPropio){
                        showCreateValoracion.setVisibility(View.GONE);
                        valoracionCons.setVisibility(View.GONE);
                    }
                }
            }
        },300);

        sendValoracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Valoracion valoracion= new Valoracion();
                valoracion.setSenderPfp(currentUserPfp);
                valoracion.setSender(sender);
                valoracion.setReceiver(mcorreo);
                valoracion.setOpinion(valoracionComentario.getText().toString());
                valoracion.setEstrellas(ratingBar.getRating());
                valoracion.setNombreAutor(currentUserName);
                ValoracionHandler valoracionHandler= new ValoracionHandler();
                valoracionHandler.insertValoracion(valoracion);
                sendNotification();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    private void sendNotification(){
        String title = currentUserName+" te ha valorado.";
        String message =ratingBar.getRating()+"/5 - "+valoracionComentario.getText().toString();
        FCMSend.pushNotification(getContext(),receiverToken,title,message,Constants.NOTIFICATION_TYPE_VALORACION);
    }
    private UsuarioHandler.OnReadListener setOnReadListener(UsuarioHandler.OnReadListener onReadListener) {
        onReadListener=new UsuarioHandler.OnReadListener() {
            @Override
            public void onReadSuccess(Usuario usuario) {
                currentUserName=usuario.getNombre()+" "+usuario.getApellido();
                currentUserPfp=usuario.getPfp();
            }

            @Override
            public void onReadError(String error) {
                System.out.println("OCURRIO: "+error);
            }
        };
        return onReadListener;
    }

    private ValoracionHandler.OnValoracionesRead setOnValoracionesRead(ValoracionHandler.OnValoracionesRead onValoracionesRead){
        onValoracionesRead=new ValoracionHandler.OnValoracionesRead() {
            @Override
            public void onReadSuccess(ArrayList<Valoracion> valoracionArrayList) {
                valoracionesArrayList=valoracionArrayList;
            }
        };
        return onValoracionesRead;
    }

    private UsuarioHandler.OnTokenReadListener setOnTokenReadListener(UsuarioHandler.OnTokenReadListener onTokenReadListener){
        onTokenReadListener=new UsuarioHandler.OnTokenReadListener() {
            @Override
            public void onReadTokenSuccess(String token) {
                receiverToken=token;
            }
        };
        return onTokenReadListener;
    }

}