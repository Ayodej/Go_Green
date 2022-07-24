package com.app.gogreen.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.app.gogreen.R;

public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Thread obj = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME_OUT);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        Intent intent = new Intent(getApplicationContext(), SelectionActivity.class);
                        startActivity(intent);
                        finish();

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        };obj.start();
    }
}