package com.app.hammocklife;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.hammocklife.fragment.Frm_Home;
import com.app.hammocklife.fragment.Frm_Login;
import com.app.hammocklife.model.ObjectUser;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Main extends BaseActivity {
    RelativeLayout rl_loading;
    boolean doubleBackToExitPressedOnce = false;
    private DatabaseReference mDataAllUser = FirebaseDatabase.getInstance().getReference("users");
    public ObjectUser dataUser = null;
    public Frm_Home frm_home;
    public Frm_Login frm_login;
    public String tagDetail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);
        if (getIntent()!=null && getIntent().getStringExtra("Login")!=null && Objects.equals(getIntent().getStringExtra("Login"), "Skip")){
            frm_home = new Frm_Home();
            addFragment(frm_home);
        }else {
            frm_login = new Frm_Login();
            addFragment(frm_login);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        }
        rl_loading = findViewById(R.id.rl_loading);
        getDataUser();
    }

    public void showLoading(){
        try {
            rl_loading.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void hideLoading(){
        try {
            rl_loading.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getDataUser(){
        mDataAllUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataUser = null;
                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        if (ds!=null && ds.getKey().equals(mAuth.getUid()) && ds.child("serverTime").getValue(Long.class)!=null && ds.child("role").getValue(String.class)!=null && ds.child("createdAt").getValue(Long.class)!=null) {
                            long createAt = ds.child("createdAt").getValue(Long.class);
                            String email = ds.child("email").getValue(String.class);
                            String employment = ds.child("employment").getValue(String.class);
                            List<String> hobbies = new ArrayList<>();
                            try {
                                for (int q = 0; q <= ds.child("hobbies").getChildrenCount()-1; q++){
                                    hobbies.add(ds.child("hobbies").child(q+"").getValue(String.class));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            String livingLocation = ds.child("livingLocation").getValue(String.class);
                            String name = ds.child("name").getValue(String.class);
                            String profileUrl = ds.child("profileUrl").getValue(String.class);
                            String role = ds.child("role").getValue(String.class);
                            if (role.equals("User")){
                                checkUser = false;
                            } else if (role.equals("Admin")){
                                checkUser = true;
                                try{
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    try {
                                                        if (!task.isSuccessful()) {
                                                            Log.e("getInstanceId failed", task.getException().getMessage());
                                                            return;
                                                        }
                                                        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        String date = df.format(Calendar.getInstance().getTime());
                                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                        String token = task.getResult().getToken();
                                                        mDatabase.child("admin_device_tokens").child(token).setValue(date+" - "+ds.getKey());
                                                        Log.e("task push", token);
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            long serverTime = ds.child("serverTime").getValue(Long.class);
                            dataUser = new ObjectUser(createAt, serverTime, email, employment, livingLocation, name, profileUrl, role, hobbies);
                            frm_home.setTextUser(dataUser);
                            Log.e("getUser","getUser");
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (tagDetail!=null && tagDetail.length()>0){
            try {
                Log.e("tagDetail", tagDetail+" - - ");
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
                tagDetail = "";
                super.onBackPressed();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            }
            if (!doubleBackToExitPressedOnce) {
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                this.doubleBackToExitPressedOnce = true;
            }
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (frm_login != null) {
            frm_login.onActivityResult(requestCode, resultCode, data);
        }
    }
}
