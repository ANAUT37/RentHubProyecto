package com.example.renthubpablo.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.renthubpablo.controller.contratos.ContratoPlantilla;
import com.example.renthubpablo.resources.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ContratoHandler {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void readDefaultContratos(String owner, OnReadDefaultContratosListener listener) {
        db.collection(Constants.COLLECTION_PLANTILLAS)
                .whereEqualTo("owner", owner)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                ArrayList<ContratoPlantilla> contratoArrayList=new ArrayList<>();
                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                    ContratoPlantilla contrato=new ContratoPlantilla();
                                    contrato.setTitle(documentSnapshot.getString("title"));
                                    contrato.setDescription(documentSnapshot.getString("description"));
                                    contrato.setFileName(documentSnapshot.getString("fileName"));
                                    contrato.setOwner(documentSnapshot.getString("owner"));

                                    String location="usercontratos/";
                                    if(documentSnapshot.getString("owner").equals("renthub")){
                                        location="appcontratos/";
                                    }

                                    try {
                                        StorageReference fileRef = FirebaseStorage.getInstance().getReference( location+ documentSnapshot.getString("fileName"));
                                        final File archivo = File.createTempFile("tempfile", ".pdf");
                                        fileRef.getFile(archivo).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                contrato.setArchivo(archivo);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                                    } catch (IOException e) {
                                    }
                                    contratoArrayList.add(contrato);
                                }


                                listener.onReadSuccess(contratoArrayList);
                            }
                        } else {
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void readUnoContratos(String fileName, OnReadUnoContratoListener listener) {
        db.collection(Constants.COLLECTION_PLANTILLAS)
                .whereEqualTo("fileName", fileName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                    ContratoPlantilla contrato = new ContratoPlantilla();
                                    contrato.setTitle(documentSnapshot.getString("title"));
                                    contrato.setDescription(documentSnapshot.getString("description"));
                                    contrato.setFileName(documentSnapshot.getString("fileName"));
                                    contrato.setOwner(documentSnapshot.getString("owner"));
                                    try {
                                        StorageReference fileRef = FirebaseStorage.getInstance().getReference("usercontratos/" + documentSnapshot.getString("fileName"));
                                        final File archivo = File.createTempFile("tempfile", ".pdf");
                                        fileRef.getFile(archivo).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                contrato.setArchivo(archivo);
                                                listener.onReadSuccess(contrato);
                                            }
                                        });
                                    } catch (IOException e) {
                                        System.out.println("OCURRIO: " + e.getMessage());
                                    }
                                }
                            }
                        } else {
                            System.out.println("OCURRIO: SIN CONTRATOS");
                        }
                    }
                });
    }

    public void insertNewPlantilla(ContratoPlantilla contratoPlantilla,Uri uri) {
        HashMap<String,Object> data=new HashMap<>();
        data.put("description",contratoPlantilla.getDescription());
        data.put("title",contratoPlantilla.getTitle());
        data.put("owner",contratoPlantilla.getOwner());
        data.put("fileName",contratoPlantilla.getFileName());
        db.collection("plantillas").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                StorageReference storageRef = FirebaseStorage.getInstance()
                        .getReference("usercontratos/").child(contratoPlantilla.getFileName());

                storageRef.putFile(uri)
                        .addOnSuccessListener(taskSnapshot -> {
                        })
                        .addOnFailureListener(exception -> {
                        });
            }
        });
    }

    public interface OnReadUnoContratoListener{
        void onReadSuccess(ContratoPlantilla contratoPlantilla);
    }
    public interface OnReadDefaultContratosListener{
        void onReadSuccess(ArrayList<ContratoPlantilla> contratoArrayList);
    }
}
