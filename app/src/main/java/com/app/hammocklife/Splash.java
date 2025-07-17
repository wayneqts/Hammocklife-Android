package com.app.hammocklife;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.util.Objects;

public class Splash extends BaseActivity {
    LinearLayout ln_animation;
    AppCompatButton bt_login, bt_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUI();
        printHashKey(Splash.this);
        bt_login.setOnClickListener(view -> {
            startActivity(new Intent(Splash.this, Main.class).putExtra("Login", "Login"));
            finish();
        });

        bt_skip.setOnClickListener(view -> loginAnonymously());
    }

    private void initUI() {
        AppCompatImageView img_logo = findViewById(R.id.img_logo);
        ln_animation = findViewById(R.id.ln_animation);
        bt_skip = findViewById(R.id.bt_skip);
        bt_login = findViewById(R.id.bt_login);
        setLayoutView(img_logo, (wwidth - 100), (wwidth- 100)*342/931);

        img_logo.setVisibility(View.VISIBLE);
        img_logo.startAnimation(AnimationUtils.loadAnimation(Splash.this, R.anim.anim_top));
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            ln_animation.setVisibility(View.VISIBLE);
            ln_animation.startAnimation(AnimationUtils.loadAnimation(Splash.this, android.R.anim.fade_in));
        }, 500);

       rqMapPer();
    }

    // request map permission
    private void rqMapPer(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        }
    }

    private void loginAnonymously(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(Splash.this, Main.class).putExtra("Login", "Skip"));
                        finish();
                    } else {
                        Toast.makeText(Splash.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e("aaa", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
            Log.e("aaa", "printHashKey()", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.please_allow_map_access_to_use_the_app), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
