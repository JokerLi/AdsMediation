package com.cmcm.utils;

import android.content.Context;
import android.os.Handler;

import com.cmcm.adsdk.CMAdManager;

import java.util.concurrent.Callable;


/**
 * Created by chenhao on 15/12/1.
 * 此类的作用是检测广告是否真正的展示
 * 检测逻辑：
 *    每隔一秒调度一次
 *    (如果是锁屏情况下会放置耗电会停止检测，添加广播接受者，开屏后继续检测
 *    直到检测到了展示了就停止广告的检测)
 *
 *
 */

// FIXME: 同一个view 被多次注册时, 也会出问题

public class ViewCheckHelper {
    private static final String TAG = "ViewCheckHelper";
    private int mScheduleCheckViewTime;
    private Context mContext;
    public Handler mHandler;
    private boolean mImpressionRetryScheduled;
    private Callable<Boolean> mCheckTask;
    public ViewCheckHelper(Context context, Callable<Boolean> checkTask){
        this.mContext = context.getApplicationContext();
        this.mImpressionRetryScheduled = true;
        mScheduleCheckViewTime = CMAdManager.getPegasusReportViewCheckIntervalMillisecond();
        mHandler = new Handler();
        mCheckTask = checkTask;
        CMReceiverUtils.addViewCheckHelperObj(this);
    }

    public void startWork(){
        Logger.i(TAG, "start check view");
        if (Commons.isScreenOn(mContext)) {
            try {
                Boolean isNeedStop = mCheckTask.call();
                if(isNeedStop){
                    stopWork("first check over");
                }else {
                    mHandler.postDelayed(sendImpressionRunnable, mScheduleCheckViewTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Logger.i(TAG, "lock screen,cancel schedule check view");
            cancelImpressionRetry();
        }
    }

    public void stopWork(String reason){
        Logger.i(TAG, "stop check view: " + reason);
        cancelImpressionRetry();
        CMReceiverUtils.removeViewCheckHelperObj();
    }

    private  Runnable sendImpressionRunnable = new Runnable() {
        public void run() {
            if(mImpressionRetryScheduled){
                try {
                    Boolean isNeedStop = mCheckTask.call();
                    if(isNeedStop){
                        stopWork("check over");
                    }else {
                        if(mHandler != null){
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
        if (!mImpressionRetryScheduled){
            mImpressionRetryScheduled = true;
            mHandler.postDelayed(sendImpressionRunnable, mScheduleCheckViewTime);
        }
    }

    public synchronized void cancelImpressionRetry() {
        Logger.i(TAG,"cancelImpressionRetry");
        if (this.mImpressionRetryScheduled) {
            this.mHandler.removeCallbacks(this.sendImpressionRunnable);
            //不再重试
            this.mImpressionRetryScheduled = false;
        }
    }

    public void onScreenOn(){
       scheduleImpressionRetry();
    }

    public void onScreenOff(){
        cancelImpressionRetry();
    }

}
