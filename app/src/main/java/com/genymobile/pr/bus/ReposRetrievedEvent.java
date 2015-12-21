package com.genymobile.pr.bus;

import com.genymobile.pr.model.Repo;

import java.util.List;

public class ReposRetrievedEvent {
    private final List<Repo> repos;

    public ReposRetrievedEvent(List<Repo> repos) {
        this.repos = repos;
    }

    public List<Repo> getRepos() {
        return repos;
    }
}
