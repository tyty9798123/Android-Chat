package com.example.activityhomework;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class LogInFragment extends Fragment {
    private EditText userAccount, userPassword;
    private static LogInFragment instance;
    private Button login_button;
    private FragmentManager manager;

    private String TAG = LogInFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_in, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.navigationView.getMenu().getItem(2).setChecked(true);
        getActivity().setTitle("Log In");
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
        userAccount = getView().findViewById(R.id.login_account);
        userPassword = getView().findViewById(R.id.login_password);
        login_button = getView().findViewById(R.id.login_button);
        login_button.setOnClickListener(log_in_btn_clicked);
    }


    public static LogInFragment getInstance(){
        if (instance == null){
            instance = new LogInFragment();
        }
        return instance;
    }

    public Button.OnClickListener log_in_btn_clicked = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            String usrAccount = userAccount.getText().toString();
            String usrPassword = userPassword.getText().toString();
            if (!(usrAccount.length() > 0 && usrPassword.length() > 0)){
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.alert)
                        .setTitle("警告")
                        .setMessage("輸入欄位不得為空值。")
                        .setPositiveButton("確定", null)
                        .show();
                return;
            }
            Map<String, String> map = new HashMap<>();
            map.put("userAccount", usrAccount);
            map.put("userPassword", usrPassword);
            new LogInAsyncPost(getContext(), map, getActivity());
        }
    };

}
