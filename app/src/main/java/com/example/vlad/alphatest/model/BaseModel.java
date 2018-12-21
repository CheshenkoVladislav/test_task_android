package com.example.vlad.alphatest.model;

import com.example.vlad.alphatest.data.RxResult;
import com.example.vlad.alphatest.interfaceses.threads.PostExecutionThread;
import com.example.vlad.alphatest.interfaceses.threads.ThreadExecutor;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

import io.reactivex.schedulers.Schedulers;

abstract class BaseModel {
    private ThreadExecutor threadExecutor;
    private PostExecutionThread postExecutionThread;
    private int timeoutSec = 20;


    BaseModel(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    <R extends RxResult> Observable<R> buildObservable(ObservableOnSubscribe<R> emitter) {
        return Observable.create(emitter).subscribeOn(postExecutionThread.getScheduler()).timeout(timeoutSec, TimeUnit.SECONDS);
    }

    <R> Observable<R> buildAsyncObservable(ObservableOnSubscribe<R> emitter) {
        return Observable.create(emitter).subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler()).timeout(timeoutSec, TimeUnit.SECONDS);
    }
}
