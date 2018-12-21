package com.example.vlad.alphatest.interfaceses.threads;

import io.reactivex.Scheduler;

public interface PostExecutionThread {
    Scheduler getScheduler();
}
