package com.genymobile.pr.model;

import com.google.gson.annotations.SerializedName;

public class Repo {
    private String name;

    @SerializedName("full_name")
    private String fullName;
    @SerializedName("html_url")
    private String htmlUrl;
    private User owner;

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public User getOwner() {
        return owner;
    }
}
