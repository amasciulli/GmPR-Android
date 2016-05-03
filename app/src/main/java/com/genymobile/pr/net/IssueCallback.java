package com.genymobile.pr.net;

import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.IssueRetrievedEvent;
import com.genymobile.pr.model.Issue;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueCallback implements Callback<Issue> {

    private static final String TAG = IssueCallback.class.getSimpleName();

    @Override
    public void onResponse(Call<Issue> call, Response<Issue> response) {
        if (response.isSuccessful()) {
            BusProvider.getInstance().post(new IssueRetrievedEvent(response.body()));
        } else {
            Log.e(TAG, "Couldn't load issue : " + response.message());
        }
        //TODO handle error
    }

    @Override
    public void onFailure(Call<Issue> call, Throwable t) {
        Log.e(TAG, "Couldn't load issue", t);
        //TODO handle error
    }
}
