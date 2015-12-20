package com.genymobile.pr.net;

import android.util.Log;

import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.PullRequestsRetrievedEvent;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.util.List;

public class PullRequestsCallback implements Callback<List<PullRequest>> {
    private static final String TAG = PullRequestsCallback.class.getSimpleName();

    @Override
    public void onResponse(Response<List<PullRequest>> response, Retrofit retrofit) {
        if (response.isSuccess()) {
            BusProvider.getInstance().post(new PullRequestsRetrievedEvent(response.body()));
        } else {
            Log.e(TAG, "Couldn't load pull requests : " + response.message());
        }
        //TODO handle error
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e(TAG, "Couldn't load pull requests", t);
        //TODO handle error
    }
}
