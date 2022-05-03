package com.example.letseat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class request_profile {

    private String restName;
    private String restLabels;
    private String restAdd;
    private String invitedBy;
    private String imageUrl;
    private String email;
    private String fileId;

    public request_profile (String restName, String restLabels, String restAdd, String invitedBy, String imageUrl, String email, String fileId) {
        this.restName = restName;
        this.restAdd = restAdd;
        this.restLabels = restLabels;
        this.invitedBy = invitedBy;
        this.imageUrl = imageUrl;
        this.email = email;
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

