package com.example.renthubpablo.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.types.Valoracion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Handler;

public class ValoracionHandler {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ValoracionHandler() {
    }

    public void insertValoracion(Valoracion valoracion){
        HashMap<String,Object> data= new HashMap<>();
        data.put("sender",valoracion.getSender());
        data.put("nombreAutor",valoracion.getNombreAutor());
        data.put("fotoAutor",valoracion.getSenderPfp());
        data.put("opinion",valoracion.getOpinion());
        data.put("estrellas",valoracion.getEstrellas());
        data.put("receiver",valoracion.getReceiver());
        db.collection("valoraciones").add(data);
    }
    public void getValoraciones(String correo, OnValoracionesRead listener){
        db.collection("valoraciones")
                .whereEqualTo("receiver",correo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Valoracion> valoracionArrayList= new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Valoracion valoracion=new Valoracion();
                                valoracion.setNombreAutor(document.getString("nombreAutor"));
                                valoracion.setReceiver(correo);
                                valoracion.setSender(document.getString("sender"));
                                valoracion.setEstrellas(document.getLong("estrellas"));
                                valoracion.setOpinion(document.getString("opinion"));
                                valoracion.setSenderPfp(document.getString("fotoAutor"));
                                valoracionArrayList.add(valoracion);
                            }
                            listener.onReadSuccess(valoracionArrayList);
                        } else {
                        }
                    }
                });
    }

    public interface OnValoracionesRead{
        void onReadSuccess(ArrayList<Valoracion> valoracionArrayList);
    }
}
