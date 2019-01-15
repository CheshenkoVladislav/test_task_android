package com.example.vlad.alphatest.interfaceses.model;

import android.graphics.drawable.Drawable;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.data.ProgressResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface AGalleryMvpModel extends MvpModel{
    Observable <ProgressResult<Image>> uploadPhoto(File file);
    void pauseUpload();
    void resumeUpload();
    void cancelUpload();
    boolean inPause();
    ArrayList<Image> getCachedImageList();
    Observable <ProgressResult<File>> savePhotoToInternalStorage(Drawable drawable);
    Observable <List<Image>> getImageList();
    Observable writeUrlToDatabase(Image image);
}
