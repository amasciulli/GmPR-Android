package com.genymobile.pr.net;

import com.genymobile.pr.model.Issue;
import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.model.Repo;

import android.util.Base64;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import java.io.IOException;
import java.util.List;

public class GitHubProvider {
    private static final String URL_GITHUB = "https://api.github.com";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_AUTHORIZATION_BASIC = "Basic ";

    private final OkHttpClient httpClient = new OkHttpClient();
    private final GitHubApi api;

    public GitHubProvider(String username, String password) {
        api = createApi(GitHubApi.class, username, password);
    }

    public Call<List<Repo>> getRepos(String organization) {
        return api.getRepos(organization);
    }

    public Call<List<PullRequest>> getPullRequests(String owner, String repo) {
        return api.getPullRequests(owner, repo);
    }

    public Call<Issue> getIssue(String owner, String repo, int number) {
     return api.getIssue(owner, repo, number);
    }

    private <T> T createApi(Class<T> apiClass, String username, String password) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL_GITHUB)
                .addConverterFactory(GsonConverterFactory.create());

        if (username != null && password != null) {
            String credentials = username + ":" + password;
            final String basic = HEADER_AUTHORIZATION_BASIC
                    + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header(HEADER_AUTHORIZATION, basic)
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(apiClass);
    }
}
