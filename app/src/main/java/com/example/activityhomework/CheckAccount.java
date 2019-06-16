package com.example.activityhomework;

public class CheckAccount {
    public CheckAccount(){
        //建構函數
    }
    public boolean checkAccPassLength(String str, int min, int max){
        if(str.length() >= min && str.length() <= max){
            return true;
        }
        return false;
    }
}
