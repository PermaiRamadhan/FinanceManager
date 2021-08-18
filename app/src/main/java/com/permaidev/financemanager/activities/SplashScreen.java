package com.permaidev.financemanager.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
/*

       30/07/2021
        splash screen

        nim   : 10118060
        nama  : Permai Ramadhan
        kelas : if-2

        */

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                // close splash activity
                finish();

            }
        },SPLASH_TIME_OUT);
    }
}