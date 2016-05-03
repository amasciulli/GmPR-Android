package com.genymobile.pr.bus;

import android.support.annotation.Nullable;
import retrofit2.Response;

public class LoadingErrorEvent {
    private final Response response;

    public LoadingErrorEvent(@Nullable Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}
