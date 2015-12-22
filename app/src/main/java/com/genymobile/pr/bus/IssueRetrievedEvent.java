package com.genymobile.pr.bus;

import com.genymobile.pr.model.Issue;

public class IssueRetrievedEvent {

    public IssueRetrievedEvent(Issue issue) {
        this.issue = issue;
    }

    private Issue issue;

    public Issue getIssue() {
        return issue;
    }
}
