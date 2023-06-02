package com.example.renthubpablo.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Anuncio;
import com.example.renthubpablo.controller.Inmueble;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.controller.types.Garaje;
import com.example.renthubpablo.controller.types.Habitacion;
import com.example.renthubpablo.controller.types.Local;
import com.example.renthubpablo.controller.types.Nave;
import com.example.renthubpablo.controller.types.Oficina;
import com.example.renthubpablo.controller.types.Terreno;
import com.example.renthubpablo.controller.types.Trastero;
import com.example.renthubpablo.controller.types.Vivienda;
import com.example.renthubpablo.resources.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AnuncioHandler {
    Context context;
    List<Uri> imagesSliderUris=new ArrayList<>();
    String currentDireccion;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static AnuncioHandler.OnGetImagenesInmuebleListener onGetImagenesInmuebleListener;
    public AnuncioHandler(Context context){this.context=context;}
    public AnuncioHandler(){}

    public synchronized String insertarAnuncio(Anuncio anuncio, String type){
        System.out.println("OCURRIO: CATEGORIA: "+type);
        if(type!=null){
            DocumentReference docRef = db.collection("anuncios")
                    .document(type)
                    .collection(type)
                    .document();

            db.runTransaction(new Transaction.Function<Void>() {
                        @Nullable
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(docRef);
                            if (!snapshot.exists()) {
                                transaction.set(docRef, anuncio);
                            }
                            return null;
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println("OCURRIO: ANUNCIO INSERTADO");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("OCURRIO: ERROR AL INSERTAR ANUNCIO: "+e.getMessage());
                        }
                    });
            return docRef.getId();
        }
        return null;
    }

    public void uploadFotosAnuncio(Uri uri, String name){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.CAPERTA_FOTO_ANUNCIO).child(name);
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }
    public void onReadAnuncios(String id,String categoria,OnReadAnuncioListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentRef = db.collection("anuncios").document(categoria)
                .collection(categoria).document(id);

        documentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    listener.onReadError(Constants.ANUNCIO_404);
                    return;
                }
                if (value != null && value.exists()) {
                    try {
                        AnuncioHandler anuncioHandler = new AnuncioHandler();
                        Anuncio anuncio= new Anuncio();
                        anuncio.setDireccion(value.getString("direccion"));
                        anuncio.setIdAnuncio(value.getId());
                        anuncio.setCategoria(value.getString("categoria"));
                        anuncio.setDescripcion(value.getString("descripcion"));
                        anuncio.setEmailEmisor(value.getString("emailEmisor"));
                        anuncio.setFavoritos(Math.toIntExact(value.getLong("favoritos")));
                        anuncio.setFecha(value.getTimestamp("fechaPublicacion").toDate());
                        anuncio.setImagen((List<String>) value.getData().get("imagen"));
                        anuncio.setInmueble(anuncioHandler.getTipoInmueble(anuncio.getCategoria(),value));
                        anuncio.setLatitud(value.getString("latitud"));
                        anuncio.setLongitud(value.getString("longitud"));
                        anuncio.setPrecio(Math.toIntExact(value.getLong("precio")));
                        anuncio.setTitulo(value.getString("titulo"));
                        anuncio.setVisitas(Math.toIntExact(value.getLong("visitas")));

                        listener.onReadSucess(anuncio);
                    } catch (Exception e) {
                        System.out.println("OCURRIO: EL ERROR: "+e.getMessage()+" "
                                +e.getCause()+""+e.toString());
                        listener.onReadError(e.getMessage());
                    }
                } else {
                    listener.onReadError(Constants.ANUNCIO_404);
                }
            }
        });
    }

    public static Inmueble getTipoInmueble(String categoria, DocumentSnapshot valueAnuncio) {
        HashMap<String, Object> value = (HashMap<String, Object>) valueAnuncio.get("inmueble");
        if(Objects.equals(categoria, "vivienda")){
            System.out.println("OCURRIO: ES VIVIENDA");
            Vivienda vivienda= new Vivienda();
            vivienda.setPiso((String) value.get("piso"));
            vivienda.setAscensor((Boolean) value.get("ascensor"));
            vivienda.setGaraje((Boolean) value.get("garaje"));
            vivienda.setTrastero((Boolean) value.get("trastero"));
            vivienda.setBanyos((String) value.get("banyos"));
            vivienda.setDormitorios((String) value.get("dormitorios"));
            vivienda.setTamanyo((String) value.get("tamanyo"));
            return vivienda;
        }else if(categoria=="garaje"){
            return new Garaje();
        }else if(categoria=="habitacion"){
            return new Habitacion();
        }else if(categoria=="local"){
            return new Local();
        }else if(categoria=="nave"){
            return new Nave();
        }else if(categoria=="oficina"){
            return new Oficina();
        }else if(categoria=="terreno"){
            return new Terreno();
        }else if(categoria=="trastero"){
            return new Trastero();
        }else {
            System.out.println("OCURRIO: NO ES NADA");
            return null;
        }
    }


    public void getImagenesInmueble(String name, OnGetImagenesInmuebleListener listener){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.CAPERTA_FOTO_ANUNCIO + name);

        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Uri imageUri = Uri.fromFile(localFile);
                    listener.onGetImagesSuccess(imageUri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.onGetImagesError(e.getMessage());
                }
            });
        } catch (IOException e) {
            listener.onGetImagesError(e.getMessage());
        }
    }
    public void addVisita(String id, String categoria){
        DocumentReference documentoRef = db.collection("anuncios").document(categoria).collection(categoria).document(id);
        documentoRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    long valorActual = documentSnapshot.getLong("visitas");
                    long nuevoValor = valorActual + 1;
                    documentoRef.update("visitas", nuevoValor)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
                } else {
                }
            }
        });
    }
    public void changeFavorito(String id, String categoria, boolean sumar){
        DocumentReference documentoRef = db.collection("anuncios").document(categoria).collection(categoria).document(id);
        documentoRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    long valorActual = documentSnapshot.getLong("favoritos");
                    long nuevoValor=valorActual;
                    if(sumar){
                        nuevoValor = valorActual + 1;
                    }else{
                        nuevoValor = valorActual - 1;
                    }

                    documentoRef.update("favoritos", nuevoValor)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
                } else {
                }
            }
        });
    }


    public interface OnReadAnuncioListener{
        void onReadSucess(Anuncio anuncio);
        void onReadError(String error);
    }
    public interface OnGetImagenesInmuebleListener{
        void onGetImagesSuccess(Uri uri);
        void onGetImagesComplete();
        void onGetImagesError(String error);
    }


}

