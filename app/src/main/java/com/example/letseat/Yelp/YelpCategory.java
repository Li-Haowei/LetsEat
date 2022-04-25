package com.example.letseat.Yelp;

import com.google.gson.annotations.SerializedName;

public class YelpCategory {
    @SerializedName("title")
    String title;
    @SerializedName("alias")
    String alias;

    public String getTitle() {
        return title;
    }

    public String getAlias() {
        return alias;
    }
}
