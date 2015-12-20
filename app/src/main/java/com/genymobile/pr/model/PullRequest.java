package com.genymobile.pr.model;

import com.google.gson.annotations.SerializedName;

public class PullRequest {
    private String title;
    private Head head;
    private String body;
    private User user;
    @SerializedName("html_url")
    private String htmlUrl;

    public String getTitle() {
        return title;
    }

    public Head getHead() {
        return head;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }
}
