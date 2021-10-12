package com.example.vlad.alphatest.repository;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.data.ProgressResult;
import com.example.vlad.alphatest.interfaceses.api.GalleryApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class GalleryRestRepository implements GalleryRepository {

    private GalleryApi api;

    public GalleryRestRepository(GalleryApi api) {
        this.api = api;
    }

    @Override
    public Observable<List<Image>> getImagesFromServer() {
        return api.getImages().map(result -> {
            List<Image> images = new ArrayList<>();
            for (int i = 0; i < result.getImages().size(); i++) {
                Image image = new Image(String.valueOf(i), result.getImages().get(i));
                images.add(image);
            }
            return images;
        });
    }

    @Override
    public Completable writeNewImageToDB(Image image) {
        return null;
    }

    @Override
    public Observable<ProgressResult<Image>> uploadData(File file) {
        return null;
    }
}
