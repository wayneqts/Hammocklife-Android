package com.app.hammocklife.custom.ViewImage.draweeview;

/**
 * Created by MyPC on 12/04/2018.
 */

public interface OnScaleDragGestureListener {
    void onDrag(float dx, float dy);

    void onFling(float startX, float startY, float velocityX, float velocityY);

    void onScale(float scaleFactor, float focusX, float focusY);

    void onScaleEnd();
}
