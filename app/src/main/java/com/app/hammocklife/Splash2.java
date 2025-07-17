package com.app.hammocklife;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash2 extends BaseActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        AppCompatImageView img_logo = findViewById(R.id.img_logo);
        setLayoutView(img_logo, (wwidth - 100), (wwidth- 100)*342/931);
        img_logo.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null) {
                    startActivity(new Intent(Splash2.this, Main.class).putExtra("Login", "Skip"));
                }else {
                    startActivity(new Intent(Splash2.this, Splash.class));
                }
                finish();
            } catch (Exception e){
                e.printStackTrace();
                startActivity(new Intent(Splash2.this, Splash.class));
                finish();
            }
        }, 1000);
    }
}
