package com.example.renthubpablo.resources;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.renthubpablo.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

public class NotificationService extends FirebaseMessagingService {
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        boolean sendNotification=false;
        sharedPreferences= getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NOTIFICACIONES, MODE_PRIVATE);
        if (isAppInForeground()) {
            return;
        }

        String title=message.getNotification().getTitle();
        String body=message.getNotification().getBody();

        Map<String, String> data = message.getData();
        String type = data.get(Constants.NOTIFICATION_TYPE);

        if(sharedPreferences.getString(Constants.NOTIFICACIONES_ALL,"").equals("true")){
            if(sharedPreferences.getString(Constants.NOTIFICACIONES_MENSAJES,"").equals("true")
            &&type.equals(Constants.NOTIFICATION_TYPE_MENSAJE)){
                sendNotification=true;
            }else if (sharedPreferences.getString(Constants.NOTIFICACIONES_VALORACIONES,"").equals("true")
                    &&type.equals(Constants.NOTIFICATION_TYPE_VALORACION)) {
                sendNotification = true;
            }else {
                sendNotification=false;
            }
        }

        if(sendNotification){
            String CHANNEL_ID=Constants.NOTIFICATION_CHANNEL_ID;

            NotificationChannel channel= new NotificationChannel(
                    CHANNEL_ID,
                    Constants.NOTIFICATION_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo_icon_noti)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat.from(getApplicationContext()).notify(1,builder.build());
        }
        super.onMessageReceived(message);

    }

    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : appProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (processInfo.processName.equals(getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
