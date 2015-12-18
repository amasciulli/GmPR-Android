package com.genymobile.pr;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.util.List;

public class PullRequestsCallback implements Callback<List<PullRequest>> {
    @Override
    public void onResponse(Response<List<PullRequest>> response, Retrofit retrofit) {
        if (response.isSuccess()) {
            BusProvider.getInstance().post(new PullRequestsRetrievedEvent(response.body()));
        }
    }

    @Override
    public void onFailure(Throwable t) {

    }
}
