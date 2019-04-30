package com.example.activityhomework;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class addFriendsAsyncPost {

    private Context ctx;
    private FragmentActivity act;
    private Map map;

    public addFriendsAsyncPost(Context ctx, FragmentActivity act, Map map){
        this.ctx = ctx;
        this.act = act;
        this.map = map;
    }

    class send_post extends AsyncTask<Map<String, String>, Void, String>{

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("account", maps[0].get("userAccount").toString())
                    .add("password", maps[0].get("userPassword").toString())
                    .build();
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:3060/login")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
