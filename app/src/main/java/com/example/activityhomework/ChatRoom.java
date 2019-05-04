package com.example.activityhomework;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ChatRoom extends AppCompatActivity {

    private int roomID;
    private Socket mSocket;
    private Map<String, String> map;
    private final static String TAG = ChatRoom.class.getSimpleName();
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    String date_time;
                    try {
                        message = data.getString("message");
                        date_time = data.getString("date_time");
                    } catch (JSONException e) {
                        return;
                    }
                    map.put("who", String.valueOf(1)); //her
                    map.put("message", message);
                    map.put("date_time", date_time);
                    // add the message to view
                }
            });
        }
    };
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        /*
            聊天室邏輯：
            1. 取得聊天室ID
            2. 取得自身UserID
            3. get聊天室雙方的userData，即可知道對方UserInfo
            4. 之後再add to 所以recyclerView array
            5. 所以recyclerView 必須傳入兩個 array 1.userData 2.message
        */

        // 1. 取得聊天室ID
        roomID = getSharedPreferences("ChatRoom", MODE_PRIVATE).getInt("roomid", 0);
        // 2. 取得自身USERID
        userID = getSharedPreferences("auth", Context.MODE_PRIVATE).getString("userID", null);
        // 3. 獲得聊天室雙方的userData
        __getBothUserData(roomID);

        //連接socket
        initSocket();
        mSocket.on("new message", onNewMessage);
        mSocket.connect();
    }




    public void initSocket(){
        try {
            mSocket = IO.socket("http:127.0.0.1:3060");
        } catch (URISyntaxException e) {
            Toast.makeText(this, "WebSocket未連接成功。", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    public void __getBothUserData(int roomid){
        OkHttpClient client = new OkHttpClient();
        final Request request =  new Request.Builder().url("http://10.0.2.2:3060/getbothdata?roomid="+ roomid).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ChatRoom.this ,"網路發生問題。", Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d(TAG, "onResponse: " + res);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String success = jsonObject.getString("success");
                    if (success.equals("true")){
                        //不要用recyclerView做好了= =!




                    }else{
                        returnError();
                    }
                } catch (JSONException e) {
                    returnError();
                    e.printStackTrace();
                }
            }
            public void returnError(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatRoom.this ,"網路發生問題。", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
