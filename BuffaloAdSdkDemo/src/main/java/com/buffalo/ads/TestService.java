package com.buffalo.ads;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.buffalo.adsdk.nativead.NativeAdManager;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;

public class TestService extends Service {

    private NativeAdManager nativeAdManager;
    private String mAdPosid = "1094101";

    public TestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nativeAdManager = new NativeAdManager(this, mAdPosid);
        nativeAdManager.setNativeAdListener(new INativeAdLoaderListener() {
            @Override
            public void adLoaded() {
                Toast.makeText(getBaseContext(), "Service:adLoaded", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void adFailedToLoad(int i) {
                Toast.makeText(getBaseContext(), "Service:Ad failed to load errorCode:" + i,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void adClicked(INativeAd ad) {
                Toast.makeText(getBaseContext(), "Service:adClicked",
                        Toast.LENGTH_SHORT).show();
            }
        });
        nativeAdManager.loadAd();
    }
}
