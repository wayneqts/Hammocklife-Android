package com.app.hammocklife.custom.ViewImage.draweeview;

/**
 * Created by MyPC on 12/04/2018.
 */

public interface OnScaleChangeListener {
    /**
     * Callback for when the scale changes
     *
     * @param scaleFactor the scale factor
     * @param focusX focal point X position
     * @param focusY focal point Y position
     */
    void onScaleChange(float scaleFactor, float focusX, float focusY);
}
