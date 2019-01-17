package com.example.vlad.alphatest.presenter;

import android.content.Context;
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
    protected Context context;
    protected V view;
    protected M model;
    CompositeDisposable disposables = new CompositeDisposable();

    BasePresenter(Context context, V view, M model) {
        this.view = view;
        this.model = model;
        this.context = context;
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
}
