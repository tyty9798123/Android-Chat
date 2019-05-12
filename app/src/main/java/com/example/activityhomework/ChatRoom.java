package com.example.activityhomework;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ChatRoom extends AppCompatActivity {

    private int roomID;
    private Map<String, String> map;
    private final static String TAG = ChatRoom.class.getSimpleName();
    public ArrayList< Map<String, String> > userInfo;
    /*
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
    */
    private String userID;
    private EditText chatbox;
    private Button submit;
    private Socket mSocket;
    private ScrollView scrollView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        {
            try {
                mSocket = IO.socket("http://10.0.2.2:3060/");
            } catch (URISyntaxException e) {

            }
        }
        findViews();
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
        //ㄙmSocket.on("socketData", onNewMessage);
        mSocket.connect();
    }


    public void findViews(){
        linearLayout = findViewById(R.id.linearLayout);
        chatbox = findViewById(R.id.chatbox);
        submit = findViewById(R.id.submit);
        scrollView = findViewById(R.id.scrollView);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chatText = null;
                try{
                    chatText = chatbox.getText().toString();
                }
                catch (Exception e){
                    Toast.makeText(ChatRoom.this, "字串解析不正確", Toast.LENGTH_LONG).show();
                    return;
                }
                if ( chatText.length() > 0){
                    Map<String, String> map = new HashMap<>();
                    map.put("roomID", String.valueOf(roomID));
                    map.put("userID", String.valueOf(userID));
                    map.put("message", chatbox.getText().toString());
                    //如果是圖片的話就傳送roomID, userID, image
                    String stringData = new JSONObject(map).toString();

                    addMessage("","你媽逼啦");

                    mSocket.emit("socketData", stringData);
                }else{
                    Toast.makeText(ChatRoom.this, "輸入欄位不得為空", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    private void addMessage(String name, String message){
        TextView textView = new TextView(ChatRoom.this);
        textView.setGravity(Gravity.RIGHT);

        textView.setPadding(10, 10 ,10 ,10);
        textView.setMaxWidth(30);
        textView.setBackgroundResource(R.drawable.msg_bg);
        textView.setText(message);

        ChatRoom.this.linearLayout.addView(textView);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
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
                        try {
                            userInfo = new ArrayList<>();
                            String result = jsonObject.getString("result");
                            JSONArray jsonArray = new JSONArray(result);
                            JSONObject[] userData = {
                                    new JSONObject(jsonArray.getString(0)),
                                    new JSONObject(jsonArray.getString(1))
                            };
                            for (int i = 0; i < 2; i++){
                                Map<String, String> map = new HashMap<>();
                                map.put("UserID", userData[i].getString("UserID"));
                                map.put("UserName", userData[i].getString("UserName"));
                                map.put("UserAccount", userData[i].getString("UserAccount"));
                                map.put("UserPassword", userData[i].getString("UserPassword"));
                                userInfo.add(map);
                            }
                            Log.d(TAG, "UserIDUserID: " + userInfo.get(1).get("UserID"));
                        }
                        catch (Exception e){
                            returnError();
                            e.printStackTrace();
                        }

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
