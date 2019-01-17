package com.example.vlad.alphatest.presenter;


import android.content.Intent;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.data.ProgressResult;
import com.example.vlad.alphatest.interfaceses.model.AGalleryMvpModel;
import com.example.vlad.alphatest.interfaceses.view.AGalleryMvpView;
import com.example.vlad.alphatest.support.Constants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class AGalleryTestPresenter {

    @Mock
    private AGalleryMvpModel mockModel;
    @Mock
    private AGalleryMvpView mockView;

    private AGalleryPresenter presenter;

    private ProgressResult<Image> imageRxResult;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new AGalleryPresenter(RuntimeEnvironment.application.getApplicationContext(), mockView, mockModel);
    }

    @Test
    public void init() {
        List<Image> imageList = new ArrayList<>();
        imageList.add(new Image());
        doReturn(Observable.just(imageList)).when(mockModel).getImageList();
        Assert.assertTrue(presenter.init(new Bundle(), new Intent()));
        verify(mockView).showProgressAlertDialog();
        verify(mockView).initRecyclerView(imageList);
        verify(mockView).dismissAlertDialog();
    }

    @Test
    public void activityResultWithError() {
        doReturn(Observable.error(new Throwable())).when(mockModel).uploadPhoto(Mockito.any());
        presenter.activityResult(Constants.CAMERA_REQUEST_CODE, -1, Mockito.any());
        verify(mockView).dismissAlertDialog();
        verify(mockView).toggleBtn(true);
    }

    @Test
    public void activityResultWithProgressTest() {
        imageRxResult = new ProgressResult<>(null, 50);
        doReturn(Observable.just(imageRxResult)).when(mockModel).uploadPhoto(Mockito.any());
        presenter.activityResult(Constants.CAMERA_REQUEST_CODE, -1, Mockito.any());
        verify(mockView).showProgressAlertDialog();
        verify(mockView).setUploadProgress(Mockito.anyLong());
    }

    @Test
    public void activityResultWithImageTest() {
        Image image = new Image();
        imageRxResult = new ProgressResult<>(image, 0);
        doReturn(Observable.just(imageRxResult)).when(mockModel).uploadPhoto(Mockito.any());
        doReturn(Completable.complete().toObservable()).when(mockModel).writeUrlToDatabase(image);
        presenter.activityResult(Constants.CAMERA_REQUEST_CODE, -1, Mockito.any());
        verify(mockView).addNewPhoto(Mockito.any());
        verify(mockView).toggleBtn(Mockito.anyBoolean());
    }

    @Test
    public void havePermissionResult() {
        presenter.permissionResult(Constants.REQUEST_PERMISSION_CAMERA, true);
        verify(mockView).startCamera(Mockito.any());
    }

    @Test
    public void haveNotPermissionResult() {
        presenter.permissionResult(Constants.REQUEST_PERMISSION_CAMERA, false);
        verify(mockView).toggleBtn(true);
    }

    @Test
    public void openPhotoViewer() {
        ProgressResult<File> fileResult = new ProgressResult<>(new File("2"), 0);
        doReturn(Observable.just(fileResult)).when(mockModel).savePhotoToInternalStorage(Mockito.any());
        presenter.requestToOpenPhotoViewer(new DrawableContainer());
        verify(mockView).startGallery(Mockito.any());
    }

    @Test
    public void openPhotoViewerError() {
        doReturn(Observable.error(new Throwable())).when(mockModel).savePhotoToInternalStorage(Mockito.any());
        presenter.requestToOpenPhotoViewer(new DrawableContainer());
        verify(mockView).togglePhotosClickable(true);
    }

    @Test
    public void checkPermission() {
        presenter.requestToCheckPermission();
        verify(mockView).toggleBtn(false);
        verify(mockView).checkCameraPermissions();
    }

    @Test
    public void addPhotoWithGrantedPermissions() {
        presenter.requestToAddPhoto(true);
        verify(mockView).startCamera(Mockito.any());
    }

    @Test
    public void addPhotoWithDeniedPermission() {
        presenter.requestToAddPhoto(false);
        verify(mockView).toggleBtn(true);
    }

    @Test
    public void cancelUpload() {
        presenter.requestToCancelUpload();
        verify(mockView).dismissAlertDialog();
    }

    @Test
    public void uploadAgainInProgress() {
        String url = "image url";
        String id = "1";
        Image image = new Image(id, url);
        ProgressResult<Image> progressResult = new ProgressResult<>(null, 50);
        doReturn(Observable.just(progressResult)).when(mockModel).uploadPhoto(Mockito.any());

        presenter.requestToUploadAgain();
        verify(mockView).dismissAlertDialog();
        verify(mockView).showProgressAlertDialog();
        verify(mockView).setUploadProgress(50);
    }

    @Test
    public void uploadAgainError() {
        doReturn(Observable.error(new Throwable())).when(mockModel).uploadPhoto(Mockito.any());

        presenter.requestToUploadAgain();
        verify(mockView, atLeast(2)).dismissAlertDialog();
        verify(mockView).toggleBtn(true);
    }

    @Test
    public void uploadAgain() {
        Image image = createImage(null, null);
        ProgressResult<Image> progressResult = createProgressResult(image, 0);
        doReturn(Observable.just(progressResult)).when(mockModel).uploadPhoto(Mockito.any());

        presenter.requestToUploadAgain();
        verify(mockView, atLeast(2)).dismissAlertDialog();
        verify(mockModel).writeUrlToDatabase(Mockito.any());
    }

    @Test
    public void writeUrlToDatabaseAfterUploadImage() {
        Image image = createImage(null, null);
        ProgressResult<Image> progressResult = createProgressResult(image, 0);
        doReturn(Observable.just(progressResult)).when(mockModel).uploadPhoto(Mockito.any());
        doReturn(Completable.complete().toObservable()).when(mockModel).writeUrlToDatabase(Mockito.any());

        presenter.requestToUploadAgain();
        verify(mockView, atLeast(2)).dismissAlertDialog();
        verify(mockView).addNewPhoto(image);
        verify(mockView).toggleBtn(true);
    }

    @Test
    public void writeUrlToDatabaseAfterUploadImageError() {
        Image image = createImage(null, null);
        ProgressResult<Image> progressResult = createProgressResult(image, 0);
        doReturn(Observable.just(progressResult)).when(mockModel).uploadPhoto(Mockito.any());
        doReturn(Observable.error(new Throwable())).when(mockModel).writeUrlToDatabase(Mockito.any());

        presenter.requestToUploadAgain();
        verify(mockView, atLeast(2)).dismissAlertDialog();
        verify(mockView).toggleBtn(true);

    }

    private <T> ProgressResult<T> createProgressResult(T resultObject, int progress) {
        T object = resultObject;
        return new ProgressResult<>(object, progress);
    }

    private Image createImage(String id, String url) {
        if (id == null || url == null) {
            return new Image("1", "image url");
        }
        return new Image(id, url);
    }
}
