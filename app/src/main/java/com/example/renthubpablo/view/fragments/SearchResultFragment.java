package com.example.renthubpablo.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Anuncio;
import com.example.renthubpablo.model.FavoritoHandler;
import com.example.renthubpablo.model.SearchHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.resources.FavoritoUtil;
import com.example.renthubpablo.view.AnuncioViewExtended;
import com.example.renthubpablo.view.adapter.FeedItemAdapter;
import com.example.renthubpablo.view.adapter.SelectListenerFeed;

import java.util.ArrayList;
import java.util.Locale;


public class SearchResultFragment extends Fragment implements SelectListenerFeed {
    String categoria, direccion,correo;
    boolean filtrosClicked=false;
    static double latitud,longitud, rango=0.1;
    private ProgressBar waitingForResults;
    //LISTENERS
    private static SearchHandler.OnRealizarBusquedaListener onRealizarBusquedaListener;
    public static  SelectListenerFeed selectListenerFeed;

    FeedItemAdapter feedItemAdapter;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    static ArrayList<Anuncio> anunciosList=new ArrayList<>();

    RecyclerView recyclerView;
    ConstraintLayout consFiltros;
    Button filtros, applyFiltros;
    SeekBar seekPrecioMinimo,seekPrecioMaximo, seekDistancia;
    TextView noResultados, numResultados, descBusqueda, precioMin,precioMax,distanciaMax;
    LayoutInflater inflater;
    ViewGroup container;
    Bundle savedInstanceState;
    public SearchResultFragment() {
    }
    public static SearchResultFragment newInstance(String categoria, double latitud, double longitud, String direccion) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString("direccion",direccion);
        args.putString("categoria", categoria);
        args.putDouble("latitud", latitud);
        args.putDouble("longitud", longitud);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoria = getArguments().getString("categoria");
            latitud = getArguments().getDouble("latitud");
            longitud = getArguments().getDouble("longitud");
            direccion=getArguments().getString("direccion");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater=inflater;
        this.container=container;
        this.savedInstanceState=savedInstanceState;
        anunciosList.clear();
        View view=inflater.inflate(R.layout.fragment_search_result, container, false);
        waitingForResults=view.findViewById(R.id.pbFeedWaiting);
        noResultados=view.findViewById(R.id.tvNoResultados);
        recyclerView= view.findViewById(R.id.rvFeed);
        numResultados=view.findViewById(R.id.tvNumResultados);
        descBusqueda=view.findViewById(R.id.tvDescBusqueda);
        filtros=view.findViewById(R.id.bttnFiltrosOpen);
        consFiltros=view.findViewById(R.id.consFiltrosSelector);
        seekPrecioMinimo=view.findViewById(R.id.seekBarPrecioMinimo);
        seekPrecioMaximo=view.findViewById(R.id.seekBarPrecioMaximo);
        seekDistancia=view.findViewById(R.id.seekBarDistancia);
        precioMax=view.findViewById(R.id.tvPrecioMaxi);
        precioMin=view.findViewById(R.id.tvPrecioMini);
        distanciaMax=view.findViewById(R.id.tvDistanciaMax);
        applyFiltros=view.findViewById(R.id.bttnFiltrosAplicar);
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String correo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");
        descBusqueda.setText(Character.toUpperCase(categoria.charAt(0)) + categoria.substring(1)+" en "+direccion);

        selectListenerFeed=setSelectListenerFeed(selectListenerFeed);
        onRealizarBusquedaListener=setOnRealizarBusquedaListener(onRealizarBusquedaListener);
        makeSearch();

        numResultados.setText("Resultados: "+anunciosList.size());

        filtros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filtrosClicked==false){
                    consFiltros.setVisibility(View.VISIBLE);
                    filtros.setBackgroundResource(R.drawable.bg_button_03_clicked);
                    filtrosClicked=true;
                }else{
                    consFiltros.setVisibility(View.GONE);
                    filtros.setBackgroundResource(R.drawable.bg_button_03);
                    filtrosClicked=false;
                }
            }
        });
        applyFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSearch();
            }
        });
        setClickListenersSeek();
        return view;
    }
    private void makeSearch(){
        SearchHandler searchHandler= new SearchHandler();
        feedItemAdapter= new FeedItemAdapter(getContext(),anunciosList,selectListenerFeed,correo,getFragmentManager());
        searchHandler.realizarBusqueda(categoria,latitud,longitud,rango,onRealizarBusquedaListener);

    }

    private void setClickListenersSeek() {
        seekPrecioMinimo.setMax(Constants.VALORES_FILTRO_PRECIO_MINIMO.length-1);
        seekPrecioMinimo.setProgress(Constants.VALORES_FILTRO_PRECIO_MINIMO[0]);
        precioMin.setText("Cualquiera");
        seekPrecioMinimo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekPrecioMinimo.getProgress()<seekPrecioMaximo.getProgress()){
                    if(progress==Constants.VALORES_FILTRO_PRECIO_MINIMO[0]){
                        precioMin.setText("Cualquiera");
                    }else{
                        precioMin.setText(Constants.VALORES_FILTRO_PRECIO_MINIMO[progress]+"€");
                    }
                }else{
                    seekBar.setProgress(Constants.VALORES_FILTRO_PRECIO_MINIMO[0]);
                    precioMin.setText("Cualquiera");
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        seekPrecioMaximo.setMax(Constants.VALORES_FILTRO_PRECIO_MAXIMO.length-1);
        seekPrecioMaximo.setProgress(Constants.VALORES_FILTRO_PRECIO_MAXIMO.length-1);
        precioMax.setText("Cualquiera");
        seekPrecioMaximo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekPrecioMinimo.getProgress()<seekPrecioMaximo.getProgress()){
                    if(progress==(Constants.VALORES_FILTRO_PRECIO_MAXIMO.length-1)){
                        precioMax.setText("Cualquiera");
                    }else{
                        precioMax.setText(Constants.VALORES_FILTRO_PRECIO_MAXIMO[progress]+"€");
                    }
                }else{
                    seekPrecioMaximo.setProgress(Constants.VALORES_FILTRO_PRECIO_MAXIMO.length-1);
                    precioMax.setText("Cualquiera");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });
    seekDistancia.setMax(Constants.VALORES_FILTRO_DISTANCIA.length-1);
    seekDistancia.setProgress(0);
    seekDistancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            distanciaMax.setText(Math.round(Constants.VALORES_FILTRO_DISTANCIA[progress]*111.12)+" kms");
            rango=Constants.VALORES_FILTRO_DISTANCIA[progress];
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        FeedItemAdapter feedItemAdapter = new FeedItemAdapter(getContext(),anunciosList,selectListenerFeed,correo,getFragmentManager());
        recyclerView.setAdapter(feedItemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
    private void setAdapter(){
        try {
            feedItemAdapter= new FeedItemAdapter(getContext(),
                    anunciosList,
                    new SelectListenerFeed() {
                @Override
                public void onItemClicked(Anuncio anuncio) {
                    Intent intent = new Intent(getContext(), AnuncioViewExtended.class);
                    intent.putExtra("id",anuncio.getIdAnuncio());
                    intent.putExtra("categoria",anuncio.getCategoria().toLowerCase(Locale.ROOT));
                    intent.putExtra("email",anuncio.getEmailEmisor());
                    startActivity(intent);
                }
            },
                    correo,
                    getParentFragmentManager());
            noResultados.setVisibility(View.GONE);
            recyclerView.setAdapter(feedItemAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }catch (Exception e){

        }

    }

    public SelectListenerFeed setSelectListenerFeed(SelectListenerFeed listenerFeed){
        listenerFeed=new SelectListenerFeed() {
            @Override
            public void onItemClicked(Anuncio anuncio) {
                Intent intent = new Intent(getContext(), AnuncioViewExtended.class);
                intent.putExtra("id",anuncio.getIdAnuncio());
                intent.putExtra("categoria",anuncio.getCategoria().toLowerCase(Locale.ROOT));
                intent.putExtra("email",anuncio.getEmailEmisor());
                startActivity(intent);
            }
        };
        return listenerFeed;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreate(savedInstanceState);
    }

    public SearchHandler.OnRealizarBusquedaListener setOnRealizarBusquedaListener(SearchHandler.OnRealizarBusquedaListener listener){
        listener= new SearchHandler.OnRealizarBusquedaListener() {
            @Override
            public void onBusquedaSuccess(Anuncio anuncio) {
                anunciosList.add(anuncio);
            }
            @Override
            public void onBusquedaCompleta() {
                if(anunciosList.size()>0){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(anunciosList.size()>0){
                                waitingForResults.setVisibility(View.GONE);
                                numResultados.setVisibility(View.VISIBLE);
                                numResultados.setText("Se han encontrado "+anunciosList.size()+" resultados");
                            }else{
                                noResultados.setVisibility(View.VISIBLE);
                                waitingForResults.setVisibility(View.GONE);
                                numResultados.setVisibility(View.VISIBLE);
                                numResultados.setText("Se han encontrado "+anunciosList.size()+" resultados");
                            }
                            setAdapter();
                        }
                    },3000);

                }else if(anunciosList.size()==0){
                    noResultados.setVisibility(View.VISIBLE);
                    waitingForResults.setVisibility(View.GONE);
                    numResultados.setVisibility(View.VISIBLE);
                    numResultados.setText("Se han encontrado "+anunciosList.size()+" resultados");
                }
            }

            @Override
            public void onBusquedaError(String error) {
                noResultados.setVisibility(View.VISIBLE);
                waitingForResults.setVisibility(View.GONE);
            }
        };
        return listener;
    }


    @Override
    public void onItemClicked(Anuncio anuncio) {
        Intent intent = new Intent(getContext(), AnuncioViewExtended.class);
        intent.putExtra("anuncio",anuncio.getIdAnuncio());
        intent.putExtra("categoria",anuncio.getCategoria().toLowerCase(Locale.ROOT));
        intent.putExtra("email",anuncio.getEmailEmisor());
        startActivity(intent);
    }
}