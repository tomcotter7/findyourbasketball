package com.thomas.findyourbasketball;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    //This method displays my loading screen for four seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //This intent starts the mainActivity after four seconds of displaying the loading screen.
                //This intent provides the data - the MainActivity class.
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                //This starts the activity from the intent.
                startActivity(mainIntent);
                finish();
            }
        },4000);
    }
}
