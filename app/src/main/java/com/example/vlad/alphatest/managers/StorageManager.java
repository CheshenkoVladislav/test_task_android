package com.example.vlad.alphatest.managers;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.vlad.alphatest.data.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;

import timber.log.Timber;

public class StorageManager {
    private final FirebaseStorage fireStorage;
    private final FirebaseDatabase firebaseDatabase;
    private final Context context;
    private UploadTask taskManager;

    public StorageManager(FirebaseStorage fireStorage, FirebaseDatabase firebaseDatabase, Context context) {
        this.fireStorage = fireStorage;
        this.firebaseDatabase = firebaseDatabase;
        this.context = context;
    }

    public File saveToFiles(byte[] imageByteArray) {
        String filename = "cachedImage.jpg";
        File file = new File(context.getFilesDir(), filename);
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
            outputStream.write(imageByteArray);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public void downloadImageUrlList(ValueEventListener valueEventListener) {
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.orderByChild("images").addValueEventListener(valueEventListener);
    }

    public void writeNewImageToDB(Image image, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("images").child(image.getId()).setValue(image)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void uploadData(File file, OnProgressListener<UploadTask.TaskSnapshot> onProgressListener,
                           OnCompleteListener<Uri> completeListener, OnFailureListener failureListener) {
        try {
            reloadUploadTask();
            StorageReference reference = fireStorage.getReference().child("images/" + file.getName());
            taskManager = reference.putFile(Uri.fromFile(file));
            taskManager.addOnProgressListener(onProgressListener)
                    .addOnPausedListener(taskPaused -> Timber.d("UPLOAD PAUSED"))
                    .addOnCanceledListener(() -> Timber.d("UPLOAD CANCELLED"))
                    .continueWithTask(task -> {
                        Timber.d("TASK URL %s", reference.getDownloadUrl());
                        return reference.getDownloadUrl();
                    })
                    .addOnCompleteListener(completeListener)
                    .addOnFailureListener(failureListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void reloadUploadTask() {
        if (taskManager != null) {
            taskManager.cancel();
            taskManager = null;
        }
    }

}
