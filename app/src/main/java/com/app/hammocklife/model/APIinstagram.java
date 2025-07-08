package com.app.hammocklife.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIinstagram {
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("user_id")
    @Expose
    private long userId;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
