package com.example.vlad.alphatest.model;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.data.RxResult;
import com.example.vlad.alphatest.interfaceses.model.AGalleryMvpModel;
import com.example.vlad.alphatest.interfaceses.threads.PostExecutionThread;
import com.example.vlad.alphatest.interfaceses.threads.ThreadExecutor;
import com.example.vlad.alphatest.managers.StorageManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import timber.log.Timber;

public class AGalleryModel extends BaseModel implements AGalleryMvpModel {

    private final StorageManager storageManager;

    @Inject
    public AGalleryModel(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, StorageManager storageManager) {
        super(threadExecutor, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Observable<RxResult<Image>> uploadPhoto(File file) {
        return buildObservable(subscriber -> {
            Timber.d("Upload photo: %s", file.getName());
            storageManager.uploadData(
                    file,
                    taskProgress -> {
                        if (taskProgress != null) {
                            RxResult<Image> result = new RxResult<>(null,
                                    (100 * taskProgress.getBytesTransferred()) / taskProgress.getTotalByteCount());
                            sendOnNext(subscriber, result);
                        }
                    },
                    taskComplete -> {
                        if (taskComplete.getResult() != null) {
                            Image image = new Image(file.getName().substring(0, 21), taskComplete.getResult().toString());
                            writeUrlToDatabase(image,
                                    success -> {
                                        Timber.d("WRITE TO DATABASE SUCCESS");
                                        RxResult<Image> imageResult = new RxResult<>(image, 0);
                                        sendOnComplete(subscriber, imageResult);
                                    },
                                    error -> sendOnError(subscriber, error));
                        }
                    },
                    taskError -> sendOnError(subscriber, taskError)
            );
        });
    }

    @Override
    public void pauseUpload() {
        storageManager.uploadToPause();
    }

    @Override
    public void resumeUpload() {
        storageManager.uploadToResume();
    }

    @Override
    public void cancelUpload() {
        storageManager.uploadToCancel();
    }

    @Override
    public Observable<RxResult<File>> savePhotoToInternalStorage(Drawable drawable) {
        return buildAsyncObservable(subscriber -> {
            Timber.d("START SAVE FILE");
            if (drawable != null) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                File file = storageManager.saveToFiles(bitmapdata);
                RxResult<File> fileResult = new RxResult<>(file, 0);
                sendOnComplete(subscriber, fileResult);
            }
            else {
                sendOnError(subscriber, new Exception());
            }
        });
    }

    @Override
    public Observable<RxResult<List<Image>>> downloadImages() {
        return buildObservable(subscriber -> storageManager.downloadImageUrlList(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Image> imageList = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren().iterator().next().getChildren()) {
                    Image image = data.getValue(Image.class);
                    imageList.add(image);
                }
                Timber.d("PHOTOS DOWNLOADED, RESULT: %s", imageList.toArray());
                RxResult<List<Image>> imageListResult = new RxResult<>(imageList, 0);
                imageListResult.setResult(imageList);
                sendOnComplete(subscriber, imageListResult);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                sendOnError(subscriber, databaseError.toException());
            }
        }));
    }

    private void writeUrlToDatabase(Image image, OnSuccessListener<Void> successListener,
                                    OnFailureListener failureListener) {
        Timber.d("IMAGE UPLOAD TO DATABASE IMAGE URL: %s", image.getUrl());
        storageManager.writeNewImageToDB(image, successListener, failureListener);
    }

    private void sendOnNext(ObservableEmitter subscriber, RxResult result) {
        if (!subscriber.isDisposed()) {
            subscriber.onNext(result);
        }
    }

    private void sendOnComplete(ObservableEmitter subscriber, RxResult result) {
        if (!subscriber.isDisposed()) {
            subscriber.onNext(result);
            subscriber.onComplete();
        }
    }

    private void sendOnError(ObservableEmitter subscriber, @NonNull Throwable error) {
        if (!subscriber.isDisposed())
            subscriber.onError(error);
    }
}
