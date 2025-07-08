package com.app.hammocklife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPassword extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_loading;
    AppCompatEditText edt_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        AppCompatImageButton img_back = findViewById(R.id.img_back);
        AppCompatButton bt_reset = findViewById(R.id.bt_reset);
        img_back.setOnClickListener(this);
        bt_reset.setOnClickListener(this);
        rl_loading = findViewById(R.id.rl_loading);
        edt_email = findViewById(R.id.edt_email);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                try {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.bt_reset:
                try {
                    String emailAddress = Objects.requireNonNull(edt_email.getText()).toString().trim();
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                        rl_loading.setVisibility(View.VISIBLE);
                        FirebaseAuth.getInstance().sendPasswordResetEmail(Objects.requireNonNull(edt_email.getText()).toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ForgotPassword.this, "Email sent", Toast.LENGTH_LONG).show();
                                        }else {
                                            Toast.makeText(ForgotPassword.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                        }
                                        rl_loading.setVisibility(View.GONE);
                                    }
                                });
                    }else {
                        Toast.makeText(ForgotPassword.this, "invalid Email address", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
