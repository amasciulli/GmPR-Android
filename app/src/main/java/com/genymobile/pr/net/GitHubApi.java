package com.genymobile.pr.net;

import com.genymobile.pr.model.Issue;
import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.model.Repo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface GitHubApi {
    @GET("/orgs/{organization}/repos")
    Call<List<Repo>> getRepos(@Path("organization") String organization);

    @GET("/repos/{owner}/{repo}/pulls")
    Call<List<PullRequest>> getPullRequests(@Path("owner") String owner, @Path("repo")String repo);

    @GET("/repos/{owner}/{repo}/issues/{number}")
    Call<Issue> getIssue(@Path("owner") String owner, @Path("repo") String repo, @Path("number") int number);

    @GET("/user/subscriptions")
    Call<List<Repo>> getWatchedRepos();
}
