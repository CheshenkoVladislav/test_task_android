package com.example.vlad.alphatest.di.component;

import com.example.vlad.alphatest.app.Application;
import com.example.vlad.alphatest.di.modules.AndroidModule;
import com.example.vlad.alphatest.di.modules.FirebaseModule;
import com.example.vlad.alphatest.di.modules.ManagersModule;
import com.example.vlad.alphatest.di.modules.NetworkModule;
import com.example.vlad.alphatest.di.modules.RepositoryModule;
import com.example.vlad.alphatest.di.modules.builders.MainBuilderModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidModule.class,
        MainBuilderModule.class,
        ManagersModule.class,
        NetworkModule.class,
        RepositoryModule.class,
        FirebaseModule.class,
        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class})
public interface AppComponent {

    void inject(Application application);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder app(Application application);

        AppComponent build();
    }
}
