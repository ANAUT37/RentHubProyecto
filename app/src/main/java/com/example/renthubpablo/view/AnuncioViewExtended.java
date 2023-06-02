package com.example.renthubpablo.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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
import com.example.renthubpablo.view.adapter.FeedSliderAdapter;
import com.example.renthubpablo.view.extendedfragments.ViviendaExtended;
import com.example.renthubpablo.view.fragments.EditAnuncioFragment;
import com.example.renthubpablo.view.fragments.ProfileFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnuncioViewExtended extends AppCompatActivity implements OnMapReadyCallback {
    private static final AnuncioHandler anuncioHandler = new AnuncioHandler();
    private static ViviendaExtended VIVIENDA_EXTENDED;
    private Fragment currentFragment;
    private static Context globalContext;
    private static UsuarioHandler.OnReadListener onReadListener;
    private static AnuncioHandler.OnReadAnuncioListener onReadAnuncioListener;
    private static AnuncioHandler.OnGetImagenesInmuebleListener onGetImagenesInmuebleListener;
    public static String id, categoria,email;
    private List<Uri> imagesSliderUris;
    private List<String> idImages;
    private static FeedSliderAdapter feedSliderAdapter;
    private boolean busqueda;
    float latitud, longitud;
    FragmentManager fragmentManager;
    MapView mapa;
    GoogleMap googleMap;
    TextView titulo, precio, propName, direccion, descripcion, fechaPublicacion;
    CircleImageView propPfp;
    Button propContactar, editar, borrar;
    ProgressBar progressImages;
    ConstraintLayout propLayout;
    ViewPager viewPager;
    FrameLayout inmuebleLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busqueda=false;
        setContentView(R.layout.activity_anuncio_view_extended);
        globalContext = getApplication().getApplicationContext();
        id = getIntent().getStringExtra("id");
        categoria = getIntent().getStringExtra("categoria");
        email = getIntent().getStringExtra("email");
        viewPager = findViewById(R.id.iExtendedSlider);
        titulo = findViewById(R.id.iExtendedTitulo);
        precio = findViewById(R.id.iExtendedPrecio);
        propName = findViewById(R.id.tvExtendedPropName);
        direccion = findViewById(R.id.iExtendedDireccion);
        descripcion = findViewById(R.id.tvExtendedDescripcion);
        fechaPublicacion = findViewById(R.id.iExtendedFechaPublicación);
        propPfp = findViewById(R.id.ivExtendedPropPfp);
        propLayout = findViewById(R.id.clExtendedPropietario);
        propContactar = findViewById(R.id.bttnExtendedPropContactar);
        progressImages = findViewById(R.id.iExtendedProgressImages);
        mapa = findViewById(R.id.iExtendedMapa);
        inmuebleLayout=findViewById(R.id.frameInmuebleExtended);
        editar=findViewById(R.id.bttnEditarAnuncio);
        borrar=findViewById(R.id.bttnEditarBorrar);

        imagesSliderUris= new ArrayList<>();
        imagesSliderUris.clear();
        System.out.println("OCURRIO: "+imagesSliderUris.size());
        feedSliderAdapter= new FeedSliderAdapter(imagesSliderUris, globalContext,getSupportFragmentManager());
        viewPager.setAdapter(feedSliderAdapter);

        mapa.onCreate(savedInstanceState);
        mapa.getMapAsync(this);

        try {
            setDatosProp();
            setDatosAnuncio();
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    marcarUbicacion();
                    for (String image : idImages) {
                        anuncioHandler.getImagenesInmueble(image, onGetImagenesInmuebleListener);
                    }
                }
            },300);

        } catch (Exception e) {
            e.printStackTrace();
        }
        propLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ProfileFragment PROFILE_FRAGMENT = ProfileFragment.newInstance(false, email);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(android.R.id.content, PROFILE_FRAGMENT);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        SharedPreferences prefs = globalContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (Objects.equals(email, prefs.getString(Constants.SHARED_PREFERENCES_CORREO, ""))) {
            propContactar.setVisibility(View.GONE);
            editar.setVisibility(View.VISIBLE);
            editar.setEnabled(true);
            borrar.setVisibility(View.VISIBLE);
            borrar.setEnabled(true);
        } else {
            propContactar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(globalContext, ChatActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("anuncio",id);
                    intent.putExtra("categoria",categoria);
                    startActivity(intent);

                }
            });
        }
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAnuncioFragment EDITAR_ANUNCIO_FRAGMENT=EditAnuncioFragment.newInstance(id,categoria);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(android.R.id.content, EDITAR_ANUNCIO_FRAGMENT);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils dialogUtils= DialogUtils.newInstance("¡Atención!",
                        "¿Estás segurx de que quieres eliminar este anuncio?" +
                                "\n\nEl anuncio se eliminará permanentemente, aunque tus contratos permanecerán hasta que finalicen.",
                        true);
                dialogUtils.show(getSupportFragmentManager(), "dialog_example");

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        marcarUbicacion();

    }

    private void marcarUbicacion() {
        LatLng ubicacion = new LatLng(latitud, longitud);
        CircleOptions circleOptions = new CircleOptions()
                .center(ubicacion)
                .radius(200)
                .strokeWidth(2) // Ancho de la línea del círculo
                .strokeColor(Color.BLUE) // Color de la línea del círculo
                .fillColor(Color.parseColor("#200000FF")); // Color de relleno del círculo (semi-transparente)

        Circle circle = googleMap.addCircle(circleOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));

    }


    private void handleCategoria(Inmueble inmueble, String categoria) {
        if (categoria.equals("vivienda")) {
            System.out.println("OCURRIO: ES VIVIENDAAAAAAAAAAA");
            inflateVivienda(inmueble);
        } else if (categoria == "garaje") {
            loadFragment(null);
        } else if (categoria == "habitacion") {
            loadFragment(null);
        } else if (categoria == "oficina") {
            loadFragment(null);
        } else if (categoria == "local") {
            loadFragment(null);
        } else if (categoria == "trastero") {
            loadFragment(null);
        } else if (categoria == "terreno") {
            loadFragment(null);
        } else if (categoria == "nave") {
            loadFragment(null);
        } else {
            loadFragment(VIVIENDA_EXTENDED);
        }


    }

    @SuppressLint("SetTextI18n")
    private void inflateVivienda(Inmueble inmueble) {
        Vivienda vivienda = (Vivienda) inmueble;
        VIVIENDA_EXTENDED=ViviendaExtended.newInstance(
                vivienda.getTamanyo(),
                vivienda.getDormitorios(),
                vivienda.getBanyos(),
                vivienda.getPiso(),
                vivienda.isGaraje(),
                vivienda.isAscensor(),
                vivienda.isTrastero()
        );
        loadFragment(VIVIENDA_EXTENDED);
    }

    private void loadFragment(Fragment fragment) {
        currentFragment=fragment;
        inmuebleLayout.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameInmuebleExtended, fragment);
        fragmentTransaction.commit();
    }


    private void setDatosProp() throws Exception {
        onReadListener = setOnReadListener(onReadListener);
        UsuarioHandler usuarioHandler = new UsuarioHandler();
        usuarioHandler.readUserData(email, onReadListener, "");
    }

    private void setDatosAnuncio() {
        imagesSliderUris.clear();
        onReadAnuncioListener = setOnReadAnuncioListener(onReadAnuncioListener);
        onGetImagenesInmuebleListener = setOnGetImagenesInmuebleListener(onGetImagenesInmuebleListener);

        anuncioHandler.onReadAnuncios(id, categoria, onReadAnuncioListener);
        anuncioHandler.addVisita(id,categoria);

    }

    private UsuarioHandler.OnReadListener setOnReadListener(UsuarioHandler.OnReadListener onReadListener) {
        onReadListener = new UsuarioHandler.OnReadListener() {
            @Override
            public void onReadSuccess(Usuario usuario) {
                propName.setText(usuario.getNombre() + " " + usuario.getApellido());

                StorageReference storageReference = FirebaseStorage.getInstance().getReference("pfp/" + usuario.getPfp());

                try {
                    File localFile = File.createTempFile("tempfile", ".jpg");
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            propPfp.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            propPfp.setImageResource(R.drawable.user_no_pic);
                        }
                    });
                } catch (IOException e) {
                    throwErrorDialog(e.getMessage());
                }
            }

            @Override
            public void onReadError(String error) {
                throwErrorDialog(error);
            }
        };
        return onReadListener;
    }

    private AnuncioHandler.OnReadAnuncioListener setOnReadAnuncioListener(AnuncioHandler.OnReadAnuncioListener onReadAnuncioListener) {
        onReadAnuncioListener = new AnuncioHandler.OnReadAnuncioListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReadSucess(Anuncio anuncio) {
                id=anuncio.getIdAnuncio();
                categoria=anuncio.getCategoria();
                System.out.println("OCURRIO: "+id+" "+categoria);

                precio.setText(anuncio.getPrecio() + "€/mes");
                titulo.setText(anuncio.getTitulo());
                direccion.setText(anuncio.getDireccion());
                System.out.println("OCURRIO: CATEGORIA: "+anuncio.getCategoria());
                descripcion.setText(anuncio.getDescripcion());
                Date fecha = anuncio.getFecha();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fechaFormateada = sdf.format(fecha);
                fechaPublicacion.setText("Publicado el " + fechaFormateada);
                latitud= Float.parseFloat(anuncio.getLatitud());
                longitud=Float.parseFloat(anuncio.getLongitud());
                idImages=anuncio.getImagen();
                System.out.println("OCURRIO: EXTENDED: "+anuncio.toString());

                handleCategoria(anuncio.getInmueble(),anuncio.getCategoria().toLowerCase(Locale.ROOT));
            }



            @Override
            public void onReadError(String error) {
                throwErrorDialog(error);
            }
        };
        return onReadAnuncioListener;
    }

    private AnuncioHandler.OnGetImagenesInmuebleListener setOnGetImagenesInmuebleListener(AnuncioHandler.OnGetImagenesInmuebleListener onGetImagenesInmuebleListener) {
        onGetImagenesInmuebleListener = new AnuncioHandler.OnGetImagenesInmuebleListener() {
            @Override
            public void onGetImagesSuccess(Uri uri) {
                imagesSliderUris.add(uri);
                feedSliderAdapter.notifyDataSetChanged();
                progressImages.setVisibility(View.GONE);
            }

            @Override
            public void onGetImagesComplete() {

            }

            @Override
            public void onGetImagesError(String error) {
                throwErrorDialog(error);
            }
        };
        return onGetImagenesInmuebleListener;
    }
    private void throwErrorDialog(String error){
        DialogUtils dialogUtils = DialogUtils.newInstance("Ups! Error: ", error,false);
        dialogUtils.setOnDismissListener(new DialogUtils.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        dialogUtils.show(getSupportFragmentManager(), "dialog_example");
    }

}