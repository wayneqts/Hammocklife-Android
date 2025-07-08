package com.app.hammocklife.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hammocklife.R;
import com.app.hammocklife.custom.ViewImage.ImageOverlayView;
import com.app.hammocklife.custom.ViewImage.ImageViewer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> arrData;
    private LayoutInflater mInflater;
    private int wwidth = 0, height = 0;
    private Context context;

    public MyRecyclerViewAdapter(Context context, ArrayList<String> arrData, int wwidth, int height) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.arrData = arrData;
        this.wwidth = wwidth;
        this.height = height;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.line_photo, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setLayoutView(holder.myImage, wwidth/3-50,wwidth/3-50);
        Glide.with(context)
                .load(arrData.get(position))
                .transform(new CenterCrop(), new RoundedCorners(8))
                .into(holder.myImage);
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
        AppCompatImageView myImage;
        ImageOverlayView overlayView;

        ViewHolder(View itemView) {
            super(itemView);
            myImage = itemView.findViewById(R.id.myImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.e("urlImage", arrData.size()+" - ");
            ImageViewer.Builder builder = new ImageViewer.Builder<>(context, arrData)
                    .setStartPosition(getAdapterPosition());

            builder.allowSwipeToDismiss(true);
            builder.hideStatusBar(true);
            builder.allowZooming(true);

            overlayView = new ImageOverlayView(context, getAdapterPosition(), arrData);
            builder.setOverlayView(overlayView);
            builder.setImageChangeListener(getImageChangeListener());

            builder.show();
        }

        private ImageViewer.OnImageChangeListener getImageChangeListener() {
            return new ImageViewer.OnImageChangeListener() {
                @Override
                public void onImageChange(int position) {
                    overlayView.setDescription( position + 1, arrData.size());
                }
            };
        }
    }
}