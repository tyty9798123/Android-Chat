package com.example.activityhomework;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class FriendListAdapter extends  RecyclerView.Adapter<FriendListAdapter.FunctionViewHolder>{
    Context context;
    ArrayList<Map<String, String>> arrayList;
    public FriendListAdapter(Context context, ArrayList<Map<String, String>> arrayList){
        this.context = context;
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

