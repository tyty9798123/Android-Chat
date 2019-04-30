package com.example.activityhomework;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LogoutFragment extends Fragment {
    private FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        // SignUpFragment.getInstance() 必須換成首頁
        fragmentTransaction.replace(R.id.container_fragment, HomeFragement.getInstance());
        fragmentTransaction.commit();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
