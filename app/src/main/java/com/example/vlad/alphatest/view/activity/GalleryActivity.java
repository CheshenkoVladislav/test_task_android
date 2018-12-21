package com.example.vlad.alphatest.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vlad.alphatest.R;
import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.interfaceses.listeners.OnImageClickListener;
import com.example.vlad.alphatest.interfaceses.presenter.AGalleryMvpPresenter;
import com.example.vlad.alphatest.interfaceses.view.AGalleryMvpView;
import com.example.vlad.alphatest.support.Constants;
import com.example.vlad.alphatest.view.adapter.GalleryAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public class GalleryActivity extends BaseActivity<AGalleryMvpPresenter> implements AGalleryMvpView,
        OnImageClickListener{

    private GalleryAdapter adapter;
    private AlertDialog progressDialog;

    @BindView(R.id.parent_view)
    ViewGroup parentView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.get_photo_btn)
    Button getPhoto;

    @OnClick(R.id.get_photo_btn)
    public void onGetPhotoClick() {
        presenter.requestToAddPhoto();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_gallery);
        super.onCreate(savedInstanceState);
        presenter.init();
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
        presenter.permissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void initPhotos(List<Image> images) {
        adapter.replaceData(images);
    }

    @Override
    public void initRecyclerView(List<Image> imageList) {
        adapter = new GalleryAdapter(imageList, this);
        recyclerView.setAdapter(adapter);
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
        adapter.setClickable(clickable);
    }

    @Override
    public void showProgressDialog() {
       progressDialog = new AlertDialog.Builder(this).setView(R.layout.progress_dialog_layout)
                .setTitle("Loading...")
                .setCancelable(false)
                .create();
        progressDialog.show();
    }

    @Override
    public void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void setListeners() {
    }

    @Override
    public void setProgress(long progress) {

    }

    @Override
    public void sendCameraIntent(Intent cameraIntent) {
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void sendGalleryIntent(Intent intent) {
        startActivity(intent);
    }
}
