package com.example.vlad.alphatest.di.modules.model;

import com.example.vlad.alphatest.interfaceses.model.AGalleryMvpModel;
import com.example.vlad.alphatest.interfaceses.threads.PostExecutionThread;
import com.example.vlad.alphatest.interfaceses.threads.ThreadExecutor;
import com.example.vlad.alphatest.managers.StorageManager;
import com.example.vlad.alphatest.model.*;

import dagger.Module;
import dagger.Provides;

@Module
public class AGalleryModelModule {

    @Provides
    AGalleryMvpModel provideGalleryMvpModel(ThreadExecutor threadExecutor,
                                            PostExecutionThread postExecutionThread,
                                            StorageManager storageManager) {
        return new AGalleryModel(threadExecutor, postExecutionThread, storageManager);
    }
}
