package com.app.hammocklife.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import com.app.hammocklife.Main;
import com.app.hammocklife.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class Frm_Register extends BaseFragment implements View.OnClickListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private AppCompatEditText edt_confirm_password, edt_password, edt_email, edt_name;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.frm_register, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        AppCompatImageButton img_back = view.findViewById(R.id.img_back);
        AppCompatButton bt_register = view.findViewById(R.id.bt_register);
        edt_name = view.findViewById(R.id.edt_name);
        edt_email = view.findViewById(R.id.edt_email);
        edt_password = view.findViewById(R.id.edt_password);
        edt_confirm_password = view.findViewById(R.id.edt_confirm_password);

        img_back.setOnClickListener(this);
        bt_register.setOnClickListener(this);
    }

    private void registerClick(String email, String password){
        try{
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                addUserData(task);
                            } else {
                                hideLoading();
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (Exception e){
            hideLoading();
            e.printStackTrace();
        }
    }

    private void addUserData(final Task<AuthResult> taskResult){
        try{
            long startTime = System.currentTimeMillis()/1000;
            mDatabase.child("users").child(taskResult.getResult().getUser().getUid()).child("createdAt").setValue(startTime);
            mDatabase.child("users").child(taskResult.getResult().getUser().getUid()).child("email").setValue(taskResult.getResult().getUser().getEmail());
            mDatabase.child("users").child(taskResult.getResult().getUser().getUid()).child("name").setValue(edt_name.getText().toString());
            mDatabase.child("users").child(taskResult.getResult().getUser().getUid()).child("role").setValue("User");
            mDatabase.child("users").child(taskResult.getResult().getUser().getUid()).child("serverTime").setValue(startTime);
            try{
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                Log.w("TAG", "Fetching FCM registration token failed", task1.getException());
                                return;
                            }

                            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String date = df.format(Calendar.getInstance().getTime());
                            String token = task1.getResult();
                            DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
                            mDb.child("device_tokens").child(taskResult.getResult().getUser().getUid()).child(token).setValue(date);
                        });
            }catch (Exception e){
                e.printStackTrace();
            }

            hideLoading();
            ((Main) getActivity()).frm_home = new Frm_Home();
            addFragmentBack(((Main) getActivity()).frm_home);
            ((Main) getActivity()).getDataUser();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                try {
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.bt_register:
                try {
                    showLoading();
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(edt_email.getText().toString().trim()).matches()) {
                        if (edt_password.getText().toString().length()>=6 && edt_confirm_password.getText().toString().length()>=6){
                            if (edt_password.getText().toString().equals(edt_confirm_password.getText().toString())){
                                registerClick(edt_email.getText().toString().trim(), edt_password.getText().toString());
                            }else {
                                hideLoading();
                                Toast.makeText(getActivity(), "Confirm password does not match", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            hideLoading();
                            Toast.makeText(getActivity(), "The password too short (should equal or greater than 6)", Toast.LENGTH_LONG).show();
                        }
                    } else{
                        hideLoading();
                        if (edt_email.getText().toString().length()>0) {
                            Toast.makeText(getActivity(), "The email is invalid", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getActivity(), "Email is required", Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
}
