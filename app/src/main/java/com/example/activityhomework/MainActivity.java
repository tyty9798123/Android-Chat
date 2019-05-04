package com.example.activityhomework;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    public static NavigationView navigationView;
    private Toolbar toolbar;
    public static FragmentManager manager;
    public static FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        toolBartoogle();
        setListeners();

        fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment, HomeFragement.getInstance());
        fragmentTransaction.commit();

        itemChecked();
    }
    public void findViews(){
        manager = getSupportFragmentManager();
        fragmentTransaction = manager.beginTransaction();
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
    }

    public void setListeners(){
        navigationView.setNavigationItemSelectedListener(navOnCheck);
    }

    public void toolBartoogle(){
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void itemChecked(){
        Menu nav_Menu = navigationView.getMenu();
        if (getBaseContext().getSharedPreferences("auth", MODE_PRIVATE).getString("userID",null) == null){
            nav_Menu.findItem(R.id.action_login).setVisible(true);
            nav_Menu.findItem(R.id.action_signup).setVisible(true);
            nav_Menu.findItem(R.id.action_logout).setVisible(false);
        }else{
            nav_Menu.findItem(R.id.action_logout).setVisible(true);
            nav_Menu.findItem(R.id.action_login).setVisible(false);
            nav_Menu.findItem(R.id.action_signup).setVisible(false);
        }
    }

    NavigationView.OnNavigationItemSelectedListener navOnCheck = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            drawerLayout.closeDrawer(GravityCompat.START);
            int id = item.getItemId();
            switch (id){
                case R.id.action_home:
                    try {
                        fragmentTransaction = manager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment, HomeFragement.getInstance());
                        fragmentTransaction.commit();
                    }
                    catch (Exception e){
                        return false;
                    }
                    return true;
                case R.id.action_signup:

                    try {
                        fragmentTransaction = manager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment, SignUpFragment.getInstance());
                        fragmentTransaction.commit();
                    }
                    catch (Exception e){
                        return false;
                    }
                    return true;
                case R.id.action_login:
                    try {
                        fragmentTransaction = manager.beginTransaction();
                        fragmentTransaction.remove(HomeFragement.getInstance());
                        fragmentTransaction.replace(R.id.container_fragment, LogInFragment.getInstance());
                        fragmentTransaction.commit();
                    }
                    catch (Exception e){
                        return false;
                    }
                    return true;
                case R.id.action_logout:
                    getBaseContext().getSharedPreferences("auth", Context.MODE_PRIVATE).edit().putString("userName", null).commit();
                    getBaseContext().getSharedPreferences("auth", Context.MODE_PRIVATE).edit().putString("userID", null).commit();
                    fragmentTransaction = manager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_fragment, new LogoutFragment());
                    fragmentTransaction.commit();
                    navigationView.getMenu().getItem(0).setChecked(true);
                    return false;
            }
            return false;
        }
    };

}
