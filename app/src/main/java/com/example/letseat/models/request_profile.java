package com.example.letseat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class request_profile {
    @SerializedName("restName")
    @Expose
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

    private String email;

    public request_profile (String restName, String restLabels, String restAdd, String invitedBy, String imageUrl, String email) {
        this.restName = restName;
        this.restAdd = restAdd;
        this.restLabels = restLabels;
        this.invitedBy = invitedBy;
        this.imageUrl = imageUrl;
        this.email = email;
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

    public void setEmail (String email) {
        this.email = email;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }


}

