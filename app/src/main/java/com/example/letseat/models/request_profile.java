package com.example.letseat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class request_profile {

    private String restName;

    @SerializedName("restLabels")
    @Expose
    private String restLabels;

    @SerializedName("restAdd")
    @Expose
    private String restAdd;

    @SerializedName("invitedBy")
    @Expose
    private String invitedBy;

    @SerializedName("url")
    @Expose
    private String imageUrl;

    @SerializedName("matchRate")
    @Expose
    private int matchRate;

    @SerializedName("common")
    @Expose
    private ArrayList<String> common;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("message")
    @Expose
    private String message;

    private String fileId;

    private String email;

    public request_profile (String restName, String restLabels, String restAdd, String invitedBy, String imageUrl, String email, String time, String message, int matchRate, ArrayList<String> common, String fileId) {
        this.restName = restName;
        this.restAdd = restAdd;
        this.restLabels = restLabels;
        this.invitedBy = invitedBy;
        this.imageUrl = imageUrl;
        this.email = email;
        this.time = time;
        this.message = message;
        this.matchRate = matchRate;
        this.common = common;
        this.fileId = fileId;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String name) {
        this.restName = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRestLabels() {
        return restLabels;
    }

    public void setRestLabels(String restLabels) {
        this.restLabels = restLabels;
    }

    public String getRestAdd() {
        return restAdd;
    }

    public void setRestAdd(String restAdd) {
        this.restAdd = restAdd;
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public String getEmail () {
        return email;
    }

    public String getTime(){ return time; }

    public String getMessage(){ return message; }

    public int getMatchRate(){ return matchRate; }

    public ArrayList<String> getCommon(){ return common; }

    public void setEmail (String email) {
        this.email = email;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getFileId () {
        return fileId;
    }
    public void setFileId (String id) {
        this.fileId = fileId;
    }

}

