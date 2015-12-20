package com.genymobile.pr.model;

public class PullRequest {
    private String title;
    private Head head;
    private String body;

    public String getTitle() {
        return title;
    }

    public Head getHead() {
        return head;
    }

    public String getBody() {
        return body;
    }
}
