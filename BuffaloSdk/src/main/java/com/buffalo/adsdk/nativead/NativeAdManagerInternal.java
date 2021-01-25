package com.buffalo.adsdk.nativead;

import android.content.Context;
import android.text.TextUtils;

import com.buffalo.adsdk.BuildConfig;
import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.NativeAdError;
import com.buffalo.adsdk.RequestParams;
import com.buffalo.adsdk.base.INativeRequestCallBack;
import com.buffalo.adsdk.config.PosBean;
import com.buffalo.adsdk.config.RequestConfig;
import com.buffalo.adsdk.utils.NativeReportUtil;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoader;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;
import com.buffalo.utils.Logger;
import com.buffalo.utils.ThreadHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NativeAdManagerInternal implements INativeRequestCallBack, LifeCycleDelegate, INativeAd.IAdOnClickListener {
    protected static String TAG = Const.TAG;
    // AD 优先级保护时间
    private static final int AD_PRIORITY_PROTECTION_TIME = BuildConfig.DEBUG ? 8 * 1000 : 8 * 1000;
    public static final int PRELOAD_REQUEST_SIZE = 1;
    public static final int DEFAULT_REQUEST_SIZE = 2;

    protected final Context mContext;
    protected final String mPositionId;
    protected RequestParams mRequestParams;

    private long mLoadStartTime = 0;

    protected List<PosBean> mConfigBeans;
    protected volatile boolean mIsFinished = true;
    private boolean mHaveCalledLoad = false;
    private INativeAdLoaderListener mCallBack;

    protected boolean mIsPreload = false;

    private int mCheckPointNum = 0;
    private int mCurrentPointId = 0;
    private int mFirstCheckTime = 4000;
    private int mCheckPointIntervalTime = 2000;

    TimeoutTask mPriorityProtectionTimer = null;
    TimeoutTask mCheckPointTimer = null;

    protected NativeAdLoaderMap mLoaderMap = new NativeAdLoaderMap();
    public RequestResultLogger mRequestLogger = new RequestResultLogger();
    public RequestLoadingStatus mLoadingStatus = new RequestLoadingStatus();

    protected boolean mOptimizeEnabled = true;
    protected boolean mHighPriorityLoaded = false;
    private long ONE_MINTURE = 1 * 60 * 1000;
    private boolean mVideoAdEnable = false;
    private boolean mBannerAdEnable = false;
    private List<String> mDisableTypeList = new ArrayList<String>();
    private boolean mIsFeed = false;


    public NativeAdManagerInternal(Context context, String posId) {
        mContext = context;
        mPositionId = posId;
    }

    public void setCheckPointNum(int num) {
        if (num < 1) {
            mCheckPointNum = 0;
        } else if (num > 2) {
            mCheckPointNum = 2;
        }
        mCheckPointNum = num;
    }

    public void setFirstCheckTime(int time) {
        if (time < 0 || time > AD_PRIORITY_PROTECTION_TIME) {
            mFirstCheckTime = 4000;
        }
        mFirstCheckTime = time;
    }

    public void setCheckPointIntervalTime(int time) {
        if (time < 0 || time > AD_PRIORITY_PROTECTION_TIME) {
            mCheckPointIntervalTime = 2000;
        }
        mCheckPointIntervalTime = time;
    }

    public void setPreload(boolean isPreload) {
        this.mIsPreload = isPreload;
    }

    public void setRequestParams(RequestParams requestParams) {
        mRequestParams = requestParams;
    }

    public void setAdListener(INativeAdLoaderListener adListener) {
        this.mCallBack = adListener;
    }

    public INativeAdLoaderListener getAdListener() {
        return this.mCallBack;
    }

    public void loadAd() {
        Logger.i(Const.TAG, "posid " + mPositionId + " loadAd...");
        mHaveCalledLoad = true;
        if (!mIsFinished) {
            if ((System.currentTimeMillis() - mLoadStartTime) < ONE_MINTURE) {
                Logger.i(Const.TAG, "wait and reuse for last result");
                NativeReportUtil.doNativeAdFailReport(Const.Event.LOAD_START_FAIL, mPositionId, "the last request is loading", mIsPreload);
                return;
            }
        }

        mIsFinished = false;
        mLoadStartTime = System.currentTimeMillis();
        RequestConfig.getInstance().getBeans(mPositionId, new RequestConfig.ICallBack() {

            @Override
            public void onConfigLoaded(String posId, List<PosBean> beans) {
                filterDisableConfig(beans);
                loadAd(beans);
            }
        });
    }

    private void filterDisableConfig(List<PosBean> beans) {
        if (beans == null || beans.isEmpty() || mDisableTypeList.isEmpty()) {
            return;
        }
        Iterator<PosBean> iterator = beans.iterator();
        while (iterator.hasNext()) {
            PosBean posBean = iterator.next();
            if (posBean != null) {
                if (mDisableTypeList.contains(posBean.getAdName())) {
                    Logger.d(Const.TAG, "ad type:" + posBean.getAdName() + " is disable in posid:" + mPositionId);
                    iterator.remove();
                }
            }
        }
    }

    public INativeAd getAd() {
        List<INativeAd> ads = getAdList(1);
        INativeAd ad = null;
        if (ads == null || ads.isEmpty()) {
            doGetAdFailReport();
        } else {
            ad = ads.get(0);
            String adTypeName = ad.getAdTypeName();
            int adIndex = getAdTypeNameIndex(adTypeName);
        }
        return ad;
    }

    private void doGetAdFailReport() {
        String errorStr;
        if (!mHaveCalledLoad) {
            errorStr = "have not called preload()/load()";
        } else if (!mIsFinished) {
            errorStr = "ad is loading";
        } else {
            errorStr = "ad is null";
        }
    }

    public List<PosBean> getPosBeans() {
        return mConfigBeans;
    }

    private void loadAd(List<PosBean> beans) {
        if (beans == null || beans.isEmpty()) {
            //如果配置是空直接回调出去，请求失败
            Logger.e(Const.TAG, "the posid:" + mPositionId + "no config, may be has closed");
            notifyAdFailed(NativeAdError.NO_CONFIG_ERROR);
            return;
        }
        mLoaderMap.enableBanner(mBannerAdEnable);
        mLoaderMap.enableVideo(mVideoAdEnable);
        mLoaderMap.updateLoaders(mContext, beans, this);
        for (String invalidBeanName : mLoaderMap.mFailedLoaderNames) {
            boolean removed = removeInvalidBeans(beans, invalidBeanName);
            Logger.i(Const.TAG, "filter invalid " + invalidBeanName + ",remove:" + removed);
        }

        mHighPriorityLoaded = false;
        mConfigBeans = beans;
        loadChildAds();
    }

    private boolean removeInvalidBeans(List<PosBean> beans, String name) {
        if (beans == null || beans.isEmpty() || TextUtils.isEmpty(name)) {
            return false;
        }

        boolean removed = false;
        Iterator<PosBean> iterator = beans.iterator();
        while (iterator.hasNext()) {
            PosBean posBean = iterator.next();
            if (posBean != null && name.equalsIgnoreCase(posBean.name)) {
                removed = true;
                iterator.remove();
            }
        }

        return removed;
    }

    private void loadChildAds() {
        mRequestLogger.reset();
        mLoadingStatus.resetLoadingStatus(mConfigBeans.size());
        mCurrentPointId = 0;

        boolean adIssued = false;
        int needLoadSize = getLoadAdTypeSize();
        Logger.i(Const.TAG, "is preload:" + mIsPreload + " loadsize:" + needLoadSize);
        for (int i = 0; i < needLoadSize; i++) {
            if (issueToLoadNext()) {
                adIssued = true;
            }
        }

        // 如果没有任何广告loader 拉取被触发, 则认为失败
        if (!adIssued) {
            Logger.i(Const.TAG, "loadChildAds no-loader was issued");
            notifyAdFailed(-1);
            return;
        }

        // 如果没有超级抢量, 或者顺序load , 不需要优先级保护
        if (needLoadSize > 1) {
            mPriorityProtectionTimer = new TimeoutTask(mAsyncFinishCheckRunnable, "PriorityProtectionTimer");
            mPriorityProtectionTimer.start(AD_PRIORITY_PROTECTION_TIME);
        }

        if (mCheckPointNum > 0) {
            mCheckPointTimer = new TimeoutTask(mAsyncCheckPointRunnable, "CheckPointTimer");
            mCheckPointTimer.start(mFirstCheckTime);
        }
    }

    protected int getAdTypeNameIndex(String adtypeName) {
        for (int i = 0; i < mConfigBeans.size(); i++) {
            PosBean posBean = mConfigBeans.get(i);
            if (posBean.getAdName().equalsIgnoreCase(adtypeName)) {
                return i;
            }
        }
        return -1;
    }

    protected int getLoadAdTypeSize() {
        //如果为空直接返回
        if (mConfigBeans == null || mConfigBeans.isEmpty()) {
            return 0;
        }
        if (mIsPreload) {
            return Math.min(mConfigBeans.size(), PRELOAD_REQUEST_SIZE);
        }

        //并发请求策略
        return Math.min(mConfigBeans.size(), DEFAULT_REQUEST_SIZE);
    }

    private boolean issueToLoadNext() {
        Logger.i(Const.TAG, "issueToLoadNext index waiting :" + mLoadingStatus.getWaitingBeansNumber() + ",config size:" + mConfigBeans.size());
        if (mIsFinished) {
            return false;
        }

        boolean issued = false;
        for (int i = 0; i < mConfigBeans.size(); ++i) {
            if (mLoadingStatus.isBeanLoading(i)) {
                continue;
            }
            issued = requestBean(i);
            if (issued)
                break;
        }

        if (!issued) {
            Logger.i(Const.TAG, "the load index is last one,remove no callback task");
        }

        return issued;
    }

    private boolean requestBean(int index) {
        if (index >= 0 && index < mConfigBeans.size()) {
            if (mLoadingStatus.setBeanLoading(index, true)) {
                PosBean posBean = mConfigBeans.get(index);
                if (requestBean(posBean)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean requestBean(PosBean bean) {
        // FIXME: 需要确保如果已经notify 成功后, 不应该再加装

        String adName = bean.getAdName();
        Logger.i(Const.TAG, "to load " + adName);

        mRequestLogger.requestBegin(adName);
        NativeAdLoader adLoader = mLoaderMap.getAdLoader(mContext, bean, this);
        if (adLoader != null) {
            if (mRequestParams != null) {
                adLoader.setRequestParams(mRequestParams);
            }
            adLoader.setLoadCallBack(this);
            adLoader.setPreload(mIsPreload);
            adLoader.setAdIndex(getAdTypeNameIndex(adName));
            adLoader.setIsFeed(mIsFeed);
            adLoader.loadAd();
            return true;
        } else {
            adFailedToLoad(adName, String.valueOf(NativeAdError.NO_AD_TYPE_EROOR));
            return false;
        }
    }

    @Override
    public void adLoaded(final String adTypeName) {
        Logger.i(Const.TAG, adTypeName + " load success");
        mRequestLogger.requestEnd(adTypeName, true, null);
        int index = getAdTypeNameIndex(adTypeName);
        if (checkPreAdIsLoading(index)) {
            mHighPriorityLoaded = true;
        }
        asyncCheckIfAllFinished("ad loaded:" + adTypeName);
        asyncIssueNext();
    }


    private boolean checkPreAdIsLoading(int index) {
        for (int i = 0; i < index; i++) {
            if (!mLoadingStatus.isBeanLoading(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void adFailedToLoad(final String adTypeName, final String errorString) {
        Logger.i(Const.TAG, adTypeName + " load fail :error" + errorString);
        mRequestLogger.requestEnd(adTypeName, false, errorString);
        asyncCheckIfAllFinished("ad load fail:" + adTypeName);
        asyncIssueNext();
    }

    @Override
    public void onAdClick(final INativeAd nativeAd) {
        ThreadHelper.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCallBack != null) {
                    mCallBack.adClicked(nativeAd);
                }
            }
        });
    }

    Runnable mAsyncCheckPointRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mRequestLogger.checkIfHaveAdLoadFinish() && (mCurrentPointId < mCheckPointNum) &&
                    ((mFirstCheckTime + mCurrentPointId * mCheckPointIntervalTime) < AD_PRIORITY_PROTECTION_TIME)) {
                asyncIssueNext();
                mCurrentPointId++;
                mCheckPointTimer = new TimeoutTask(mAsyncCheckPointRunnable, "CheckPointTimer");
                mCheckPointTimer.start(mCheckPointIntervalTime);
            } else {
                stopCheckPointTimeOutTask();
            }
        }
    };

    Runnable mAsyncIssueNextRunnable = new Runnable() {
        @Override
        public void run() {

            if (!mIsFinished) {
                if (!mOptimizeEnabled || !mHighPriorityLoaded) {
                    issueToLoadNext();
                } else {
                    Logger.i(Const.TAG, "optimized skip issueNext");
                }
            }
        }
    };

    Runnable mAsyncFinishCheckRunnable = new Runnable() {
        @Override
        public void run() {
            asyncCheckIfAllFinished("timeout ");
        }
    };

    Runnable mFinishCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkIfAllfinished();
        }
    };

    protected void asyncCheckIfAllFinished(String from) {
        Logger.d(Const.TAG, "async check if all finished --> " + from);
        ThreadHelper.postOnUiThread(mFinishCheckRunnable);
    }

    /*
         判断本次请求是否结束：
         1、高优先级的如果没返回且没超时，等待
         2、高优先级的成功了，回调成功
         3、高优先级的失败了判断低优先级的
    */
    protected void checkIfAllfinished() {
        Logger.i(Const.TAG, "check finish");

        if (mIsFinished) {
            Logger.w(Const.TAG, "already finished");
            return;
        }

        for (PosBean posBean : mConfigBeans) {
            String adTypeName = posBean.getAdName();
            RequestResultLogger.Model resultModel = mRequestLogger.getFinishedItem(adTypeName);
            if (resultModel == null && mPriorityProtectionTimer != null && !mPriorityProtectionTimer.mTimeout) {
                Logger.w(Const.TAG, "is timeout:" + mPriorityProtectionTimer.mTimeout + "...wait");
                return;
            } else if (resultModel != null && resultModel.isSuccess()) {
                notifyAdLoaded();
                break;
            }
        }

        if (!mIsFinished) {
            if (isAllLoaderFinished()) {
                notifyAdFailed(NativeAdError.NO_FILL_ERROR);
            }
        }
    }

    // 是否所有loader 都已经加载成功
    protected boolean isAllLoaderFinished() {
        boolean allLoaderFinished = true;

        if (mLoadingStatus.getWaitingBeansNumber() == 0) {
            for (PosBean posBean : mConfigBeans) {
                String adTypeName = posBean.getAdName();
                NativeAdLoader loader = mLoaderMap.getAdLoader(adTypeName);
                if (loader != null && !loader.isLoaded()) {
                    allLoaderFinished = false;
                    break;
                }
            }
        } else {
            allLoaderFinished = false;
        }

        return allLoaderFinished;
    }

    private void asyncIssueNext() {
        ThreadHelper.postOnUiThread(mAsyncIssueNextRunnable);
    }

    void stopTimeOutTask() {
        if (mPriorityProtectionTimer != null) {
            mPriorityProtectionTimer.stop();
            mPriorityProtectionTimer = null;
        }
    }

    void stopCheckPointTimeOutTask() {
        if (mCheckPointTimer != null) {
            mCheckPointTimer.stop();
            mCheckPointTimer = null;
        }
    }

    protected void notifyAdLoaded() {
        Logger.i(Const.TAG, "notifyAdLoaded time(ms): " + (System.currentTimeMillis() - mLoadStartTime));
        notifyAdLoadFinished(true, 0);
    }

    protected void notifyAdFailed(int errorCode) {
        Logger.i(Const.TAG, "notifyAdFailed time(ms): " + (System.currentTimeMillis() - mLoadStartTime));
        notifyAdLoadFinished(false, errorCode);
    }

    protected void notifyAdLoadFinished(final boolean loaded, final int errorCode) {

        mIsFinished = true;
        ThreadHelper.revokeOnUiThread(mAsyncCheckPointRunnable);
        stopCheckPointTimeOutTask();
        mRequestLogger.setRequestResult(loaded ? "ok" : "fail.error:" + errorCode);

        ThreadHelper.revokeOnUiThread(mFinishCheckRunnable);
        ThreadHelper.revokeOnUiThread(mAsyncFinishCheckRunnable);
        ThreadHelper.revokeOnUiThread(mAsyncIssueNextRunnable);
        stopTimeOutTask();
        ThreadHelper.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCallBack != null) {
                    //移除吐出去的广告
                    //removeAdFromPool(ad);
                    if (loaded) {
                        mCallBack.adLoaded();
                    } else {
                        mCallBack.adFailedToLoad(errorCode);
                    }
                }
            }
        });
    }

    public List<INativeAd> getAdList(int num) {
        // FIXME: 这里的priority 需要重新管理, 用优先级列表进行管理
        Logger.i(Const.TAG, "getAdList");

        List<INativeAd> mAdList = new ArrayList<INativeAd>();
        if (mConfigBeans == null || mConfigBeans.isEmpty() || mLoaderMap == null) {
            return mAdList;
        }

        if (mAdList.size() < num) {
            //再按照优先级去取
            for (PosBean bean : mConfigBeans) {
                INativeAdLoader loader = mLoaderMap.getAdLoader(bean.getAdName());
                if (loader == null) {
                    continue;
                }

                int needAdNum = num - mAdList.size();
                List<INativeAd> tempList = loader.getAdList(needAdNum);
                if (tempList != null && !tempList.isEmpty()) {
                    mAdList.addAll(tempList);
                    Logger.d(Const.TAG, "this mAdList size =" + mAdList.size());
                }

                if (mAdList.size() >= num) {
                    break;
                }
            }
        }

        for (INativeAd ad : mAdList) {
            NativeAd nativeAd = (NativeAd) ad;
            nativeAd.setReUseAd();
        }
        return mAdList;
    }

    @Override
    public void onPause() {
        if (mConfigBeans == null || mConfigBeans.isEmpty()) {
            return;
        }
        for (PosBean posBean : mConfigBeans) {
            NativeAdLoader loader = mLoaderMap.getAdLoader(posBean.name);
            if (loader != null) {
                loader.onPause();
            }
        }
    }

    @Override
    public void onResume() {
        if (mConfigBeans == null || mConfigBeans.isEmpty()) {
            return;
        }
        for (PosBean posBean : mConfigBeans) {
            NativeAdLoader loader = mLoaderMap.getAdLoader(posBean.name);
            if (loader != null) {
                loader.onResume();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mConfigBeans == null || mConfigBeans.isEmpty()) {
            return;
        }
        for (PosBean posBean : mConfigBeans) {
            NativeAdLoader loader = mLoaderMap.getAdLoader(posBean.name);
            if (loader != null) {
                loader.onDestroy();
            }
        }
    }


    public void enableVideoAd() {
        mVideoAdEnable = true;
    }

    public void enableBannerAd() {
        mBannerAdEnable = true;
    }


    public void setDisableAdType(List<String> adTypes) {
        if (adTypes == null) {
            return;
        }
        mDisableTypeList.addAll(adTypes);
    }

    public void setIsFeed() {
        mIsFeed = true;
    }

}
