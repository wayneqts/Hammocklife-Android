package com.app.hammocklife.custom.ViewImage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.hammocklife.R;

import java.util.ArrayList;
import java.util.List;

public class ImageOverlayView extends RelativeLayout {

    private TextView tv_pagenum;
    ImageView img_back;
    Context context;
    int position = -1;
    String source = "";
    List<String> galleryPhoto = new ArrayList<>();

    public ImageOverlayView(Context context, int position, List<String> galleryPhoto) {
        super(context);
        this.context = context;
        this.position = position;
        this.galleryPhoto = galleryPhoto;
        init();
    }

    public ImageOverlayView(Context context) {
        super(context);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setDescription(int count, int size) {
        tv_pagenum.setText(count + " of " + size);
        position = count - 1;

    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_image_overlay, this);
        tv_pagenum = findViewById(R.id.tv_pagenum);
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageViewer.dialog.dismiss();
            }
        });

    }
}
