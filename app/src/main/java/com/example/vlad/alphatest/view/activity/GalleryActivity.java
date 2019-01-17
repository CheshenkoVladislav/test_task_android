package com.example.vlad.alphatest.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vlad.alphatest.R;
import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.interfaceses.listeners.OnImageClickListener;
import com.example.vlad.alphatest.interfaceses.presenter.AGalleryMvpPresenter;
import com.example.vlad.alphatest.interfaceses.view.AGalleryMvpView;
import com.example.vlad.alphatest.support.Constants;
import com.example.vlad.alphatest.support.IntentUtils;
import com.example.vlad.alphatest.support.PermissionHelper;
import com.example.vlad.alphatest.view.adapter.GalleryAdapter;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public class GalleryActivity extends BaseActivity<AGalleryMvpPresenter> implements AGalleryMvpView,
        OnImageClickListener {

    private GalleryAdapter adapter;
    private AlertDialog alertDialog;
    private AlertDialog errorDialog;

    @BindView(R.id.parent_view)
    ViewGroup parentView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.get_photo_btn)
    Button getPhoto;

    @OnClick(R.id.get_photo_btn)
    public void onTakePhotoClick() {
        presenter.requestToCheckPermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_gallery);
        super.onCreate(savedInstanceState);
        presenter.init(savedInstanceState, getIntent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            presenter.activityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionGranted = PermissionHelper.checkGranted(grantResults);
        presenter.permissionResult(requestCode, permissionGranted);
    }

    @Override
    public void replaceImageList(List<Image> images) {
    }

    @Override
    public void initRecyclerView(List<Image> imageList) {
        Timber.d("INIT RECYCLER VIEW, SIZE: = %s", imageList.size());
        adapter = new GalleryAdapter(imageList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void checkCameraPermissions() {
        presenter.requestToAddPhoto(PermissionHelper.mayRequestCamera(this));
    }

    @Override
    public void startCamera(File file) {
        Intent cameraIntent = IntentUtils.createCameraIntent(this, file);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void startGallery(File file) {
        Intent galleryIntent = IntentUtils.createGalleryIntent(this, file);
        startActivity(galleryIntent);
    }

    @Override
    public void onImageClick(Drawable imageDrawable) {
        presenter.requestToOpenPhotoViewer(imageDrawable);
    }

    @Override
    public void addNewPhoto(Image image) {
        adapter.addPhoto(image);
        Timber.d("New Photo Added %s", image.getUrl());
    }

    @Override
    public void setUploadProgress(long progress) {
        Timber.d("UPLOAD PROGRESS: %s", progress);
    }

    @Override
    public void toggleBtn(boolean enable) {
        getPhoto.setEnabled(enable);
    }

    @Override
    public void togglePhotosClickable(boolean clickable) {
        if (adapter != null)
            adapter.setClickable(clickable);
    }

    @Override
    public void showProgressAlertDialog() {
        alertDialog = new AlertDialog.Builder(this).setView(R.layout.progress_dialog_layout)
                .setTitle("Loading...")
                .setCancelable(false)
                .setOnDismissListener((DialogInterface.OnDismissListener) presenter)
                .create();
        alertDialog.show();
    }

    @Override
    public void showConnectionErrorAlertDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Ошибка сети")
                .setMessage("Проверьте подключение")
                .setPositiveButton("Обновить", (dialog, which) -> presenter.requestToReinit())
                .setOnDismissListener((DialogInterface.OnDismissListener) presenter)
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

    @Override
    public void showImageUploadErrorAlertDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Ошибка загрузки")
                .setMessage("При загрузке фото произошла ошибка")
                .setPositiveButton("загрузить снова", (dialog, which) -> presenter.requestToUploadAgain())
                .setNegativeButton("Отменить", (dialog, which) -> presenter.requestToCancelUpload())
                .setOnDismissListener((DialogInterface.OnDismissListener) presenter)
                .setOnCancelListener((DialogInterface.OnCancelListener) presenter)
                .create();
        alertDialog.show();
    }

    @Override
    public void dismissAlertDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void clearAlertDialogListeners() {
        if (alertDialog != null) {
            alertDialog.setOnCancelListener(null);
            alertDialog.setOnDismissListener(null);
        }
    }

    @Override
    public void setListeners() {

    }
}
