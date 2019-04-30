package com.example.activityhomework;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;

public class SignUpFragment extends Fragment {
    private static SignUpFragment instance;
    private EditText userName, userAccount, userPassword, confirmPassword;
    private Button signup_btn;
    private String TAG = SignUpFragment.class.getSimpleName();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Sign Up");
        findViews();
        Menu nav_Menu = MainActivity.navigationView.getMenu();
        if (getContext().getSharedPreferences("auth", Context.MODE_PRIVATE).getString("userID",null) == null){
            nav_Menu.findItem(R.id.action_login).setVisible(true);
            nav_Menu.findItem(R.id.action_signup).setVisible(true);
            nav_Menu.findItem(R.id.action_logout).setVisible(false);
        }else{
            nav_Menu.findItem(R.id.action_logout).setVisible(true);
            nav_Menu.findItem(R.id.action_login).setVisible(false);
            nav_Menu.findItem(R.id.action_signup).setVisible(false);
        }
    }

    public void findViews(){
        userName = getView().findViewById(R.id.signup_name);
        userAccount = getView().findViewById(R.id.signup_account);
        userPassword = getView().findViewById(R.id.signup_password);
        confirmPassword = getView().findViewById(R.id.signup_confirm_password);
        signup_btn = getView().findViewById(R.id.signup_btn);
        signup_btn.setOnClickListener(signup_btn_clicked);
    }

    public boolean passwordCheck(){
        if (userPassword.getText().toString().equals(confirmPassword.getText().toString())){
            return true;
        }
        return false;
    }

    public static SignUpFragment getInstance(){
        if (instance == null){
            instance = new SignUpFragment();
        }
        return instance;
    }

    private Button.OnClickListener signup_btn_clicked = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!passwordCheck()){
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.alert)
                        .setTitle("警告")
                        .setMessage("				密碼輸入不一致！")
                        .setPositiveButton("確認", null)
                        .show();
                return;
            }
            CheckAccount ca = new CheckAccount();
            String usrName = userName.getText().toString();
            String usrAccount = userAccount.getText().toString();
            String usrPassword = userPassword.getText().toString();
            boolean ifLengthOK = ca.checkAccPassLength(usrName, 2 , 10) & ca.checkAccPassLength(usrAccount, 6 , 15)
            & ca.checkAccPassLength(usrPassword, 6 , 15);
            if (!ifLengthOK){
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.alert)
                        .setTitle("警告")
                        .setMessage("長度限制！\n姓名長度限制為2~10\n帳號或密碼長度限制為6~15")
                        .setPositiveButton("確定", null)
                        .show();
                return;
            }
            Map<String, String > map = new HashMap<>();
            map.put("userName", usrName);
            map.put("userAccount", usrAccount);
            map.put("userPassword", usrPassword);
            new SignUpAsyncPost(getContext(), map, getActivity());
        }
    };
}
