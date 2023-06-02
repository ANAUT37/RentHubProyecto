package com.example.renthubpablo.model;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.renthubpablo.resources.Constants;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AppHandler {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Context context;

    public AppHandler(){}

    public void checkAppSettings(String campo, OnCheckAppSettingsListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentRef = db.collection(Constants.RUTA_APPSETTINGS).document(Constants.RUTA_APPSETTINGS);

        documentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    listener.onCheckError(error.getMessage());
                    return;
                }
                if (value != null && value.exists()) {
                    try {
                        boolean result = value.getBoolean(campo);
                        listener.onCheckSuccess(result);
                    } catch (Exception e) {
                        listener.onCheckError(e.getMessage());
                    }
                } else {
                    listener.onCheckError("No se encontró el documento");
                }
            }
        });
    }
    public interface OnCheckAppSettingsListener{
        void onCheckSuccess(boolean result);
        void onCheckError(String error);
    }
}
