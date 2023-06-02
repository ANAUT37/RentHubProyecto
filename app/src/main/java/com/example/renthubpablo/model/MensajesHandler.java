package com.example.renthubpablo.model;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.example.renthubpablo.resources.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MensajesHandler {
    private static FirebaseFirestore db=FirebaseFirestore.getInstance();

    public void sendMessage(String message, String sender, String receiver, String type, Date timestamp,String anuncio){
        HashMap<String,Object>data=new HashMap<>();
        data.put("mensaje",message);
        data.put("sender",sender);
        data.put("receiver",receiver);
        data.put("type",type);
        data.put("timestamp",timestamp);
        data.put("anuncio",anuncio);

        db.collection("mensajes").add(data);

    }

    public void createChat(String sender, String receiver, String id, String titulo,String categoria){
            HashMap<String,Object>data=new HashMap<>();
            data.put("sender",sender);
            data.put("receiver",receiver);
            data.put("anuncio",id);
            data.put("titulo",titulo);
            data.put("categoria",categoria);
            db.collection("conversaciones").add(data);

            HashMap<String,Object>dataDos=new HashMap<>();
            dataDos.put("sender",receiver);
            dataDos.put("receiver",sender);
            dataDos.put("anuncio",id);
            dataDos.put("titulo",titulo);
            dataDos.put("categoria",categoria);
            db.collection("conversaciones").add(dataDos);
    }

    public void uploadImagenMensaje(Uri uri, String name){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.CARPETA_FOTO_MENSAJE).child(name);
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });


    }
}
