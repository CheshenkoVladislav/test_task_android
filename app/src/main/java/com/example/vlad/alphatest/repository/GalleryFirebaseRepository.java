package com.example.vlad.alphatest.repository;

import android.net.Uri;
import android.util.Pair;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.data.ProgressResult;
import com.example.vlad.alphatest.threads.JobExecutor;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.ashdavies.rx.rxtasks.SingleTaskKt;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class GalleryFirebaseRepository extends BaseRepository implements GalleryRepository {
    private final FirebaseStorage fireStorage;
    private final FirebaseDatabase firebaseDatabase;
    private UploadTask taskManager;
    private final Executor executor;

    public GalleryFirebaseRepository(JobExecutor executor, FirebaseStorage fireStorage, FirebaseDatabase firebaseDatabase) {
        this.fireStorage = fireStorage;
        this.firebaseDatabase = firebaseDatabase;
        this.executor = executor;
    }

    @Override
    public Observable<List<Image>> getImagesFromServer() {
        return SingleTaskKt.toSingle(fireStorage.getReference().child("images").listAll())
                .toObservable()
                .observeOn(Schedulers.io())
                .flatMapIterable(ListResult::getItems)
                .concatMap(item -> SingleTaskKt.toSingle(
                        item.getDownloadUrl().continueWith(task ->
                                new Pair<>(item.getName(), task.getResult()))).toObservable()
                )
                .collect((Callable<List<Image>>) ArrayList::new, (list, stringUriPair) ->
                        list.add(new Image(stringUriPair.first, stringUriPair.second.toString())))
                .toObservable();
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
