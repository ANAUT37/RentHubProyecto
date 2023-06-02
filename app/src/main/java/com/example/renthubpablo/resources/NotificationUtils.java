package com.example.renthubpablo.resources;

import static com.example.renthubpablo.resources.Constants.SERVER_KEY;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.example.renthubpablo.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.annotations.SerializedName;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationUtils extends FirebaseMessagingService {

    private static final String BASE_URL = "https://fcm.googleapis.com/";
    private ApiService apiService;
    private static final String CHANNEL_ID = "my_channel_id";
    private static final String CHANNEL_NAME = "My Channel";
    private static final String CHANNEL_DESCRIPTION = "Sample Notification Channel";


    public NotificationUtils(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        //sendNotiOnMessageSent(message.getSenderId(), "", getApplicationContext());

        inflateNotification(message.getNotification().getTitle(),
                message.getNotification().getBody(),
                getApplicationContext());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);
    }

    public static void inflateNotification(String title, String body, Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_icon_noti)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManager.notify(0, builder.build());
    }


    public void sendNotification(String recipientToken, String title, String message) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setTo(recipientToken);
        notificationRequest.setData(new NotificationData(title, message));

        apiService.sendNotification(SERVER_KEY, notificationRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                   System.out.println("OCURRIO: NOTIFICACIONE ENVIADA EXITOSAMENTE");
                } else {
                    System.out.println("OCURRIO: ERROR CASI CASI "+response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("OCURRIO: VACIIOOOO");
            }
        });
    }
    static class NotificationRequest {
        @SerializedName("to")
        private String to;

        @SerializedName("data")
        private NotificationData data;

        public void setTo(String to) {
            this.to = to;
        }

        public void setData(NotificationData data) {
            this.data = data;
        }
    }
    private static class NotificationData {
        @SerializedName("title")
        private String title;

        @SerializedName("message")
        private String message;

        public NotificationData(String title, String message) {
            this.title = title;
            this.message = message;
        }
    }


    //METODO 2?



}


