package com.example.vlad.alphatest.presenter;

import android.content.Intent;
import android.os.Bundle;

import com.example.vlad.alphatest.interfaceses.model.MvpModel;
import com.example.vlad.alphatest.interfaceses.presenter.MvpPresenter;
import com.example.vlad.alphatest.interfaceses.view.MvpView;
import com.example.vlad.alphatest.support.Constants;
import com.example.vlad.alphatest.view.activity.BaseActivity;
import com.example.vlad.alphatest.view.fragment.BaseFragment;

import java.io.Serializable;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public abstract class BasePresenter<V extends MvpView, M extends MvpModel> implements MvpPresenter {
    protected BaseActivity activity;
    protected V view;
    protected M model;
    CompositeDisposable disposables = new CompositeDisposable();

    BasePresenter(V view, M model) {
        this.view = view;
        this.model = model;
        if (view != null) {
            initActivity(view);
        }
    }

    @Override
    public boolean init(Bundle savedInstanceState, Intent intent) {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
    }

    @Override
    public void attachView(MvpView view) {
        this.view = (V) view;
        initActivity(this.view);
    }

    @Override
    public void dettachView() {
        this.view = null;
    }

    @Override
    public void destroy() {
        disposables.dispose();
        disposables = new CompositeDisposable();
    }

    private void initActivity(V view) {
        if (view instanceof BaseActivity) {
            activity = (BaseActivity) view;
        } else if (view instanceof BaseFragment) {
            activity = (BaseActivity) ((BaseFragment) view).getActivity();
        }
    }
}
