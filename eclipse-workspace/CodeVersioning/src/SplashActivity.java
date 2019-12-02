package uk.ac.mmu.electricchargingproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    //// this is the splash screen activity
    ////  MainActivity started after the Splash screen

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** START - this is the purpose of this Activity */
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        /** END - everything more than this is time consuming */
    }

}
