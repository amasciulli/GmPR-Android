package com.genymobile.pr.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("avatar_url")
    private String avatarUrl;
    private String login;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getLogin() {
        return login;
    }
}
