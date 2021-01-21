package com.buffalo.adsdk;

import android.content.Context;
import android.text.TextUtils;

import com.buffalo.adsdk.config.RequestUFS;
import com.buffalo.adsdk.unifiedreport.UnifiedReporter;
import com.buffalo.picks.internal.ReceiverUtils;
import com.buffalo.utils.BackgroundThread;
import com.buffalo.utils.CMReceiverUtils;
import com.buffalo.utils.Logger;
import com.buffalo.utils.gaid.AdvertisingIdHelper;

import java.util.Hashtable;
import java.util.Map;


/**
 * Created by chenhao on 2015/7/9.
 */
public abstract class AdManager {
    private static Context mContext;
    private static String mMid;
    //全局的开关
    public static final int DEFAULT_SSPID = -1;
    private static String sChannelId;
    private static CMBaseFactory sAdFactory = null;
    //控制offer上报开关
    private static int sReportSwitcher = 0;
    // request ufs
    private static boolean sIsRequestUfs = false;

    private static boolean sIsDebug = false;
    private static int mPegasusReportViewCheckIntervalMills = 500;
    public static boolean sIsCnVersion = true;
    private static Map<String, String> mOrionTestAppId = new Hashtable<String, String>();

    public static void applicationInit(Context context, String mid, boolean isCnVersion){
        applicationInit(context, mid, isCnVersion, "");
    }

    public static void applicationInit(final Context context, String mid, boolean isCnVersion, String channelId){
        //第三个参数是渠道id
        if(TextUtils.isEmpty(mid)){
            throw new IllegalArgumentException("PublisherID cannot be null or empty");
        }
        sIsCnVersion =isCnVersion;
        mContext = context;
        mMid = mid;
        sChannelId = channelId;
        AdvertisingIdHelper.getInstance().getGAId();
        // FIXME: 2016/7/12 
//        PicksMob.getInstance().init();
        BackgroundThread.postOnIOThread(new Runnable() {
            @Override
            public void run() {
                // FIXME: 2016/7/12
//                freshPicksConfig();
                ReceiverUtils.regist(mContext);
                CMReceiverUtils.regist(mContext);
                createFactory();
                if (sAdFactory != null) {
                    sAdFactory.initConfig();
                }
                RequestUFS.getInstance().requestUFSInfo();
            }
        });
    }

    //更新配置
    // FIXME: 2016/7/12 
    /*private static void freshPicksConfig() {
        if (MarketConfig.isExpired(MarketConfig.KEY_MARKET_CONFIG, MarketConfig.EXPIRE_FOR_ONE_DAY)) {
            CmMarketHttpClient.getInstance().freshConfig(getMid());
        }
    }*/

    public static Context getContext(){
        return mContext;
    }


    public static String getMid(){
        return mMid;
    }

    public static String getChannelId(){
        return sChannelId;
    }

    public static void enableLog() {
        Logger.isDebug = true;
    }

    //将CMAdManagerFactory放置到扩展包，因为base包没有
    public static CMBaseFactory createFactory() {
        if (sAdFactory == null) {
            try {
                Class className = Class.forName("com.buffalo.adsdk.CMAdManagerFactory");
                if (sAdFactory == null) {
                    sAdFactory = (CMBaseFactory) className.newInstance();
                }
            } catch (Exception e) {
            }
        }
        return sAdFactory;
    }

    public static void addLoaderClass(String loaderKey, String loaderClass) {
        CMBaseFactory adFactory = createFactory();
        if (adFactory != null) {
            adFactory.addLoaderClass(loaderKey, loaderClass);
        }
    }

    public static void addRenderAdapter(String loaderKey, NativeAdTemplate.ICMNativeAdViewAdapter adapter){
        CMBaseFactory adFactory = createFactory();
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

    public static void setDebug(){
        sIsDebug = true;
    }

    public static boolean isDebug(){
        return sIsDebug;
    }

    public static void reportPV(int pageId){
        try{
            UnifiedReporter.getInstance().reportShow(pageId);
        }catch (Exception e){
        }
    }

    public static void setRequestUfs(boolean isRequestUfs){
        sIsRequestUfs = isRequestUfs;
    }

    public static boolean isRequestUfs(){
        return sIsRequestUfs;
    }

    public static void setPegasusReportViewCheckIntervalMillisecond(int timeMillisecond){
        if (timeMillisecond < 300) {
            timeMillisecond = 300;
        } else if (timeMillisecond > 1000) {
            timeMillisecond = 1000;
        }
        mPegasusReportViewCheckIntervalMills = timeMillisecond;
    }

    public static int getPegasusReportViewCheckIntervalMillisecond(){
        return mPegasusReportViewCheckIntervalMills;
    }
}
