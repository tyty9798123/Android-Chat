package com.example.activityhomework;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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
    private final static String TAG = addFriendsAsyncPost.class.getSimpleName();
    public addFriendsAsyncPost(Context ctx, FragmentActivity act, Map map){
        this.ctx = ctx;
        this.act = act;
        new send_post().execute(map);
    }

    class send_post extends AsyncTask<Map<String, String>, Void, String>{

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("user1ID", maps[0].get("user1ID").toString())
                    .add("user2Account", maps[0].get("user2Account").toString())
                    .build();
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:3060/addfriend")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                return responseString;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                String msg = jsonObject.getString("message");
                if (jsonObject.getString("success").equals("false")){
                    Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
                    return;
                }else{
                    Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
                    //reload(尚未製作)
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
