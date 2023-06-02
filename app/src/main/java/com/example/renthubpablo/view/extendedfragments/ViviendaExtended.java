package com.example.renthubpablo.view.extendedfragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.renthubpablo.R;


public class ViviendaExtended extends Fragment {
    private FrameLayout frameLayout;
    TextView tvMetros, tvBanyos, tvDormitorios, tvPiso,mGaraje,mAscensor,mTrastero;
    static String metros, doritorios, banyos, piso;
    boolean garaje, ascensor, trastero;

    public ViviendaExtended() {
    }

    public static ViviendaExtended newInstance(String metros, String doritorios, String banyos, String piso, boolean garaje, boolean ascensor, boolean trastero) {
        ViviendaExtended fragment = new ViviendaExtended();
        Bundle args= new Bundle();
        args.putString("metros",metros);
        args.putString("dormitorios",doritorios);
        args.putString("banyos",banyos);
        args.putString("piso",piso);
        args.putBoolean("garaje",garaje);
        args.putBoolean("ascensor",ascensor);
        args.putBoolean("trastero",trastero);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            metros=getArguments().getString("metros");
            doritorios=getArguments().getString("dormitorios");
            banyos=getArguments().getString("banyos");
            piso=getArguments().getString("piso");
            garaje=getArguments().getBoolean("garaje");
            ascensor=getArguments().getBoolean("ascensor");
            trastero=getArguments().getBoolean("trastero");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_vivienda_extended, container, false);
        tvMetros=view.findViewById(R.id.tvExViviendaMetros);
        tvBanyos=view.findViewById(R.id.tvExViviendaBanyo);
        tvDormitorios =view.findViewById(R.id.tvExViviendaHabitacion);
        tvPiso =view.findViewById(R.id.tvExViviendaPiso);
        mGaraje=view.findViewById(R.id.tvExViviendaGaraje);
        mTrastero=view.findViewById(R.id.tvExViviendaTrastero);
        mAscensor=view.findViewById(R.id.tvExViviendaAscensor);

        tvMetros.setText(metros+" m/2");
        tvBanyos.setText(banyos+" baños");
        tvDormitorios.setText(doritorios+ " habs");
        tvPiso.setText(piso+" planta");
        mGaraje.setText(mGaraje.getText().toString()+" "+setBoleans(garaje));
        mTrastero.setText(mTrastero.getText().toString()+" "+setBoleans(trastero));
        mAscensor.setText(mAscensor.getText().toString()+" "+setBoleans(ascensor));

        return view;
    }
    private String setBoleans(boolean var){
        if(var){
            return "Sí";
        }else {
            return "No";
        }
    }
}