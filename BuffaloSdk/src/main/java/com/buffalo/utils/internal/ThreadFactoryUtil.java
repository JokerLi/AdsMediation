package com.buffalo.utils.internal;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactoryUtil {
    public static ThreadFactory createNamedThreadFactory(@NonNull final String threadName) {
        return new ThreadFactory() {
            // 每次给线程一个不同的名字，方便跟踪
            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, threadName + " # " + mCount.getAndIncrement());
            }
        };
    }
}
