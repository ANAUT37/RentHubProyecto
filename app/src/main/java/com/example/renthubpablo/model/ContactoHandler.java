package com.example.renthubpablo.model;

import androidx.annotation.Nullable;

import com.example.renthubpablo.resources.ContactoUtil;
import com.example.renthubpablo.resources.MensajeUtil;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactoHandler {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getContactosFromUser(String user, OnReadContactos listener){
        db.collection("conversaciones")
                .whereEqualTo("sender",user)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentSnapshot documentChange : value.getDocuments()){
                            ContactoUtil contactoUtil= new ContactoUtil(documentChange.getString("receiver"),
                                    documentChange.getString("anuncio"),
                                    documentChange.getString("titulo"),
                                    documentChange.getString("categoria"));
                            listener.onReadSuccess(contactoUtil);
                        }
                        listener.onReadComplete();
                    }
                });
    }
    public interface OnReadContactos{
        void onReadSuccess(ContactoUtil contactoUtil);
        void onReadComplete();
    }
}
