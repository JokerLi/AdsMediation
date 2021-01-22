package com.buffalo.adsdk.nativead;

import android.content.Context;
import android.text.TextUtils;

import com.buffalo.adsdk.BuildConfig;
import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.NativeAdError;
import com.buffalo.adsdk.adapter.BaseNativeLoaderAdapter;
import com.buffalo.adsdk.adapter.CustomVideoAdapter;
import com.buffalo.adsdk.adapter.NativeloaderAdapter;
import com.buffalo.adsdk.banner.BannerParams;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.buffalo.adsdk.config.PosBean;
import com.buffalo.adsdk.report.ReportFactory;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.utils.Logger;
import com.buffalo.utils.ThreadHelper;
import com.buffalo.utils.UniReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdLoader extends BaseNativeLoaderAdapter implements NativeloaderAdapter.NativeAdapterListener, INativeAd.IAdOnClickListener, LifeCycleDelegate {
    private static final String TAG = "NativeAdLoader";

    final private static int DEFAULT_TRY_NUMBER = 2;
    final private static int DEFAULT_TIMEOUT_TIME = 8 * 1000;

    final public String[] mPlacementIds;
    final public PosBean mPosBean;
    private List<INativeAd> mAdPool;
    private NativeloaderAdapter mInternalNativeLoader;
    private boolean mLoaded = true;
    Map<String, Object> mLocalExtras;

    private long mLastLoadTime = 0;
    private int mPlacementIndex = 0;
    private int mTryNumber = 1;
    private int mLoadNumber = 1;
    private TimeoutTask mLoaderTimerOutTask = null;
    //判断是否需要挑选下所有的超级强量的广告
    private boolean mSelectAllPriorityAd = true;
    private String mPosId;
    private boolean mIsPreload = false;
    private int mAdIndex = 0;
    private boolean mIsFeed = false;

    public NativeAdLoader(Context context, String posId, String adTypeName, String params, PosBean posBean, NativeloaderAdapter internalLoader) {
        super(context, posId, adTypeName);
        mPosId = posId;
        mPosBean = posBean;
        mInternalNativeLoader = internalLoader;

        if (!TextUtils.isEmpty(params)) {
            String loaderType = internalLoader.getAdKeyType();
            if (Const.KEY_FB.equals(loaderType)) {
                mPlacementIds = params.split(",");
            } else {
                mPlacementIds = new String[1];
                mPlacementIds[0] = params;
            }
            if (mInternalNativeLoader != null && mInternalNativeLoader instanceof CustomVideoAdapter) {
                initVideoSDK();//video init sdk to load
            }

        } else {
            mPlacementIds = null;
        }
        mAdPool = new ArrayList<INativeAd>();
    }

    public void setPreload(boolean isPreload) {
        this.mIsPreload = isPreload;
    }

    public void setAdIndex(int index) {
        this.mAdIndex = index;
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    @Override
    public void loadAds(int num) {
        load(num);
    }

    @Override
    public void loadAd() {
        load(1);
    }

    private void load(int num) {
        if (mPlacementIds == null || mPlacementIds.length == 0) {
            if (mNativeAdListener == null) {
                return;
            }
            mNativeAdListener.adFailedToLoad(this.getAdTypeName(), NativeAdError.ERROR_CONFIG);
            return;
        }

        //如果缓存足够就不再去load了
        removeExpiredAds(mAdPool);
        if (mAdPool.size() >= num) {
            Logger.i(Const.TAG, "adload has cache , cache size :" + mAdPool.size());
            if (mNativeAdListener == null) {
                return;
            }
            mNativeAdListener.adLoaded(getAdTypeName());
            return;
        }

        if (!mLoaded) {
            return;
        }
        mLastLoadTime = System.currentTimeMillis();
        mLoadNumber = Math.max(num, mInternalNativeLoader.getDefaultLoadNum());
        //外部可以指定自家广告请求大小，不走默认的十个
        mTryNumber = mPlacementIds.length > 1 ? DEFAULT_TRY_NUMBER : 1;
        mLoaded = false;
        if (mLoaderTimerOutTask == null) {
            mLoaderTimerOutTask = new TimeoutTask(mTimeoutRun, "Loader_Timeout");
            mLoaderTimerOutTask.start(DEFAULT_TIMEOUT_TIME);
        }
        if (requestParams != null) {
            mSelectAllPriorityAd = requestParams.isSelectAllPriorityAd();
        }
        issueNextPlacementId();
    }

    private void issueNextPlacementId() {
        mTryNumber = mTryNumber - 1;

        int index = mPlacementIndex % mPlacementIds.length;
        String placementId = mPlacementIds[index];

        mPlacementIndex = mPlacementIndex + 1;
        mLocalExtras = getLoadExtras(mLoadNumber, placementId);
        mInternalNativeLoader.setAdapterListener(this);
        mLastLoadTime = System.currentTimeMillis();
        mInternalNativeLoader.loadNativeAd(mContext, mLocalExtras);

        if (BuildConfig.DEBUG) {
            Logger.i(Const.TAG, "adload load.begin:" + getAdTypeName() + " num:" + mLoadNumber + " placement:" + placementId);
        }
    }

    private Map<String, Object> getLoadExtras(int num, String placementId) {
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put(BaseNativeAd.KEY_JUHE_POSID, mPositionId);
        extras.put(BaseNativeAd.KEY_PLACEMENT_ID, placementId);
        extras.put(BaseNativeAd.KEY_LOAD_SIZE, num);

        extras.put(BaseNativeAd.KEY_RCV_REPORT_RES, mInternalNativeLoader.getReportRes(getAdTypeName()));
//        extras.put(BaseNativeAd.KEY_PEG_REPORT_RES, mInternalNativeLoader.getReportRes(NativeloaderAdapter.RES_TYPE_PEG, getAdTypeName()));

        extras.put(BaseNativeAd.KEY_REPORT_PKGNAME, mInternalNativeLoader.getReportPkgName(getAdTypeName()));
//        long cacheTime = AdManager.getAdTypeCacheTime(mInternalNativeLoader.getAdKeyType());
        long defaultCacheTime = mInternalNativeLoader.getDefaultCacheTime();
        if (defaultCacheTime <= Const.cacheTime.min_cache_time) {
            Logger.e(Const.TAG, "default cache time to low: " + defaultCacheTime + " reset to 30min");
            defaultCacheTime = Const.cacheTime.min_cache_time;
        }
        //缓存时间暂时不支持外部设置，到时候云端配置更合适
        extras.put(BaseNativeAd.KEY_CACHE_TIME, defaultCacheTime);
        if (requestParams != null) {
            if (requestParams instanceof BannerParams) {
                extras.put(BaseNativeAd.KEY_BANNER_VIEW_SIZE, ((BannerParams) requestParams).getBannerAdSize());
            }
            extras.put(BaseNativeAd.KEY_CHECK_VIEW, !requestParams.getReportShowIgnoreView());
            extras.put(BaseNativeAd.KEY_FILTER_ADMOB_INSTALL_AD, requestParams.isFilterAdmobInstallAd());
            extras.put(BaseNativeAd.KEY_FILTER_ADMOB_CONTENT_AD, requestParams.isFilterAdmobContentAd());
            extras.put(BaseNativeAd.KEY_EXTRA_OBJECT, requestParams.getExtraObject());
            extras.put(BaseNativeAd.KEY_TAB_ID, requestParams.getTabId());
            extras.put(BaseNativeAd.KEY_IS_TOP, requestParams.getIsTop());
        } else {
            extras.put(BaseNativeAd.KEY_CHECK_VIEW, true);
        }
        extras.put(BaseNativeAd.KEY_IS_FEED, mIsFeed);
        return extras;
    }

    @Override
    public INativeAd getAd() {
        removeExpiredAds(mAdPool);

        INativeAd nativeAd = null;
        if (!mAdPool.isEmpty()) {
            nativeAd = mAdPool.remove(0);
        }
        return nativeAd;
    }

    @Override
    public List<INativeAd> getAdList(int num) {
        return getPriorityAdList(false, num);
    }

    public List<INativeAd> getPriorityAdList(int num) {
        return getPriorityAdList(true, num);
    }

    /**
     * @param num 需要的个数
     * @return
     */
    private List<INativeAd> getPriorityAdList(boolean filterPriority, int num) {
        removeExpiredAds(mAdPool);
        ArrayList<INativeAd> tempList = new ArrayList<INativeAd>();
        int count = mAdPool.size();
        for (int i = 0; i < count; i++) {
            INativeAd ad = mAdPool.get(i);
            if (filterPriority) {
                if (ad.isPriority()) {
                    tempList.add(ad);
                } else if (!mSelectAllPriorityAd) {
                    //如果需要超级强量，碰到没有超级强量的直接跳出循环不再判断下面的，以免打乱顺序。
                    break;
                }
            } else {
                //如果不特别的过滤超级抢量就直接添加
                tempList.add(ad);
            }
            if (tempList.size() >= num) {
                break;
            }
        }
        mAdPool.removeAll(tempList);
        return tempList;
    }

    @Override
    public void onNativeAdLoaded(INativeAd nativeAd) {
        mLoaded = true;
        appendAd(nativeAd);
        stopTimeOutTask();
        if (mNativeAdListener == null) {
            return;
        }
        mNativeAdListener.adLoaded(getAdTypeName());
    }

    @Override
    public void onNativeAdFailed(String errorCode) {
        if (BuildConfig.DEBUG) {
            Logger.i(Const.TAG, "adload load.end.failed(" + getAdTypeName() + ") error:" + errorCode);
        }
        if (mTryNumber <= 0) {
            mLoaded = true;
            stopTimeOutTask();
            if (mNativeAdListener == null) {
                return;
            }
            mNativeAdListener.adFailedToLoad(getAdTypeName(), errorCode);
        } else {
            issueNextPlacementId();
        }
    }

    @Override
    public void onAdClick(INativeAd nativeAd) {
        if (mNativeAdClickListener != null) {
            mNativeAdClickListener.onAdClick(nativeAd);
        }
        recordClick(nativeAd);
    }

    @Override
    public void onNativeAdLoaded(List<INativeAd> list) {
        mLoaded = true;

        appendAd(list);
        stopTimeOutTask();
        if (mNativeAdListener == null) {
            return;
        }
        mNativeAdListener.adLoaded(getAdTypeName());
    }

    void appendAd(List<INativeAd> adList) {
        if (adList == null) {
            return;
        }

        for (INativeAd ad : adList) {
            appendAd(ad);
        }
    }

    void appendAd(INativeAd ad) {
        if (BuildConfig.DEBUG) {
            Logger.i(Const.TAG, "adload load.end.adloaded(" + getAdTypeName() + ") title:" + (ad != null ? ad.getAdTitle() : "null"));
        }
        mLocalExtras.put(BaseNativeAd.KEY_AD_TYPE_NAME, getAdTypeName());
        NativeAd nativeAd = new NativeAd(mContext, this, mLocalExtras, (BaseNativeAd) ad);
        nativeAd.setAdPriorityIndex(mAdIndex);
        mAdPool.add(nativeAd);
    }

    private void recordClick(INativeAd nativeAd) {
        if (nativeAd == null) {
            return;
        }
        Map<String, String> reportParams = null;
        String rawString = "";
        try {
            NativeAd ad = (NativeAd) nativeAd;
            //getRawString for click
            rawString = ad.getRawString(2);
            reportParams = ad.addDupReportExtra(true, ad.hasReportClick(), ad.getExtraReportParams());
            ad.setHasReportClick(true);
        } catch (Exception e) {

        }
        String placementID = (String) mLocalExtras.get(BaseNativeAd.KEY_PLACEMENT_ID);
        boolean isNativeAd = mInternalNativeLoader.getAdType() == Const.AdType.NATIVE;
        UniReport.report(ReportFactory.CLICK, mInternalNativeLoader.getReportPkgName(getAdTypeName()),
                mPositionId, mInternalNativeLoader.getReportRes(getAdTypeName()),
                reportParams, placementID, isNativeAd, rawString);
    }

    Runnable mTimeoutRun = new Runnable() {
        @Override
        public void run() {
            ThreadHelper.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Logger.i(TAG, getAdTypeName() + " 8s no callback timeout");
                    mTryNumber = 0;
                    onNativeAdFailed("8 timeout");
                }
            });
        }
    };

    void stopTimeOutTask() {
        if (mLoaderTimerOutTask != null) {
            mLoaderTimerOutTask.stop();
            mLoaderTimerOutTask = null;
        }
    }

    @Override
    public void onPause() {
        if (mInternalNativeLoader instanceof CustomVideoAdapter) {
            ((CustomVideoAdapter) mInternalNativeLoader).onPause();
        }
    }

    @Override
    public void onResume() {
        if (mInternalNativeLoader instanceof CustomVideoAdapter) {
            ((CustomVideoAdapter) mInternalNativeLoader).onResume();
        }
    }

    @Override
    public void onDestroy() {
        if (mInternalNativeLoader instanceof CustomVideoAdapter) {
            ((CustomVideoAdapter) mInternalNativeLoader).onDestroy();
        }
    }

    public Const.AdType getAdType() {
        return mInternalNativeLoader.getAdType();
    }


    public void initVideoSDK() {
        if (mInternalNativeLoader instanceof CustomVideoAdapter && (mPlacementIds != null && mPlacementIds.length > 0)) {
            ((CustomVideoAdapter) mInternalNativeLoader).initVideoSDK(mContext, getLoadExtras(1, mPlacementIds[0]));
        }
    }

    public void setIsFeed(boolean isFeed) {
        mIsFeed = isFeed;
    }

}
