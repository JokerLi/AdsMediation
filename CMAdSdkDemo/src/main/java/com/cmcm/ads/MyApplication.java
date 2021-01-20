package com.cmcm.ads;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cmcm.ads.ui.AdViewConfigHelper;
import com.cmcm.ads.utils.FileUtils;
import com.cmcm.ads.utils.LocaleConfig;
import com.cmcm.ads.utils.VolleyUtil;
import com.cmcm.adsdk.BitmapListener;
import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.CMAdManagerFactory;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.ImageDownloadListener;
import com.cmcm.adsdk.utils.ReportProxy;
import com.cmcm.orion.adsdk.GifStreamListener;
import com.cmcm.orion.adsdk.OrionSdk;

import java.util.Map;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
        super.onCreate();
        Log.i("TestService", "MyApplication onCreate pid:" + android.os.Process.myPid());
        String processName = getCurProcessName(this);
        Log.i("TestService", "MyApplication onCreate pid:" + android.os.Process.myPid()+"processName:"+processName);
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

    private void initSDK(){

        //开启Debug模式，默认不开启不会打印log
        CMAdManager.enableLog();
        //设置是否是内部产品

        if (BuildConfig.IS_CN_VERSION) {
            //初始化聚合sdk
            // 开启本地的配置文件模式
            //TODO:默认配置
            CMAdManagerFactory.setDefaultConfig(FileUtils.readStringFromAsset(this, "default.udconf"), false);
            //第一个参数：Context
            //第三个参数：产品的渠道ID

            CMAdManager.applicationInit(this, "1096", BuildConfig.IS_CN_VERSION);
            OrionSdk.applicationInit(this, "1096");

            CMAdManager.addLoaderClass(Const.KEY_GDT, "com.cmcm.adsdk.adapter.GDTNativeAdapter");
            CMAdManager.addLoaderClass(Const.KEY_BD, "com.cmcm.adsdk.adapter.BaiduNativeAdapter");
        } else {
            CMAdManagerFactory.setDefaultConfig(FileUtils.readStringFromAsset(this, "default_en.udconf"), true);

            //初始化聚合sdk
            //第一个参数：Context
            //第二个参数：mid =1005
            CMAdManager.applicationInit(this, "10000", BuildConfig.IS_CN_VERSION);
            OrionSdk.setTestDataModel(true);
            OrionSdk.applicationInit(this, "128");

            CMAdManager.addLoaderClass(Const.KEY_FB, "com.cmcm.adsdk.adapter.FacebookNativeAdapter");
            CMAdManager.addLoaderClass(Const.KEY_YH, "com.cmcm.adsdk.adapter.YahooNativeAdapter");
            CMAdManager.addLoaderClass(Const.KEY_MP, "com.cmcm.adsdk.adapter.MopubNativeAdapter");
            CMAdManager.addLoaderClass(Const.KEY_AB, "com.cmcm.adsdk.adapter.AdmobNativeAdapter");
            CMAdManager.addLoaderClass(Const.KEY_CM_BANNER, "com.cmcm.adsdk.adapter.PicksBannerAdapter");
            CMAdManager.addLoaderClass(Const.KEY_MP_BANNER, "com.cmcm.adsdk.adapter.MopubBannerAdapter");
            CMAdManager.addLoaderClass(Const.KEY_FB_INTERSTITIAL, "com.cmcm.adsdk.adapter.FacebookInterstitialAdapter");
            CMAdManager.addLoaderClass(Const.KEY_AB_INTERSTITIAL, "com.cmcm.adsdk.adapter.AdmobInterstitialAdapter");
            CMAdManager.addLoaderClass(Const.KEY_MV, "com.cmcm.adsdk.adapter.MobvistaNativeAdapter");
            CMAdManager.addLoaderClass(Const.KEY_IM, "com.cmcm.adsdk.adapter.InmobiNativeAdapter");
        }

        CMAdManager.addLoaderClass(Const.KEY_CM, "com.cmcm.adsdk.adapter.PicksNativeAdapter");
        CMAdManager.addLoaderClass(Const.KEY_CM_INTERSTITIAL, "com.cmcm.adsdk.adapter.PicksInterstatialAdapter");
        AdViewConfigHelper.setRenderAdapter();

        //banner , interstitialad ,vast video
        CMAdManagerFactory.setImageDownloadListener(new MyImageLoadListener());
        initReport();
        OrionSdk.setImageDownloadListener(new com.cmcm.orion.adsdk.ImageDownloadListener() {
            @Override
            public void getBitmap(final String url, final com.cmcm.orion.adsdk.BitmapListener bitmapListener) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(url)) {
                            if (bitmapListener != null) {
                                bitmapListener.onFailed("url is null");
                            }
                            return;
                        }
                        VolleyUtil.loadImage(url, new ImageLoader.ImageListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                if (bitmapListener != null) {
                                    bitmapListener.onFailed(volleyError.getMessage());
                                }
                            }

                            @Override
                            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                                if (imageContainer != null && imageContainer.getBitmap() != null) {
                                    if (bitmapListener != null) {
                                        bitmapListener.onSuccessed(imageContainer.getBitmap());
                                    }
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void getGifStream(String s, GifStreamListener gifStreamListener) {

            }
        });

        CMAdManager.enableLog();
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

        CMAdManagerFactory.setReportProxy(new ReportProxy() {
            @Override
            public void doNativeReport(Const.Event event, Map<String, String> extras) {
                Log.e("MyApplication","brands doNativeReport : event = " + event + extras.toString());
            }

            @Override
            public void doNetworkingReport(Map<String, String> extras) {

            }
        });
    }
}
