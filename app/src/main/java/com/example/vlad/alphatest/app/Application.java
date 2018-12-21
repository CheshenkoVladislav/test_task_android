package com.example.vlad.alphatest.app;

import android.app.Activity;

import com.example.vlad.alphatest.BuildConfig;
import com.example.vlad.alphatest.di.component.AppComponent;
import com.example.vlad.alphatest.di.component.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import timber.log.Timber;

public class Application extends android.app.Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        getComponent().inject(this);
        initTimber();
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

    public AppComponent getComponent() {
        if (component == null) {
            component = DaggerAppComponent.builder()
                    .app(this)
                    .build();
        }
        return component;
    }

    private void initTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
    }
}
