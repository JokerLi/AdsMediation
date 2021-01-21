package com.buffalo.adsdk.nativead;

/**
 * Created by chenhao on 16/3/31.
 */
public interface LifeCycleDelegate {
    void onPause();
    void onResume();
    void onDestroy();
}
