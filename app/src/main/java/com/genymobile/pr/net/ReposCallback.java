package com.genymobile.pr.net;

import android.util.Log;

import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.LoadingErrorEvent;
import com.genymobile.pr.bus.NetworkErrorEvent;
import com.genymobile.pr.bus.ReposRetrievedEvent;
import com.genymobile.pr.model.Repo;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ReposCallback implements Callback<List<Repo>> {
    private static final String TAG = ReposCallback.class.getSimpleName();

    @Override
    public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
        if (response.isSuccess()) {
            BusProvider.getInstance().post(new ReposRetrievedEvent(response.body()));
        } else {
            Log.e(TAG, "Couldn't load repos : " + response.message());
            BusProvider.getInstance().post(new LoadingErrorEvent(response));
        }
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e(TAG, "Couldn't load repos", t);
        BusProvider.getInstance().post(new NetworkErrorEvent());
    }
}
