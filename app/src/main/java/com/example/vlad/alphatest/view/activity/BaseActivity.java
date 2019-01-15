package com.example.vlad.alphatest.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.vlad.alphatest.interfaceses.presenter.MvpPresenter;
import com.example.vlad.alphatest.interfaceses.view.MvpView;
import com.example.vlad.alphatest.support.Constants;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.Lazy;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public abstract class BaseActivity<P extends MvpPresenter> extends AppCompatActivity
        implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;
    Unbinder unbinder;

    @Inject
    Lazy<P> lazyInjectPresrnter;

    P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        Timber.d("Create");
        unbinder = ButterKnife.bind(this);
        injectPresenter();
        presenter.attachView((MvpView) this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("Resume");
        presenter.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.d("Stop");
        presenter.stop();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("Destroy");
        if (isFinishing())
            presenter.destroy();
        presenter.dettachView();
        unbinder.unbind();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        sendResult(requestCode, data);
    }

    private void sendResult(int requestCode, @Nullable Intent data) {
        if (getSupportFragmentManager() != null)
            for (Fragment f : getSupportFragmentManager().getFragments()) {
                f.onActivityResult(requestCode, requestCode, data);
            }
    }

    private void injectPresenter() {
        if (getLastNonConfigurationInstance() != null)
            presenter = (P) getLastCustomNonConfigurationInstance();
        else
            presenter = lazyInjectPresrnter.get();
    }
}
