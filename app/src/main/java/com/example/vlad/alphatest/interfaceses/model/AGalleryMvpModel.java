package com.example.vlad.alphatest.interfaceses.model;

import android.graphics.drawable.Drawable;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.data.RxResult;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

public interface AGalleryMvpModel extends MvpModel{
    Observable <RxResult<Image>> uploadPhoto(File file);
    void pauseUpload();
    void resumeUpload();
    void cancelUpload();
    Observable <RxResult<File>> savePhotoToInternalStorage(Drawable drawable);
    Observable <RxResult<List<Image>>> downloadImages();
}
