package com.genymobile.pr;

import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

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
}
