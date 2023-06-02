package com.example.renthubpablo.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.resources.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UsuarioHandler extends AppCompatActivity {
    Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public UsuarioHandler(Context context){
        this.context=context;
    }
    public UsuarioHandler(){}

    public void registerUser(Usuario usuario, OnRegisterListener listener) throws Exception {
        System.out.println("OCURRIO: "+usuario);
        TreeMap<String,Object> datosUsuario= new TreeMap<>();
        datosUsuario.put("nombre",usuario.getNombre());
        datosUsuario.put("apellido",usuario.getApellido());
        datosUsuario.put("email",usuario.getEmail());
        datosUsuario.put("genero",usuario.getGenero());
        datosUsuario.put("pronombres",usuario.getPronombres());
        datosUsuario.put("password",usuario.getPassword());
        datosUsuario.put("pfp",usuario.getPfp());
        datosUsuario.put("phone",usuario.getPhone());
        datosUsuario.put("descripcion",usuario.getDescripcion());
        datosUsuario.put("fechanacimiento",usuario.getDate());
        datosUsuario.put("estado",usuario.isEstado());

        String correoAsId=usuario.desencriptar(usuario.getEmail());


        db.collection("usuarios").document(correoAsId).set(datosUsuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onRegisterSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    public void uploadFotoPerfil(Uri uri, String name){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.CARPETA_FOTO_PERFIL).child(name);
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    public void modificarUsuario(Map<String, Object> data , String currentCorreo){
        DocumentReference documentReference=db.collection(Constants.COLLECTION_USUARIOS).document(currentCorreo);
        documentReference.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }
    public void loginUser(String email, String password, OnLoginListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectionRef = db.collection("usuarios");
        DocumentReference documentRef = collectionRef.document(email);

        documentRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    String hashedPassword = snapshot.getString("password");
                    if (password.equals(hashedPassword)) {

                        listener.onLoginSuccess();
                    } else {
                        listener.onPasswordIncorrect();
                    }
                } else {
                    listener.onUserNotFound();
                }
            } else {
                listener.onLoginError();
            }
        });
    }
    public void readUserData(String email, OnReadListener listener, String titulo) throws Exception {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentRef = db.collection("usuarios").document(email);

        documentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    listener.onReadError(error.getMessage());
                    return;
                }
                if (value != null && value.exists()) {
                    Usuario usuario = new Usuario();
                    try {
                        usuario.setEmail(usuario.desencriptar(value.getString(Constants.USUARIO_EMAIL)));
                        usuario.setNombre(usuario.desencriptar(value.getString(Constants.USUARIO_NOMBRE)));
                        usuario.setApellido(usuario.desencriptar(value.getString(Constants.USUARIO_APELLIDO)));
                        usuario.setDescripcion(usuario.desencriptar(value.getString(Constants.USUARIO_DESCRIPCION)));
                        usuario.setGenero(usuario.desencriptar(value.getString(Constants.USUARIO_GENERO)));
                        usuario.setPronombres(usuario.desencriptar(value.getString(Constants.USUARIO_PRONOMBRES)));
                        usuario.setPfp(value.getString(Constants.USUARIO_FOTO_PERFIL));
                        usuario.setDate(usuario.desencriptar(value.getString(Constants.USUARIO_FECHA)));
                        usuario.setPhone(usuario.desencriptar(value.getString(Constants.USUARIO_TELEFONO)));
                        usuario.setEstado(value.getBoolean(Constants.USUARIO_ESTADO_CUENTA));
                        if(titulo.length()>0){
                            usuario.setDescripcion(titulo);
                        }

                        listener.onReadSuccess(usuario);
                    } catch (Exception e) {
                        listener.onReadError(e.getMessage());
                    }
                } else {
                    listener.onReadError("No se encontró el documento");
                }
            }
        });
    }

    public void updateIdAnuncioUser(String id,String categoria,Context context) {
        System.out.println("OCURRIO: id: "+id);
        Usuario usuario = new Usuario();
        String correo=usuario.getCurrentUserEmail(context);
        Map<String,HashMap> modificador=new HashMap<>();
        HashMap<String,String> idAnuncio=new HashMap<>();
        idAnuncio.put(id,categoria);
        modificador.put("anuncios",idAnuncio);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection("usuarios").document(correo);

        documentRef.set(modificador, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("OCURRIO: Campo lista agregado correctamente");
                        } else {
                            System.out.println("OCURRIO: Error al agregar el campo lista: " + task.getException().getMessage());
                        }
                    }
                });
    }
    public void readUserAnuncios(String email,OnUserAnuncioListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentRef = db.collection("usuarios").document(email);

        documentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    listener.onUserAnuncioError(error.getMessage());
                    return;
                }
                if (value != null && value.exists()) {
                    Usuario usuario = new Usuario();
                    try {
                        Map<String,String> anunciosIds=new HashMap<>();
                        Map<String, String> anunciosMap = (Map<String, String>) value.getData().get("anuncios");
                        for (Map.Entry<String, String> entry : anunciosMap.entrySet()) {
                            anunciosIds.put(entry.getKey(),entry.getValue());
                        }
                        if(anunciosIds!=null){
                            listener.onUserAnuncioSuccess(anunciosIds);
                        }else{
                            listener.onUserAnuncioError("no-data");
                        }
                    } catch (Exception e) {
                        listener.onUserAnuncioError(e.getMessage());
                    }
                } else {
                    listener.onUserAnuncioError("No se encontró el documento");
                }
            }
        });
    }

    public void createUserToken(OnTokenListener listener){
        Task<String> messaging = FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                listener.onCreateTokenSuccess(s);
            }
        });
    }

    public void setToken(String correo,String token) {
        HashMap data= new HashMap();
        data.put("token",token);
        data.put("owner",correo);
        db.collection("tokens").add(data);
    }

    public void getToken(String correo, OnTokenReadListener onTokenReadListener){
        db.collection("tokens")
                .whereEqualTo("owner",correo)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.size()>0){
                            onTokenReadListener.onReadTokenSuccess(value.getDocuments().get(0).getString("token"));
                        }else{
                            onTokenReadListener.onReadTokenSuccess("");
                        }
                    }
                });
    }

    public void removeToken(String correo) {
        db.collection("tokens")
                .whereEqualTo("owner", correo)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            document.getReference().delete();
                        }
                    }
                });
    }




    public interface OnRegisterListener{
        void onRegisterSuccess();
    }

    public interface OnLoginListener {
        void onLoginSuccess();
        void onPasswordIncorrect();
        void onUserNotFound();
        void onLoginError();
    }

    public interface OnReadListener{
        void onReadSuccess(Usuario usuario);
        void onReadError(String error);
    }

    public interface OnUserAnuncioListener{
        void onUserAnuncioSuccess(Map<String,String> anunciosIds);
        void onUserAnuncioError(String error);
    }

    public interface OnTokenListener{
        void onCreateTokenSuccess(String token);
    }
    public interface OnTokenReadListener{
        void onReadTokenSuccess(String token);
    }


}

