package com.example.vlad.alphatest.di.modules.main;

import com.example.vlad.alphatest.interfaceses.model.AGalleryMvpModel;
import com.example.vlad.alphatest.interfaceses.presenter.AGalleryMvpPresenter;
import com.example.vlad.alphatest.interfaceses.view.AGalleryMvpView;
import com.example.vlad.alphatest.presenter.AGalleryPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class AGalleryModule {

    @Provides
    AGalleryMvpPresenter provideAGalleryPresenter(AGalleryMvpView view, AGalleryMvpModel model) {
        return new AGalleryPresenter(view, model);
    }
}
