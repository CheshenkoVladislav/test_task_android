package com.example.vlad.alphatest.di.modules.model;

import com.example.vlad.alphatest.di.CustomAnnotations;
import com.example.vlad.alphatest.managers.StorageManager;
import com.example.vlad.alphatest.repository.GalleryRepository;
import com.example.vlad.alphatest.interfaceses.model.AGalleryMvpModel;
import com.example.vlad.alphatest.model.*;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;

@Module
public class AGalleryModelModule {

    @Provides
    AGalleryMvpModel provideGalleryMvpModel(Scheduler postExecutionScheduler,
                                            @CustomAnnotations.FirebaseRepository GalleryRepository repository,
                                            StorageManager storageManager) {
        return new AGalleryModel(postExecutionScheduler, repository, storageManager);
    }
}
