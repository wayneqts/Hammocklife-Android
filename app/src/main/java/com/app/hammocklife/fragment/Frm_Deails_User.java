package com.app.hammocklife.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hammocklife.InforUser;
import com.app.hammocklife.Main;
import com.app.hammocklife.R;
import com.app.hammocklife.Splash;
import com.app.hammocklife.adapter.MyRecyclerViewAdapter;
import com.app.hammocklife.model.ObjectHammocks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Frm_Deails_User extends BaseFragment implements View.OnClickListener {
    AppCompatTextView tv_name, tv_location, tv_description, tv_created_name, tv_created, tv_note, tv_name_note;
    ObjectHammocks objectHammocks;
    RecyclerView rcv_photo;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    private DatabaseReference mDataAllUser = FirebaseDatabase.getInstance().getReference("my_hammock");
    private DatabaseReference mDataAllHammock = FirebaseDatabase.getInstance().getReference("all_hammocks");

    public Frm_Deails_User(ObjectHammocks objectHammocks) {
        this.objectHammocks = objectHammocks;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.frm_details_user, container, false);
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
        tv_note = view.findViewById(R.id.tv_note);
        tv_name_note = view.findViewById(R.id.tv_name_note);

        img_back.setOnClickListener(this);
        img_delete.setOnClickListener(this);
        tv_created_name.setOnClickListener(this);
        try{
            tv_name.setText(objectHammocks.getName());
            tv_location.setText(objectHammocks.getAddress());
            tv_location.setText(objectHammocks.getAddress());
            tv_description.setText(objectHammocks.getDescription());
            rcv_photo.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            Log.e("getPhotoURLs",objectHammocks.getPhotoURLs().size()+"--");
            for (int q=0; q<=objectHammocks.getPhotoURLs().size()-1; q++){
                if (objectHammocks.getPhotoURLs().get(q)==null){
                    objectHammocks.getPhotoURLs().remove(q);
                }
            }
            Log.e("getPhotoURLs",objectHammocks.getPhotoURLs().size()+"--");
            myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(), objectHammocks.getPhotoURLs(), wwidth, height);
            rcv_photo.setAdapter(myRecyclerViewAdapter);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String dateString = formatter.format(new Date(objectHammocks.getCreatedAt() * 1000L));
            tv_created.setText(dateString+" by ");
            tv_created_name.setText(objectHammocks.getCreatedBy());
            try {
                tv_note.setText(objectHammocks.getNoteFromAdmin());
                if (objectHammocks.getNoteFromAdmin()!=null && objectHammocks.getNoteFromAdmin().length()>0){
                    tv_name_note.setVisibility(View.VISIBLE);
                }else {
                    tv_name_note.setVisibility(View.GONE);
                }
            }catch (Exception e){
                e.printStackTrace();
                tv_name_note.setVisibility(View.GONE);
            }
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
        }
    }
}
