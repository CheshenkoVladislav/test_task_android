package com.example.vlad.alphatest.interfaceses.view;


import android.content.Intent;

import com.example.vlad.alphatest.data.Image;

import java.io.File;
import java.util.List;

public interface AGalleryMvpView extends MvpView {
    void initPhotos(List<Image> images);
    void initRecyclerView(List<Image> imageList);
    void sendCameraIntent(Intent cameraIntent);
    void sendGalleryIntent(Intent intent);
    void addNewPhoto(Image image);
    void setUploadProgress(long progress);
    void toggleBtn(boolean enable);
    void togglePhotosClickable(boolean clickable);
    void showProgressDialog();
    void closeProgressDialog();
    void setProgress(long progress);
    void setListeners();
}
