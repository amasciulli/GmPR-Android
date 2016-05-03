package com.genymobile.pr.net;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AuthorizationInterceptor implements Interceptor {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private final String authorization;

    public AuthorizationInterceptor(String authorization) {
        this.authorization = authorization;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
                .header(HEADER_AUTHORIZATION, authorization)
                .method(original.method(), original.body());

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
