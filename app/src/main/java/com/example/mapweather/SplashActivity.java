package com.example.mapweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView =  findViewById(R.id.image_view);
        Glide.with(this).load(R.drawable.welcome).centerCrop().into(imageView);

        Thread thread= new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 3 seconds
                    sleep(1*1000);

                    // After 3 seconds redirect to MapsActivity
                    Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
                    startActivity(intent);

                    //Remove current activity
                    finish();
                } catch (Exception e) {
                }
            }
        };
        thread.start();
    }
}
