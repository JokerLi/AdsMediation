package com.buffalo.adsdk;

import android.content.Context;
import android.text.TextUtils;

import com.buffalo.adsdk.config.RequestUFS;
import com.buffalo.adsdk.unifiedreport.UnifiedReporter;
import com.buffalo.utils.BackgroundThread;
import com.buffalo.utils.Logger;
import com.buffalo.utils.ReceiverUtils;
import com.buffalo.utils.gaid.AdvertisingIdHelper;

public abstract class AdManager {
    private static Context mContext;
    private static String mMid;
    private static String sChannelId;
    private static BaseFactory sAdFactory = null;
    //控制offer上报开关
    private static int sReportSwitcher = 0;
    // request ufs
    private static boolean sIsRequestUfs = false;

    private static boolean sIsDebug = false;
    private static int mPegasusReportViewCheckIntervalMills = 500;
    public static boolean sIsCnVersion = true;

    public static void applicationInit(Context context, String mid, boolean isCnVersion) {
        applicationInit(context, mid, isCnVersion, "");
    }

    public static void applicationInit(final Context context, String mid, boolean isCnVersion, String channelId) {
        //第三个参数是渠道id
        if (TextUtils.isEmpty(mid)) {
            throw new IllegalArgumentException("PublisherID cannot be null or empty");
        }
        sIsCnVersion = isCnVersion;
        mContext = context;
        mMid = mid;
        sChannelId = channelId;
        AdvertisingIdHelper.getInstance().getGAId();
        BackgroundThread.postOnIOThread(new Runnable() {
            @Override
            public void run() {
                com.buffalo.picks.internal.ReceiverUtils.regist(mContext);
                ReceiverUtils.regist(mContext);
                createFactory();
                if (sAdFactory != null) {
                    sAdFactory.initConfig();
                }
                RequestUFS.getInstance().requestUFSInfo();
            }
        });
    }

    public static Context getContext() {
        return mContext;
    }

    public static String getMid() {
        return mMid;
    }

    public static String getChannelId() {
        return sChannelId;
    }

    public static void enableLog() {
        Logger.isDebug = true;
    }

    //将CMAdManagerFactory放置到扩展包，因为base包没有
    public static BaseFactory createFactory() {
        if (sAdFactory == null) {
            try {
                Class className = Class.forName("com.buffalo.adsdk.NativeAdManagerFactory");
                if (sAdFactory == null) {
                    sAdFactory = (BaseFactory) className.newInstance();
                }
            } catch (Exception e) {
            }
        }
        return sAdFactory;
    }

    public static void addLoaderClass(String loaderKey, String loaderClass) {
        BaseFactory adFactory = createFactory();
        if (adFactory != null) {
            adFactory.addLoaderClass(loaderKey, loaderClass);
        }
    }

    public static void addRenderAdapter(String loaderKey, NativeAdTemplate.INativeAdViewAdapter adapter) {
        BaseFactory adFactory = createFactory();
        if (adFactory != null) {
            adFactory.addRenderAdapter(loaderKey, adapter);
        }
    }

    public static void setReportSwitcher(int reportSwitcher) {
        sReportSwitcher = reportSwitcher;
    }

    public static int getReportSwitcher() {
        return sReportSwitcher;
    }

    public static void setDebug() {
        sIsDebug = true;
    }

    public static boolean isDebug() {
        return sIsDebug;
    }

    public static void reportPV(int pageId) {
        try {
            UnifiedReporter.getInstance().reportShow(pageId);
        } catch (Exception e) {
        }
    }

    public static void setRequestUfs(boolean isRequestUfs) {
        sIsRequestUfs = isRequestUfs;
    }

    public static boolean isRequestUfs() {
        return sIsRequestUfs;
    }

    public static void setPegasusReportViewCheckIntervalMillisecond(int timeMillisecond) {
        if (timeMillisecond < 300) {
            timeMillisecond = 300;
        } else if (timeMillisecond > 1000) {
            timeMillisecond = 1000;
        }
        mPegasusReportViewCheckIntervalMills = timeMillisecond;
    }

    public static int getPegasusReportViewCheckIntervalMillisecond() {
        return mPegasusReportViewCheckIntervalMills;
    }
}
