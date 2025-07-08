package com.app.hammocklife.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.hammocklife.ForgotPassword;
import com.app.hammocklife.Main;
import com.app.hammocklife.R;
import com.app.hammocklife.Splash;
import com.app.hammocklife.custom.AppPreferences;
import com.app.hammocklife.custom.AuthenticationDialog;
import com.app.hammocklife.custom.AuthenticationListener;
import com.app.hammocklife.custom.RetrofitClient;
import com.app.hammocklife.custom.ServiceCallbacks;
import com.app.hammocklife.model.APIinstagram;
import com.app.hammocklife.model.APIinstagram2;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.GraphRequest.TAG;

public class Frm_Login extends BaseFragment implements View.OnClickListener, AuthenticationListener {
    private AppCompatEditText edt_email, edt_password;
    private CallbackManager mCallbackManager;
    private AppCompatImageButton img_fb_login, img_instagram;
    private AppPreferences appPreferences = null;
    private AuthenticationDialog authenticationDialog = null;
    private String token = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.frm_login, container, false);
        initUI(view);
        loginFacebook();
        return view;
    }

    private void initUI(View view) {
        try {
            AppCompatImageView img_logo = view.findViewById(R.id.img_logo);
            AppCompatImageButton img_back = view.findViewById(R.id.img_back);
            AppCompatButton bt_login = view.findViewById(R.id.bt_login);
            LinearLayout ln_register = view.findViewById(R.id.ln_register);
            AppCompatTextView tv_forgot_password = view.findViewById(R.id.tv_forgot_password);
            edt_password = view.findViewById(R.id.edt_password);
            edt_email = view.findViewById(R.id.edt_email);
            img_fb_login = view.findViewById(R.id.img_fb_login);
            img_instagram = view.findViewById(R.id.img_instagram);

            img_back.setOnClickListener(this);
            ln_register.setOnClickListener(this);
            bt_login.setOnClickListener(this);
            tv_forgot_password.setOnClickListener(this);
            img_instagram.setOnClickListener(this);
            setLayoutView(img_logo, (wwidth - 100), (wwidth - 100) * 342 / 931);

            edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        if (Objects.requireNonNull(edt_password.getText()).toString().length()>=6) {
                            showLoading();
                            loginClick(Objects.requireNonNull(edt_email.getText()).toString(), edt_password.getText().toString());
                        }
                        try {
                            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(INPUT_METHOD_SERVICE);
                            Objects.requireNonNull(imm).hideSoftInputFromWindow(Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    return false;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loginClick(String email, String password){
        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                try {
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> taska) {
                                                    try {
                                                        if (!taska.isSuccessful()) {
                                                            Log.e("getInstanceId failed", taska.getException().getMessage());
                                                            return;
                                                        }
                                                        // Get new Instance ID token
                                                        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        String date = df.format(Calendar.getInstance().getTime());
                                                        String token = taska.getResult().getToken();
                                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                        mDatabase.child("device_tokens").child(task.getResult().getUser().getUid()).child(token).setValue(date);
                                                        Log.e("date", date);
                                                        Log.e("token", token);
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                    ((Main) getActivity()).frm_home = new Frm_Home();
                                    addFragmentBack(((Main) getActivity()).frm_home);
                                    ((Main) getActivity()).getDataUser();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                hideLoading();
                            } else {
                                Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("error", Objects.requireNonNull(task.getException().getMessage()));
                                hideLoading();
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
            hideLoading();
        }
    }

    private void registerClick(final String email, final String password){
        try{
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                try {
                                    hideLoading();
                                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                    long startTime = System.currentTimeMillis()/1000;
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("createdAt").setValue(startTime);
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("email").setValue(email);
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("name").setValue("");
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("role").setValue("User");
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("serverTime").setValue(startTime);
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> taska) {
                                                    try {
                                                        if (!taska.isSuccessful()) {
                                                            Log.e("getInstanceId failed", taska.getException().getMessage());
                                                            return;
                                                        }
                                                        // Get new Instance ID token
                                                        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        String date = df.format(Calendar.getInstance().getTime());
                                                        String token = taska.getResult().getToken();
                                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                        mDatabase.child("device_tokens").child(task.getResult().getUser().getUid()).child(token).setValue(date);
                                                        Log.e("date", date);
                                                        Log.e("token", token);
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                    ((Main) getActivity()).frm_home = new Frm_Home();
                                    addFragmentBack(((Main) getActivity()).frm_home);
                                    ((Main) getActivity()).getDataUser();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else {
                                loginClick(email, password);
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loginFacebook(){
        try {
            mCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.e("Error", "facebook:onSuccess:" + loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.e("Error", "facebook:onCancel");
                    // ...
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e("Error", "facebook:onError", error);
                    // ...
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        img_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        showLoading();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback(){

                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String email = object.getString("email");
                                        Log.e("email","email -"+email+"---"+user.getEmail()+"---"+user.getPhoneNumber()+"---"+response.getJSONObject().toString()+"---"+object.toString());
                                        addUserData(user, email);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        hideLoading();
                                    }
                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields","id,email,first_name,last_name");
                            request.setParameters(parameters);
                            request.executeAsync();
                            // Sign in success, update UI with the signed-in user's information

                        }else {
                            hideLoading();
                            // If sign in fails, display a message to the user.
                            Log.e("Error", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserData(final FirebaseUser taskResult, final String email){
        try{
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference uidRef = rootRef.child("users").child(taskResult.getUid());
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        Log.e("exists","exists");
                        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        String facebook_ID= Profile.getCurrentProfile().getId();
                        Log.e("FACEBOOK_ID",facebook_ID);
                        long startTime = System.currentTimeMillis()/1000;
                        if (taskResult.getEmail() != null && taskResult.getEmail().length() > 0){
                            mDatabase.child("users").child(taskResult.getUid()).child("email").setValue(taskResult.getEmail());
                        } else {
                            if(email!=null && email.length()>0) {
                                mDatabase.child("users").child(taskResult.getUid()).child("email").setValue(email);
                                taskResult.updateEmail(email);
                            }else {
                                mDatabase.child("users").child(taskResult.getUid()).child("email").setValue(facebook_ID+"@facebook.com");
                                taskResult.updateEmail(facebook_ID+"@facebook.com");
                            }
                        }
                        mDatabase.child("users").child(taskResult.getUid()).child("createdAt").setValue(startTime);
                        mDatabase.child("users").child(taskResult.getUid()).child("name").setValue(taskResult.getDisplayName());
                        mDatabase.child("users").child(taskResult.getUid()).child("role").setValue("User");
                        mDatabase.child("users").child(taskResult.getUid()).child("serverTime").setValue(startTime);
                        mDatabase.child("users").child(taskResult.getUid()).child("profileUrl").setValue("https://graph.facebook.com/"+facebook_ID+"/picture?width=500&height=500");
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
                                                // Get new Instance ID token
                                                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                String date = df.format(Calendar.getInstance().getTime());
                                                String token = task.getResult().getToken();
                                                mDatabase.child("device_tokens").child(taskResult.getUid()).child(token).setValue(date);
                                                Log.e("date", date);
                                                Log.e("token", token);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            hideLoading();
                            ((Main) getActivity()).frm_home = new Frm_Home();
                            addFragmentBack(((Main) getActivity()).frm_home);
                            ((Main) getActivity()).getDataUser();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else {
                        Log.e("exists1","exists1");
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
                                                // Get new Instance ID token
                                                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                String date = df.format(Calendar.getInstance().getTime());
                                                String token = task.getResult().getToken();
                                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                mDatabase.child("device_tokens").child(taskResult.getUid()).child(token).setValue(date);
                                                Log.e("date", date);
                                                Log.e("token", token);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            hideLoading();
                            ((Main) getActivity()).frm_home = new Frm_Home();
                            addFragmentBack(((Main) getActivity()).frm_home);
                            ((Main) getActivity()).getDataUser();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            uidRef.addListenerForSingleValueEvent(eventListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                try {
                    startActivity(new Intent(getActivity(), Splash.class));
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    getActivity().finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.ln_register:
                try {
                    Frm_Register frm_register = new Frm_Register();
                    addFragmentBack(frm_register);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.bt_login:
                try {
                    showLoading();
                    String emailAddress = Objects.requireNonNull(edt_email.getText()).toString().trim();
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                        if (Objects.requireNonNull(edt_password.getText()).toString().length()>=6) {
                            showLoading();
                            loginClick(edt_email.getText().toString(), edt_password.getText().toString());
                        }else {
                            hideLoading();
                            Toast.makeText(getActivity(), "The password too short (should equal or greater than 6)", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
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
            case R.id.tv_forgot_password:
                try {
                    startActivity(new Intent(getActivity(), ForgotPassword.class));
                    Objects.requireNonNull(getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.img_instagram:
                try {
                    authenticationDialog = new AuthenticationDialog(getActivity(), this);
                    authenticationDialog.setCancelable(true);
                    authenticationDialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTokenReceived(String auth_token) {
        if (auth_token == null)
            return;
        appPreferences = new AppPreferences(getActivity());
        appPreferences.putString(AppPreferences.TOKEN, auth_token);
        token = auth_token;
        showLoading();
        getUserInfoByAccessToken(token);
    }

    private void getUserInfoByAccessToken(String token) {
        RetrofitClient.getClient("https://api.instagram.com/oauth/").create(ServiceCallbacks.class).postApi(
                "537367623582326",
                "5e580f87075530ea9da4608d7e103dcd",
                "authorization_code",
                "https://socialsizzle.herokuapp.com/auth/",
                token
        ).enqueue(new Callback<APIinstagram>() {
            @Override
            public void onResponse(Call<APIinstagram> call, Response<APIinstagram> response) {
                if (response.isSuccessful() && response.code()==200) {
                    registerClick(response.body().getUserId()+"@instagram.com", response.body().getUserId()+"@instagram.com");
                }
            }

            @Override
            public void onFailure(Call<APIinstagram> call, Throwable t) {

            }
        });
    }
}
