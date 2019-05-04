package com.example.activityhomework;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendListAdapter extends  RecyclerView.Adapter<FriendListAdapter.FunctionViewHolder>{
    public static final String TAG = FriendListAdapter.class.getSimpleName();
    Context context;
    FragmentActivity act;
    ArrayList<Map<String, String>> arrayList;

    public FriendListAdapter(Context context, FragmentActivity act, ArrayList<Map<String, String>> arrayList){
        this.context = context;
        this.act = act;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public FunctionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.friend_list_template, viewGroup, false
        );
        return new FunctionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FunctionViewHolder functionViewHolder, int i) {
        //functionViewHolder.nameText.setText(functions[i]);
        functionViewHolder.imageView.setImageResource(R.drawable.avatar1);
        functionViewHolder.nameText.setText(arrayList.get(i).get("userName"));
        final String currentFriendID = arrayList.get(i).get("FriendID");
        functionViewHolder.btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            /*
            進入聊天室邏輯：
            1. 使用FriendID向後台取得真正的RoomID(找出另一組，使用 FriendID 排序)。
            2. 回傳RoomID後儲存於SharedPreferences內，進入房間後再使用RoomID WebSocket進行通訊。
            */
                Map<String, String> map = new HashMap<>();
                map.put("friendid", currentFriendID);
                new getRoomIDAsyncGet(context, act, map);

                ////以下可以等getRoomIDAsync做完在跳頁
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }



    public class FunctionViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView nameText;
        Button btn;

        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.list_avator);
            nameText = itemView.findViewById(R.id.list_name);
            btn = itemView.findViewById(R.id.list_btn);
        }
    }

}

