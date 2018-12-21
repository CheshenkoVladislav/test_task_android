package com.example.vlad.alphatest.di.modules.builders;

import com.example.vlad.alphatest.di.modules.main.AGalleryModule;
import com.example.vlad.alphatest.di.modules.model.AGalleryModelModule;
import com.example.vlad.alphatest.di.modules.view.AGalleryViewModule;
import com.example.vlad.alphatest.view.activity.GalleryActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainBuilderModule {
//    activities
    @ContributesAndroidInjector(modules = {AGalleryViewModule.class, AGalleryModelModule.class, AGalleryModule.class})
    abstract GalleryActivity bindGalleryActivity();
}
