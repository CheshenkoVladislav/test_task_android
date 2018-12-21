package com.example.vlad.alphatest.di.modules;

import android.content.Context;

import com.example.vlad.alphatest.app.Application;
import com.example.vlad.alphatest.interfaceses.threads.PostExecutionThread;
import com.example.vlad.alphatest.interfaceses.threads.ThreadExecutor;
import com.example.vlad.alphatest.threads.JobExecutor;
import com.example.vlad.alphatest.threads.UIThread;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    ThreadExecutor provideExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }
}
