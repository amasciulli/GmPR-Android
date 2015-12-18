package com.genymobile.pr.net;

import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.PullRequestsRetrievedEvent;

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
