package com.app.hammocklife.model;

import java.util.ArrayList;

public class ObjectHammocks {
    String key, address, createdBy, description, name, status, userUDID, noteFromAdmin;
    long createdAt, serverTime;
    ObjectLocation objectLocation;
    ArrayList<String> photoURLs;

    public ObjectHammocks(String key, String address, String createdBy, String description, String name, String status, String userUDID, long createdAt, long serverTime, ObjectLocation objectLocation, ArrayList<String> photoURLs, String noteFromAdmin) {
        this.key = key;
        this.address = address;
        this.createdBy = createdBy;
        this.description = description;
        this.name = name;
        this.status = status;
        this.userUDID = userUDID;
        this.createdAt = createdAt;
        this.serverTime = serverTime;
        this.objectLocation = objectLocation;
        this.photoURLs = photoURLs;
        this.noteFromAdmin = noteFromAdmin;
    }

    public String getNoteFromAdmin() {
        return noteFromAdmin;
    }

    public void setNoteFromAdmin(String noteFromAdmin) {
        this.noteFromAdmin = noteFromAdmin;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserUDID() {
        return userUDID;
    }

    public void setUserUDID(String userUDID) {
        this.userUDID = userUDID;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public ObjectLocation getObjectLocation() {
        return objectLocation;
    }

    public void setObjectLocation(ObjectLocation objectLocation) {
        this.objectLocation = objectLocation;
    }

    public ArrayList<String> getPhotoURLs() {
        return photoURLs;
    }

    public void setPhotoURLs(ArrayList<String> photoURLs) {
        this.photoURLs = photoURLs;
    }
}
