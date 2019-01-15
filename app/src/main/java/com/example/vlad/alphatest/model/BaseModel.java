package com.example.vlad.alphatest.model;

import com.example.vlad.alphatest.threads.SubscriberManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

abstract class BaseModel extends SubscriberManager {

    private static final String NULL_OBSERVABLE_ERROR = "Observable is null";

    private Scheduler executeScheduler;
    private int timeoutSec = 4;

    BaseModel(Scheduler executeScheduler) {
        this.executeScheduler = executeScheduler;
    }

    <R> Observable<R> buildObservable(Observable<R> observable) {
        if (observable != null)
            return observable.timeout(timeoutSec, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread());
        else
            return createObservableError(new NullPointerException(NULL_OBSERVABLE_ERROR));
    }

    <R> Observable<R> buildObservable(ObservableOnSubscribe<R> emitter) {
        if (emitter != null)
            return Observable.create(emitter).timeout(timeoutSec, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread());
        else
            return createObservableError(new NullPointerException(NULL_OBSERVABLE_ERROR));

    }

    <R> Observable<R> buildAsyncObservable(Observable<R> observable) {
        if (observable != null)
            return observable.subscribeOn(executeScheduler).timeout(timeoutSec, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread());
        else
            return createObservableError(new NullPointerException(NULL_OBSERVABLE_ERROR));
    }

    <R> Observable<R> buildAsyncObservable(ObservableOnSubscribe<R> emitter) {
        if (emitter != null)
            return Observable.create(emitter).subscribeOn(executeScheduler).timeout(timeoutSec, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread());
        else
            return createObservableError(new NullPointerException(NULL_OBSERVABLE_ERROR));
    }

    private <R> Observable<R> createObservableError(Throwable throwable) {
        return Observable.error(throwable);
    }
}
