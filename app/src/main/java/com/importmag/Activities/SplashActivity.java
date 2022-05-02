package com.importmag.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;


import com.importmag.MainActivity;
import com.importmag.R;

public class SplashActivity extends AppCompatActivity {
    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String email = getFromSharedPreferences(SplashActivity.this, "email");
        String passwd = getFromSharedPreferences(SplashActivity.this, "psswd");

        // HANDLER QUE PERMITE UN TIEMPO DE EJECUCION EN DETERMINADA ACTIVIDAD
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!email.isEmpty() && !passwd.isEmpty()) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        }, 2500);
    }

    public static String getFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences("logvalidate", Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

}


