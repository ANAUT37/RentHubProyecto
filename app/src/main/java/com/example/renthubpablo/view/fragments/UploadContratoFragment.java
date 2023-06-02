package com.example.renthubpablo.view.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.controller.contratos.ContratoPlantilla;
import com.example.renthubpablo.model.ContratoHandler;
import com.example.renthubpablo.resources.Constants;

import java.io.File;


public class UploadContratoFragment extends Fragment {
    private static final int REQUEST_CODE_PICK_PDF = 1;
    private String sender;
    private static Usuario usuario=new Usuario();
    EditText title, description;
    TextView archivoFileName,error;
    Button openFinder, aceptar;
    static File archivo;
    static Uri uri;

    public UploadContratoFragment() {

    }

    public static UploadContratoFragment newInstance() {
        UploadContratoFragment fragment = new UploadContratoFragment();
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
        View view=inflater.inflate(R.layout.fragment_upload_contrato, container, false);
        openFinder=view.findViewById(R.id.bttnContratoSubirPersonalizadoSeleccionar);
        aceptar=view.findViewById(R.id.bttnUploadContratoPersonalizado);
        title=view.findViewById(R.id.etNewContratoTitle);
        description=view.findViewById(R.id.etNewContratoDescripcion);
        archivoFileName=view.findViewById(R.id.tvNewContratoFileName);
        error=view.findViewById(R.id.tvErrorMessageNewContrato);

        SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sender=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");

        openFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Selecciona el nuevo contrato"), REQUEST_CODE_PICK_PDF);
            }
        });
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().length()==0){
                    setErrorMessage("Debes introducir un título");
                }else{
                    if(description.getText().length()==0){
                        setErrorMessage("Debes introducir una descripción");
                    }else{
                        if(uri==null){
                            setErrorMessage("Debes seleccionar un archivo");
                        }else{
                            ContratoPlantilla contratoPlantilla=new ContratoPlantilla();
                            String fileName=Usuario.generarNombreImagenPerfil()+".pdf";
                            contratoPlantilla.setArchivo(archivo);
                            contratoPlantilla.setTitle(title.getText().toString());
                            contratoPlantilla.setDescription(description.getText().toString());
                            contratoPlantilla.setOwner(sender);
                            contratoPlantilla.setFileName(fileName);
                            ContratoHandler contratoHandler=new ContratoHandler();
                            contratoHandler.insertNewPlantilla(contratoPlantilla,uri);
                        }
                    }
                }
            }
        });
        return view;
    }

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String pdfName="";
        /*
        if (requestCode == REQUEST_CODE_PICK_PDF && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    String name=getNombreArchivoOriginal(uri);
                    archivo = new File(data.getData().getPath());
                    archivoFileName.setText(name);
                }
            }
        }

         */

        if (requestCode == REQUEST_CODE_PICK_PDF && resultCode == RESULT_OK) {
            uri=data.getData();
            String name=getNombreArchivoOriginal(uri);
            if(uri.toString().startsWith("content://")){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Cursor cursor= getActivity().getContentResolver().query(uri,null,null,null);
                    if(cursor!=null&&cursor.moveToFirst()){

                        pdfName=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            }else if(uri.toString().startsWith("file://")){
                pdfName=new File(uri.toString()).getName();
            }
            archivoFileName.setText(name);
        }
    }
    private String getNombreArchivoOriginal(Uri uri) {
        String originalFileName = null;
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
            cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                originalFileName = cursor.getString(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return originalFileName;
    }
    private void setErrorMessage(String errorMessage) {
        error.setVisibility(View.VISIBLE);
        error.setText(errorMessage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                error.setVisibility(View.GONE);
            }
        }, 3000);
    }
}