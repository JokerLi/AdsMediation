package com.cmcm.ads;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.cmcm.adsdk.nativead.NativeAdManager;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;

public class TestService extends Service {

    private NativeAdManager nativeAdManager;
    private String mAdPosid = BuildConfig.IS_CN_VERSION ? "1096101" : "1094101";

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
