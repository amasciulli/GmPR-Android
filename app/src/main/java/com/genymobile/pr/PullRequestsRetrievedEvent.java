package com.genymobile.pr;

import java.util.List;

public class PullRequestsRetrievedEvent {

    PullRequestsRetrievedEvent(List<PullRequest> pullRequests) {
        this.pullRequests = pullRequests;
    }

    private List<PullRequest> pullRequests;

    public List<PullRequest> getPullRequests() {
        return pullRequests;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PullRequest pr : pullRequests) {
            sb.append(pr.getHead().getRepo().getFullName() + " " + pr.getTitle());
            sb.append("\n");
        }
        return sb.toString();
    }
}
