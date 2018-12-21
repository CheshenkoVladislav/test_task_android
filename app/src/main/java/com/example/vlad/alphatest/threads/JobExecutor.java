package com.example.vlad.alphatest.threads;

import android.support.annotation.NonNull;

import com.example.vlad.alphatest.interfaceses.threads.ThreadExecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class JobExecutor implements ThreadExecutor {
    final ThreadPoolExecutor threadPoolExecutor;

    @Inject
    public JobExecutor() {
        this.threadPoolExecutor = new ThreadPoolExecutor(3, 5, 10,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new JobThreadFactory());
    }

    @Override
    public void execute(@NonNull  Runnable command) {
        this.threadPoolExecutor.execute(command);
    }

    private static class JobThreadFactory implements ThreadFactory {
        private int counter = 0;

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "android_" + counter++);
        }
    }
}
