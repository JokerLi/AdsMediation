package com.cmcm.adsdk.config;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.InternalAdError;
import com.cmcm.adsdk.utils.NativeReportUtil;
import com.cmcm.adsdk.utils.PerferenceUtil;
import com.cmcm.utils.BackgroundThread;
import com.cmcm.utils.Commons;
import com.cmcm.utils.Logger;
import com.cmcm.utils.Networking;
import com.cmcm.utils.ThreadHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// FIXME: 2016/7/12
//import com.cmcm.picks.internal.loader.CmMarketHttpClient;


public class RequestConfig {
    private static final String TAG = "RequestConfig";
    private static final int DEFAULT_INTERVAL = 2 * 60 * 60;
    private static final String KEY_CONFIG_LOADED_TIME = "config_loaded_time";
    private static final String KEY_DEFAULT_CONFIG = "default_config";

    private static RequestConfig sInstance;

    private Context mContext;
    private String mMid;
    //TODO 拿到json中的splash_frequency后保存到cm_ad_cache.xml中，key为 splash_frequency

    private String mDefaultConfig = null;
    private boolean mForceDefaultConfig = false;

    private boolean mConfigLoaded = false;
    private volatile boolean mIsLoading = false;
    private boolean mIsPreload = false;

    private List<QueueTask> mBackupQueueTask = new ArrayList<>();
    private Map<String, ConfigResponse.AdPosInfo> mConfigMap = new HashMap<>();

    public interface ICallBack {
        void onConfigLoaded(String posId, List<PosBean> beans);
    }

    static class QueueTask {
        QueueTask(String posId, ICallBack callback) {
            mPosId = posId;
            mCallback = callback;
        }

        String mPosId;
        ICallBack mCallback;
    }

    public void init(final Context context, String publishId) {
        mContext = context;
        mMid = publishId;
        if (Commons.isMainProcess(context)) {
            Logger.i(TAG, "is main process, start config monitor");
            BackgroundThread.postOnIOThread(new Runnable() {
                @Override
                public void run() {
                    //开启定时器每隔一段时间去请求配置信息
                    ConfigChangeMonitor.getInstance(context).start(mMid);
                }
            });
        }
    }

    public static RequestConfig getInstance() {
        if (sInstance == null) {
            sInstance = new RequestConfig();
        }
        return sInstance;
    }

    private RequestConfig() {
    }

    public void setPreload(boolean isPreload){
        this.mIsPreload = isPreload;
    }

    public void setDefaultConfig(String strConfig, boolean force) {
        mDefaultConfig = strConfig;
        mForceDefaultConfig = force;
    }

    public void requestConfig(boolean forceLoad) {
        if (mContext == null) {
            return;
        }
        if (!Commons.isMainProcess(mContext)) {
            Logger.i(TAG, "this process is not main process");
            return;
        }
        if (getConfigLoadedTime() > 0 && !mConfigLoaded) {
            loadFromLocal();
        }

        if (mForceDefaultConfig || forceLoad || shouldRequestConfig()) {
            loadFromNetwork();
        }
    }

    public void getBeans(final String placeId, final ICallBack callback) {
        if (ThreadHelper.runningOnUiThread()) {
            getBeansOnUI(placeId, callback);
        } else {
            ThreadHelper.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getBeansOnUI(placeId, callback);
                }
            });
        }
    }

    private void getBeansOnUI(String placeId, ICallBack callback) {
        if (mConfigLoaded) {
            getBeansSync(placeId, callback);
        } else {
            loadFromLocal();
            mBackupQueueTask.add(new QueueTask(placeId, callback));
        }
    }

    private void getBeansSync(final String placeId, ICallBack callback) {
        if (callback != null) {
            List<PosBean> beans = null;
            ConfigResponse.AdPosInfo adPos = mConfigMap.get(placeId);
            if (adPos != null) {
                beans = adPos.orders;
            } else {
                //埋点整个config有，但是该posid没有配的情况
//                if(mConfigMap.size() > 0) {
//                    String errorCode = "config ad pos is null.";
//                    NativeReportUtil.doNativeAdFailReport(Const.Event.GET_CONFIG_NULL, placeId,errorCode, mIsPreload);
//                    Map<String,String> extraContent = UniReportHelper.getLoadPreloadExtra(ReportFactory.GET_CONFIG_NULL,
//                            errorCode, "", "", mIsPreload ? ReportFactory.PRELOAD: ReportFactory.LOAD, "", "", "", "");
//                    UniReport.reportUni(ReportFactory.REQUEST_AD, "", 0, placeId, extraContent, "", "", false);
//                }
            }
            callback.onConfigLoaded(placeId, beans);
        }
    }

    private void loadFromLocal() {
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        updateToLocalAsync(null);
    }

    private void loadFromNetwork() {
        NativeReportUtil.doNativeAdSuccessReport(Const.Event.CONFIG_START, mMid);

//        UniReportHelper.reportConfig(ReportFactory.START);

        final long startConfigRequestTime = System.currentTimeMillis();
        mIsLoading = true;
        String url = CMAdManager.sIsCnVersion ? Const.CONFIG_URL_CN : Const.CONFIG_URL;
        String param = buildParams(mMid);
        Networking.get(url, param, new Networking.HttpListener() {
            @Override
            public void onResponse(int responseCode, HashMap<String, String> headers,
                                   InputStream result, String encode, int contentLength) {
                String obj = Networking.readString(result, encode);
                if (ConfigResponse.isValidResponse(obj)) {
                   NativeReportUtil.doNativeAdSuccessReport(Const.Event.CONFIG_SUCCESS, mMid,
                           System.currentTimeMillis() - startConfigRequestTime);

//                    UniReportHelper.reportConfig(ReportFactory.SUCCESS,
//                            System.currentTimeMillis() - startConfigRequestTime + "");

                    putConfigLoadedTime(System.currentTimeMillis() / 1000);
                    updateToLocalAsync(obj);
                } else {
                    NativeReportUtil.doNativeAdFailReport(Const.Event.CONFIG_FAIL, mMid,
                            System.currentTimeMillis() - startConfigRequestTime, "config file is null.");

//                    UniReportHelper.reportConfig(ReportFactory.FAIL, "config file is null.",
//                            System.currentTimeMillis() - startConfigRequestTime + "");

                    Logger.e(TAG, "request config failed...response is invalid");
                    updateToLocalAsync(null);
                }
            }

            @Override
            public void onError(int responseCode, InternalAdError error) {
                NativeReportUtil.doNativeAdFailReport(Const.Event.CONFIG_FAIL, mMid,
                        System.currentTimeMillis() - startConfigRequestTime, error.getErrorMessage());

//                UniReportHelper.reportConfig(ReportFactory.FAIL, error.getErrorMessage(),
//                        System.currentTimeMillis() - startConfigRequestTime + "");

                Logger.e(TAG, "request failed..." + error.getErrorMessage());
                updateToLocalAsync(null);
            }
        });
    }

    private boolean shouldRequestConfig() {
        if (mForceDefaultConfig)
            return true;

        long timeElapsed = System.currentTimeMillis() / 1000 - getConfigLoadedTime();
        if (timeElapsed >= DEFAULT_INTERVAL) {
            Logger.i(TAG, "time:" + timeElapsed);
            return true;
        }
        return false;
    }

    private void updateToLocalAsync(final String config) {
        Logger.i(TAG, "update config in db");
        ThreadHelper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BackgroundThread.executeAsyncTask(new AsyncTask<Void, Void, ConfigResponse>() {
                    @Override
                    protected ConfigResponse doInBackground(Void... Object) {
                        return updateToLocal(config);
                    }

                    @Override
                    protected void onPostExecute(ConfigResponse configResponse) {
                        Logger.i(TAG, "onPostExecute isSuccess:" + configResponse);

                        if (configResponse != null && configResponse.getPosConfigMap() != null) {
                            mConfigMap = configResponse.getPosConfigMap();
                        }
                        notifyLoaded();
                    }
                });
            }
        });
    }

    private void notifyLoaded() {
        mIsLoading = false;
        mConfigLoaded = true;
        issueWaitingQueries();
    }

    private void issueWaitingQueries() {
        for (QueueTask task : mBackupQueueTask) {
            if (task.mCallback != null) {
                getBeansSync(task.mPosId, task.mCallback);
            }
        }
        mBackupQueueTask.clear();
    }

    private ConfigResponse updateToLocal(String obj) {
        if (TextUtils.isEmpty(obj)) {
            Logger.i(TAG, "request server config failed, use last local config");
            obj = PerferenceUtil.getCacheJsonStr("");
        }
        if (mForceDefaultConfig || (TextUtils.isEmpty(obj) && !TextUtils.isEmpty(mDefaultConfig) && !getDefaultConfigUsed())) {
            setDefaultConfigUsed(true);
            Logger.i(TAG, "request server config failed, use default config");
            obj = mDefaultConfig;
        }

        if (TextUtils.isEmpty(obj)) {
            Logger.i(TAG, "request server config and default config failed, update config failed");
            return null;
        }
        Logger.i(TAG, "save config to shareprefrence:" + obj);
        PerferenceUtil.saveCacheJsonStr(obj);
        ConfigResponse response = ConfigResponse.createFrom(obj);
        Logger.i(TAG, "reponse:" + response);
        return response;
    }

//    private boolean shouldTryDefaultConfig() {
//        return false;
//    }

    public void destroy() {
    }

    public void putConfigLoadedTime(long value) {
        PerferenceUtil.putLong(KEY_CONFIG_LOADED_TIME, value);
    }

    public long getConfigLoadedTime() {
        return PerferenceUtil.getLong(KEY_CONFIG_LOADED_TIME, 0);
    }

    public boolean getDefaultConfigUsed() {
        return PerferenceUtil.getBoolean(KEY_DEFAULT_CONFIG, false);
    }

    public void setDefaultConfigUsed(boolean value) {
        PerferenceUtil.putBoolean(KEY_DEFAULT_CONFIG, value);
    }


    /**
     * action：pos_config固定值，表示哪种服务请求
     * postype：1--固定值，表示哪种服务请求
     * mid：媒体ID
     * posid：广告位ID
     * cver：sdk的版本
     * lan: 国家_语言
     * v: 协议版本号
     */
    public static String buildParams(String mid) {
        //action=pos_config&postype=1&mid=104&posid=&cver=508&lan=en_us&v=13";
        StringBuilder sb = new StringBuilder();
        sb.append("action=pos_config");
        sb.append("&postype=1");
        sb.append("&mid=" + mid);
        sb.append("&posid=");
        sb.append("&androidid=" + Commons.getAndroidId());
        sb.append("&cver=" + Commons.getAppVersionCode(CMAdManager.getContext()));
        sb.append("&lan=" + Commons.getCountry(CMAdManager.getContext()) + "_" + Commons.getLanguage(CMAdManager.getContext()));
        // FIXME: 2016/7/12
        sb.append("&v=" + "22");
        sb.append("&sdkv=" + Const.VERSION);
        return sb.toString();
    }
}
