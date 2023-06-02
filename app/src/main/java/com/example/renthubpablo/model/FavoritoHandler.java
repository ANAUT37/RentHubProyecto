package com.example.renthubpablo.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.renthubpablo.resources.FavoritoUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FavoritoHandler {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void setFavorito(FavoritoUtil favoritoUtil){
        HashMap<String,Object> data=new HashMap<>();
        data.put("anuncio",favoritoUtil.getAnuncio());
        data.put("categoria",favoritoUtil.getCategoria());
        data.put("correo",favoritoUtil.getUser());

        db.collection("favoritos").add(data);
    }
    public void removeFavorito(FavoritoUtil favoritoUtil){
        db.collection("favoritos")
                .whereEqualTo("anuncio", favoritoUtil.getAnuncio())
                .whereEqualTo("categoria", favoritoUtil.getCategoria())
                .whereEqualTo("correo", favoritoUtil.getUser())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                    documentSnapshot.getReference().delete();
                                }
                            }
                        } else {
                        }
                    }
                });
    }
    public void verificarFavorito(FavoritoUtil favoritoUtil, OnFavoritoVerificadoListener listener) {
        db.collection("favoritos")
                .whereEqualTo("anuncio", favoritoUtil.getAnuncio())
                .whereEqualTo("categoria", favoritoUtil.getCategoria())
                .whereEqualTo("correo", favoritoUtil.getUser())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean existeResultado = false;
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                existeResultado = true;
                            }
                        } else {
                            listener.onFavoritoVerificado(existeResultado);
                        }
                        listener.onFavoritoVerificado(existeResultado);
                    }
                });
    }
    public interface OnFavoritoVerificadoListener{
        void onFavoritoVerificado(boolean existe);
    }
}
