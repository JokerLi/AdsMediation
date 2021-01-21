package com.buffalo.utils;

import android.content.Context;
import android.os.Handler;

import com.buffalo.adsdk.AdManager;

import java.util.concurrent.Callable;

public class ViewCheckHelper {
    private static final String TAG = "ViewCheckHelper";
    private int mScheduleCheckViewTime;
    private Context mContext;
    public Handler mHandler;
    private boolean mImpressionRetryScheduled;
    private Callable<Boolean> mCheckTask;

    public ViewCheckHelper(Context context, Callable<Boolean> checkTask) {
        this.mContext = context.getApplicationContext();
        this.mImpressionRetryScheduled = true;
        mScheduleCheckViewTime = AdManager.getPegasusReportViewCheckIntervalMillisecond();
        mHandler = new Handler();
        mCheckTask = checkTask;
        ReceiverUtils.addViewCheckHelperObj(this);
    }

    public void startWork() {
        Logger.i(TAG, "start check view");
        if (Commons.isScreenOn(mContext)) {
            try {
                Boolean isNeedStop = mCheckTask.call();
                if (isNeedStop) {
                    stopWork("first check over");
                } else {
                    mHandler.postDelayed(sendImpressionRunnable, mScheduleCheckViewTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Logger.i(TAG, "lock screen,cancel schedule check view");
            cancelImpressionRetry();
        }
    }

    public void stopWork(String reason) {
        Logger.i(TAG, "stop check view: " + reason);
        cancelImpressionRetry();
        ReceiverUtils.removeViewCheckHelperObj();
    }

    private Runnable sendImpressionRunnable = new Runnable() {
        public void run() {
            if (mImpressionRetryScheduled) {
                try {
                    Boolean isNeedStop = mCheckTask.call();
                    if (isNeedStop) {
                        stopWork("check over");
                    } else {
                        if (mHandler != null) {
                            mHandler.postDelayed(this, mScheduleCheckViewTime);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };

    // FIXME: 在list 这种情况下, 性能可能会有问题
    public synchronized void scheduleImpressionRetry() {
        Logger.i(TAG, "scheduleImpressionRetry");
        if (!mImpressionRetryScheduled) {
            mImpressionRetryScheduled = true;
            mHandler.postDelayed(sendImpressionRunnable, mScheduleCheckViewTime);
        }
    }

    public synchronized void cancelImpressionRetry() {
        Logger.i(TAG, "cancelImpressionRetry");
        if (this.mImpressionRetryScheduled) {
            this.mHandler.removeCallbacks(this.sendImpressionRunnable);
            //不再重试
            this.mImpressionRetryScheduled = false;
        }
    }

    public void onScreenOn() {
        scheduleImpressionRetry();
    }

    public void onScreenOff() {
        cancelImpressionRetry();
    }

}
