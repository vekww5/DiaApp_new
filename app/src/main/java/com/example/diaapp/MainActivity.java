package com.example.diaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.User;
import com.example.diaapp.database.UserDAO;
import com.example.diaapp.user_fragments.BluetoothScanFragment;
import com.example.diaapp.user_fragments.DiaryFragment;
import com.example.diaapp.user_fragments.ExportFragment;
import com.example.diaapp.user_fragments.HistoryFragment;
import com.example.diaapp.user_fragments.LoginFragment;
import com.example.diaapp.user_fragments.ProfileFragment;
import com.example.diaapp.user_fragments.StatisticsFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    public static SharedPreferences sharedPreferencesEmail;
    public static SharedPreferences sharedPreferencesPassword;
    public static final String PREFERENCES_EMAIL = "email";

    public static final String PREFERENCES_PASSWORD = "password";


    public static User user; // Глобальная User( для аккаунта)
;

    DiaDataBase db;
    UserDAO data;
 // TODO Запись в постоянную память
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        // навигация
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        sharedPreferencesEmail = getSharedPreferences(PREFERENCES_EMAIL, MODE_PRIVATE);
        sharedPreferencesPassword = getSharedPreferences(PREFERENCES_PASSWORD, MODE_PRIVATE);

        String email = sharedPreferencesEmail.getString(PREFERENCES_EMAIL, "none").trim();
        String password = sharedPreferencesPassword.getString(PREFERENCES_PASSWORD, "none").trim();


        if(email.equals("none") || password.equals("none")) {
            getSupportFragmentManager().beginTransaction().addToBackStack("b").replace(R.id.fragment_container,
                    new LoginFragment()).commit();
        }
       else {
            db = DiaDataBase.getDatabase(this);
            data = db.dataDao();
            user = data.verify(email, password);
            List<User> sl = data.getAll();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DiaryFragment()).commit();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.exit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_log_out:
               SharedPreferences.Editor editor1 = getSharedPreferences(PREFERENCES_EMAIL, MODE_PRIVATE).edit();
                editor1.clear();
                editor1.apply();

                SharedPreferences.Editor editor2 = getSharedPreferences(PREFERENCES_PASSWORD, MODE_PRIVATE).edit();
                editor2.clear();
                editor2.apply();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LoginFragment()).commit();

                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_diary:
                getSupportFragmentManager().beginTransaction().addToBackStack("a").replace(R.id.fragment_container, new DiaryFragment()).commit();
                break;
            case R.id.nav_statistics:
                getSupportFragmentManager().beginTransaction().addToBackStack("b").replace(R.id.fragment_container, new StatisticsFragment()).commit();
                break;
            case R.id.nav_export:
                getSupportFragmentManager().beginTransaction().addToBackStack("b").replace(R.id.fragment_container, new ExportFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().addToBackStack("b").replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_scan:
                getSupportFragmentManager().beginTransaction().addToBackStack("b").replace(R.id.fragment_container, new BluetoothScanFragment()).commit();
                break;
            case R.id.nav_history:
                getSupportFragmentManager().beginTransaction().addToBackStack("b").replace(R.id.fragment_container, new HistoryFragment()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}