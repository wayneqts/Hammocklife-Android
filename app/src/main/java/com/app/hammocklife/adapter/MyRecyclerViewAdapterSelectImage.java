package com.app.hammocklife.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hammocklife.AddNew;
import com.app.hammocklife.Main;
import com.app.hammocklife.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.io.File;
import java.util.ArrayList;

public class MyRecyclerViewAdapterSelectImage extends RecyclerView.Adapter<MyRecyclerViewAdapterSelectImage.ViewHolder> {

    private ArrayList<Uri> arrData;
    private LayoutInflater mInflater;
    private int wwidth = 0, height = 0;
    private Context context;

    public MyRecyclerViewAdapterSelectImage(Context context, ArrayList<Uri> arrData, int wwidth, int height) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.arrData = arrData;
        this.wwidth = wwidth;
        this.height = height;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.line_photo_upload, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setLayoutView(holder.myImage, wwidth / 3 - 50, wwidth / 3 - 50);
        if (arrData.get(position)!=null && arrData.get(position).toString().length()>0) {
            Log.e("error", arrData.get(position).toString());
            if (arrData.get(position).toString().equals("http://www.google.com")) {
                setLayoutView(holder.rl_img_one, wwidth / 3 - 50, wwidth / 3 - 50);
                Glide.with(context)
                        .load(R.drawable.ic_camera)
                        .transform(new CenterCrop(), new RoundedCorners(8))
                        .into(holder.myImage);
                holder.rl_img_one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ((AddNew) context).checkPermission();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                holder.rl_img_one.setVisibility(View.GONE);
                Glide.with(context)
                        .load(arrData.get(position))
                        .transform(new CenterCrop(), new RoundedCorners(8))
                        .into(holder.myImage);
                holder.img_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            File fileDelete = new File(arrData.get(position).getPath());
                            boolean deleted = fileDelete.delete();
                            Log.e("file2",deleted+" - ");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        arrData.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, arrData.size());
                    }
                });
            }
        } else {
            Log.e("error remove", arrData.get(position).toString());
            arrData.remove(position);
            notifyDataSetChanged();
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return (null != arrData ? arrData.size() : 0);
    }

    public void setLayoutView(View view, int width, int height) {
        if (view!=null) {
            view.getLayoutParams().width = width;
            view.getLayoutParams().height = height;
        }
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatImageView myImage, img_delete;
        RelativeLayout rl_img_one;

        ViewHolder(View itemView) {
            super(itemView);
            myImage = itemView.findViewById(R.id.myImage);
            img_delete = itemView.findViewById(R.id.img_delete);
            rl_img_one = itemView.findViewById(R.id.rl_img_one);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}