package com.example.vlad.alphatest.model;

import android.graphics.drawable.Drawable;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.data.ProgressResult;
import com.example.vlad.alphatest.interfaceses.model.AGalleryMvpModel;
import com.example.vlad.alphatest.managers.StorageManager;
import com.example.vlad.alphatest.repository.GalleryFirebaseRepository;
import com.example.vlad.alphatest.repository.GalleryRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import timber.log.Timber;

public class AGalleryModel extends BaseModel implements AGalleryMvpModel {

    private final GalleryRepository repository;
    private final StorageManager storageManager;
    private ArrayList<Image> imageList;

    public AGalleryModel(Scheduler executeScheduler, GalleryRepository repository, StorageManager storageManager) {
        super(executeScheduler);
        this.repository = repository;
        this.storageManager = storageManager;
        this.imageList = new ArrayList<>();
    }

    @Override
    public void init() {
    }

    @Override
    public Observable<ProgressResult<Image>> uploadPhoto(File file) {
        return buildObservable(repository.uploadData(file));
    }

    @Override
    public void pauseUpload() {
        if (repository instanceof GalleryFirebaseRepository)
            ((GalleryFirebaseRepository) repository).uploadToPause();
    }

    @Override
    public void resumeUpload() {
        if (repository instanceof GalleryFirebaseRepository)
            ((GalleryFirebaseRepository) repository).uploadToResume();
    }

    @Override
    public void cancelUpload() {
        if (repository instanceof GalleryFirebaseRepository)
            ((GalleryFirebaseRepository) repository).uploadToCancel();
    }

    @Override
    public boolean inPause() {
        if (repository instanceof GalleryFirebaseRepository)
            return ((GalleryFirebaseRepository) repository).inPause();
        else
            return false;
    }

    @Override
    public ArrayList<Image> getCachedImageList() {
        return imageList;
    }

    @Override
    public Observable<ProgressResult<File>> savePhotoToInternalStorage(Drawable drawable) {
        return buildAsyncObservable(subscriber -> {
            Timber.d("START SAVE FILE");
            if (drawable != null) {
                ProgressResult<File> fileResult = new ProgressResult<>(null, 0);
                File file = storageManager.saveToFiles(drawable);
                if (file != null) {
                    fileResult.setResult(file);
                    sendOnComplete(subscriber, fileResult);
                } else {
                    sendOnError(subscriber, new NullPointerException());
                }
            } else {
                sendOnError(subscriber, new NullPointerException());
            }
        });
    }

    @Override
    public Observable<List<Image>> getImageList() {
        return buildAsyncObservable(repository.getImagesFromServer().doOnError(error -> {
            Timber.d("getImagesFromServerError: " + error);
        }).doOnNext(imageList -> {
            this.imageList = (ArrayList<Image>) imageList;
            Timber.d("GET IMAGELIST IN MODEL, SIZE: = %s", this.imageList.size());
        }));
    }

    @Override
    public Observable<Void> writeUrlToDatabase(Image image) {
        Timber.d("IMAGE UPLOAD TO DATABASE IMAGE URL: %s", image.getUrl());
        return buildObservable(repository.writeNewImageToDB(image).toObservable());
    }
}
