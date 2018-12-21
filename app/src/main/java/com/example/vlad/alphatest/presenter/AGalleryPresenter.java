package com.example.vlad.alphatest.presenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.vlad.alphatest.interfaceses.model.AGalleryMvpModel;
import com.example.vlad.alphatest.interfaceses.presenter.AGalleryMvpPresenter;
import com.example.vlad.alphatest.interfaceses.view.AGalleryMvpView;
import com.example.vlad.alphatest.support.Constants;
import com.example.vlad.alphatest.support.PermissionHelper;
import com.example.vlad.alphatest.support.PhotoUtils;

import java.io.File;
import java.util.ArrayList;

import timber.log.Timber;

import static com.example.vlad.alphatest.support.Constants.CAMERA_REQUEST_CODE;

public class AGalleryPresenter extends BasePresenter<AGalleryMvpView, AGalleryMvpModel>
        implements AGalleryMvpPresenter {

    private boolean inited;
    private File photoFile;
    private Drawable drawable;

    public AGalleryPresenter(AGalleryMvpView view, AGalleryMvpModel model) {
        super(view, model);
    }

    @Override
    public boolean init() {
        super.init();
        Timber.d("Gallery init");
        if (view != null && model != null) {
            view.setListeners();
            view.initRecyclerView(new ArrayList<>());
            initData();
            return inited = true;
        }
        return inited = false;
    }

    @Override
    public void start() {
        super.start();
        if (view != null) {
            view.togglePhotosClickable(true);
            view.toggleBtn(true);
        }
        if (model != null)
            model.resumeUpload();
    }

    @Override
    public void stop() {
        super.stop();
        if (model != null)
            model.pauseUpload();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (model != null)
            model.cancelUpload();
    }

    @Override
    public void requestToAddPhoto() {
        if (view != null) {
            view.toggleBtn(false);
            if (PermissionHelper.mayRequestCamera(activity)) {
                Timber.d("PERMISSION GRANTED");
                createTempFile();
                view.sendCameraIntent(createCameraIntent());
            }
        }
    }

    @Override
    public void requestToOpenPhotoViewer(Drawable imageDrawable) {
        if (model != null && view != null) {
            view.togglePhotosClickable(false);
            disposables.add(model.savePhotoToInternalStorage(imageDrawable).subscribe(fileResult -> {
                        Timber.d("IMAGE CACHED, URI: %s", fileResult.getResult().getPath());
                        if (view != null) {
                            view.sendGalleryIntent(PhotoUtils.createGalleryIntent(activity, fileResult.getResult()));
                        }
                    },
                    error -> {
                        //handle error
                        error.printStackTrace();
                        if (view != null)
                            view.togglePhotosClickable(true);
                    }));
        }
    }

    @Override
    public void activityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            Timber.d("Response from camera intent: RESULT CODE = " + resultCode + "\nDATA = " + data);
            if (model != null) {
                disposables.add(model.uploadPhoto(photoFile)
                        .doOnSubscribe(disposable -> showProgressDialog())
                        .doOnComplete(this::closeProgressDialog)
                        .subscribe(
                                imageUrlResult -> {
                                    if (view != null) {
                                        if (imageUrlResult.getResult() == null)
                                            view.setUploadProgress(imageUrlResult.getProgress());
                                        else {
                                            view.addNewPhoto(imageUrlResult.getResult());
                                            view.toggleBtn(true);
                                        }
                                    }
                                },
                                error -> {
                                    //handle error
                                    if (view != null) {
                                        view.closeProgressDialog();
                                        view.toggleBtn(true);
                                    }
                                }));
            }
            //getImage from data
        }

    }

    @Override
    public void permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = PermissionHelper.checkGranted(grantResults);
        switch (requestCode) {
            case Constants.REQUEST_PERMISSION_CAMERA:
                if (permissionGranted && view != null) {
                    createTempFile();
                    Timber.d("PERMISSION GRANTED");
                    view.sendCameraIntent(createCameraIntent());
                } else {
                    Timber.d("onRequestPermissionsResult: permission denied");
                }
                break;
        }
    }

    private void initData() {
        disposables.add(model.downloadImages().subscribe(imageListResult -> {
                    if (view != null)
                        view.initPhotos(imageListResult.getResult());
                },
                Throwable::printStackTrace));
    }

    private void closeProgressDialog() {
        if (view != null)
            view.closeProgressDialog();
    }

    private void showProgressDialog() {
        if (view != null)
            view.showProgressDialog();
    }

    private void createTempFile() {
        if (activity != null)
            photoFile = PhotoUtils.createImageFile(activity);
    }

    private Intent createCameraIntent() {
        if (activity != null && photoFile != null)
            return PhotoUtils.createCameraIntent(activity, photoFile);
        else
            return null;
    }
}
