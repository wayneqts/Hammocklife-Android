package com.app.hammocklife.custom;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.hammocklife.model.ObjectUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AppPreferences {
    public static final String APP_PREFERENCES_FILE_NAME = "userdata";
    public static final String USER_ID = "userID";
    public static final String TOKEN = "token";
    public static final String PROFILE_PIC = "profile_pic";
    public static final String USER_NAME = "username";

    private SharedPreferences preferences;

    public AppPreferences(Context context) {
        this.preferences = context.getSharedPreferences(APP_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key) {
        return preferences.getString(key, null);
    }

    public void putString(String key, String value)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void clear()
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    // save profile
    public void setPf(ObjectUser pf) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pf);
        editor.putString("profile", json).apply();
    }

    public ObjectUser getPf() {
        Gson gson = new Gson();
        String json = preferences.getString("profile", null);
        Type type = new TypeToken<ObjectUser>() {}.getType();
        return gson.fromJson(json, type);
    }
}
