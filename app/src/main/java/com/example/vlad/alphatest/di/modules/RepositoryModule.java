package com.example.vlad.alphatest.di.modules;

import com.example.vlad.alphatest.di.CustomAnnotations;
import com.example.vlad.alphatest.interfaceses.api.GalleryApi;
import com.example.vlad.alphatest.repository.GalleryFirebaseRepository;
import com.example.vlad.alphatest.repository.GalleryRepository;
import com.example.vlad.alphatest.repository.GalleryRestRepository;
import com.example.vlad.alphatest.threads.JobExecutor;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @CustomAnnotations.FirebaseRepository
    @Provides
    GalleryRepository provideGalleryRepository(JobExecutor executor, FirebaseStorage firebaseStorage, FirebaseDatabase firebaseDatabase) {
        return new GalleryFirebaseRepository(executor, firebaseStorage, firebaseDatabase);
    }

    @CustomAnnotations.RestRepository
    @Provides
    GalleryRepository provideGalleryRestRepository(GalleryApi api) {
        return new GalleryRestRepository(api);
    }
}
