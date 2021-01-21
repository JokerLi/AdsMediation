package com.buffalo.adsdk.config;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.buffalo.utils.Logger;

/**
 * 每隔2小时去服务端拉一下最新数据
 * 然后更新到本地
 * @author i
 *
 */
public class ConfigChangeMonitor {
    private static ConfigChangeMonitor sInstance = null;
    private static final String ALARM_RECEIVER_ACTION = "com.buffalo.adsdk.ConfigMonitor_Action";
    private static final long DEFAULT_INTERVAL = 4*60*60*1000L;
    private static final long ONE_MINUTE = 1*60*1000;
    private static final String TAG = "ConfigChangeMonitor";
    private Context mContext;
    private String mMid;
    private boolean mIsStart = false;
    private PendingIntent mAlarmPendingItent ;
    private AlarmReceiver mAlarmReceiver;
    private long lastMonitorTime;
    private ConfigChangeMonitor(Context context) {
    	mContext = context;
    }

    public synchronized static ConfigChangeMonitor getInstance(Context context) {
    	if (null == sInstance) {
    		sInstance = new ConfigChangeMonitor(context);
        }
        return sInstance;
    }

    public synchronized void start(String mid) {
        if(mIsStart){
            Logger.i(TAG, "has start monitor, avoid repeat monitor ...");
            return;
        }
        Logger.i(TAG, "start monitor...");
        mMid = mid;
        mIsStart = true;
        try {
             if(mAlarmReceiver == null){
                mAlarmReceiver = new AlarmReceiver();
             }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ALARM_RECEIVER_ACTION);

            mContext.getApplicationContext().registerReceiver(mAlarmReceiver, intentFilter);
            Intent intent = new Intent();
            intent.setAction(ALARM_RECEIVER_ACTION);
            if (mAlarmPendingItent == null) {
                mAlarmPendingItent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
            }
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            long when = System.currentTimeMillis() + DEFAULT_INTERVAL;
            alarmManager.setRepeating(AlarmManager.RTC, when, DEFAULT_INTERVAL, mAlarmPendingItent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void stop() {
        Logger.i(TAG, "stop monitor...");
        if( mContext != null){
            try {
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                if(mAlarmPendingItent != null) {
                   alarmManager.cancel(mAlarmPendingItent);
                   mAlarmPendingItent = null;
                }
                if(mAlarmReceiver != null) {

                    mContext.getApplicationContext().unregisterReceiver(mAlarmReceiver);
                    mAlarmReceiver = null;
                }
            }catch (Exception e){
                    e.printStackTrace();
            }
        }
        mIsStart = false;
    }


    private class AlarmReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase(ALARM_RECEIVER_ACTION)) {
                Logger.i(TAG, "monitor requestConfig...");
                if(System.currentTimeMillis() - lastMonitorTime <= ONE_MINUTE){
                    Logger.i(TAG, "last monitor requestconfig in one minute");
                    return;
                }
                lastMonitorTime = System.currentTimeMillis();
                RequestConfig.getInstance().requestConfig(true);
                RequestUFS.getInstance().requestUFSInfo();
            }
        }
    }


}
