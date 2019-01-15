package com.example.vlad.alphatest.interfaceses.presenter;

import android.content.Intent;
import android.os.Bundle;

import com.example.vlad.alphatest.interfaceses.view.MvpView;

public interface MvpPresenter {
    boolean init(Bundle savedInstanceState, Intent intent);
    void attachView(MvpView view);
    void dettachView();
    void start();
    void stop();
    void destroy();
}
