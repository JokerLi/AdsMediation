package com.buffalo.adsdk.nativead;

import com.buffalo.adsdk.Const;
import com.buffalo.utils.Logger;

import java.util.Timer;
import java.util.TimerTask;

class TimeoutTask extends TimerTask {

    Timer mTimer = null;
    boolean mTimeout = false;
    Runnable mRun;
    String name;

    TimeoutTask(Runnable r, String name) {
        mRun = r;
        this.name = name;
    }

    @Override
    public void run() {
        Logger.i(Const.TAG, name + " timeout, to check this load finish");
        mTimeout = true;

        if (mRun != null) {
            mRun.run();
        }
    }

    public void start(int time) {
        mTimeout = false;
        try {
            mTimer = new Timer();
            mTimer.schedule(this, time);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (null != mTimer) {
                mTimeout = true;
                mTimer.cancel();
                mTimer = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
