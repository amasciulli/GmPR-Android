package com.genymobile.pr.model;

public class PullRequest {
    private String title;
    private Head head;
    private String body;
    private User user;

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
}
