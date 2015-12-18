package com.genymobile.pr.net;

import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.model.Repo;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import java.util.List;

public class GitHubProvider {

    private final GitHubApi api;

    public GitHubProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(GitHubApi.class);
    }

    public Call<List<Repo>> getRepos(String organization) {
        return api.getRepos(organization);
    }

    public Call<List<PullRequest>> getPullRequests(String owner, String repo) {
        return api.getPullRequests(owner, repo);
    }
}
