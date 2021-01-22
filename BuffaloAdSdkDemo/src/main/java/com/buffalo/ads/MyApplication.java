package com.buffalo.ads;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.buffalo.ads.ui.AdViewConfigHelper;
import com.buffalo.ads.utils.FileUtils;
import com.buffalo.ads.utils.LocaleConfig;
import com.buffalo.ads.utils.VolleyUtil;
import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.BitmapListener;
import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.ImageDownloadListener;
import com.buffalo.adsdk.NativeAdManagerFactory;
import com.buffalo.adsdk.utils.ReportProxy;

import java.util.Map;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TestService", "MyApplication onCreate pid:" + android.os.Process.myPid());
        String processName = getCurProcessName(this);
        Log.i("TestService", "MyApplication onCreate pid:" + android.os.Process.myPid() + "processName:" + processName);
        Log.i("TestService", "init SDK");
        initSDK();
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    private void initSDK() {

        //开启Debug模式，默认不开启不会打印log
        AdManager.enableLog();
        //设置是否是内部产品

        NativeAdManagerFactory.setDefaultConfig(FileUtils.readStringFromAsset(this, "default_en.udconf"), true);

        //初始化聚合sdk
        //第一个参数：Context
        //第二个参数：mid =1005
        AdManager.applicationInit(this, "10000", false);

        AdManager.addLoaderClass(Const.KEY_FB, "com.buffalo.ads.adapter.FacebookNativeAdapter");
        AdManager.addLoaderClass(Const.KEY_YH, "com.buffalo.ads.adapter.YahooNativeAdapter");
        AdManager.addLoaderClass(Const.KEY_MP, "com.buffalo.ads.adapter.MopubNativeAdapter");
        AdManager.addLoaderClass(Const.KEY_AB, "com.buffalo.ads.adapter.AdmobNativeAdapter");
        AdManager.addLoaderClass(Const.KEY_MP_BANNER, "com.buffalo.ads.adapter.MopubBannerAdapter");
        AdManager.addLoaderClass(Const.KEY_FB_INTERSTITIAL, "com.buffalo.ads.adapter.FacebookInterstitialAdapter");
        AdManager.addLoaderClass(Const.KEY_AB_INTERSTITIAL, "com.buffalo.ads.adapter.AdmobInterstitialAdapter");
        AdManager.addLoaderClass(Const.KEY_MV, "com.buffalo.ads.adapter.MobvistaNativeAdapter");
        AdManager.addLoaderClass(Const.KEY_IM, "com.buffalo.ads.adapter.InmobiNativeAdapter");

        AdViewConfigHelper.setRenderAdapter();

        //banner , interstitialad ,vast video
        NativeAdManagerFactory.setImageDownloadListener(new MyImageLoadListener());
        initReport();

        AdManager.enableLog();
        LocaleConfig.init();
    }

    class MyImageLoadListener implements ImageDownloadListener {

        @Override
        public void getBitmap(final String url, boolean isOnlyCache, final BitmapListener imageListener) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(url)) {
                        if (imageListener != null) {
                            imageListener.onFailed("url is null");
                        }
                        return;
                    }
                    VolleyUtil.loadImage(url, new ImageLoader.ImageListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if (imageListener != null) {
                                imageListener.onFailed(volleyError.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            if (imageContainer != null && imageContainer.getBitmap() != null) {
                                if (imageListener != null) {
                                    imageListener.onSuccessed(imageContainer.getBitmap());
                                }
                            }
                        }
                    });
                }
            });
        }

    }

    private static final String VAST_TAG = "MyVastReport";

    private void initReport() {

        NativeAdManagerFactory.setReportProxy(new ReportProxy() {
            @Override
            public void doNativeReport(Const.Event event, Map<String, String> extras) {
                Log.e("MyApplication", "brands doNativeReport : event = " + event + extras.toString());
            }

            @Override
            public void doNetworkingReport(Map<String, String> extras) {

            }
        });
    }
}
