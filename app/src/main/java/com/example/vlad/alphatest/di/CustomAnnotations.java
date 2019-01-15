package com.example.vlad.alphatest.di;

import javax.inject.Qualifier;

public interface CustomAnnotations {

    @Qualifier
    @interface RestRepository {

    }

    @Qualifier
    @interface FirebaseRepository {

    }
}
