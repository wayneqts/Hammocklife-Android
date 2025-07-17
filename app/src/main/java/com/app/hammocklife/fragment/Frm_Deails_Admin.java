package com.app.hammocklife.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hammocklife.AddNew;
import com.app.hammocklife.InforUser;
import com.app.hammocklife.Main;
import com.app.hammocklife.R;
import com.app.hammocklife.adapter.MyRecyclerViewAdapter;
import com.app.hammocklife.api.APIService;
import com.app.hammocklife.api.APIUtils;
import com.app.hammocklife.api.RetrofitClient;
import com.app.hammocklife.custom.ServiceCallbacks;
import com.app.hammocklife.custom.Utils;
import com.app.hammocklife.model.ObjectHammocks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Frm_Deails_Admin extends BaseFragment implements View.OnClickListener {
    AppCompatTextView tv_name, tv_location, tv_description, tv_created_name, tv_created;
    ObjectHammocks objectHammocks;
    RecyclerView rcv_photo;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    AppCompatButton bt_reject, bt_approve;
    Main activity;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public Frm_Deails_Admin(ObjectHammocks objectHammocks) {
        this.objectHammocks = objectHammocks;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (Main) getActivity();
        View view = inflater.inflate(R.layout.frm_details_admin, container, false);
        initUI(view);
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initUI(View view) {
        AppCompatImageButton img_back = view.findViewById(R.id.img_back);
        tv_name = view.findViewById(R.id.tv_name);
        tv_location = view.findViewById(R.id.tv_location);
        tv_description = view.findViewById(R.id.tv_description);
        rcv_photo = view.findViewById(R.id.rcv_photo);
        tv_created_name = view.findViewById(R.id.tv_created_name);
        tv_created = view.findViewById(R.id.tv_created);
        bt_approve = view.findViewById(R.id.bt_approve);
        bt_reject = view.findViewById(R.id.bt_reject);

        img_back.setOnClickListener(this);
        bt_reject.setOnClickListener(this);
        bt_approve.setOnClickListener(this);
        tv_created_name.setOnClickListener(this);
        try {
            tv_name.setText(objectHammocks.getName());
            tv_location.setText(objectHammocks.getAddress());
            tv_location.setText(objectHammocks.getAddress());
            tv_description.setText(objectHammocks.getDescription());
            rcv_photo.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            Log.e("getPhotoURLs", objectHammocks.getPhotoURLs().size() + "--");
            myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(), objectHammocks.getPhotoURLs(), wwidth, height);
            rcv_photo.setAdapter(myRecyclerViewAdapter);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String dateString = formatter.format(new Date(objectHammocks.getCreatedAt() * 1000L));
            tv_created.setText(dateString + " by ");
            tv_created_name.setText(objectHammocks.getCreatedBy());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void rejectHammock() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Note for Creater");

            final EditText input = new EditText(getActivity());
            input.setHint("Notes (optional)");
            input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input.setSingleLine(false);
            input.setLines(5);
            input.setMaxLines(5);
            input.setGravity(Gravity.LEFT | Gravity.TOP);
            builder.setView(input);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    mDatabase.child("my_hammock").child(objectHammocks.getUserUDID()).child(objectHammocks.getKey()).child("noteFromAdmin").setValue(input.getText().toString() + "");
                    mDatabase.child("my_hammock").child(objectHammocks.getUserUDID()).child(objectHammocks.getKey()).child("status").setValue("reject");
                    mDatabase.child("pending_approval").child(objectHammocks.getKey()).removeValue();
                    getTokenUser("Hammocks Reject");
                    hideLoading();
                    try {
                        assert getFragmentManager() != null;
                        getFragmentManager().popBackStack();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void approveHammocks() {
        try {
            mDatabase.child("my_hammock").child(objectHammocks.getUserUDID()).child(objectHammocks.getKey()).child("status").setValue("approval");
            Map<String, Object> updates = new HashMap<String, Object>();
            long startTime = System.currentTimeMillis() / 1000;
            updates.put("address", objectHammocks.getAddress());
            updates.put("createdAt", startTime);
            updates.put("createdBy", objectHammocks.getCreatedBy());
            updates.put("description", objectHammocks.getDescription());
            updates.put("name", objectHammocks.getName());
            updates.put("serverTime", startTime);
            updates.put("status", "approval");
            updates.put("userUDID", objectHammocks.getUserUDID());
            mDatabase.child("all_hammocks").child(objectHammocks.getKey()).setValue(updates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    mDatabase.child("all_hammocks").child(databaseReference.getKey()).child("location").child("_lat").setValue(objectHammocks.getObjectLocation().get_lat());
                    mDatabase.child("all_hammocks").child(databaseReference.getKey()).child("location").child("_lng").setValue(objectHammocks.getObjectLocation().get_lng());
                    for (int q = 0; q <= objectHammocks.getPhotoURLs().size() - 1; q++) {
                        mDatabase.child("all_hammocks").child(databaseReference.getKey()).child("photoURLs").child(q + "").setValue(objectHammocks.getPhotoURLs().get(q));
                    }
                    getTokenUser("Hammocks Approve");
                }
            });
            mDatabase.child("pending_approval").child(objectHammocks.getKey()).removeValue();
            hideLoading();
            try {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                try {
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_reject:
                try {
                    showLoading();
                    rejectHammock();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_approve:
                try {
                    showLoading();
                    approveHammocks();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_created_name:
                try {
                    try {
                        if (objectHammocks.getUserUDID().equals(mAuth.getUid())) {
                            startActivityForResult(new Intent(getActivity(), InforUser.class).putExtra("dataUser", ((Main) getActivity()).dataUser), 121);
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Frm_InfoUserOther frm_deails = new Frm_InfoUserOther(objectHammocks.getUserUDID());
                            ((Main) getActivity()).addFragment(frm_deails);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    private void getTokenUser(String name) {
        mDatabase.child("device_tokens").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    if (Objects.equals(ds.getKey(), objectHammocks.getUserUDID())){
                        for (DataSnapshot dsCh : ds.getChildren()){
                            getAccessToken(name, dsCh.getKey());
                        }
                    }
                }
            }
        });
    }
    // get access token
    private void getAccessToken(String name, String userTk){
        APIService api = APIUtils.getOauthService();
        api.getAccessToken("urn:ietf:params:oauth:grant-type:jwt-bearer", Utils.createJwt(activity)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String token = jsonObject.optString("token_type")+" "+jsonObject.optString("access_token");
                        sendPush(name, userTk, token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("ACCESS_TOKEN", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // send push
    private void sendPush(String name, String userTk, String accTk){
        APIService api = APIUtils.getMessageService();
        DataNoti dataNoti = new DataNoti("HammockLife", name);
        Map<String, Object> messData = new HashMap<>();
        messData.put("token", userTk);
        messData.put("notification", dataNoti);
        Map<String, Object> data = new HashMap<>();
        data.put("message", messData);
        api.sendPush("v1/projects/hammocklife-a7eff/messages:send", accTk, data).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("TAG", "Success");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    class DataNoti {
        String title;
        String body;

        public DataNoti(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }
}
