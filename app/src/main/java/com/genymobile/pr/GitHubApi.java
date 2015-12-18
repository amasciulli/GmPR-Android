package com.genymobile.pr;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

public interface GitHubApi {
    @GET("/orgs/{organization}/repos")
    Call<List<Repo>> getRepos(@Path("organization") String organization);
}
