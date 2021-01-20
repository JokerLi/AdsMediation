package com.cmcm.ads;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.cmcm.ads.ui.ShowCasePagerAdapter;



/**
 * Created by chenhao on 16/11/19.
 */

public class ScreenSaverActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private PowerManager.WakeLock mWakeLock;
    private KeyguardManager.KeyguardLock mKeyguardLock;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screensaver);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.SCREEN_DIM_WAKE_LOCK |
                        PowerManager.ON_AFTER_RELEASE, "SimpleTimer");


        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new ShowCasePagerAdapter(this));
        KeyguardManager mKeyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");
        mKeyguardLock.disableKeyguard();//解锁屏幕，也就是 关闭 屏幕 锁定 功能
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("ScreenSaver", "onPause");
        mWakeLock.acquire();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ScreenSaver", "onResume");
        mWakeLock.acquire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mKeyguardLock != null){
            mKeyguardLock.reenableKeyguard();
        }
    }
}
