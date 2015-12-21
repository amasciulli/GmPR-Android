package com.genymobile.pr.bus;

import android.support.annotation.Nullable;

import retrofit.Response;

public class LoadingErrorEvent {
    private final Response response;
    private final boolean networkError;

    public LoadingErrorEvent(boolean networkError, @Nullable Response response) {
        this.networkError = networkError;
        this.response = response;
    }

    public boolean isNetworkError() {
        return networkError;
    }

    public Response getResponse() {
        return response;
    }
}
