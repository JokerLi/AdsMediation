package com.buffalo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public class ReceiverUtils extends BroadcastReceiver {
    private static ReceiverUtils mInstance;
    //添加action，请现在这里声明
    public static ViewCheckHelper mViewHelper;
    private static final Object lock = new Object();

    public static void addViewCheckHelperObj(ViewCheckHelper obj) {
        mViewHelper = obj;
    }

    public static void removeViewCheckHelperObj() {
        mViewHelper = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            if (mViewHelper != null) {
                mViewHelper.onScreenOff();
            }
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            if (mViewHelper != null) {
                mViewHelper.onScreenOn();
            }
        }
    }

    public static void regist(Context context) {
        if (null == context) {
            return;
        }
        IntentFilter screenOnOrOff = new IntentFilter();
        screenOnOrOff.addAction(Intent.ACTION_SCREEN_OFF);
        screenOnOrOff.addAction(Intent.ACTION_SCREEN_ON);
        if (null == mInstance) {
            mInstance = new ReceiverUtils();
        }
        context.registerReceiver(mInstance, screenOnOrOff);
    }

    public static void unRegist(Context context) {
        if (null == context || null == mInstance) {
            return;
        }
        context.unregisterReceiver(mInstance);
        mInstance = null;
    }
}
