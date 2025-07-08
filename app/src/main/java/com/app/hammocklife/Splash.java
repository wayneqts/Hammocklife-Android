package com.app.hammocklife;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.hammocklife.fragment.Frm_Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_animation.setVisibility(View.VISIBLE);
                ln_animation.startAnimation(AnimationUtils.loadAnimation(Splash.this, android.R.anim.fade_in));
            }
        }, 500);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Splash.this, Main.class).putExtra("Login", "Login"));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        bt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginClick("android@gmail.com", "123456");
            }
        });
    }

    private void registerClick(final String email, final String password){
        try{
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Splash.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(Splash.this, Main.class).putExtra("Login", "Skip"));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loginClick(final String email, final String password){
        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Objects.requireNonNull(Splash.this), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(Splash.this, Main.class).putExtra("Login", "Skip"));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            } else {
                                registerClick(email, password);
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
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

}
