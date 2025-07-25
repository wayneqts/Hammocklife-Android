package com.app.hammocklife.custom.ViewImage.draweeview;

import android.view.View;

/**
 * Created by MyPC on 12/04/2018.
 */

public interface OnPhotoTapListener {

    /**
     * A callback to receive where the user taps on a photo. You will only receive a callback if
     * the user taps on the actual photo, tapping on 'whitespace' will be ignored.
     *
     * @param view - View the user tapped.
     * @param x - where the user tapped from the of the Drawable, as percentage of the
     * Drawable width.
     * @param y - where the user tapped from the top of the Drawable, as percentage of the
     * Drawable height.
     */
    void onPhotoTap(View view, float x, float y);
}
