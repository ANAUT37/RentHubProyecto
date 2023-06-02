package com.example.renthubpablo.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Anuncio;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.controller.contratos.ContratoPlantilla;
import com.example.renthubpablo.model.AnuncioHandler;
import com.example.renthubpablo.model.ContratoHandler;
import com.example.renthubpablo.model.MensajesHandler;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.resources.FCMSend;
import com.example.renthubpablo.resources.MensajeUtil;
import com.example.renthubpablo.view.adapter.ContratoAdapter;
import com.example.renthubpablo.view.adapter.MensajesAdapter;
import com.example.renthubpablo.view.fragments.ImageViewFragment;
import com.example.renthubpablo.view.fragments.PDFViewFragment;
import com.example.renthubpablo.view.fragments.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private static ContratoHandler contratoHandler= new ContratoHandler();
    private static UsuarioHandler.OnReadListener onReadListener;
    private static UsuarioHandler.OnReadListener onReadListenerSender;
    private static UsuarioHandler.OnTokenReadListener onTokenReadListener;
    private static AnuncioHandler.OnReadAnuncioListener onReadAnuncioListener;
    private static ContratoHandler.OnReadDefaultContratosListener onReadPersonalizadosContratosListener;
    private static FirebaseFirestore db=FirebaseFirestore.getInstance();
    private static ArrayList<MensajeUtil> chatMessages;
    private static ArrayList<ContratoPlantilla> contratosPersonalizados;
    private static TextView userName, precio, titulo, direccion, noAnuncio,noContratosPersonalizados;
    private static ProgressBar chatProgress;
    private static RecyclerView chatRecycler, contratosRecycler;
    private static MensajesAdapter mensajesAdapter;
    private static ContratoAdapter contratoPersonalizadosAdapter;
    private static CircleImageView userPfp;
    private static String senderCorreo,receiverCorreo, categoria, senderName, receiverToken;
    private static EditText input;
    private static ImageButton send, plus;
    private static Button detalles, camera, contrato;
    private static ConstraintLayout consDetalles, consPlus, consContratos;
    private static String anuncio;
    private static boolean existeChat;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        contratosPersonalizados=new ArrayList<>();
        anuncio=getIntent().getStringExtra("anuncio");
        categoria=getIntent().getStringExtra("categoria");


        userName=findViewById(R.id.chatUserName);
        userPfp=findViewById(R.id.chatUserPfp);
        chatRecycler=findViewById(R.id.chatRecycler);
        chatProgress=findViewById(R.id.chatProgress);
        detalles=findViewById(R.id.bttnShowDetalles);
        consDetalles=findViewById(R.id.consAnuncioMini);
        precio=findViewById(R.id.AnunMiniPrecio);
        direccion=findViewById(R.id.AnunMiniDireccion);
        titulo=findViewById(R.id.AnunMiniTitulo);
        plus =findViewById(R.id.bttnCameraOptions);
        noAnuncio=findViewById(R.id.chatDetEliminado);
        consPlus=findViewById(R.id.chatConsPlusOptions);
        camera=findViewById(R.id.chatBttnCamera);
        contrato=findViewById(R.id.chatBttnContratos);
        consContratos=findViewById(R.id.consSelectContrato);
        contratosRecycler=findViewById(R.id.chatSelectContratoRecycler);
        noContratosPersonalizados=findViewById(R.id.noContratosEnChat);



        input=findViewById(R.id.chatInputMessage);
        send=findViewById(R.id.bttnSendMessage);

        db=FirebaseFirestore.getInstance();

        try {
            chatMessages=new ArrayList<>();
            getUserData();

        } catch (Exception e) {
        }
        setDatosAnuncio();

        onReadPersonalizadosContratosListener=setOnReadPersonalizadosContratosListener(onReadPersonalizadosContratosListener);
        contratoHandler.readDefaultContratos(senderCorreo,onReadPersonalizadosContratosListener);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                contratoPersonalizadosAdapter=new ContratoAdapter(contratosPersonalizados, getApplicationContext(), new ContratoAdapter.OnContratoClick() {
                    @Override
                    public void onContratoClick(ContratoPlantilla contrato) {
                        sendContratoMessage(contrato.getFileName());
                        consContratos.setVisibility(View.GONE);
                        consPlus.setVisibility(View.GONE);
                        sendNotification(senderName+" te ha enviado una solicitud de contrato",
                                Constants.UNICODE_EMOJI_CONTRATO+" Contrato");
                    }

                    @Override
                    public void onImageClick(Bitmap image) {

                    }
                });
                contratosRecycler.setAdapter(contratoPersonalizadosAdapter);
                contratosRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        },500);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextMessage(input.getText().toString());
                if(chatMessages.size()==0){
                    MensajesHandler mensajesHandler= new MensajesHandler();
                    mensajesHandler.createChat(senderCorreo,receiverCorreo,anuncio,titulo.getText().toString(),categoria);
                }
                sendNotification(senderName+" te ha enviado un mensaje",Constants.UNICODE_EMOJI_BOCADILLO+" "+input.getText().toString());
                input.setText(null);
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(consPlus.getVisibility()==View.GONE){
                    consPlus.setVisibility(View.VISIBLE);
                }else{
                    consPlus.setVisibility(View.GONE);
                }
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE);
            }
        });
        contrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(consContratos.getVisibility()==View.GONE){
                    consContratos.setVisibility(View.VISIBLE);
                }else {
                    consContratos.setVisibility(View.GONE);
                }
            }
        });
        detalles.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(noAnuncio.getVisibility()==View.GONE){
                    if(consDetalles.getVisibility()==View.GONE){
                        detalles.setText("Pulsa para ocultar los detalles del anuncio");
                        consDetalles.setVisibility(View.VISIBLE);
                    }else{
                        detalles.setText("Pulsa para mostrar los detalles del anuncio");
                        consDetalles.setVisibility(View.GONE);
                    }
                }else{
                    detalles.setVisibility(View.INVISIBLE);
                    consDetalles.setVisibility(View.VISIBLE);
                }

            }
        });
        if(noAnuncio.getVisibility()==View.VISIBLE){
            detalles.setVisibility(View.INVISIBLE);
            consDetalles.setVisibility(View.VISIBLE);
        }
        userPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                ProfileFragment PROFILE_FRAGMENT = ProfileFragment.newInstance(false, receiverCorreo);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(android.R.id.content, PROFILE_FRAGMENT);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            sendImageMessage(imageUri);
            sendNotification(senderName+" te ha enviado una imagen", Constants.UNICODE_EMOJI_CAMERA+" Imagen");
        }
    }
    private void init(){
        mensajesAdapter=new MensajesAdapter(senderCorreo, chatMessages, new ContratoAdapter.OnContratoClick() {
            @Override
            public void onContratoClick(ContratoPlantilla contrato) {
                PDFViewFragment pdfViewFragment = PDFViewFragment.newInstance(contrato.getArchivo(), contrato.getTitle());
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(android.R.id.content, pdfViewFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onImageClick(Bitmap image) {
                ImageViewFragment imageViewFragment= ImageViewFragment.newInstance(image);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(android.R.id.content, imageViewFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        chatRecycler.setAdapter(mensajesAdapter);

        chatRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void sendNotification(String title,String message){
        FCMSend.pushNotification(getApplicationContext(),receiverToken,title,message,Constants.NOTIFICATION_TYPE_MENSAJE);
    }


    private void getUserData() throws Exception {
        //RECIBE LOS DATOS DEL USUARIO (SENDER)
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        senderCorreo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");
        anuncio=getIntent().getStringExtra("anuncio");

        UsuarioHandler usuarioHandler= new UsuarioHandler();
        onReadListenerSender=setOnReadListenerSender(onReadListenerSender);
        usuarioHandler.readUserData(senderCorreo,onReadListenerSender, "");
        //RECIBE LOS DATOS DEL CONTACTO (RECEIVER)
        onReadListener=setOnReadListener(onReadListener);
        usuarioHandler.readUserData(getIntent().getStringExtra("email"),onReadListener, "");
    }
    private void setDatosAnuncio() {
        onReadAnuncioListener = setOnReadAnuncioListener(onReadAnuncioListener);
        AnuncioHandler anuncioHandler = new AnuncioHandler();

        if(anuncio!=null){
            anuncioHandler.onReadAnuncios(anuncio,
                    categoria,
                    onReadAnuncioListener);
        }
    }

    private void sendTextMessage(String message){
        MensajesHandler mensajesHandler = new MensajesHandler();
        mensajesHandler.sendMessage(message,senderCorreo,receiverCorreo,"texto", new Date(),anuncio);

    }
    private void sendImageMessage(Uri uri){
        MensajesHandler mensajesHandler=new MensajesHandler();
        String message=Usuario.generarNombreImagenPerfil();
        mensajesHandler.sendMessage(message,senderCorreo,receiverCorreo,Constants.MESSAGE_TYPE_IMG,new Date(),anuncio);
        mensajesHandler.uploadImagenMensaje(uri,message);
    }
    private void sendContratoMessage(String message){
        MensajesHandler mensajesHandler=new MensajesHandler();
        mensajesHandler.sendMessage(message,senderCorreo,receiverCorreo,Constants.MESSAGE_TYPE_CONTRATO, new Date(),anuncio);
    }
    private void listenMessage(){
        db.collection("mensajes")
                .whereEqualTo("sender",senderCorreo)
                .whereEqualTo("receiver",receiverCorreo)
                .whereEqualTo("anuncio",anuncio)
                .addSnapshotListener(eventListener);

        db.collection("mensajes")
                .whereEqualTo("sender",receiverCorreo)
                .whereEqualTo("receiver",senderCorreo)
                .whereEqualTo("anuncio",anuncio)
                .addSnapshotListener(eventListener);


    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error!=null){
            return;
        }
        if(value != null){
            int count =chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType()== DocumentChange.Type.ADDED){
                    MensajeUtil mensajeUtil = new MensajeUtil();
                    mensajeUtil.setSender(documentChange.getDocument().getString("sender"));
                    mensajeUtil.setReceiver(documentChange.getDocument().getString("receiver"));
                    mensajeUtil.setMessage(documentChange.getDocument().getString("mensaje"));
                    mensajeUtil.setTimestamp(documentChange.getDocument().getTimestamp("timestamp"));
                    mensajeUtil.setType(documentChange.getDocument().getString("type"));
                    chatMessages.add(mensajeUtil);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(chatMessages, Comparator.comparing(MensajeUtil::getTimestamp));
            }
            init();
            if(count==0){
                mensajesAdapter.notifyDataSetChanged();
            }else{
                mensajesAdapter.notifyItemRangeChanged(chatMessages.size(),chatMessages.size());
                chatRecycler.smoothScrollToPosition(chatMessages.size()-(chatMessages.size()-1));
                chatRecycler.smoothScrollToPosition(chatMessages.size()-1);
            }
            chatRecycler.setVisibility(View.VISIBLE);
        }
        chatProgress.setVisibility(View.GONE);
    };

    private UsuarioHandler.OnReadListener setOnReadListener(UsuarioHandler.OnReadListener onReadListener) {
        //LISTENER DE LECTURA DEL USUARIO
        onReadListener=new UsuarioHandler.OnReadListener() {
            @Override
            public void onReadSuccess(Usuario usuario) {
                userName.setText(usuario.getNombre()+" "+usuario.getApellido());
                receiverCorreo=usuario.getEmail();

                UsuarioHandler usuarioHandler= new UsuarioHandler();
                onTokenReadListener=setOnTokenReadListener(onTokenReadListener);

                usuarioHandler.getToken(usuario.getEmail(),onTokenReadListener);
                listenMessage();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference("pfp/"+usuario.getPfp());

                try {
                    File localFile = File.createTempFile("tempfile",".jpg");
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            userPfp.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            userPfp.setImageResource(R.drawable.user_no_pic);
                        }
                    });
                } catch (IOException e) {

                }
            }

            @Override
            public void onReadError(String error) {

            }
        };
        return onReadListener;
    }
    private String getFormatedDate(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private AnuncioHandler.OnReadAnuncioListener setOnReadAnuncioListener(AnuncioHandler.OnReadAnuncioListener onReadAnuncioListener) {
        onReadAnuncioListener = new AnuncioHandler.OnReadAnuncioListener() {
            @Override
            public void onReadSucess(Anuncio anuncio) {
                if(anuncio!=null){
                    precio.setText(anuncio.getPrecio() + "€/mes");
                    titulo.setText(anuncio.getTitulo());
                    direccion.setText(anuncio.getDireccion());
                }else{
                    precio.setVisibility(View.GONE);
                    titulo.setVisibility(View.GONE);
                    direccion.setVisibility(View.GONE);
                    detalles.setVisibility(View.INVISIBLE);

                    consDetalles.setVisibility(View.VISIBLE);
                    consDetalles.setBackgroundResource(R.drawable.bg_button_04);
                    noAnuncio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReadError(String error) {
                precio.setVisibility(View.GONE);
                titulo.setVisibility(View.GONE);
                direccion.setVisibility(View.GONE);
                detalles.setVisibility(View.INVISIBLE);

                consDetalles.setVisibility(View.VISIBLE);
                consDetalles.setBackgroundResource(R.drawable.bg_button_04);
                noAnuncio.setVisibility(View.VISIBLE);

            }
        };
        return onReadAnuncioListener;
    }

    private UsuarioHandler.OnReadListener setOnReadListenerSender(UsuarioHandler.OnReadListener onReadListener){
        onReadListener=new UsuarioHandler.OnReadListener() {
            @Override
            public void onReadSuccess(Usuario usuario) {
                senderName=usuario.getNombre();
            }

            @Override
            public void onReadError(String error) {

            }
        };
        return  onReadListener;
    }

    private UsuarioHandler.OnTokenReadListener setOnTokenReadListener(UsuarioHandler.OnTokenReadListener onTokenReadListener){
        onTokenReadListener=new UsuarioHandler.OnTokenReadListener() {
            @Override
            public void onReadTokenSuccess(String token) {
                receiverToken=token;
            }
        };
        return onTokenReadListener;
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
            }
        };
        return onReadDefaultContratosListener;
    }

}