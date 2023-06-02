package com.example.renthubpablo.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renthubpablo.R;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.util.FileUtils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class PDFViewFragment extends Fragment {
    private static final int REQUEST_CODE_GUARDAR_PDF = 1;
    private static final int REQUEST_CODE_SELECCIONAR_RUTA = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private static PDFView pdfView;
    private static TextView titulo, noArchivo;
    private static Button download;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static File archivo;
    private String title;
    public PDFViewFragment() {

    }

    public static PDFViewFragment newInstance(File file, String param2) {
        PDFViewFragment fragment = new PDFViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, file);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            archivo = (File) getArguments().getSerializable(ARG_PARAM1);
            title = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_p_d_f_view, container, false);
        titulo=view.findViewById(R.id.tvWhatFDFtitle);
        titulo.setText(title);
        pdfView=view.findViewById(R.id.pdfView);
        noArchivo=view.findViewById(R.id.noArchivoEncontrado);
        download=view.findViewById(R.id.bttnDownloadFile);
        if(archivo!=null){
            noArchivo.setVisibility(View.GONE);
            pdfView.fromFile(archivo)
                    .load();
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermissionAndSavePdf(archivo);
                }
            });
        }else{
            download.setVisibility(View.GONE);
            noArchivo.setVisibility(View.VISIBLE);
        }



        return view;
    }

    private void checkPermissionAndSavePdf(File sourceFile) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            savePdfToFile(sourceFile);
        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                savePdfToFile(archivo);
            } else {
                Toast.makeText(getActivity(), "Permiso denegado. No se puede guardar el PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePdfToFile(File sourceFile) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "RentHub_"+title.replace(' ','_')+".pdf");

        startActivityForResult(intent, REQUEST_CODE_GUARDAR_PDF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GUARDAR_PDF && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                File destinationFile = new File(uri.getPath());
                if (copyFile(archivo, destinationFile)) {
                    Toast.makeText(getActivity(), "PDF guardado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error al guardar el PDF", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean copyFile(File sourceFile, File destinationFile) {
        try {
            FileInputStream inputStream = new FileInputStream(archivo);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}