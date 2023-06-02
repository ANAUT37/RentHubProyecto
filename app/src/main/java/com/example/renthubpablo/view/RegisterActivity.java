package com.example.renthubpablo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.MainActivity;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.resources.DialogUtils;
import com.example.renthubpablo.view.adapter.SpinnerAdapter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RegisterActivity extends AppCompatActivity {
    private static UsuarioHandler.OnRegisterListener onRegisterListener;

    private static UsuarioHandler usuarioHandler= new UsuarioHandler();
    Spinner spinnerGenero;
    private static String genero;
    Uri imageUri=null;
    EditText otroGenero, nombre, apellidos,pronombres,telefono, edad,correo,contraseña, descripcion;
    Button submit;
    Date fechaNac;
    ImageView imagenPerfil;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imagenPerfil=findViewById(R.id.ivPfpRegister);
        nombre=findViewById(R.id.etNameRegister);
        apellidos=findViewById(R.id.etApellidosRegister);
        pronombres=findViewById(R.id.etPronombresRegister);
        telefono=findViewById(R.id.etUploadLocalizacion);
        edad=findViewById(R.id.etEdadRegister);
        correo=findViewById(R.id.etCorreoRegister);
        contraseña=findViewById(R.id.etPasswordRegister);
        submit=findViewById(R.id.bttnRegisterSubmit);
        spinnerGenero =findViewById(R.id.spinnerGenero);
        otroGenero=findViewById(R.id.etOtroGeneroRegister);
        descripcion=findViewById(R.id.etDescripcionRegister);

        onRegisterListener=setOnLoginListener(onRegisterListener);





        edad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            RegisterActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                    edad.setText(formattedDate);
                                    fechaNac= new Date(formattedDate);
                                }
                            },
                            year,
                            month,
                            day
                    );
                    datePickerDialog.show();
                }
            }
        });
        //SELECCION DE IMAGEN DE PERFIL;
        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE);
            }
        });

        List<String> values = Arrays.asList("Selecciona tu género",
                "Masculino", "Femenino", "Neutro","No binario","Género fluido","Otro");
        SpinnerAdapter adapter = new SpinnerAdapter(this, values);
        spinnerGenero.setAdapter(adapter);
        spinnerGenero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if(selectedOption=="Otro"){
                    otroGenero.setVisibility(View.VISIBLE);
                }else{
                    if(otroGenero.getVisibility()==View.VISIBLE){
                        otroGenero.setVisibility(View.GONE);
                    }
                }
                genero=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        submit.setOnClickListener(View ->{
            //UsuarioHandler usuarioHandler = new UsuarioHandler(this.getApplicationContext());
            Usuario usuario = new Usuario();

            try {
                String mNombre= usuario.encrypt(nombre.getText().toString());
                String mApellido =usuario.encrypt(apellidos.getText().toString());
                String mPassword=contraseña.getText().toString();
                mPassword=usuario.hashPassword(mPassword);
                String mCorreo=usuario.encrypt(correo.getText().toString());
                String mTelefono=usuario.encrypt(telefono.getText().toString());
                String mDescripcion=usuario.encrypt(descripcion.getText().toString());
                String mFecha_nacimiento = usuario.encrypt(fechaNac.toString());
                String mGenero=usuario.encrypt(genero);
                String mPronombres=usuario.encrypt(pronombres.getText().toString());
                String mImagen=usuario.generarNombreImagenPerfil();
                boolean mEstado=true;
                Usuario user = new Usuario(mNombre,mApellido,mPassword,mCorreo,mTelefono,mDescripcion,
                        mFecha_nacimiento,mGenero,mPronombres,mEstado,mImagen);
                usuarioHandler.uploadFotoPerfil(imageUri,mImagen);
                System.out.println("OCURRIO: token: "+user.getToken());
                usuarioHandler.registerUser(user, onRegisterListener);

            } catch (Exception e) {
                System.out.println("OCURRIO: "+e.getMessage());
            }

        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imagenPerfil.setImageURI(imageUri);
        }
    }
    public UsuarioHandler.OnRegisterListener setOnLoginListener(UsuarioHandler.OnRegisterListener listener){
        listener=new UsuarioHandler.OnRegisterListener() {
            @Override
            public void onRegisterSuccess() {
                DialogUtils dialogUtils = DialogUtils.newInstance("Cuenta creada", "Enhorabuena! Ya eres miembrx de RêntHûb!",false);
                dialogUtils.setOnDismissListener(new DialogUtils.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
                dialogUtils.show(getSupportFragmentManager(), "dialog_example");
            }
        };
        return listener;
    }



}