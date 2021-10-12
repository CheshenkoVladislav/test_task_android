package com.example.vlad.alphatest;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class Interceptor implements okhttp3.Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request.Builder builder = originRequest.newBuilder();
        builder.addHeader("Content-Type", "application/json");
        return chain.proceed(builder.build());
    }
}
