package com.example.renthubpablo.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.contratos.ContratoPlantilla;
import com.example.renthubpablo.model.ContratoHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.view.adapter.ContratoAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContratosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContratosFragment extends Fragment {
    private static ContratoHandler contratoHandler= new ContratoHandler();
    public static ContratoHandler.OnReadDefaultContratosListener onReadDefaultContratosListener;
    public static ContratoHandler.OnReadDefaultContratosListener onReadPersonalizadosContratosListener;
    private static ArrayList<ContratoPlantilla> contratosDefault;
    private static ArrayList<ContratoPlantilla> contratosPersonalizados;
    private static RecyclerView recyclerContratosDefault, recyclerContratosPersonalizados;
    private static ContratoAdapter contratoDefaultAdapter, contratoPersonalizadosAdapter;
    private static String sender;
    TextView noContratosPersonalizados;
    Button subirContrato;
    public ContratosFragment() {
        // Required empty public constructor
    }

    public static ContratosFragment newInstance(String param1, String param2) {
        ContratosFragment fragment = new ContratosFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contratosDefault=new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contratos, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sender=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");

        noContratosPersonalizados=view.findViewById(R.id.noContratosPersonalizados);
        onReadDefaultContratosListener=setOnReadDefaultContratosListener(onReadDefaultContratosListener);
        onReadPersonalizadosContratosListener=setOnReadPersonalizadosContratosListener(onReadPersonalizadosContratosListener);
        contratoHandler.readDefaultContratos("renthub",onReadDefaultContratosListener);
        contratoHandler.readDefaultContratos(sender,onReadPersonalizadosContratosListener);
        recyclerContratosPersonalizados=view.findViewById(R.id.rvContratosPersonalizados);


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerContratosDefault=view.findViewById(R.id.rvContratosDefault);
                contratoDefaultAdapter=new ContratoAdapter(contratosDefault, getContext(), new ContratoAdapter.OnContratoClick() {
                    @Override
                    public void onContratoClick(ContratoPlantilla contratoPlantilla) {
                        PDFViewFragment pdfViewFragment = PDFViewFragment.newInstance(contratoPlantilla.getArchivo(), contratoPlantilla.getTitle());
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(android.R.id.content, pdfViewFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onImageClick(Bitmap image) {

                    }
                });
                recyclerContratosDefault.setAdapter(contratoDefaultAdapter);
                recyclerContratosDefault.setLayoutManager(new LinearLayoutManager(getContext()));

                contratoPersonalizadosAdapter= new ContratoAdapter(contratosPersonalizados, getContext(), new ContratoAdapter.OnContratoClick() {
                    @Override
                    public void onContratoClick(ContratoPlantilla contrato) {
                        PDFViewFragment pdfViewFragment = PDFViewFragment.newInstance(contrato.getArchivo(), contrato.getTitle());
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(android.R.id.content, pdfViewFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onImageClick(Bitmap image) {

                    }
                });
                recyclerContratosPersonalizados.setAdapter(contratoPersonalizadosAdapter);
                recyclerContratosPersonalizados.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        },500);
        subirContrato=view.findViewById(R.id.bttnContratoSubirPerdonalizado);
        subirContrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadContratoFragment UPLOAD_CONTRATO_FRAGMENT=new UploadContratoFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(android.R.id.content, UPLOAD_CONTRATO_FRAGMENT);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    private ContratoHandler.OnReadDefaultContratosListener setOnReadDefaultContratosListener(ContratoHandler.OnReadDefaultContratosListener onReadDefaultContratosListener) {
        onReadDefaultContratosListener=new ContratoHandler.OnReadDefaultContratosListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onReadSuccess(ArrayList<ContratoPlantilla> contratoArrayList) {
                contratosDefault=contratoArrayList;
                System.out.println("OCURRIO: LOS RECIBO");
            }
        };
        return onReadDefaultContratosListener;
    }

    private ContratoHandler.OnReadDefaultContratosListener setOnReadPersonalizadosContratosListener(ContratoHandler.OnReadDefaultContratosListener onReadDefaultContratosListener) {
        onReadDefaultContratosListener=new ContratoHandler.OnReadDefaultContratosListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onReadSuccess(ArrayList<ContratoPlantilla> contratoArrayList) {
                contratosPersonalizados=contratoArrayList;
                if(contratosPersonalizados.size()!=0){
                    noContratosPersonalizados.setVisibility(View.GONE);
                }
                System.out.println("OCURRIO: LOS RECIBO");
            }
        };
        return onReadDefaultContratosListener;
    }
}