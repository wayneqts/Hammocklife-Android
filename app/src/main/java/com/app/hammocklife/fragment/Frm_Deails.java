package com.app.hammocklife.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hammocklife.InforUser;
import com.app.hammocklife.Main;
import com.app.hammocklife.R;
import com.app.hammocklife.adapter.MyRecyclerViewAdapter;
import com.app.hammocklife.model.ObjectHammocks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Frm_Deails extends BaseFragment implements View.OnClickListener {
    AppCompatTextView tv_name, tv_location, tv_description, tv_created_name, tv_created;
    ObjectHammocks objectHammocks;
    RecyclerView rcv_photo;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    RelativeLayout rl_directions;
    AppCompatButton bt_directions;
    private DatabaseReference mDataAllUser = FirebaseDatabase.getInstance().getReference("my_hammock");
    private DatabaseReference mDataAllHammock = FirebaseDatabase.getInstance().getReference("all_hammocks");

    public Frm_Deails(ObjectHammocks objectHammocks) {
        this.objectHammocks = objectHammocks;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.frm_details, container, false);
        initUI(view);
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initUI(View view) {
        AppCompatImageButton img_back = view.findViewById(R.id.img_back);
        AppCompatImageButton img_delete = view.findViewById(R.id.img_delete);
        tv_name = view.findViewById(R.id.tv_name);
        tv_location = view.findViewById(R.id.tv_location);
        tv_description = view.findViewById(R.id.tv_description);
        rcv_photo = view.findViewById(R.id.rcv_photo);
        tv_created_name = view.findViewById(R.id.tv_created_name);
        tv_created = view.findViewById(R.id.tv_created);
        rl_directions = view.findViewById(R.id.rl_directions);
        bt_directions = view.findViewById(R.id.bt_directions);

        img_back.setOnClickListener(this);
        img_delete.setOnClickListener(this);
        tv_created_name.setOnClickListener(this);
        rl_directions.setOnClickListener(this);
        bt_directions.setOnClickListener(this);
        try{
            tv_name.setText(objectHammocks.getName());
            tv_location.setText(objectHammocks.getAddress());
            tv_location.setText(objectHammocks.getAddress());
            tv_description.setText(objectHammocks.getDescription());
            rcv_photo.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            Log.e("getPhotoURLs",objectHammocks.getPhotoURLs().size()+"--");
            myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(), objectHammocks.getPhotoURLs(), wwidth, height);
            rcv_photo.setAdapter(myRecyclerViewAdapter);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String dateString = formatter.format(new Date(objectHammocks.getCreatedAt() * 1000L));
            tv_created.setText(dateString+" by ");
            tv_created_name.setText(objectHammocks.getCreatedBy());
            if (objectHammocks.getUserUDID().equals(mAuth.getUid())){
                img_delete.setVisibility(View.VISIBLE);
            }else {
                img_delete.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
            img_delete.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                try {
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack();
                    try {
                        ((Main)getActivity()).tagDetail = "";
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_created_name:
                try {
                    if (objectHammocks.getUserUDID().equals(mAuth.getUid())){
                        startActivityForResult(new Intent(getActivity(), InforUser.class).putExtra("dataUser",((Main)getActivity()).dataUser),121);
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else {
                        Frm_InfoUserOther frm_deails = new Frm_InfoUserOther(objectHammocks.getUserUDID());
                        ((Main) getActivity()).addFragment(frm_deails);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.img_delete:
                try {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Do you want to Delete?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mDataAllUser.child(mAuth.getUid()).child(objectHammocks.getKey()).removeValue();
                                    assert getFragmentManager() != null;
                                    getFragmentManager().popBackStack();
                                    mDataAllHammock.child(objectHammocks.getKey()).removeValue();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.rl_directions:
                try {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="+objectHammocks.getObjectLocation().get_lng()+"&daddr="+objectHammocks.getObjectLocation().get_lat()));
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.bt_directions:
                try {
                    Log.e("error location:", location_current.getLongitude()+" -- "+location_current.getLatitude()+" -- ");
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.co.in/maps/dir//"+objectHammocks.getObjectLocation().get_lat()+","+objectHammocks.getObjectLocation().get_lng()+"/@"+location_current.getLongitude()+","+location_current.getLatitude()+",12z"));
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
}
