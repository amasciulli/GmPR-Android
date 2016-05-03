package com.genymobile.pr.net;

import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.LoadingErrorEvent;
import com.genymobile.pr.bus.NetworkErrorEvent;
import com.genymobile.pr.bus.ReposRetrievedEvent;
import com.genymobile.pr.model.Repo;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class ReposCallback implements Callback<List<Repo>> {
    private static final String TAG = ReposCallback.class.getSimpleName();

    @Override
    public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
        if (response.isSuccessful()) {
            BusProvider.getInstance().post(new ReposRetrievedEvent(response.body()));
        } else {
            Log.e(TAG, "Couldn't load repos : " + response.message());
            BusProvider.getInstance().post(new LoadingErrorEvent(response));
        }
    }

    @Override
    public void onFailure(Call<List<Repo>> call, Throwable t) {
        Log.e(TAG, "Couldn't load repos", t);
        BusProvider.getInstance().post(new NetworkErrorEvent());
    }
}
