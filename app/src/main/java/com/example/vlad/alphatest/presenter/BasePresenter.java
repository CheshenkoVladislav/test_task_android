package com.example.vlad.alphatest.presenter;

import com.example.vlad.alphatest.interfaceses.model.MvpModel;
import com.example.vlad.alphatest.interfaceses.presenter.MvpPresenter;
import com.example.vlad.alphatest.interfaceses.view.MvpView;
import com.example.vlad.alphatest.view.activity.BaseActivity;
import com.example.vlad.alphatest.view.fragment.BaseFragment;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V extends MvpView, M extends MvpModel> implements MvpPresenter {
    protected V view;
    protected M model;
    protected BaseActivity activity;
    protected CompositeDisposable disposables = new CompositeDisposable();

    BasePresenter(V view, M model) {
        this.view = view;
        this.model = model;
        if (view instanceof BaseActivity) {
            activity = (BaseActivity)view;
        }
        else if (view instanceof BaseFragment) {
            activity = (BaseActivity) ((BaseFragment) view).getActivity();
        }
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
        disposables.dispose();
        disposables = new CompositeDisposable();
    }
}
