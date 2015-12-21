package com.genymobile.pr.net;

import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.model.Repo;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

import java.util.List;

public interface GitHubApi {
    @GET("/orgs/{organization}/repos")
    Call<List<Repo>> getRepos(@Path("organization") String organization);

    @GET("/repos/{owner}/{repo}/pulls")
    Call<List<PullRequest>> getPullRequests(@Path("owner") String owner, @Path("repo")String repo);
}
