
package com.example.activityhomework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ChatRoom extends AppCompatActivity {
    private MessageList adapter;
    private int roomID;
    private Map<String, String> map;
    private final static String TAG = ChatRoom.class.getSimpleName();
    public ArrayList< Map<String, String> > userInfo;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "message run: " + args[0]);
                    String resp = String.valueOf(args[0]);
                    try {
                        JSONObject jsonObj = new JSONObject(resp);
                        String message = jsonObj.getString("message");
                        String getRoomID = jsonObj.getString("roomID");
                        if (roomID == Integer.parseInt(getRoomID)){
                            //addMessageLeft("", message);
                            Map<String, String> adapterMap = new HashMap<>();
                            String UserName = null;
                            for (int i = 0; i < 2; i++){
                                if (!userInfo.get(i).get("UserID").equals(userID)){
                                    UserName = userInfo.get(i).get("UserName");
                                    break;
                                }
                            }
                            adapterMap.put("userName", UserName);
                            adapterMap.put("message", message);
                            adapterMap.put("dateTime", jsonObj.getString("nowDateTime"));
                            adapterMap.put("viewType", "2");
                            adapterArray.add(adapterMap);
                            adapter.notifyItemInserted(adapterArray.size() - 1);
                            adapter.notifyDataSetChanged();

                            setRecyclerViewToBottom();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "receive message: " + args[0]);
                    /*
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

                    */
                }
            });
        }
    };

    private Emitter.Listener onNewImage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "message run: " + args[0]);
                    String resp = String.valueOf(args[0]);

                    try {
                        JSONObject jsonObj = new JSONObject(resp);

                        String fileName = jsonObj.getString("fileName");
                        String getRoomID = jsonObj.getString("roomID");
                        if (roomID == Integer.parseInt(getRoomID)){
                            //addMessageLeft("", message);
                            Map<String, String> adapterMap = new HashMap<>();
                            String UserName = null;
                            for (int i = 0; i < 2; i++){
                                if (!userInfo.get(i).get("UserID").equals(userID)){
                                    UserName = userInfo.get(i).get("UserName");
                                    break;
                                }
                            }
                            adapterMap.put("userName", UserName);
                            adapterMap.put("fileName", fileName);
                            adapterMap.put("dateTime", jsonObj.getString("nowDateTime"));
                            adapterMap.put("viewType", "4");
                            adapterArray.add(adapterMap);
                            adapter.notifyItemInserted(adapterArray.size() - 1);
                            adapter.notifyDataSetChanged();

                            setRecyclerViewToBottom();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    public static final int GET_FROM_GALLERY = 3;

    private String userID;
    private EditText chatbox;
    private Button submit;
    private Socket mSocket;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private ArrayList<Map<String, String>> adapterArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        {
            try {
                mSocket = IO.socket("http://172.105.226.60:3060/");
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
        userID = getSharedPreferences("auth", Context.MODE_PRIVATE).getString("userID", "");
        // 3. 獲得聊天室雙方的userData
        __getBothUserData(roomID);

        //連接socket
        mSocket.on("socketData", onNewMessage);
        mSocket.on("socketImage", onNewImage);
        mSocket.connect();
    }

    public void readHistory(){
        OkHttpClient client = new OkHttpClient();
        final Request request =  new Request.Builder().url("http://172.105.226.60:3060/gethistory?roomid="+ roomID).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                filure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    if (jsonObject.getString("success").equals("true")){
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
                        for (int i = 0; i < jsonArray.length(); i++){
                            final JSONObject row = new JSONObject(jsonArray.get(i).toString());
                            if ( row.getString("message").contains(".jpg") ){
                                if ( row.getString("userID").equals( userID ) ) {
                                    Map<String, String> adapterMap = new HashMap<>();
                                    adapterMap.put("userName", userInfo.get(0).get("UserName"));
                                    adapterMap.put("encodedImage", String.valueOf(row.getString("message")));
                                    adapterMap.put("dateTime", row.getString("dateTime"));
                                    adapterMap.put("viewType", "3");
                                    adapterMap.put("viewType2", "1");
                                    adapterArray.add(adapterMap);
                                    //adapter = new MessageList(ChatRoom.this, adapterArray);
                                    adapter.notifyItemInserted(adapterArray.size() - 1);
                                    adapter.notifyDataSetChanged();
                                    setRecyclerViewToBottom();
                                }else{
                                    Map<String, String> adapterMap = new HashMap<>();
                                    String userName = "";
                                    for (int j = 0; j < userInfo.size(); j++) {
                                        if ( !userID.equals(userInfo.get(j).get("UserID")) ){
                                            userName = userInfo.get(j).get("UserName");
                                            adapterMap.put("userName", userName);
                                            break;
                                        }
                                    }
                                    adapterMap.put("fileName", row.getString("message"));
                                    adapterMap.put("dateTime", row.getString("dateTime"));
                                    adapterMap.put("viewType", "4");
                                    adapterArray.add(adapterMap);
                                    adapter.notifyItemInserted(adapterArray.size() - 1);
                                    adapter.notifyDataSetChanged();

                                    setRecyclerViewToBottom();
                                }
                            }else{
                                if ( row.getString("userID").equals( userID ) ){
                                    Map<String, String > adapterMap = new HashMap<>();
                                    adapterMap.put("userName", userInfo.get(0).get("UserName"));
                                    adapterMap.put("message", row.getString("message"));
                                    adapterMap.put("dateTime", row.getString("dateTime"));
                                    adapterMap.put("viewType", "0");
                                    adapterArray.add(adapterMap);
                                    //adapter = new MessageList(ChatRoom.this, adapterArray);
                                    adapter.notifyItemInserted(adapterArray.size() - 1);
                                    adapter.notifyDataSetChanged();
                                    setRecyclerViewToBottom();
                                }else{
                                    Map<String, String> adapterMap = new HashMap<>();
                                    String userName = "";
                                    for (int j = 0; j < userInfo.size(); j++) {
                                        if ( !userID.equals(userInfo.get(j).get("UserID")) ){
                                            userName = userInfo.get(j).get("UserName");
                                            adapterMap.put("userName", userName);
                                            break;
                                        }
                                    }
                                    adapterMap.put("message", row.getString("message"));
                                    adapterMap.put("dateTime", row.getString("dateTime"));
                                    adapterMap.put("viewType", "2");
                                    adapterArray.add(adapterMap);
                                    adapter.notifyItemInserted(adapterArray.size() - 1);
                                    adapter.notifyDataSetChanged();

                                    setRecyclerViewToBottom();
                                }
                            }
                        }
                    }else{
                        filure();
                    }

                } catch (JSONException e) {
                    filure();
                    e.printStackTrace();
                }
            }
            public void filure(){
                ChatRoom.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatRoom.this ,"還沒有任何對話紀錄。", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
        });

    }
    public void setRecyclerViewToBottom(){
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    public void findViews() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        recyclerView = findViewById(R.id.recyclerview2);
        chatbox = findViewById(R.id.chatbox);
        submit = findViewById(R.id.submiting);
        scrollView = findViewById(R.id.scrollView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageList(ChatRoom.this, adapterArray);
        recyclerView.setAdapter(adapter);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                chatbox.onEditorAction(EditorInfo.IME_ACTION_DONE);
                String chatText = null;

                try {
                    chatText = chatbox.getText().toString();
                } catch (Exception e) {
                    Toast.makeText(ChatRoom.this, "字串解析不正確", Toast.LENGTH_LONG).show();
                    return;
                }
                if (chatText.length() > 0) {
                    Map<String, String> map = new HashMap<>();
                    map.put("roomID", String.valueOf(roomID));
                    map.put("userID", String.valueOf(userID));
                    map.put("message", chatbox.getText().toString());
                    //如果是圖片的話就傳送roomID, userID, image
                    String stringData = new JSONObject(map).toString();
                    Calendar a =  Calendar.getInstance();
                    Map<String, String> adapterMap = new HashMap<>();
                    String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                            a.getTime()
                    );


                    adapterMap.put("userName", userInfo.get(0).get("UserName"));
                    adapterMap.put("message", chatText);
                    adapterMap.put("dateTime", timeStamp);
                    adapterMap.put("viewType", "0");
                    adapterArray.add(adapterMap);
                    //adapter = new MessageList(ChatRoom.this, adapterArray);
                    adapter.notifyItemInserted(adapterArray.size() - 1);
                    adapter.notifyDataSetChanged();

                    setRecyclerViewToBottom();
                    mSocket.emit("socketData", stringData);

                    chatbox.setText("");
                } else {
                    Toast.makeText(ChatRoom.this, "輸入欄位不得為空", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }


    public void __getBothUserData(int roomid){
        OkHttpClient client = new OkHttpClient();
        final Request request =  new Request.Builder().url("http://172.105.226.60:3060/getbothdata?roomid="+ roomid).build();
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
                            readHistory();
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

    public void sendImage(View view){
        startActivityForResult(
                new Intent
                        (Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                GET_FROM_GALLERY);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                Map<String, String> map = new HashMap<>();
                map.put("roomID", String.valueOf(roomID));
                map.put("userID", userID);
                map.put("encodedImage", encodedImage);

                String stringData = new JSONObject(map).toString();

                mSocket.emit("socketImage", stringData);

                Map<String, String> adapterMap = new HashMap<>();
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

                adapterMap.put("userName", userInfo.get(0).get("UserName"));
                adapterMap.put("encodedImage", String.valueOf(encodedImage));
                adapterMap.put("dateTime", timeStamp);
                adapterMap.put("viewType", "3");
                adapterMap.put("viewType2", "0");

                adapterArray.add(adapterMap);
                //adapter = new MessageList(ChatRoom.this, adapterArray);
                adapter.notifyItemInserted(adapterArray.size() - 1);
                adapter.notifyDataSetChanged();
                setRecyclerViewToBottom();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}