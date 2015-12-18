package com.genymobile.pr.net;

import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.ReposRetrievedEvent;
import com.genymobile.pr.model.Repo;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ReposCallback implements Callback<List<Repo>> {
    @Override
    public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
        if (response.isSuccess()) {
            BusProvider.getInstance().post(new ReposRetrievedEvent(response.body()));
        }
        //TODO handle error
    }

    @Override
    public void onFailure(Throwable t) {
        //TODO handle error
    }
}
