package com.example.postie;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.postie.Registration.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}