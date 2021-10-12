package com.example.vlad.alphatest.repository;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.data.ProgressResult;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface GalleryRepository extends Repository {
    Observable<List<Image>> getImagesFromServer();
    Completable writeNewImageToDB(Image image);
    Observable<ProgressResult<Image>> uploadData(File file);
}
