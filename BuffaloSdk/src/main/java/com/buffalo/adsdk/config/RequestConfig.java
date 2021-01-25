package com.buffalo.adsdk.config;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.InternalAdError;
import com.buffalo.adsdk.utils.NativeReportUtil;
import com.buffalo.adsdk.utils.PreferenceUtil;
import com.buffalo.utils.BackgroundThread;
import com.buffalo.utils.Commons;
import com.buffalo.utils.Logger;
import com.buffalo.utils.Networking;
import com.buffalo.utils.ThreadHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static com.buffalo.adsdk.Const.KEY_REQUEST_CONFIG;

public class RequestConfig {
    private static final String TAG = "RequestConfig";
    private static final int DEFAULT_INTERVAL = 2 * 60 * 60;
    private static final String KEY_CONFIG_LOADED_TIME = "config_loaded_time";
    private static final String KEY_DEFAULT_CONFIG = "default_config";

    private static RequestConfig sInstance;

    private Context mContext;
    private String mMid;

    private String mDefaultConfig = null;
    private boolean mForceDefaultConfig = false;

    private boolean mConfigLoaded = false;
    private volatile boolean mIsLoading = false;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

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
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(Executors.newSingleThreadExecutor(),
                        new OnCompleteListener<Boolean>() {
                            @Override
                            public void onComplete(@NonNull Task<Boolean> task) {
                            }
                        });
    }

    public static RequestConfig getInstance() {
        if (sInstance == null) {
            sInstance = new RequestConfig();
        }
        return sInstance;
    }

    private RequestConfig() {
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
                NativeReportUtil.doNativeAdSuccessReport(Const.Event.CONFIG_EMPTY, mMid);
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
        final long startConfigRequestTime = System.currentTimeMillis();
        mIsLoading = true;

        Networking.get(mFirebaseRemoteConfig.getString(KEY_REQUEST_CONFIG), new Networking.HttpListener() {
            @Override
            public void onResponse(int responseCode, HashMap<String, String> headers,
                                   InputStream result, String encode, int contentLength) {
                String obj = Networking.readString(result, encode);
                if (ConfigResponse.isValidResponse(obj)) {
                    NativeReportUtil.doNativeAdSuccessReport(Const.Event.CONFIG_SUCCESS, mMid,
                            System.currentTimeMillis() - startConfigRequestTime);

                    putConfigLoadedTime(System.currentTimeMillis() / 1000);
                    updateToLocalAsync(obj);
                } else {
                    NativeReportUtil.doNativeAdFailReport(Const.Event.CONFIG_FAIL, mMid,
                            System.currentTimeMillis() - startConfigRequestTime, "config file is null.");

                    Logger.e(TAG, "request config failed...response is invalid");
                    updateToLocalAsync(null);
                }
            }

            @Override
            public void onError(int responseCode, InternalAdError error) {
                NativeReportUtil.doNativeAdFailReport(Const.Event.CONFIG_FAIL, mMid,
                        System.currentTimeMillis() - startConfigRequestTime, error.getErrorMessage());

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
            obj = PreferenceUtil.getCacheJsonStr("");
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
        PreferenceUtil.saveCacheJsonStr(obj);
        ConfigResponse response = ConfigResponse.createFrom(obj);
        Logger.i(TAG, "reponse:" + response);
        return response;
    }

    public void destroy() {
    }

    public void putConfigLoadedTime(long value) {
        PreferenceUtil.putLong(KEY_CONFIG_LOADED_TIME, value);
    }

    public long getConfigLoadedTime() {
        return PreferenceUtil.getLong(KEY_CONFIG_LOADED_TIME, 0);
    }

    public boolean getDefaultConfigUsed() {
        return PreferenceUtil.getBoolean(KEY_DEFAULT_CONFIG, false);
    }

    public void setDefaultConfigUsed(boolean value) {
        PreferenceUtil.putBoolean(KEY_DEFAULT_CONFIG, value);
    }

}
