package com.example.vlad.alphatest.interfaceses.view;


import com.example.vlad.alphatest.data.Image;

import java.io.File;
import java.util.List;

public interface AGalleryMvpView extends MvpView {
    void replaceImageList(List<Image> images);
    void initRecyclerView(List<Image> imageList);
    void checkCameraPermissions();
    void startCamera(File cameraIntent);
    void startGallery(File file);
    void addNewPhoto(Image image);
    void setUploadProgress(long progress);
    void toggleBtn(boolean enable);
    void togglePhotosClickable(boolean clickable);
    void showProgressAlertDialog();
    void dismissAlertDialog();
    void clearAlertDialogListeners();
    void showConnectionErrorAlertDialog();
    void showImageUploadErrorAlertDialog();
    void setListeners();
}
