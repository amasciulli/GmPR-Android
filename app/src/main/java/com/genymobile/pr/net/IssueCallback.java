package com.genymobile.pr.net;

import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.IssueRetrievedEvent;
import com.genymobile.pr.model.Issue;

import android.util.Log;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class IssueCallback implements Callback<Issue> {

    private static final String TAG = IssueCallback.class.getSimpleName();

    @Override
    public void onResponse(Response<Issue> response, Retrofit retrofit) {
        if (response.isSuccess()) {
            BusProvider.getInstance().post(new IssueRetrievedEvent(response.body()));
        } else {
            Log.e(TAG, "Couldn't load issue : " + response.message());
        }
        //TODO handle error
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e(TAG, "Couldn't load issue", t);
        //TODO handle error
    }
}
