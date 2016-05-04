package com.genymobile.pr.net;

import com.genymobile.pr.BuildConfig;
import com.genymobile.pr.model.Issue;
import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.model.Repo;

import android.util.Base64;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class GitHubProvider {
    private static final String URL_GITHUB = "https://api.github.com";
    private static final String HEADER_AUTHORIZATION_BASIC = "Basic ";

    private final GitHubApi api;

    public GitHubProvider(String username, String password) {
        api = createApi(GitHubApi.class, username, password);
    }

    public Call<List<Repo>> getRepos(String organization) {
        return api.getRepos(organization);
    }

    public Call<List<Repo>> getWatchedRepos() {
        return api.getWatchedRepos();
    }

    public Call<List<PullRequest>> getPullRequests(String owner, String repo) {
        return api.getPullRequests(owner, repo);
    }

    public Call<Issue> getIssue(String owner, String repo, int number) {
        return api.getIssue(owner, repo, number);
    }

    private <T> T createApi(Class<T> apiClass, String username, String password) {
        String credentials = username + ":" + password;
        final String basic = HEADER_AUTHORIZATION_BASIC
                + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthorizationInterceptor(basic))
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_GITHUB)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        return retrofit.create(apiClass);
    }
}
