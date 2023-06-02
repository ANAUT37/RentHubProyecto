package com.example.renthubpablo.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Anuncio;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.model.SearchHandler;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.resources.BusquedaUtil;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.view.NoAppStatus;
import com.example.renthubpablo.view.adapter.HistorialAdapter;
import com.example.renthubpablo.view.adapter.PredictionAdapter;
import com.example.renthubpablo.view.adapter.SelectedListenerPrediction;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SearchFragment extends Fragment implements SelectedListenerPrediction {
    public static Context globalContext;
    private double latitud, longitud;
    private static SearchResultFragment SEARCH_RESULT_FRAGMENT;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static List<BusquedaUtil> historialList=new ArrayList<>();
    private RecyclerView recyclerViewPrediction, recyclerViewHistorial;
    private  TextView welcome, error, noHistorial;
    private EditText ubiInput;
    private Button buscarSubmit;
    private ImageButton selectedType;
    private static UsuarioHandler.OnReadListener onReadListener;
    private static String correo,username;
    public SearchFragment() {
    }
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comprobarPermisosUbicacion();

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        globalContext=view.getContext();
        //SETEAR DIFERENTES ELEMENTOS
        selectedType=null;
        comprobarPermisosUbicacion();
        setupListaTypes(view);
        noHistorial=view.findViewById(R.id.tvNoSearchHistory);
        recyclerViewHistorial=view.findViewById(R.id.rvHistorialSearch);
        recyclerViewPrediction=view.findViewById(R.id.rvPrediction);
        getHistorial();
        ubiInput=view.findViewById(R.id.ubiCuadro);
        onReadListener=setOnReadListener(onReadListener);

        try {
            SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            correo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");
            UsuarioHandler usuarioHandler = new UsuarioHandler();
            usuarioHandler.readUserData(correo, onReadListener, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ubiInput.setText("Zaragoza, España");//PRUEBAS

        ubiInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recyclerViewPrediction.setVisibility(View.VISIBLE);
                String searchQuery = s.toString();
                performSearch(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        welcome=view.findViewById(R.id.tvBienvenida);
        error=view.findViewById(R.id.tvErrorMessage);
        buscarSubmit=view.findViewById(R.id.btnSearchUbi);
        buscarSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedType!=null){
                    if(ubiInput.getText().length()>0){
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        SEARCH_RESULT_FRAGMENT= SearchResultFragment.newInstance("vivienda",latitud,longitud,ubiInput.getText().toString());
                        transaction.add(R.id.frame_container,SEARCH_RESULT_FRAGMENT);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        setHistorial(ubiInput.getText().toString(),"vivienda",latitud,longitud);
                        historialList.clear();
                        getHistorial();

                    }else{
                        setErrorMessage(Constants.NO_UBI_ENTERED);
                    }
                }else{
                    setErrorMessage(Constants.NO_TYPE_SELECTED);
                }
            }
        });



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        comprobarPermisosUbicacion();

    }

    private void comprobarPermisosUbicacion() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Los permisos ya están concedidos, puedes realizar las acciones que necesitas aquí
            // ...
        } else {
            // Los permisos no están concedidos, solicítalos al usuario
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Intent intent = new Intent(getContext(), NoAppStatus.class);
                intent.putExtra("errorMessage",Constants.NO_LOCATION_PERMISSION_ERROR_MESSAGE);
                startActivity(intent);
                getActivity().finish();
            } else {

            }
        }
    }

    private void cambioBienvenida() {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);

        if (hora >= 6 && hora < 12) {
            welcome.setText("¡Buenos días, "+username+"!");
        } else if (hora >= 12 && hora < 20) {
            welcome.setText("¡Buenas tardes, "+username+"!");
        } else {
            welcome.setText("¡Buenas noches, "+username+"!");
        }
        //textView.setText(textView.getText());
    }
    private void setupListaTypes(View view){
        //SETEA LOS BOTONES DE SELECCION DE TIPO DE INMUEBLE
        HashMap<ImageButton,TextView> listaTypes = new HashMap<>();
        listaTypes.put(view.findViewById(R.id.op_vivienda),view.findViewById(R.id.optext_vivienda));
        listaTypes.put(view.findViewById(R.id.op_garaje),view.findViewById(R.id.optext_garaje));
        listaTypes.put(view.findViewById(R.id.op_habitacion),view.findViewById(R.id.optext_habitacion));
        listaTypes.put(view.findViewById(R.id.op_oficina),view.findViewById(R.id.optext_oficina));
        listaTypes.put(view.findViewById(R.id.op_local),view.findViewById(R.id.optext_local));
        listaTypes.put(view.findViewById(R.id.op_trastero),view.findViewById(R.id.optext_trastero));
        listaTypes.put(view.findViewById(R.id.op_terreno),view.findViewById(R.id.optext_terreno));
        listaTypes.put(view.findViewById(R.id.op_nave),view.findViewById(R.id.optext_nave));

        //AÑADE LISTENER PARA QUE AL SER SELECCIONADOS SE SEPA CUAL ES
        for(Map.Entry<ImageButton, TextView> entry : listaTypes.entrySet()){
            ImageButton icon = entry.getKey();
            TextView iconText= entry.getValue();
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //PONDRA EL FONDO BLANCO DE TODOS Y LUEGO EL FONDO DESTACADO DEL SELECCIONADO
                    for(Map.Entry<ImageButton, TextView> entry : listaTypes.entrySet()){
                        ImageButton notIconSelected = entry.getKey();
                        TextView notTextSelected = entry.getValue();
                        notTextSelected.setBackgroundColor(getResources().getColor(R.color.blanco));
                        notIconSelected.setBackgroundColor(getResources().getColor(R.color.blanco));
                    }
                    icon.setBackgroundColor(getResources().getColor(R.color.azulClaro));
                    iconText.setBackgroundColor(getResources().getColor(R.color.azulClaro));
                    selectedType=icon;
                }
            });
        }
    }

    private void setErrorMessage(String errorMessage){
        error.setVisibility(View.VISIBLE);
        error.setText(errorMessage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                error.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void performSearch(String searchQuery){
        Places.initialize(globalContext, Constants.MAPS_API_KEY);
        PlacesClient placesClient = Places.createClient(globalContext);
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(searchQuery)
                .build();

        placesClient.findAutocompletePredictions(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FindAutocompletePredictionsResponse response = task.getResult();
                if (response != null) {
                    List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
                    handleAutocompletePredictions(predictions);
                }
            } else {
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    handleApiException(statusCode);
                } else {
                    handleError(exception);
                }
            }
        });

    }
    private void handleApiException(int statusCode) {
        error.setText("Se ha producido un error, Código: "+statusCode);
    }

    private void handleError(Exception exception) {
        error.setText("Se ha producido un error, Código: "+exception);
    }

    private void handleAutocompletePredictions(List<AutocompletePrediction> predictions) {
        if(predictions.size()>0){
            PredictionAdapter predictionAdapter=new PredictionAdapter(getContext(),predictions,this);
            recyclerViewPrediction.setAdapter(predictionAdapter);
            recyclerViewPrediction.setLayoutManager(new LinearLayoutManager(getContext()));
        }else if(predictions.size()==0){
            recyclerViewPrediction.setVisibility(View.GONE);
        }

    }
    private void getLatLongAdapted(String place) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<String, Void, GeocodingResult[]> geocodingTask = new AsyncTask<String, Void, GeocodingResult[]>() {
            @Override
            protected GeocodingResult[] doInBackground(String... params) {
                GeoApiContext context = new GeoApiContext.Builder()
                        .apiKey(Constants.MAPS_API_KEY)
                        .build();
                try {
                    return GeocodingApi.geocode(context, params[0]).await();
                } catch (com.google.maps.errors.ApiException | InterruptedException | IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(GeocodingResult[] results) {
                if (results != null && results.length > 0) {
                    GeocodingResult result = results[0];
                    LatLng latLng = result.geometry.location;
                    latitud = latLng.lat;
                    longitud = latLng.lng;
                } else {
                    setErrorMessage("Se ha producido un error!");
                }
            }
        };
        geocodingTask.execute(place);
    }

    private void setHistorial(String direccion, String categoria, double latitud, double longitud){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_HISTORIAL_NAME, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        int count = allEntries.size();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        BusquedaUtil busquedaUtil= new BusquedaUtil(direccion,categoria,latitud,longitud);
        if (!historialList.contains(busquedaUtil)) {
            Gson gson = new Gson();
            String objetoSerializado = gson.toJson(busquedaUtil);
            editor.putString("busqueda"+(count+1),objetoSerializado);
            editor.apply();
        }



    }
    private void getHistorial() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_HISTORIAL_NAME, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String objetoSerializado = (String) entry.getValue();
            Gson gson = new Gson();
            BusquedaUtil busquedaUtil = gson.fromJson(objetoSerializado, BusquedaUtil.class);
            historialList.add(busquedaUtil);
        }
        if(historialList.size()>0){
            recyclerViewHistorial.setVisibility(View.VISIBLE);
            noHistorial.setVisibility(View.GONE);
            HistorialAdapter historialAdapter= new HistorialAdapter(historialList, globalContext, new HistorialAdapter.OnHistorialItemListener() {
                @Override
                public void onItemClicked(String direccion, String categoria) {
                    getLatLongAdapted(direccion);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            SEARCH_RESULT_FRAGMENT= SearchResultFragment.newInstance(categoria,latitud,longitud,direccion);
                            transaction.add(R.id.frame_container,SEARCH_RESULT_FRAGMENT);
                            transaction.addToBackStack(null);
                            transaction.setReorderingAllowed(true);
                            transaction.commit();;
                        }
                    }, 300);


                }
            });
            recyclerViewHistorial.setAdapter(historialAdapter);
            recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(globalContext));
            noHistorial.setVisibility(View.GONE);
        }else{
            noHistorial.setVisibility(View.VISIBLE);
            recyclerViewHistorial.setVisibility(View.GONE);
        }
    }
    private UsuarioHandler.OnReadListener setOnReadListener(UsuarioHandler.OnReadListener onReadListener) {
        onReadListener=new UsuarioHandler.OnReadListener() {
            @Override
            public void onReadSuccess(Usuario usuario) {
                username=usuario.getNombre();
                cambioBienvenida();
            }

            @Override
            public void onReadError(String error) {
                System.out.println("OCURRIO: "+error);
            }
        };
        return onReadListener;
    }

    @Override
    public void onItemClicked(String string) throws IOException, InterruptedException, com.google.maps.errors.ApiException {
        getLatLongAdapted(string);
        ubiInput.setText(string);
        recyclerViewPrediction.setVisibility(View.GONE);
    }
}
