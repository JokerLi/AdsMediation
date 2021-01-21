package com.buffalo.utils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class BackgroundThread {
    //用作IO线程，异步读写文件
    private static Handler sIOHandler = null;
    private static HandlerThread sIOHandlerThread;

    private static void ensureIOThread() {
        if (null == sIOHandlerThread) {
            sIOHandlerThread = new HandlerThread("IOThread");
            sIOHandlerThread.start();
        }
        if (sIOHandler == null) {
            sIOHandler = new Handler(sIOHandlerThread.getLooper());
        }
    }

    static class RunnableWrapper implements Runnable {

        private Runnable runnable;

        public RunnableWrapper(Runnable r) {
            this.runnable = r;
        }

        @Override
        public void run() {
            if (runnable != null) {
                long startTime = System.currentTimeMillis();
                Logger.i("IOThread task run start");
                runnable.run();
                long endTime = System.currentTimeMillis();
                Logger.i("IOThread task run end");
                if (endTime - startTime >= 200) {
                    Logger.e("IOThread task spent exceed 200 millis");
                }
            }
        }
    }

    public static void postOnIOThread(Runnable runnable) {
        ensureIOThread();
        sIOHandler.post(new RunnableWrapper(runnable));
    }

    public static void postOnIOThreadDelay(final Runnable runnable, long delayMillis) {
        ensureIOThread();
        sIOHandler.postDelayed(new RunnableWrapper(runnable), delayMillis);
    }


    public static void revokeOnIOThread(final Runnable runnable) {
        ensureIOThread();
        sIOHandler.removeCallbacks(runnable);
    }

    public static void runOnIOThread(Runnable r) {
        ensureIOThread();
        RunnableWrapper runnableWrapper = new RunnableWrapper(r);
        if (isOnIOThread()) {
            runnableWrapper.run();
        } else {
            sIOHandler.post(runnableWrapper);
        }
    }

    public static <T> T runOnIOThreadBlocking(Callable<T> c) {
        FutureTask<T> task = new FutureTask<T>(c);
        runOnIOThread(task);
        try {
            return task.get();
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isOnIOThread() {
        ensureIOThread();
        return sIOHandler.getLooper() == Looper.myLooper();
    }

    public static <T> void executeAsyncTask(final AsyncTask<T, ?, ?> task,
                                            final T... params) {
        ThreadHelper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
                    } else {
                        task.execute(params);
                    }
                } catch (Throwable e) {
                }
            }
        });
    }

    private BackgroundThread() {
    }
}
