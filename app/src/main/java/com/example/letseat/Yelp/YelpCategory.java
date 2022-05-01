package com.example.letseat.Yelp;

import com.google.gson.annotations.SerializedName;
/*
This class is separated from YelpDataClasses so programmers can use the methods
to get "tags" for user matching, and methods are made public
 */
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
