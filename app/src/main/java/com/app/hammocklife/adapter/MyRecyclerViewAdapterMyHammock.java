package com.app.hammocklife.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hammocklife.AddNew;
import com.app.hammocklife.Main;
import com.app.hammocklife.R;
import com.app.hammocklife.fragment.Frm_Deails;
import com.app.hammocklife.fragment.Frm_Deails_Admin;
import com.app.hammocklife.fragment.Frm_Deails_User;
import com.app.hammocklife.model.ObjectHammocks;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.io.File;
import java.util.ArrayList;

public class MyRecyclerViewAdapterMyHammock extends RecyclerView.Adapter<MyRecyclerViewAdapterMyHammock.ViewHolder> {

    private ArrayList<ObjectHammocks> arrData;
    private LayoutInflater mInflater;
    private Context context;

    public MyRecyclerViewAdapterMyHammock(Context context, ArrayList<ObjectHammocks> arrData) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.arrData = arrData;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.line_my_hammock, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(context)
                .load(arrData.get(position).getPhotoURLs().get(0))
                .transform(new CenterCrop(), new RoundedCorners(8))
                .into(holder.img_hammock);
        holder.tv_name.setText(arrData.get(position).getName());
        holder.tv_description.setText(arrData.get(position).getAddress());
        switch (arrData.get(position).getStatus()) {
        case "waiting":
            holder.tv_status.setText("Waiting for Approval...");
            holder.tv_status.setTextColor(Color.parseColor("#212121"));
            break;
        case "approval":
            holder.tv_status.setText("Approved");
            holder.tv_status.setTextColor(Color.parseColor("#41C261"));
            break;
        case "reject":
            holder.tv_status.setText("Rejected");
            holder.tv_status.setTextColor(Color.parseColor("#f20000"));
            break;
        case "delete":
            holder.tv_status.setText("Deleted by Admin");
            holder.tv_status.setTextColor(Color.parseColor("#f20000"));
            break;
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return (null != arrData ? arrData.size() : 0);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatImageView img_hammock;
        AppCompatTextView tv_name, tv_description, tv_status;

        ViewHolder(View itemView) {
            super(itemView);
            img_hammock = itemView.findViewById(R.id.img_hammock);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_status = itemView.findViewById(R.id.tv_status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Frm_Deails_User frm_deails = new Frm_Deails_User(arrData.get(getAdapterPosition()));
            ((Main)context).addFragment(frm_deails);
        }
    }
}