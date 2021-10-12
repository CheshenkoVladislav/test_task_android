package com.example.vlad.alphatest.threads;

import androidx.annotation.NonNull;

import com.example.vlad.alphatest.data.ProgressResult;
import io.reactivex.ObservableEmitter;

public class SubscriberManager {

    protected void sendOnNext(ObservableEmitter subscriber, ProgressResult result) {
        if (!subscriber.isDisposed()) {
            subscriber.onNext(result);
        }
    }

    protected void sendOnComplete(ObservableEmitter subscriber, ProgressResult result) {
        if (!subscriber.isDisposed()) {
            subscriber.onNext(result);
            subscriber.onComplete();
        }
    }

    protected void sendOnError(ObservableEmitter subscriber, @NonNull Throwable error) {
        if (!subscriber.isDisposed())
            subscriber.onError(error);
    }
}
