package com.example.vlad.alphatest.di.modules.view;

import com.example.vlad.alphatest.interfaceses.presenter.AGalleryMvpPresenter;
import com.example.vlad.alphatest.interfaceses.view.AGalleryMvpView;
import com.example.vlad.alphatest.view.activity.GalleryActivity;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AGalleryViewModule {

    @Binds
    abstract AGalleryMvpView provideGalleryMvpView(GalleryActivity activity);
}
