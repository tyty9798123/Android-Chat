package com.example.activityhomework;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getListDataAsyncGet {

    private Context ctx;
    private FragmentActivity act;
    private RecyclerView recyclerView;
    private Map<String, String> map;
    private final static String TAG = addFriendsAsyncPost.class.getSimpleName();

    public getListDataAsyncGet(Context ctx, FragmentActivity act, Map map, RecyclerView recyclerView) {
        this.ctx = ctx;
        this.act = act;
        this.map = map;
        this.recyclerView = recyclerView;
        __HttpGet();
    }

    private void setAdapter(ArrayList<Map<String, String>> arr){
        recyclerView.setAdapter(new FriendListAdapter(ctx, act , arr));
    }
    private void __HttpGet() {
        OkHttpClient client = new OkHttpClient();
        final Request request =  new Request.Builder().url("http://10.0.2.2:3060/getfriends?userid="+ map.get("userID")).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx ,"網路發生問題。", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                try {
                    final JSONObject jsonObject = new JSONObject(res);
                    if ( jsonObject.getString("success").equals("true") ){
                        try {
                            String result = jsonObject.getString("result");

                            JSONArray jsonArray = new JSONArray(result);

                            final ArrayList<Map<String, String>> arrayList = new ArrayList<>();
                            for (int i = 0 ; i < jsonArray.length(); i++){
                                JSONObject jo = new JSONObject(jsonArray.get(i).toString());
                                Map<String, String> map = new HashMap<>();
                                map.put("userID", jo.getString("User2ID"));
                                map.put("userName", jo.getString("UserName"));
                                map.put("FriendID", jo.getString("FriendID"));

                                Log.d(TAG, "onResponse: " + jsonArray.get(i));
                                arrayList.add(   map   );
                            }
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setAdapter(arrayList);
                                }
                            });
                        }
                        catch (JSONException e){
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(ctx, jsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
                            return;
                        }
                    }else{
                        String message = jsonObject.getString("message");
                        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx ,"網路發生問題。", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }
}