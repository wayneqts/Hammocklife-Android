package com.app.hammocklife.model;

import java.io.Serializable;
import java.util.List;

public class ObjectUser implements Serializable {
    long createAt, serverTime;
    String email, employment, livingLocation, name, profileUrl, role;
    List<String> hobbies;

    public ObjectUser(long createAt, long serverTime, String email, String employment, String livingLocation, String name, String profileUrl, String role, List<String> hobbies) {
        this.createAt = createAt;
        this.serverTime = serverTime;
        this.email = email;
        this.employment = employment;
        this.livingLocation = livingLocation;
        this.name = name;
        this.profileUrl = profileUrl;
        this.role = role;
        this.hobbies = hobbies;
    }



    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getLivingLocation() {
        return livingLocation;
    }

    public void setLivingLocation(String livingLocation) {
        this.livingLocation = livingLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }
}
