package com.genymobile.pr.net;

import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.LoadingErrorEvent;
import com.genymobile.pr.bus.NetworkErrorEvent;
import com.genymobile.pr.bus.PullRequestsRetrievedEvent;
import com.genymobile.pr.model.PullRequest;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class PullRequestsCallback implements Callback<List<PullRequest>> {
    private static final String TAG = PullRequestsCallback.class.getSimpleName();

    @Override
    public void onResponse(Call<List<PullRequest>> call, Response<List<PullRequest>> response) {
        if (response.isSuccessful()) {
            BusProvider.getInstance().post(new PullRequestsRetrievedEvent(response.body()));
        } else {
            Log.e(TAG, "Couldn't load pull requests : " + response.message());
            BusProvider.getInstance().post(new LoadingErrorEvent(response));
        }
    }

    @Override
    public void onFailure(Call<List<PullRequest>> call, Throwable t) {
        Log.e(TAG, "Couldn't load pull requests", t);
        BusProvider.getInstance().post(new NetworkErrorEvent());
    }
}
