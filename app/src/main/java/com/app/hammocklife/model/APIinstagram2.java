package com.app.hammocklife.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIinstagram2 {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
