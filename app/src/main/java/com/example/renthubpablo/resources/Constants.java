package com.example.renthubpablo.resources;

import java.util.HashMap;

public class Constants {
    public static final String KEY_DEV="";//CLAVE PARA ENCRIPTACION DE DATOS
    public static final String SERVER_KEY="key=";//CLAVE PARA CLOUD MESSAGING
    public static final String MAPS_API_KEY="";//CLAVE API NECESARIA PARA GEOLOCATION API, PLACES API, GOOGLE MAPS API

    public static final String BASE_URL="https://fcm.googleapis.com/fcm/send";
    public static final int GALLERY_REQUEST_CODE = 100;
    public static final String SHARED_PREFERENCES_NAME="renthub";
    public static final String SHARED_PREFERENCES_HISTORIAL_NAME="historial";
    public static final String SHARED_PREFERENCES_CORREO="userCorreo";
    public static final String SHARED_PREFERENCES_PASSWORD="userPassword";
    public static final int VIEW_MESSAGE_SENT=1;
    public static final int VIEW_MESSAGE_RECEIVED=2;
    public static final int VIEW_IMAGE_SENT=3;
    public static final int VIEW_IMAGE_RECEIVED=4;

    //VALORES DE FILTROS DE BUSQUEDA
    public static final int[] VALORES_FILTRO_PRECIO_MINIMO={0,100,150,200,250,300,350,400,450,500,550,600
    ,650,700,750,800,850,900,950,1000,1500,2000};
    public static final int[] VALORES_FILTRO_PRECIO_MAXIMO={250,300,350,400,450,500,550,600,650,700,750,
    800,850,900,950,1000,1200,1500,2000,3000,5000,0};
    public static final double[] VALORES_FILTRO_DISTANCIA={0.018,0.027,0.045,0.09,0.135,0.27,0.45};


    //RUTAS FIREBASE FIRESTORE
    public static final String RUTA_APPSETTINGS="appsettings";
    public static final String CAMPO_APP_STATUS="status";

    public static final String COLLECTION_USUARIOS="usuarios";
    public static final String USUARIO_NOMBRE="nombre";
    public static final String USUARIO_APELLIDO="apellido";
    public static final String USUARIO_EMAIL="email";
    public static final String USUARIO_ESTADO_CUENTA="estado";
    public static final String USUARIO_GENERO="genero";
    public static final String USUARIO_PASSWORD="password";
    public static final String USUARIO_FOTO_PERFIL="pfp";
    public static final String USUARIO_DESCRIPCION="descripcion";
    public static final String USUARIO_TELEFONO="phone";
    public static final String USUARIO_PRONOMBRES="pronombres";
    public static final String CARPETA_FOTO_PERFIL="pfp/";
    public static final String CAPERTA_FOTO_ANUNCIO="anuncios/";
    public static final String CARPETA_FOTO_APP="appinfo/";
    public static final String CARPETA_FOTO_MENSAJE = "mensajes/";
    public static final String CARPETA_FOTO_EXTENSION=".jpg";


    //ERRORES
    public static final String NO_LOCATION_PERMISSION_ERROR_MESSAGE="RentHub necesita permisos de ubicación para funcionar."
            +"\nActiva dichos permisos y vuelve a entrar a la aplicación.";
    public static final String NO_APP_STATUS_ERROR_MESSAGE="Parece que estamos temporalmente fuera de servicio o en mantenimiento," +
            " disculpa las molestias =)\n\n\nContacto:\npablomartinezanaut37@gmail.com";
    public static final String NO_TYPE_SELECTED="Debes seleccionar un tipo de inmueble";
    public static final String NO_UBI_ENTERED="Debes seleccionar una ubicación";
    public static final String WRONG_PASSWORD="Contraseña incorrecta";
    public static final String USER_NOT_EXIST="Este usuario no existe";
    public static final String CAMPO_VACIO="Este campo no puede estar vacío";

    public static final String SHARED_PREFERENCES_TOKEN = "token";

    public static final String REMOTE_MSG_AUTHORIZATION="Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE="Content-Type";
    public static final String REMOTE_MSG_DATA="data";
    public static final String REMOTE_MSG_REGISTRATION_IDS="registration_ids";
    public static final String USUARIO_FECHA = "fechanacimiento";
    public static final String MESSAGE_TYPE_IMG = "imagen";

    //NOTIFICACIONES
    public static final String REMOTE_MSG_ROUTE = "application/json";
    public static final String NOTIFICACIONES_ALL = "all";
    public static final String NOTIFICACIONES_MENSAJES = "mensajes";
    public static final String NOTIFICACIONES_VALORACIONES = "valoraciones";
    public static final String NOTIFICACIONES_FAVORITOS = "favoritos";
    public static final String NOTIFICACIONES_APP = "app";
    public static final String NOTIFICATION_TYPE = "type";
    public static final String NOTIFICATION_TO = "to";
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_BODY = "body";
    public static final String NOTIFICATION_CAT_NOTIFICATION = "notification";
    public static final String NOTIFICATION_CAT_DATA = "data";
    public static final String NOTIFICATION_TYPE_VALORACION = "valoracion";
    public static final String NOTIFICATION_TYPE_MENSAJE = "mensaje";
    public static final String SHARED_PREFERENCES_NOTIFICACIONES = "notificaciones";
    public static final CharSequence NOTIFICATION_NAME = "Message Notification";
    public static final String NOTIFICATION_CHANNEL_ID = "MESSAGE";
    public static final String ANUNCIO_404 = "anuncio_404";
    public static final String COLLECTION_PLANTILLAS = "plantillas";
    public static final String MESSAGE_TYPE_CONTRATO = "contrato";
    public static final String UNICODE_EMOJI_CAMERA = "\uD83D\uDCF7";
    public static final String UNICODE_EMOJI_CONTRATO = "\uD83D\uDCDD";
    public static final String UNICODE_EMOJI_BOCADILLO = "\uD83D\uDCAC";

    public static HashMap<String,String> remoteMsgHeaders=null;
    public static HashMap<String,String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders==null){
            remoteMsgHeaders=new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    ""
            );
        }
    return null;
    }


}
