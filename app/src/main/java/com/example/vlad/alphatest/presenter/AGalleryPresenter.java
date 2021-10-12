package com.example.vlad.alphatest.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.interfaceses.model.AGalleryMvpModel;
import com.example.vlad.alphatest.interfaceses.presenter.AGalleryMvpPresenter;
import com.example.vlad.alphatest.interfaceses.view.AGalleryMvpView;
import com.example.vlad.alphatest.support.Constants;
import com.example.vlad.alphatest.support.PhotoUtils;

import java.io.File;
import java.util.List;

import io.reactivex.functions.Action;
import timber.log.Timber;

import static com.example.vlad.alphatest.support.Constants.CAMERA_REQUEST_CODE;

public class AGalleryPresenter extends BasePresenter<AGalleryMvpView, AGalleryMvpModel>
        implements AGalleryMvpPresenter, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {

    private boolean inited;
    private boolean loading;
    private long loadingProgress;
    private File photoFile;

    private int currentError;
    private static final int NO_ERROR_STATUS = 0;
    private static final int CONNECTION_ERROR = 1;
    private static final int IMAGE_UPLOAD_ERROR = 2;

    public AGalleryPresenter(Context context, AGalleryMvpView view, AGalleryMvpModel model) {
        super(context, view, model);
    }

    @Override
    public boolean init(Bundle savedInstanceState, Intent intent) {
        super.init(savedInstanceState, intent);
        Timber.d("Gallery init");
        if (this.view != null && model != null) {
            initData();
            return inited;
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        if (view != null) {
            view.togglePhotosClickable(true);
            view.toggleBtn(true);
        }
    }

    @Override
    public void dettachView() {
        clearAlertDialogListeners();
        dismissAlertDialog();
        super.dettachView();
    }

    @Override
    public void requestToReinit() {
        currentError = NO_ERROR_STATUS;
        initData();
    }

    @Override
    public void requestToUploadAgain() {
        currentError = NO_ERROR_STATUS;
        dismissAlertDialog();
        uploadPhoto(photoFile);
    }

    @Override
    public void requestToCancelUpload() {
        currentError = NO_ERROR_STATUS;
        dismissAlertDialog();
    }

    @Override
    public void requestToCheckPermission() {
        if (view != null) {
            view.toggleBtn(false);
            view.checkCameraPermissions();
        }
    }

    @Override
    public void requestToAddPhoto(boolean permissionGranted) {
        if (view != null) {
            if (permissionGranted) {
                Timber.d("PERMISSION GRANTED");
                createTempFile();
                view.startCamera(photoFile);
            } else {
                Timber.d("PERMISSION DENIED");
                view.toggleBtn(true);
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
                            view.startGallery(fileResult.getResult());
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
            uploadPhoto(photoFile);
        }
    }

    @Override
    public void permissionResult(int requestCode, boolean permissionGranted) {
        switch (requestCode) {
            case Constants.REQUEST_PERMISSION_CAMERA:
                if (view != null) {
                    if (permissionGranted) {
                        Timber.d("PERMISSION GRANTED");
                        createTempFile();
                        view.startCamera(photoFile);
                    } else {
                        Timber.d("PERMISSION DENIED");
                        view.toggleBtn(true);
                    }
                    break;
                }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        loading = false;
        currentError = NO_ERROR_STATUS;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (currentError != 0) {
            showErrorDialog();
        }
    }

    private void initData() {
        if (!inited && currentError == NO_ERROR_STATUS)
            disposables.add(model.getImageList()
                    .doOnSubscribe(disposable -> showProgressDialog())
                    .doOnComplete(() -> {
                        inited = true;
                        loading = false;
                        currentError = NO_ERROR_STATUS;
                        dismissAlertDialog();
                    })
                    .subscribe(imageList -> {
                                Timber.d("IMAGE LIST GET: ");
                                initRecyclerView(imageList);
                            },
                            error -> {
                                Timber.d(error);
                                loading = false;
                                currentError = CONNECTION_ERROR;
                                dismissAlertDialog();
                            }));
        else if (model != null && view != null) {
            initRecyclerView(model.getCachedImageList());
            if (model.inPause()) {
                view.showProgressAlertDialog();
                model.resumeUpload();
            } else if (loading) {
                view.showProgressAlertDialog();
            } else if (currentError != NO_ERROR_STATUS)
                showErrorDialog();
        }
    }

    private void uploadPhoto(File photoFile) {
        if (model != null) {
            disposables.add(model.uploadPhoto(photoFile)
                    .doOnSubscribe(disposable -> showProgressDialog())
                    .subscribe(
                            progressResult -> {
                                if (progressResult.getResult() == null && view != null) {
                                    loadingProgress = progressResult.getProgress();
                                    view.setUploadProgress(progressResult.getProgress());
                                } else if (model != null) {
                                    writeReferenceToDB(() -> {
                                        loading = false;
                                        if (view != null) {
                                            currentError = NO_ERROR_STATUS;
                                            view.dismissAlertDialog();
                                            view.addNewPhoto(progressResult.getResult());
                                            view.toggleBtn(true);
                                        }
                                    }, progressResult.getResult());
                                }
                            },
                            error -> {
                                //handle error
                                Timber.d(error);
                                loading = false;
                                if (view != null) {
                                    currentError = IMAGE_UPLOAD_ERROR;
                                    view.dismissAlertDialog();
                                    view.toggleBtn(true);
                                }
                            }));
        }
    }

    private void writeReferenceToDB(Action completeAction, Image image) {
        disposables.add(model.writeUrlToDatabase(image).subscribe(vVoid -> {/*void*/},
                        error -> {
                            loading = false;
                            currentError = IMAGE_UPLOAD_ERROR;
                            Timber.d(error);
                            if (view != null) {
                                view.dismissAlertDialog();
                                view.toggleBtn(true);
                            }
                        },
                        completeAction
                ));
    }


    private void initRecyclerView(List<Image> imageList) {
        if (view != null)
            view.initRecyclerView(imageList);
    }

    private void dismissAlertDialog() {
        if (view != null)
            view.dismissAlertDialog();
    }

    private void clearAlertDialogListeners() {
        if (view != null)
            view.clearAlertDialogListeners();
    }

    private void showProgressDialog() {
        loading = true;
        if (view != null)
            view.showProgressAlertDialog();
    }

    private void showErrorDialog() {
        if (view != null)
            switch (currentError) {
                case CONNECTION_ERROR:
                    loading = false;
                    view.showConnectionErrorAlertDialog();
                    break;
                case IMAGE_UPLOAD_ERROR:
                    loading = false;
                    view.showImageUploadErrorAlertDialog();
                    break;
            }
    }

    private void createTempFile() {
        if (context != null)
            photoFile = PhotoUtils.createImageFile(context);
    }
}
