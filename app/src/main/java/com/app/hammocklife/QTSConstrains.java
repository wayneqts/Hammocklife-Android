package com.app.hammocklife;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MyPC on 05/12/2017.
 */
public class QTSConstrains {

    public static int getName(Context context) {
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "hammock", mode);
        return sharedPreferences.getInt("width_device", 0);
    }

    public static void setName(Context context, int num) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "hammock", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("width_device", num);
        editor.commit();
    }
}
