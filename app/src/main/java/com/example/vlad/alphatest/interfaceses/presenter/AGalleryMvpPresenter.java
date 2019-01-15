package com.example.vlad.alphatest.interfaceses.presenter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface AGalleryMvpPresenter extends MvpPresenter {
    void requestToAddPhoto();
    void requestToOpenPhotoViewer(Drawable imageDrawable);
    void permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    void activityResult(int requestCode, int resultCode, @Nullable Intent data);
    void requestToUpdate();
    void requestToUploadAgain();
    void requestToCancelUpload();
}
