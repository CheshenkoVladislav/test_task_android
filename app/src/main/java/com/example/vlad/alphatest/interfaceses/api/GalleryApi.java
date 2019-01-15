package com.example.vlad.alphatest.interfaceses.api;

import com.example.vlad.alphatest.data.RestImageModel;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface GalleryApi {
    @GET("images/get")
    Observable<RestImageModel> getImages();
}
