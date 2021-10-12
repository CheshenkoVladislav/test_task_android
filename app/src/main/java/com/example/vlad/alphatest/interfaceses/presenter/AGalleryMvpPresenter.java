package com.example.vlad.alphatest.interfaceses.presenter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;

public interface AGalleryMvpPresenter extends MvpPresenter {
    void requestToAddPhoto(boolean b);
    void requestToOpenPhotoViewer(Drawable imageDrawable);
    void permissionResult(int requestCode, boolean permissionGranted);
    void activityResult(int requestCode, int resultCode, @Nullable Intent data);
    void requestToReinit();
    void requestToUploadAgain();
    void requestToCancelUpload();

    void requestToCheckPermission();
}
