package com.example.renthubpablo.resources;

import android.content.Context;
import android.os.StrictMode;

import com.android.volley.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    public static void pushNotification(Context context, String token, String title, String message, String type){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue= Volley.newRequestQueue(context);

        try{
            JSONObject json = new JSONObject();
            json.put(Constants.NOTIFICATION_TO,token);

            JSONObject notification = new JSONObject();
            notification.put(Constants.NOTIFICATION_TITLE,title);
            notification.put(Constants.NOTIFICATION_BODY,message);

            JSONObject data = new JSONObject();
            data.put(Constants.NOTIFICATION_TYPE, type);

            json.put(Constants.NOTIFICATION_CAT_NOTIFICATION,notification);
            json.put(Constants.NOTIFICATION_CAT_DATA,data);

            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    Constants.BASE_URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params= new HashMap<>();
                    params.put(Constants.REMOTE_MSG_CONTENT_TYPE,Constants.REMOTE_MSG_ROUTE);
                    params.put(Constants.REMOTE_MSG_AUTHORIZATION,Constants.SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
