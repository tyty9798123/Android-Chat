package com.example.activityhomework;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragement extends Fragment {

    private static HomeFragement instance;
    private TextView showUserName;
    private Button addFriendsBtn;

    public Context getCurrentContext() {
        Context x = getContext();
        return x;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_page, container, false);
    }
    public static HomeFragement getInstance(){
        if (instance == null){
            instance = new HomeFragement();
        }
        return instance;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        getActivity().setTitle("首頁");

        String userName = getContext().getSharedPreferences("auth", Context.MODE_PRIVATE).getString("userName", null);
        String userID =  getContext().getSharedPreferences("auth", Context.MODE_PRIVATE).getString("userID", null);
        if (userName != null) {
            showUserName.setTextColor(Color.BLUE);
            showUserName.setTextSize(20);
            showUserName.setText("您好，" + userName + "！");
        }else{
            showUserName.setTextColor(Color.RED);
            showUserName.setTextSize(20);
            showUserName.setText("您尚未登入");
        }
        Menu nav_Menu = MainActivity.navigationView.getMenu();
        if (userID == null){
            nav_Menu.findItem(R.id.action_login).setVisible(true);
            nav_Menu.findItem(R.id.action_signup).setVisible(true);
            nav_Menu.findItem(R.id.action_logout).setVisible(false);
        }else{
            nav_Menu.findItem(R.id.action_logout).setVisible(true);
            nav_Menu.findItem(R.id.action_login).setVisible(false);
            nav_Menu.findItem(R.id.action_signup).setVisible(false);
        }
        // 如果Login了，顯示 新增好友 畫面。
        if (userID == null){
            addFriendsBtn.setVisibility(View.GONE);
        }
    }
    public void findViews(){
        showUserName = getActivity().findViewById(R.id.showUserName);
        addFriendsBtn = getView().findViewById(R.id.addFriends);

        addFriendsBtn.setOnClickListener(addFriendsBtnClicked);
    }

    private Button.OnClickListener addFriendsBtnClicked = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("請輸入好友的帳號");

            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("加入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_text;
                    m_text = input.getText().toString();
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    };
}
