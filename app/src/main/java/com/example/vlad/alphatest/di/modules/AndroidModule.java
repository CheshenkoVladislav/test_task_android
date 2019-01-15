package com.example.vlad.alphatest.di.modules;

import android.content.Context;

import com.example.vlad.alphatest.app.Application;
import com.example.vlad.alphatest.threads.JobExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

@Module
public class AndroidModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    Scheduler provideExecutor(JobExecutor jobExecutor) {
        return Schedulers.from(jobExecutor);
    }
}
