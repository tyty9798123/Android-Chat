package com.example.activityhomework;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogInAsyncPost{
    Context ctx;
    FragmentActivity act;
    private FragmentTransaction fragmentTransaction;

    public LogInAsyncPost(Context ctx, Map map, FragmentActivity activity){
        this.ctx = ctx;
        this.act = activity;
        new signUpPost().execute(map);
    }

    class signUpPost extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map... maps) {
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
                // 登入成功
                // Dialog && go to Home Page
                String result;
                try {
                    result = jsonObj.getString("result");
                }
                catch(Exception e){
                    errorDialogShow("網路連線發生問題");
                    return;
                }
                JSONArray jsonarray = null;
                try {
                    jsonarray = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String id = "", name = "";
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = jsonarray.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        id = jsonobject.getString("UserID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        name = jsonobject.getString("UserName");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (id.length() > 0 && name.length() > 0){
                    Toast.makeText(ctx, id + "," + name, Toast.LENGTH_SHORT).show();
                    ctx.getSharedPreferences("auth", Context.MODE_PRIVATE).edit().putString("userID", id).commit();
                    ctx.getSharedPreferences("auth", Context.MODE_PRIVATE).edit().putString("userName", name).commit();

                    new AlertDialog.Builder(LogInAsyncPost.this.ctx)
                            .setTitle("恭喜")
                            .setMessage(message)
                            .setPositiveButton("回首頁", goHomeFragment)
                            .show();
                }

            }else{
                // 登入失敗
                errorDialogShow(message);
            }
        }

        public void errorDialogShow(String message){
            new AlertDialog.Builder(LogInAsyncPost.this.ctx)
                    .setIcon(R.drawable.alert)
                    .setTitle("警告")
                    .setMessage(message)
                    .setPositiveButton("確定", null)
                    .show();
        }

        public DialogInterface.OnClickListener goHomeFragment = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // go home fragment

                FragmentManager fm = act.getSupportFragmentManager();
                fragmentTransaction = fm.beginTransaction();
                // SignUpFragment.getInstance() 必須換成首頁
                fragmentTransaction.replace(R.id.container_fragment, HomeFragement.getInstance());
                fragmentTransaction.commit();
            }
        };
    }
}
