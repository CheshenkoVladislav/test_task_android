package com.example.vlad.alphatest.di.modules.main;

import android.content.Context;

import com.example.vlad.alphatest.interfaceses.model.AGalleryMvpModel;
import com.example.vlad.alphatest.interfaceses.presenter.AGalleryMvpPresenter;
import com.example.vlad.alphatest.interfaceses.view.AGalleryMvpView;
import com.example.vlad.alphatest.presenter.AGalleryPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class AGalleryModule {

    @Provides
    AGalleryMvpPresenter provideAGalleryPresenter(Context context, AGalleryMvpView view, AGalleryMvpModel model) {
        return new AGalleryPresenter(context, view, model);
    }
}
