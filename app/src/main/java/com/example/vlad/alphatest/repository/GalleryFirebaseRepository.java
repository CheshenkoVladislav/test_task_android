package com.example.vlad.alphatest.repository;

import android.net.Uri;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.data.ProgressResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import durdinapps.rxfirebase2.RxFirebaseDatabase;
import durdinapps.rxfirebase2.RxFirebaseQuery;
import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;

public class GalleryFirebaseRepository extends BaseRepository implements GalleryRepository {
    private final FirebaseStorage fireStorage;
    private final FirebaseDatabase firebaseDatabase;
    private UploadTask taskManager;

    public GalleryFirebaseRepository(FirebaseStorage fireStorage, FirebaseDatabase firebaseDatabase) {
        this.fireStorage = fireStorage;
        this.firebaseDatabase = firebaseDatabase;
    }

    @Override
    public Observable<List<Image>> getImagesFromServer() {
        return RxFirebaseQuery.getInstance().filterByRefs(firebaseDatabase.getReference(),
                firebaseDatabase.getReference()).asList(resultList -> {
            List<Image> imageList = new ArrayList<>();
            for (DataSnapshot snapshot : resultList) {
                Image image = new Image(snapshot.getKey(), (String) snapshot.getValue());
                imageList.add(image);
            }
            return imageList;
        }).toObservable();
    }

    @Override
    public Completable writeNewImageToDB(Image image) {
        return RxFirebaseDatabase.setValue(getDBPostRef().child(image.getId()), image.getUrl());
    }

    @Override
    public Observable<ProgressResult<Image>> uploadData(File file) {
        return Observable.create(subscriber -> {
            try {
                reloadUploadTask();
                StorageReference reference = fireStorage.getReference().child("images/" + file.getName());
                taskManager = reference.putFile(Uri.fromFile(file));
                taskManager.addOnProgressListener(
                        progressTask -> {
                            ProgressResult<Image> result = new ProgressResult<>(null, progressTask != null ?
                                    (100 * progressTask.getBytesTransferred()) / progressTask.getTotalByteCount() : 0);
                            sendOnNext(subscriber, result);
                        })
                        .addOnPausedListener(taskPaused -> Timber.d("UPLOAD PAUSED"))
                        .addOnCanceledListener(() -> Timber.d("UPLOAD CANCELLED"))
                        .continueWithTask(task -> {
                            Timber.d("TASK URL %s", reference.getDownloadUrl());
                            return reference.getDownloadUrl();
                        })
                        .addOnCompleteListener(completeTask -> {
                            Timber.d("COMPLETE TASK: " + completeTask.toString());
                            if (completeTask.getException() == null && completeTask.getResult() != null) {
                                Image image = new Image(file.getName().substring(0, 21), completeTask.getResult().toString());
                                ProgressResult<Image> result = new ProgressResult<>(image, 100);
                                sendOnComplete(subscriber, result);
                            } else {
                                sendOnError(subscriber, completeTask.getException());
                            }
                        })
                        .addOnFailureListener(failureTask -> sendOnError(subscriber, failureTask));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public boolean inPause() {
        if (taskManager != null)
            return taskManager.isPaused();
        else
            return false;
    }

    public void uploadToPause() {
        if (taskManager != null && taskManager.isInProgress()) {
            taskManager.pause();
        }
    }

    public void uploadToResume() {
        if (taskManager != null && taskManager.isPaused()) {
            taskManager.resume();
        }
    }

    public void uploadToCancel() {
        if (taskManager != null) {
            taskManager.cancel();
        }
    }

    private DatabaseReference getDBPostRef() {
        return firebaseDatabase.getReference();
    }

    private void reloadUploadTask() {
        if (taskManager != null) {
            taskManager.cancel();
            taskManager = null;
        }
    }
}
