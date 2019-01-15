package com.example.vlad.alphatest.presenter;


import com.example.vlad.alphatest.RobolectricCustomRunner;
import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.data.ProgressResult;
import com.example.vlad.alphatest.interfaceses.model.AGalleryMvpModel;
import com.example.vlad.alphatest.interfaceses.view.AGalleryMvpView;
import com.example.vlad.alphatest.support.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.reactivex.Completable;
import io.reactivex.Observable;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricCustomRunner.class)
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
        presenter = new AGalleryPresenter(mockView, mockModel);
    }

    @Test
    public void activityResultWithError() {
        doReturn(Observable.error(new Throwable())).when(mockModel).uploadPhoto(Mockito.any());
        presenter.activityResult(Constants.CAMERA_REQUEST_CODE, -1, Mockito.any());
        verify(mockView).dismissAlertDialog();
        verify(mockView).toggleBtn(Mockito.anyBoolean());
    }

    @Test
    public void activityResultWithProgressTest() {
        imageRxResult = new ProgressResult<>(null, 50);
        doReturn(Observable.just(imageRxResult)).when(mockModel).uploadPhoto(Mockito.any());
        presenter.activityResult(Constants.CAMERA_REQUEST_CODE, -1, Mockito.any());
        verify(mockView).setUploadProgress(Mockito.anyLong());
    }

    @Test
    public void activityResultWithImageTest() {
        Image image = new Image();
        imageRxResult = new ProgressResult<>(image, 0);
        doReturn(Observable.just(imageRxResult)).when(mockModel).uploadPhoto(Mockito.any());
        doReturn(Completable.complete()).when(mockModel).writeUrlToDatabase(image);
        presenter.activityResult(Constants.CAMERA_REQUEST_CODE, -1, Mockito.any());
        verify(mockView).addNewPhoto(Mockito.any());
        verify(mockView).toggleBtn(Mockito.anyBoolean());
    }
}
