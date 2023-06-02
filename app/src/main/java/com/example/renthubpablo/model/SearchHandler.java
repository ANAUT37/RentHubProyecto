package com.example.renthubpablo.model;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.renthubpablo.controller.Anuncio;
import com.example.renthubpablo.resources.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchHandler {
    Context context;
    private static AnuncioHandler.OnGetImagenesInmuebleListener onGetImagenesInmuebleListener;
    public static List<Uri> imagesSliderUris=new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public SearchHandler(){}

    public synchronized void realizarBusqueda(String categoria, double latitud,double longitud,double radius,OnRealizarBusquedaListener listener){
        onGetImagenesInmuebleListener=setOnGetImagenesInmuebleListener(onGetImagenesInmuebleListener);

        double latMin = latitud - radius;
        double latMax = latitud + radius;
        double lngMin = longitud - radius;
        double lngMax = longitud + radius;
        GeoPoint localizacionMax = new GeoPoint(latMax,lngMax);
        GeoPoint localizacionMin = new GeoPoint(latMin,lngMin);
        if(categoria!=null&&categoria!=""){
            CollectionReference docRef = db.collection("anuncios")
                    .document(categoria)
                    .collection(categoria);

            Query query=docRef
                    .whereGreaterThanOrEqualTo("localizacion",localizacionMin)
                            .whereLessThanOrEqualTo("localizacion",localizacionMax);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot snapshot) {
                            System.out.println("OCURRIO: NULL? "+snapshot.isEmpty());
                            if(snapshot!=null&&!snapshot.isEmpty()){
                                System.out.println("OCURRIO: RESULTADOS: "+snapshot.size());
                                for(DocumentSnapshot value: snapshot.getDocuments()){
                                    AnuncioHandler anuncioHandler = new AnuncioHandler();
                                    Anuncio anuncio=new Anuncio();
                                    anuncio.setIdAnuncio(value.getId());
                                    anuncio.setDireccion(value.getString("direccion"));
                                    anuncio.setCategoria(value.getString("categoria"));
                                    anuncio.setDescripcion(value.getString("descripcion"));
                                    anuncio.setEmailEmisor(value.getString("emailEmisor"));
                                    anuncio.setFavoritos(Math.toIntExact(value.getLong("favoritos")));
                                    //anuncio.setFechaPublicacion(new Timestamp(new Date(value.getString("fechaPublicacion"))));
                                    anuncio.setImagen((List<String>) value.getData().get("imagen"));
                                    System.out.println("OCURRIO: IMG SIZE: "+anuncio.getImagen().size());
                                    anuncio.setTitulo(value.getString("titulo"));
                                    setImagesToAnuncios(anuncio.getImagen());
                                    anuncio.setInmueble(anuncioHandler.getTipoInmueble(categoria,value));
                                    anuncio.setLatitud(value.getString("latitud"));
                                    anuncio.setLongitud(value.getString("longitud"));
                                    anuncio.setPrecio(Math.toIntExact(value.getLong("precio")));
                                    anuncio.setVisitas(Math.toIntExact(value.getLong("visitas")));
                                    System.out.println("OCURRIO: COMPLETE EL ANUNCIO "+anuncio.toString());
                                    anuncio.setImagesUri(imagesSliderUris);
                                    imagesSliderUris.clear();
                                    listener.onBusquedaSuccess(anuncio);

                                }
                                listener.onBusquedaCompleta();

                            }else {
                                listener.onBusquedaError("No se encontraron resultados");
                            }
                        }
                    });
        }
    }
    private void setImagesToAnuncios(List<String> imagesToAnuncios){
        for(String nombreFoto: imagesToAnuncios){
            getImagenesInmueble(nombreFoto,onGetImagenesInmuebleListener);
        }
        //anuncio.setImagesUri(imagesSliderUris);
        //imagesSliderUris.clear();
    }
    public void getImagenesInmueble(String name, AnuncioHandler.OnGetImagenesInmuebleListener listener){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.CAPERTA_FOTO_ANUNCIO + name);
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Uri imageUri = Uri.fromFile(localFile);
                    System.out.println("OCURRIO: URI FOUND: "+imageUri);
                    imagesSliderUris.add(imageUri);
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
    private AnuncioHandler.OnGetImagenesInmuebleListener setOnGetImagenesInmuebleListener(AnuncioHandler.OnGetImagenesInmuebleListener onGetImagenesInmuebleListener){
        onGetImagenesInmuebleListener=new AnuncioHandler.OnGetImagenesInmuebleListener() {
            @Override
            public void onGetImagesSuccess(Uri uri) {
                //imagesSliderUris.add(uri);
            }

            @Override
            public void onGetImagesComplete() {

            }

            @Override
            public void onGetImagesError(String error) {

            }
        };
        return onGetImagenesInmuebleListener;
    }
    public interface OnRealizarBusquedaListener{
        void onBusquedaSuccess(Anuncio anuncio);
        void onBusquedaCompleta();
        void onBusquedaError(String error);
    }

}
