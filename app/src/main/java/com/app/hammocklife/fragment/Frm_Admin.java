package com.app.hammocklife.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hammocklife.R;
import com.app.hammocklife.adapter.MyRecyclerViewAdapterAdmin;
import com.app.hammocklife.adapter.MyRecyclerViewAdapterMyHammock;
import com.app.hammocklife.model.ObjectHammocks;
import com.app.hammocklife.model.ObjectLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Frm_Admin extends BaseFragment implements View.OnClickListener {
    RecyclerView rcv_myhammock;
    MyRecyclerViewAdapterAdmin adapterMyHammock;
    private DatabaseReference mDataAllUser = FirebaseDatabase.getInstance().getReference("pending_approval");
    private ArrayList<ObjectHammocks> arrData = new ArrayList<>();
    private AppCompatTextView tv_nodata;

    public Frm_Admin() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.frm_admin, container, false);
        initUI(view);
        showLoading();
        getData();
        return view;
    }

    private void initUI(View view) {
        AppCompatImageButton img_back = view.findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        rcv_myhammock = view.findViewById(R.id.rcv_myhammock);
        tv_nodata = view.findViewById(R.id.tv_nodata);
    }

    private void getData(){
        if (getActivity()!=null) {
            mDataAllUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    arrData.clear();
                    try {
                        for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (true) {
                                ObjectLocation objectLocation = null;
                                try {
                                    objectLocation = new ObjectLocation(ds.child("location").child("_lat").getValue(Double.class), ds.child("location").child("_lng").getValue(Double.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ArrayList<String> arrUrlLink = new ArrayList<>();
                                try {
                                    for (int q = 0; q <= ds.child("photoURLs").getChildrenCount(); q++) {
                                        arrUrlLink.add(ds.child("photoURLs").child(q + "").getValue(String.class));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ObjectHammocks objectHammocks = new ObjectHammocks(
                                        ds.getKey(),
                                        ds.child("address").getValue(String.class),
                                        ds.child("createdBy").getValue(String.class),
                                        ds.child("description").getValue(String.class),
                                        ds.child("name").getValue(String.class),
                                        ds.child("status").getValue(String.class),
                                        ds.child("userUDID").getValue(String.class),
                                        0,
                                        0,
                                        objectLocation,
                                        arrUrlLink,
                                        ""
                                );
                                arrData.add(objectHammocks);
                            }
                        }
                        if (arrData!=null && arrData.size()>0){
                            tv_nodata.setVisibility(View.GONE);
                        }else {
                            tv_nodata.setVisibility(View.VISIBLE);
                        }
                        rcv_myhammock.setLayoutManager(new LinearLayoutManager(getActivity()));
                        adapterMyHammock = new MyRecyclerViewAdapterAdmin(getActivity(), arrData);
                        rcv_myhammock.setAdapter(adapterMyHammock);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    hideLoading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
        }
    }
}
