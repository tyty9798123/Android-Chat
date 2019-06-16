package com.example.activityhomework;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getRoomIDAsyncGet {
    public static final String TAG = getRoomIDAsyncGet.class.getSimpleName();
    private Context ctx;
    private FragmentActivity act;
    private Map< String, String > map;
    public getRoomIDAsyncGet(Context ctx, FragmentActivity act, Map<String, String> map){
        this.ctx = ctx;
        this.act = act;
        this.map = map;
        __HttpGet();
    }

    private void __HttpGet() {
        OkHttpClient client = new OkHttpClient();
        Log.d(TAG, "__HttpGet: " + map.get("friendid"));
        final Request request =  new Request.Builder().url("http://172.105.226.60:3060/getroomid?friendid="+ map.get("friendid")).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ctx ,"網路發生問題。", Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d(TAG, "getRoomIDAsyncGet onResponse: " + res);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    final int roomid = jsonObject.getInt("roomid");
                    act.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ctx.getSharedPreferences("ChatRoom",Context.MODE_PRIVATE)
                                    .edit()
                                    .putInt(
                                            "roomid",roomid//waiting for response roomid
                                    ).
                                    commit();
                            Intent x = new Intent(act, ChatRoom.class);
                            act.startActivity(x);
                        }
                    });
                } catch (JSONException e) {
                    Toast.makeText(ctx ,"網路發生問題。", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });
    }

}
