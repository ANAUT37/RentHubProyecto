package com.example.renthubpablo.resources;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
            Constants.REMOTE_MSG_CONTENT_TYPE+":"+ Constants.REMOTE_MSG_ROUTE+","+
            Constants.REMOTE_MSG_AUTHORIZATION+": key="+Constants.SERVER_KEY
    })
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Header(Constants.REMOTE_MSG_AUTHORIZATION) String serverKey, @Body NotificationUtils.NotificationRequest notificationRequest);
}
