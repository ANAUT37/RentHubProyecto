package com.example.renthubpablo.view.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.UserHandle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.resources.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileFragment extends Fragment {
    private static UsuarioHandler.OnReadListener onReadListener;
    private static UsuarioHandler usuarioHandler=new UsuarioHandler();
    private static Usuario usuario= new Usuario();
    private static String currentUserCorreo, currentUserPfpName;
    private static Uri imageUri;
    Button save;
    EditText nombre, apellidos, descripcion, telefono;
    CircleImageView newPfp;
    public EditProfileFragment() {

    }

    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        currentUserCorreo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");
        nombre=view.findViewById(R.id.tvEditNombre);
        apellidos=view.findViewById(R.id.tvEditApellidos);
        descripcion=view.findViewById(R.id.etEditDescripcion);
        telefono=view.findViewById(R.id.tvEditTelefono);
        newPfp=view.findViewById(R.id.ivPfpEditProfile);
        newPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE);
            }
        });
        save=view.findViewById(R.id.bttnEditAceptar);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri!=null){
                    usuarioHandler.uploadFotoPerfil(imageUri,currentUserPfpName);
                }
                Map<String, Object> data = new HashMap<>();
                try {
                    if(nombre.getText().length()>0){
                        data.put(Constants.USUARIO_NOMBRE, usuario.encrypt(nombre.getText().toString()));
                    }
                    if(apellidos.getText().length()>0){
                        data.put(Constants.USUARIO_APELLIDO, usuario.encrypt(apellidos.getText().toString()));
                    }
                    if(descripcion.getText().length()>0){
                        data.put(Constants.USUARIO_DESCRIPCION,usuario.encrypt(descripcion.getText().toString()));
                    }
                    if(telefono.getText().length()>0){
                        data.put(Constants.USUARIO_TELEFONO,usuario.encrypt(telefono.getText().toString()));
                    }
                    if(data.size()>0){
                        usuarioHandler.modificarUsuario(data,currentUserCorreo);
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                    }
                } catch (Exception e) {} catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            onReadListener=setOnReadListener(onReadListener);
            usuarioHandler.readUserData(currentUserCorreo,onReadListener,"");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            newPfp.setImageURI(imageUri);
        }
    }

    private UsuarioHandler.OnReadListener setOnReadListener(UsuarioHandler.OnReadListener onReadListener) {
        onReadListener=new UsuarioHandler.OnReadListener() {
            @Override
            public void onReadSuccess(Usuario usuario) {
                nombre.setHint(usuario.getNombre());
                apellidos.setHint(usuario.getApellido());
                descripcion.setHint(usuario.getDescripcion());
                telefono.setHint(usuario.getPhone());
                currentUserPfpName=usuario.getPfp();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference("pfp/"+usuario.getPfp());

                try {
                    File localFile = File.createTempFile("tempfile",".jpg");
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            newPfp.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            newPfp.setImageResource(R.drawable.user_no_pic);
                        }
                    });
                } catch (IOException e) {
                    System.out.println("OCURRIO: "+e.getMessage());
                }
            }

            @Override
            public void onReadError(String error) {
            }
        };
        return onReadListener;
    }
}