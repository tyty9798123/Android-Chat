package com.example.activityhomework;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpAsyncPost {
    Context ctx;
    FragmentTransaction fragmentTransaction;
    FragmentActivity act;
    public SignUpAsyncPost(Context ctx, Map map, FragmentActivity act){
        this.ctx = ctx;
        this.act = act;
        new signUpPost().execute(map);
    }

    class signUpPost extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map... maps) {
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("name", maps[0].get("userName").toString())
                    .add("account", maps[0].get("userAccount").toString())
                    .add("password", maps[0].get("userPassword").toString())
                    .build();
            Request request = new Request.Builder()
                    .url("http://172.105.226.60:3060/signup")
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
            if (s == null){
                errorDialogShow("網路連線發生問題");
                return;
            }
            JSONObject jsonObj;
            try {
                jsonObj = new JSONObject(s);
            }
            catch (Exception e){
                errorDialogShow("網路連線發生問題");
                return;
            }
            String success;
            String message;
            try {
                success = jsonObj.getString("success");
                message = jsonObj.getString("message");
            }
            catch (Exception e){
                errorDialogShow("網路連線發生問題");
                return;
            }
            if (Boolean.valueOf(success)){
                // 註冊帳號成功
                // Dialog && go to Login Page
                new AlertDialog.Builder(SignUpAsyncPost.this.ctx)
                        .setTitle("恭喜")
                        .setMessage(message)
                        .setPositiveButton("馬上登入去", goLoginFragement)
                        .show();

            }else{
                // 註冊帳號失敗
                errorDialogShow(message);
            }
        }

        public void errorDialogShow(String message){
            new AlertDialog.Builder(SignUpAsyncPost.this.ctx)
                    .setIcon(R.drawable.alert)
                    .setTitle("警告")
                    .setMessage(message)
                    .setPositiveButton("確定", null)
                    .show();
        }

        public DialogInterface.OnClickListener goLoginFragement = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // go login fragment
                // go home fragment
                FragmentManager fm = act.getSupportFragmentManager();
                fragmentTransaction = fm.beginTransaction();
                // SignUpFragment.getInstance() 必須換成首頁
                fragmentTransaction.replace(R.id.container_fragment, LogInFragment.getInstance());
                fragmentTransaction.commit();
            }
        };
    }
}
