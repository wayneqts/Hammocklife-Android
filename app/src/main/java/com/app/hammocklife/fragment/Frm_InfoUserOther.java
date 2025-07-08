package com.app.hammocklife.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hammocklife.R;
import com.app.hammocklife.adapter.MyRecyclerViewAdapterAdmin;
import com.app.hammocklife.adapter.MyRecyclerViewAdapterUserOther;
import com.app.hammocklife.custom.BSImagePicker;
import com.app.hammocklife.model.ObjectHammocks;
import com.app.hammocklife.model.ObjectLocation;
import com.app.hammocklife.model.ObjectUser;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Frm_InfoUserOther extends BaseFragment implements View.OnClickListener {
    private AppCompatTextView tv_email, tv_date_join, tv_employment, tv_hobbies, tv_save, edt_name, edt_location;
    private ObjectUser dataUser;
    private AppCompatImageButton img_back;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mDataAllUser = FirebaseDatabase.getInstance().getReference("users");
    private CircleImageView img_avatar;
    private String ureUID = "";
    private RecyclerView rcv_hammock;
    MyRecyclerViewAdapterUserOther adapterUserOther;
    private ArrayList<ObjectHammocks> arrData = new ArrayList<>();
    public Frm_InfoUserOther(String ureUID) {
        this.ureUID = ureUID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.frm_infouser_other, container, false);
        initUI(view);
        getDataUser();
        getAllHammocks();
        return view;
    }

    private void getDataUser(){
        try {
            mDataAllUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataUser = null;
                    for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                        try {
                            if (ds != null && ds.getKey().equals(ureUID)) {
                                long createAt = ds.child("createdAt").getValue(Long.class);
                                String email = ds.child("email").getValue(String.class);
                                String employment = ds.child("employment").getValue(String.class);
                                List<String> hobbies = new ArrayList<>();
                                try {
                                    for (int q = 0; q <= ds.child("hobbies").getChildrenCount(); q++) {
                                        hobbies.add(ds.child("hobbies").child(q + "").getValue(String.class));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String livingLocation = ds.child("livingLocation").getValue(String.class);
                                String name = ds.child("name").getValue(String.class);
                                String profileUrl = ds.child("profileUrl").getValue(String.class);
                                String role = ds.child("role").getValue(String.class);
                                long serverTime = ds.child("serverTime").getValue(Long.class);
                                dataUser = new ObjectUser(createAt, serverTime, email, employment, livingLocation, name, profileUrl, role, hobbies);

                                try {
                                    tv_email.setText(dataUser.getEmail());
                                    edt_name.setText(dataUser.getName());
                                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                                    String dateString = formatter.format(new Date(dataUser.getCreateAt() * 1000L));
                                    tv_date_join.setText(dateString);
                                    if (dataUser.getProfileUrl() != null) {
                                        Glide.with(getActivity())
                                                .load(dataUser.getProfileUrl())
                                                .into(img_avatar);
                                    }
                                    if (dataUser.getLivingLocation() != null) {
                                        edt_location.setText(dataUser.getLivingLocation());
                                    }
                                    if (dataUser.getEmployment() != null) {
                                        tv_employment.setText(dataUser.getEmployment());
                                    }
                                    if (dataUser.getHobbies() != null && dataUser.getHobbies().size() > 0) {
                                        for (int q = 0; q <= dataUser.getHobbies().size() - 1; q++) {
                                            if (dataUser.getHobbies().size() == 1) {
                                                tv_hobbies.setText(dataUser.getHobbies().get(q));
                                            } else {
                                                if (q == 0) {
                                                    tv_hobbies.setText(dataUser.getHobbies().get(q));
                                                } else {
                                                    tv_hobbies.setText(tv_hobbies.getText().toString() + ", " + dataUser.getHobbies().get(q));
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initUI(View view) {
        img_avatar = view.findViewById(R.id.img_avatar);
        img_back = view.findViewById(R.id.img_back);
        tv_email = view.findViewById(R.id.tv_email);
        edt_name = view.findViewById(R.id.edt_name);
        tv_date_join = view.findViewById(R.id.tv_date_join);
        edt_location = view.findViewById(R.id.edt_location);
        tv_employment = view.findViewById(R.id.tv_employment);
        tv_hobbies = view.findViewById(R.id.tv_hobbies);
        tv_save = view.findViewById(R.id.tv_save);
        rcv_hammock = view.findViewById(R.id.rcv_hammock);

        tv_employment.setOnClickListener(this);
        tv_hobbies.setOnClickListener(this);
        img_back.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        img_avatar.setOnClickListener(this);
    }

    private void getAllHammocks(){
        arrData.clear();
        Log.e("ureUID",ureUID);
        for (int q=0; q<=arrHammock.size()-1; q++){
            Log.e("ureUID",arrHammock.get(q).getUserUDID());
            if (arrHammock.get(q).getUserUDID().equals(ureUID)){
                arrData.add(arrHammock.get(q));
            }
        }
        rcv_hammock.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterUserOther = new MyRecyclerViewAdapterUserOther(getActivity(), arrData);
        rcv_hammock.setAdapter(adapterUserOther);
        Log.e("size arr",arrData.size()+"---");
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
        }
    }
}
