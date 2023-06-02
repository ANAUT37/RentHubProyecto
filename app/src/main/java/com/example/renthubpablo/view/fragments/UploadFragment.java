package com.example.renthubpablo.view.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Anuncio;
import com.example.renthubpablo.controller.Inmueble;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.controller.types.Vivienda;
import com.example.renthubpablo.model.AnuncioHandler;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.resources.DialogUtils;
import com.example.renthubpablo.view.adapter.ImageSelectorAdapter;
import com.example.renthubpablo.view.adapter.PredictionAdapter;
import com.example.renthubpablo.view.adapter.SelectedListenerPrediction;
import com.example.renthubpablo.view.adapter.SpinnerAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UploadFragment extends Fragment implements SelectedListenerPrediction{
    private static final ViviendaFragment VIVIENDA_FRAGMENT = new ViviendaFragment();
    private static final GarajeFragment GARAJE_FRAGMENT = new GarajeFragment();
    private static final CompartidaFragment COMPARTIDA_FRAGMENT = new CompartidaFragment();
    private static final OficinaFragment OFICINA_FRAGMENT = new OficinaFragment();
    private static final LocalFragment LOCAL_FRAGMENT = new LocalFragment();
    private static final TrasteroFragment TRASTERO_FRAGMENT = new TrasteroFragment();
    private static final TerrenoFragment TERRENO_FRAGMENT = new TerrenoFragment();
    private static final NaveFragment NAVE_FRAGMENT = new NaveFragment();

    private static ArrayList<Uri> selectedImages = new ArrayList<>();

    private static Context globalContext;
    private double latitud, longitud;
    private Fragment currentFragment;
    private View childView;
    private Button submit, add, remove;
    private Spinner spinnerCategoria;
    private String categoria, type, idInmueble;
    private RecyclerView recyclerViewPrediction, recyclerViewImagenes;
    private EditText titulo, descripcion, ubiInput, precio;
    private FrameLayout frameLayout;
    private ImageSelectorAdapter imageSelectorAdapter;
    private TextView error;

    public UploadFragment() {
    }

    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        globalContext = view.getContext();
        recyclerViewPrediction = view.findViewById(R.id.rvPredictionUpload);
        recyclerViewImagenes = view.findViewById(R.id.rvEditAnunImages);
        if(selectedImages.size()==0){
            recyclerViewImagenes.setVisibility(View.GONE);
        }


        frameLayout = view.findViewById(R.id.flEditAnuncioCategoria);
        currentFragment = null;
        submit = view.findViewById(R.id.bttnUploadSubmit);
        titulo = view.findViewById(R.id.etUploadTitulo);
        descripcion = view.findViewById(R.id.etUploadDescripcion);
        precio = view.findViewById(R.id.etUploadPrecio);
        error = view.findViewById(R.id.tvErrorMessageUpload);
        add = view.findViewById(R.id.bttnAddImage);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewImagenes.setVisibility(View.VISIBLE);
                if(selectedImages.size()<10){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE);
                }
            }
        });
        remove = view.findViewById(R.id.bttnRemoveImage);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImages.size()>0){
                    selectedImages.remove(selectedImages.size()-1);
                    imageSelectorAdapter = new ImageSelectorAdapter(globalContext, selectedImages);
                    recyclerViewImagenes.setAdapter(imageSelectorAdapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(globalContext, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewImagenes.setLayoutManager(layoutManager);
                    if(selectedImages.size()==0){
                        recyclerViewImagenes.setVisibility(View.GONE);
                    }
                }
            }
        });

        imageSelectorAdapter = new ImageSelectorAdapter(globalContext, selectedImages);
        recyclerViewImagenes.setAdapter(imageSelectorAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(globalContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewImagenes.setLayoutManager(layoutManager);

        spinnerCategoria = view.findViewById(R.id.spinnerCategoría);
        List<String> values = Arrays.asList("--Selecciona una categoría--",
                "Vivienda", "Garaje", "Vivienda compartida", "Oficina", "Local", "Trastero", "Terreno", "Nave");
        SpinnerAdapter adapter = new SpinnerAdapter(globalContext, values);
        spinnerCategoria.setAdapter(adapter);
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = parent.getItemAtPosition(position).toString();
                handleCategoria(categoria);
                categoria=categoria.toLowerCase(Locale.ROOT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ubiInput = view.findViewById(R.id.etUploadLocalizacion);
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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validadPrimerosDatos()) {
                    AnuncioHandler anuncioHandler = new AnuncioHandler();
                    Inmueble inmueble = setDatosInmueble();
                    Anuncio anuncio = null;
                    try {
                        anuncio = setDatosAnuncio();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (com.google.maps.errors.ApiException e) {
                        e.printStackTrace();
                    }
                    anuncio.setInmueble(inmueble);
                    anuncio.setDireccion(ubiInput.getText().toString());
                    List<String> fotosAnuncioNames=new ArrayList<>();
                    for(int i=0;i<selectedImages.size();i++){
                        fotosAnuncioNames.add(anuncio.generarNombreImagenAnuncio());
                    }
                    for(int i=0;i<selectedImages.size();i++){
                        anuncioHandler.uploadFotosAnuncio(selectedImages.get(i),fotosAnuncioNames.get(i));
                    }
                    anuncio.setImagen(fotosAnuncioNames);
                    String id =anuncioHandler.insertarAnuncio(anuncio,type);
                    UsuarioHandler usuarioHandler = new UsuarioHandler();
                    usuarioHandler.updateIdAnuncioUser(id,anuncio.getCategoria(),globalContext);

                    DialogUtils dialogUtils = DialogUtils.newInstance("Anuncio creado", "El anuncio ha sido creado exitosamente!",false);
                    dialogUtils.setOnDismissListener(new DialogUtils.OnDismissListener() {
                        @Override
                        public void onDismiss() {

                        }
                    });
                    dialogUtils.show(getParentFragmentManager(), "dialog_example");

                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            selectedImages.add(uri);
            imageSelectorAdapter = new ImageSelectorAdapter(globalContext, selectedImages);
            recyclerViewImagenes.setAdapter(imageSelectorAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(globalContext, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewImagenes.setLayoutManager(layoutManager);
        }
    }

    private void performSearch(String searchQuery) {
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
        setErrorMessage("Se ha producido un error!: handleApiException");
    }

    private void handleError(Exception exception) {
        setErrorMessage("Se ha producido un error! handlError");
    }

    private void handleAutocompletePredictions(List<AutocompletePrediction> predictions) {
        if (predictions.size() > 0) {

            PredictionAdapter predictionAdapter = new PredictionAdapter(getContext(), predictions, (SelectedListenerPrediction) this);
            recyclerViewPrediction.setAdapter(predictionAdapter);
            recyclerViewPrediction.setLayoutManager(new LinearLayoutManager(getContext()));
        } else if (predictions.size() == 0) {
            recyclerViewPrediction.setVisibility(View.GONE);
        }

    }

    private void handleCategoria(String categoria) {
        if (categoria == "Vivienda") {
            loadFragment(VIVIENDA_FRAGMENT);
        } else if (categoria == "Garaje") {
            loadFragment(GARAJE_FRAGMENT);
        } else if (categoria == "Vivienda compartida") {
            loadFragment(COMPARTIDA_FRAGMENT);
        } else if (categoria == "Oficina") {
            loadFragment(OFICINA_FRAGMENT);
        } else if (categoria == "Local") {
            loadFragment(LOCAL_FRAGMENT);
        } else if (categoria == "Trastero") {
            loadFragment(TRASTERO_FRAGMENT);
        } else if (categoria == "Terreno") {
            loadFragment(TERRENO_FRAGMENT);
        } else if (categoria == "Nave") {
            loadFragment(NAVE_FRAGMENT);
        } else {
            frameLayout.setVisibility(View.GONE);
        }
    }

    private void loadFragment(Fragment fragment) {
        frameLayout.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flEditAnuncioCategoria, fragment);
        fragmentTransaction.commit();

        currentFragment = fragment;
    }

    private boolean validadPrimerosDatos() {
        if (titulo.getText().length() > 0) {
            if (titulo.getText().length() < 50) {
                if (descripcion.getText().length() > 0) {
                    if (descripcion.getText().length() < 512) {
                        if (ubiInput.getText().length() > 0) {
                            if (precio.getText().length() > 0) {
                                if (categoria != "--Selecciona una categoría--") {
                                    if(selectedImages.size()>0){
                                        childView = currentFragment.getView();
                                        return true;
                                    }else{
                                        setErrorMessage("Debes agregar como mínimo 1 imagen");
                                    }
                                } else {
                                    setErrorMessage("Debes alegir una categoría");
                                }
                            } else {
                                setErrorMessage("Debes agregar un precio");
                            }
                        } else {
                            setErrorMessage("Debes agregar una localización");
                        }
                    } else {
                        setErrorMessage("La descripción no puede superar los 512 carácteres");
                    }
                } else {
                    setErrorMessage("Debes agregar una descripción");
                }
            } else {
                setErrorMessage("El título no puede superar los 50 carácteres");
            }
        } else {
            setErrorMessage("Debes agregar un título");
        }
        return false;
    }

    private void getLatLongAdapted(String place) {
        System.out.println("DEBUG:" + place);

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
                    System.out.println("OCURRIO: CORD: " + latitud + " " + longitud);
                } else {
                    setErrorMessage("Se ha producido un error!");
                    DialogUtils dialogUtils= DialogUtils.newInstance("Ups!", "Se ha producido un error!",false);
                    dialogUtils.show(getActivity().getSupportFragmentManager(), "dialog_example");

                }
            }
        };

        geocodingTask.execute(place);
    }

    private Anuncio setDatosAnuncio() throws IOException, InterruptedException, com.google.maps.errors.ApiException {
        String mTitulo = titulo.getText().toString();
        String mDescripcion = descripcion.getText().toString();
        GeoPoint mLocalizacion=new GeoPoint(latitud,longitud);
        String mLatitud = String.valueOf(latitud);
        String mLongitud = String.valueOf(longitud);
        String mPrecio = precio.getText().toString();
        String mCategoria = categoria;
        Usuario usuario= new Usuario();
        String mEmailEmisor= usuario.getCurrentUserEmail(globalContext);
        return new Anuncio(mTitulo, mDescripcion, mLatitud, mLongitud, mPrecio, mCategoria, mEmailEmisor,new Date(),mLocalizacion);
    }

    private Inmueble setDatosInmueble() {
        Inmueble inmueble = null;
        if (currentFragment == VIVIENDA_FRAGMENT) {
            inmueble = createVivienda();
        }//AMPLIAR

        return inmueble;
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
    public Vivienda createVivienda() {
        EditText tamanyo = childView.findViewById(R.id.etViviendaTamanyo);
        String mTamanyo = tamanyo.getText().toString();
        EditText dormitorios = childView.findViewById(R.id.etViviendaDormitorios);
        String mDormitorios = dormitorios.getText().toString();
        EditText banyos = childView.findViewById(R.id.etViviendaBanyos);
        String mBanyos = banyos.getText().toString();
        EditText piso=childView.findViewById(R.id.etViviendaPlanta);
        String mPiso=piso.getText().toString();
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch ascensor = childView.findViewById(R.id.swViviendaAscensor);
        boolean mAscensor = ascensor.isChecked();
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch garaje = childView.findViewById(R.id.swViviendaGaraje);
        boolean mGaraje = garaje.isChecked();
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch trastero = childView.findViewById(R.id.swViviendaTrastero);
        boolean mTrastero = trastero.isChecked();
        type="vivienda";

        return new Vivienda(mPiso,mTamanyo, mDormitorios, mBanyos, mAscensor, mGaraje, mTrastero);
    }

    @Override
    public void onItemClicked(String string) throws IOException, InterruptedException, com.google.maps.errors.ApiException {
        ubiInput.setText(string);
        recyclerViewPrediction.setVisibility(View.GONE);
        getLatLongAdapted(ubiInput.getText().toString());
        System.out.println("OCURRIO: CORD: " + latitud + " " + longitud);
    }

}
