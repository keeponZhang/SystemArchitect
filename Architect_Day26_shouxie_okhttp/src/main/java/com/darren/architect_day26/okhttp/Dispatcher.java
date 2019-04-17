package com.darren.architect_day26.okhttp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hcDarren on 2017/11/18.
 * 线程池处理
 */

public class Dispatcher {
    private @Nullable
    ExecutorService executorService;

    public synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    Thread thread = new Thread(r,"OkHttp");
                    thread.setDaemon(false);
                    return thread;
                }
            });
        }
        return executorService;
    }

    public void enqueue(RealCall.AsyncCall call) {
        executorService().execute(call);
    }
}
