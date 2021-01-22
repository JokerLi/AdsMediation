package com.buffalo.adsdk.nativead;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.RequestParams;
import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.utils.NativeReportUtil;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;
import com.buffalo.utils.Logger;
import com.buffalo.utils.NetworkUtil;
import com.buffalo.utils.ThreadHelper;
import com.buffalo.baseapi.ads.INativeAd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This class is not thread safe and should only be called from the UI thread.
 */
public class FeedListAdManager {
    private static final String TAG = FeedListAdManager.class.getSimpleName();
    private static final int DEFAULT_CACHE_LIMIT = 3;
    private static final int DEFAULT_RETRY_TIME_MILLISECONDS = 1000; // 1 second
    private static final int MAXIMUM_RETRY_TIME_MILLISECONDS = 16 * 1000; // 16 second.
    private static final double EXPONENTIAL_BACKOFF_FACTOR = 2.0;
    private static final int DUPL_AD_CACHE_NUM = 50;

    private static final int ERROR_DUPLE_AD                  = 10000;//重复广告
    private static final int ERROR_AD_IS_NULL                = 10001;//广告位空
    private static final int ERROR_CACHE_IS_NULL             = 10002;//缓存个数为0
    private static final int ERROR_CACHE_IS_EXPIRED          = 10003;//广告过期
    private static final int ERROR_NOT_CALL_LOAD             = 10004;//getAd之前没有调用load
    private static final int CALLBACK_SUCCESS_AD_IS_NULL     = 100051;//

    private static final String NET_WORK_TYPE = "net_work_type";
    private static final String AD_CACHE_NUM = "ad_cache_num";

    @NonNull
    private final List<NativeAd> mNativeAdCache;
    @NonNull
    private final Handler mReplenishCacheHandler;
    @NonNull
    private final Runnable mReplenishCacheRunnable;

    private boolean mOnceRequestInFlight;
    private int mRetryTimeMilliseconds;

    @Nullable
    private NativeAdManagerInternal mNativeAdManagerInternal;
    @Nullable
    private INativeAdLoaderListener mINativeAdLoaderListener;
    private int mCacheSize = DEFAULT_CACHE_LIMIT;
    @Nullable
    private Context mContext;
    @Nullable
    private String mPosid ;
    private boolean mIsFilterDuplAd = false;
    private boolean mHaveCalledLoad = false;
    private boolean mIsSupportPriority = false;

    private long mStartRequestTime = 0L;
    private FeedListListener mFeedListener;
    private List<NativeAd> mDuplAdCache;//重复广告缓存池
    private String mLastAdTitle;
    private boolean mIsOnceLoad = true;
    private int mRequestOrionAdNum;//猎户广告请求的个数
    private AtomicBoolean mIsFirstLoadSuccess;
    private AtomicBoolean mIsFirstLoadFailed;
    private AtomicInteger mRequestDupleTimes;
    private AtomicInteger mReponseTimes;
    private Map<String, List<NativeAd>> mOrionBrandCache;
    private String mRequestTabId = "";
    private boolean mIsTop;

    public FeedListAdManager(@NonNull final Context context,
                             @NonNull final String posid) {
        this(context, posid, DEFAULT_CACHE_LIMIT);
    }

    public FeedListAdManager(@NonNull final Context context,
                             @NonNull final String posid, int cacheSize) {
        this(new ArrayList<NativeAd>(cacheSize));
        this.mContext = context;
        this.mPosid = posid;
        if(cacheSize <= 0){
            this.mCacheSize = DEFAULT_CACHE_LIMIT;
        }else{
            this.mCacheSize = cacheSize;
        }
    }

    public void setFeedListener(FeedListListener feedListener){
        mFeedListener = feedListener;
    }

    private FeedListAdManager(@NonNull final List<NativeAd> nativeAdCache) {
        mOrionBrandCache = new HashMap<String, List<NativeAd>>();
        mIsFirstLoadSuccess = new AtomicBoolean(false);
        mIsFirstLoadFailed = new AtomicBoolean(false);
        mRequestDupleTimes = new AtomicInteger(0);
        mReponseTimes = new AtomicInteger(0);
        mDuplAdCache = new ArrayList<NativeAd>();
        mNativeAdCache = nativeAdCache;
        mReplenishCacheHandler = ThreadHelper.getUiThreadHandler();
        mReplenishCacheRunnable = new Runnable() {
            @Override
            public void run() {
                if(mRetryTimeMilliseconds == 2 * DEFAULT_RETRY_TIME_MILLISECONDS){
                    NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_REQUEST_2, mPosid);
                }else if(mRetryTimeMilliseconds == 4 * DEFAULT_RETRY_TIME_MILLISECONDS){
                    NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_REQUEST_4, mPosid);
                }else if(mRetryTimeMilliseconds == 8 * DEFAULT_RETRY_TIME_MILLISECONDS){
                    NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_REQUEST_8, mPosid);
                }else if(mRetryTimeMilliseconds == 16 * DEFAULT_RETRY_TIME_MILLISECONDS){
                    NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_REQUEST_16, mPosid);
                }
                replenishCache(true);
            }
        };
        initParams();
        resetRetryTime();
    }

    private void initParams() {
        mINativeAdLoaderListener = new INativeAdLoaderListener() {
            @Override
            public void adLoaded() {
                Logger.i(TAG, "feedsAd adLoaded..... ");
                resetRetryTime();
                if (mNativeAdManagerInternal == null) {
                    Logger.i(TAG, "feedsAd adLoaded.....but status is error ");
                    return;
                }
                saveNativeAd(mNativeAdManagerInternal.getAd());
                mIsFirstLoadSuccess.set(true);
                //广告缓存可用的回调,会有多次回调的情况
                if(mNativeAdCache.size() == 1 && mFeedListener != null){
                    Logger.i(TAG, "feedlist ad call back success..... ");
                    mFeedListener.onAdsAvailable();
                }
                if(mRequestDupleTimes.get() < mCacheSize && mReponseTimes.get() < mCacheSize){
                    replenishCache(true);
                }else{
                    mOnceRequestInFlight = false;
                    Logger.i(TAG, "stop request...");
                }
            }

            @Override
            public void adFailedToLoad(int errorcode) {
                mIsFirstLoadFailed.set(true);
                //失败的上报
                Map<String, String> map = new HashMap<String, String>();
                map.put(NET_WORK_TYPE, String.valueOf(NetworkUtil.getNetworkState(AdManager.getContext())));
                map.put(AD_CACHE_NUM, String.valueOf(mNativeAdCache.size()));
                NativeReportUtil.doGetAdFailReport(Const.Event.FEED_AD_FAIL, mPosid, String.valueOf(errorcode), map);
                Logger.i(TAG, "feedsAd adFailed..... errorCode:" + errorcode);

                //重试到最大16s，停止重试
                if (mRetryTimeMilliseconds >= MAXIMUM_RETRY_TIME_MILLISECONDS) {
                    Logger.i(TAG, "feedsAd has fail to request max num");
                    resetRetryTime();
                    mOnceRequestInFlight = false;

                    if(mIsOnceLoad){//getAd还没有触发预拉取
                        Logger.i(TAG, "failed: once load end ,cahce num is :" + mNativeAdCache.size());
                        NativeReportUtil.doGetAdFailReport(Const.Event.FEED_AD_ONCE_LOAD_FAIL_NUM, mPosid, String.valueOf(errorcode), map);
                    }else{//getAd触发的预拉取
                        Logger.i(TAG, "failed: once getAd load end ,cahce num is :" + mNativeAdCache.size());
                        NativeReportUtil.doGetAdFailReport(Const.Event.FEED_AD_ONCE_GETAD_LOAD_FAIL_NUM, mPosid, String.valueOf(errorcode), map);
                    }
                    return;
                }

                updateRetryTime();
                mReplenishCacheHandler.postDelayed(mReplenishCacheRunnable, mRetryTimeMilliseconds);
            }

            @Override
            public void adClicked(INativeAd nativeAd) {
                if(mFeedListener != null){
                    mFeedListener.onAdClick(nativeAd);
                }
            }
        };
    }

    private boolean saveNativeAd(final INativeAd nativeAd){
        if(nativeAd == null || !(nativeAd instanceof NativeAd)){
            Logger.i(TAG, "feedsAd adLoaded.....but ad is null ");
            NativeReportUtil.doGetAdFailReport(Const.Event.FEED_AD_FAIL, mPosid, String.valueOf(CALLBACK_SUCCESS_AD_IS_NULL));
            return false;
        }

        if(mNativeAdCache.size() == 0){
            if(TextUtils.isEmpty(mLastAdTitle) || !mLastAdTitle.equals(nativeAd.getAdTitle()) || !mIsFilterDuplAd){
                addAd2Cache((NativeAd)nativeAd);
                Logger.i(TAG, "first ad is not duple, call back success");
            }else{
                Logger.i(TAG, "first ad is duple, try to get second priority ad");
                addAd2DupleCache(nativeAd);

                INativeAd ad = mNativeAdManagerInternal.getAd();
                if (null != ad && ad instanceof NativeAd) {
                    if (TextUtils.isEmpty(mLastAdTitle) || !mLastAdTitle.equals(ad.getAdTitle())){
                        Logger.i(TAG, "second ad is not  duple, call back success");
                        addAd2Cache((NativeAd) nativeAd);
                    }else{
                        Logger.i(TAG, "second priority ad is duple, not callback success");
                        mRequestDupleTimes.incrementAndGet();
                    }
                }else{
                    Logger.i(TAG, "second priority ad is null, not callback success");
                    mRequestDupleTimes.incrementAndGet();
                }
            }
        }else{
            addAd2Cache((NativeAd) nativeAd);
        }


        Map<String, String> map = new HashMap<String, String>();
        map.put(NET_WORK_TYPE, String.valueOf(NetworkUtil.getNetworkState(AdManager.getContext())));
        map.put(AD_CACHE_NUM, String.valueOf(mNativeAdCache.size()));
        NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_REQUEST_SUCCESS_NUM, mPosid, nativeAd.getAdTypeName(), (System.currentTimeMillis() - mStartRequestTime), map);
        Logger.i(TAG, "add to the AdCache size is :" + mNativeAdCache.size());

        return false;
    }
    private NativeAd mOrionBrandAd;
    private void addAd2Cache(final NativeAd nativeAd){
        mReponseTimes.incrementAndGet();
        if(Const.KEY_OB.equals(nativeAd.getAdTypeName())){
            if(mOrionBrandAd == null){
                mOrionBrandAd = nativeAd;
            }
            mOrionBrandAd.setAdPriorityIndex(nativeAd.getAdPriorityIndex());
            if(!mNativeAdCache.contains(mOrionBrandAd)){
                mNativeAdCache.add(mOrionBrandAd);
            }
            List<NativeAd> list = mOrionBrandCache.get(nativeAd.getTypeId());
            if(list == null){
                list = new ArrayList<NativeAd>();
            }
            list.add(nativeAd);
            mOrionBrandCache.put(nativeAd.getTypeId(), list);
        }else{
            mRequestDupleTimes.set(0);
            mNativeAdCache.add(nativeAd);
        }
    }

    public void loadAds() {
        if(!mHaveCalledLoad){
            Logger.i(TAG, "load thread:   " + Thread.currentThread().getName());
            mHaveCalledLoad = true;
            loadAds(new NativeAdManagerInternal(mContext, mPosid));
        }else{
            Logger.i(TAG, "feed ad has load, can not perform load");
        }
    }

    private void loadAds(final NativeAdManagerInternal nativeAdManagerInternal) {
        clear();
        mNativeAdManagerInternal = nativeAdManagerInternal;
        mNativeAdManagerInternal.setIsFeed();
        mNativeAdManagerInternal.setOpenPriority(mIsSupportPriority);
        mNativeAdManagerInternal.setAdListener(mINativeAdLoaderListener);
        mOnceRequestInFlight = true;
        NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_ONCE_LOAD_NUM, mPosid);
        Logger.i(TAG, "begin loadAd: once load begin load");
        replenishCache(false);
    }
    public void setIsTop(boolean isTop){
        mIsTop = isTop;
    }

    /**
     * Replenish ads in the ad source cache.
     *
     * Calling this method is useful for warming the cache without dequeueing an ad.
     */
    private void replenishCache(boolean isPreload) {
        if (mNativeAdManagerInternal != null && mNativeAdCache.size() < mCacheSize) {
            Logger.i(TAG, "replenishCache: " + isPreload);
            RequestParams params = new RequestParams();
            params.setTabId(mRequestTabId);
            params.setPicksLoadNum(mRequestOrionAdNum);
            params.setIsTop(mIsTop);
            mNativeAdManagerInternal.setRequestParams(params);
            mNativeAdManagerInternal.loadAd();
            mStartRequestTime = System.currentTimeMillis();
            //load的个数
            if(isPreload){
                NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_PRELOAD_NUM, mPosid);
            }else{
                NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_REQUEST_NUM, mPosid);
            }
        }else if(mNativeAdCache.size() >= mCacheSize){
            if(mIsOnceLoad){//getAd还没有触发预拉取
                Logger.i(TAG, "success: once load end ,cahce num is :" + mNativeAdCache.size());
                NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_ONCE_LOAD_SUCCESS_NUM, mPosid);
            }else{//getAd触发的预拉取
                Logger.i(TAG, "success: once getAd load end ,cahce num is :" + mNativeAdCache.size());
                NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_ONCE_GETAD_LOAD_SUCCESS_NUM, mPosid);
            }
            //当次load结束
            mOnceRequestInFlight = false;

        }
    }

    /**
     * Clears the ad source, removing any currently queued ads.
     */
    void clear() {
        mNativeAdCache.clear();
        resetRetryTime();
    }

    private INativeAd getAdFromOrionBrandCache(INativeAd ad, String type){
        if(ad.getAdTypeName().startsWith(Const.KEY_OB)){
            List<NativeAd> list = mOrionBrandCache.get(type);
            isAdCanUse(list);
            if(list != null && list.size() > 0){
                return list.remove(0);
            }
            return null;
        }

        return ad;
    }

    private boolean isNotNeedRemoveOb(){
        Set<String> keys = mOrionBrandCache.keySet();
        if(null != keys && keys.size() > 0){
            for(String key : keys){
                List<NativeAd> list =  mOrionBrandCache.get(key);
                return isAdCanUse(list);
            }
        }
        return false;
    }

    private boolean isAdCanUse(List<NativeAd> list){
        if(list != null && list.size() > 0){
            Iterator<NativeAd> i = list.iterator();
            while (i.hasNext()){
                NativeAd ad = i.next();
                if(ad.hasExpired()){
                    i.remove();
                }
            }
            return list.size() > 0;
        }
        return false;
    }


    public void setTabId(String tabId){
        mRequestTabId = tabId;
    }

    public INativeAd getAd(){
        return getAd("");
    }

    @Nullable
    public INativeAd getAd(final String type) {
        NativeReportUtil.doNativeAdSuccessReport(Const.Event.GET_FEED_AD, mPosid);
        if(mHaveCalledLoad){
            // Starting an ad request takes several millis. Post for performance reasons.
            if (!mOnceRequestInFlight) {
                mOnceRequestInFlight = true;
                mIsOnceLoad = false;
                mRequestTabId = type;
                Logger.i(TAG, "begin getAd: once load begin");
                NativeReportUtil.doNativeAdSuccessReport(Const.Event.FEED_AD_ONCE_GETAD_LOAD_NUM, mPosid);
                mReponseTimes.set(0);
                mReplenishCacheHandler.post(mReplenishCacheRunnable);
            }else{
                Logger.i(TAG, "begin getAd: once load is loading");
            }

            return ThreadHelper.runOnUiThreadBlockingNoException(new Callable<INativeAd>() {
                @Override
                public INativeAd call() throws Exception {
                    int errorCode = -1;
                    //从最新广告池获取
                    if(mNativeAdCache.size() <= 0){
                        errorCode = ERROR_CACHE_IS_NULL;
                    }else{
                        if(Logger.isDebug){
                            Logger.i(TAG, "before sort{");
                            for(int i = 0;i<mNativeAdCache.size();i++){
                                Logger.i(TAG, "" + mNativeAdCache.get(i).getAdPriorityIndex());
                            }
                            Logger.i(TAG, "          }");
                        }

                        Collections.sort(mNativeAdCache);

                        if(Logger.isDebug){
                            Logger.i(TAG, "after sort{");
                            for(int i = 0;i<mNativeAdCache.size();i++){
                                Logger.i(TAG, "" + mNativeAdCache.get(i).getAdPriorityIndex());
                            }
                            Logger.i(TAG, "          }");
                        }
                        Iterator<NativeAd> iterator = mNativeAdCache.iterator();
                        while (iterator.hasNext()){
                            INativeAd ad = getAdFromOrionBrandCache(iterator.next(), type);

                            if(null == ad || ad.hasExpired()){
                                if(null != ad && ad.getAdTypeName().startsWith(Const.KEY_OB)){
                                    if(!isNotNeedRemoveOb()){
                                        iterator.remove();
                                    }
                                }else{
                                    iterator.remove();
                                }
                                if(null == ad){
                                    errorCode = ERROR_AD_IS_NULL;
                                }else if(ad.hasExpired()){
                                    //DELETE_EXPIRED_AD
                                    errorCode = ERROR_CACHE_IS_EXPIRED;
                                    doGetFeedAdSuccessReport(ad, Const.Event.DELETE_EXPIRED_AD);
                                }
                                continue;
                            }
                            if(!isSameTitle(ad.getAdTitle())){
                                if(ad.getAdTypeName().startsWith(Const.KEY_OB)){
                                    if(!isNotNeedRemoveOb()){
                                        iterator.remove();
                                    }
                                }else{
                                    iterator.remove();
                                }
                                Logger.i(TAG, "getAd from new cache - after remove cache size :" + mNativeAdCache.size());
                                doGetFeedAdSuccessReport(ad, Const.Event.GET_FEED_AD_SUCCESS_FROM_CACHE);
                                doGetFeedAdSuccessReport(ad);
                                return ad;
                            }else{
                                //广告重复
                                errorCode = ERROR_DUPLE_AD;
                            }
                        }
                    }

                    //如果广告池里面全为一样的，就删除一个，保证能够触发下次load
                    if(mNativeAdCache.size() == mCacheSize && mNativeAdCache.size() > 0){
                        INativeAd deleteAd = mNativeAdCache.remove(0);
                        addAd2DupleCache(deleteAd);
                        NativeReportUtil.doNativeAdSuccessReport(Const.Event.DELETE_AD_FROM_CACHE, mPosid);
                    }
                    Logger.i(TAG, "getAd from new cache failed - errorCode :" + errorCode);
                    doGetFeedAdFailReport(Const.Event.GET_FEED_AD_FAIL_FROM_CACHE, String.valueOf(errorCode));

                    //从聚合的缓存拿数据
                    errorCode = -1;
                    INativeAd tempAd = mNativeAdManagerInternal.getAd();
                    if(null == tempAd){
                        errorCode = ERROR_CACHE_IS_NULL;
                    }
                    while (null != tempAd){
                        if(tempAd.hasExpired()){
                            doGetFeedAdSuccessReport(tempAd, Const.Event.DELETE_EXPIRED_AD);
                            tempAd = mNativeAdManagerInternal.getAd();
                            errorCode = ERROR_CACHE_IS_EXPIRED;
                        }else{
                            if(!isSameTitle(tempAd.getAdTitle())){
                                doGetFeedAdSuccessReport(tempAd);
                                doGetFeedAdSuccessReport(tempAd, Const.Event.GET_FEED_AD_SUCCESS_FROM_JUHE_CACHE);
                                Logger.i(TAG, "getAd from juhe cache...... ");
                                return tempAd;
                            }else{
                                addAd2DupleCache(tempAd);
                                tempAd = mNativeAdManagerInternal.getAd();
                                errorCode = ERROR_DUPLE_AD;
                            }
                        }
                    }
                    Logger.i(TAG, "getAd from juhe cache failed - errorCode :" + errorCode);
                    doGetFeedAdFailReport(Const.Event.GET_FEED_AD_FAIL_FROM_JUHE_CACHE, String.valueOf(errorCode));

                    //如果新缓存&聚合没有数据，则用重复的广告填充
                    errorCode = -1;
                    if(mDuplAdCache.size() <= 0){
                        errorCode = ERROR_CACHE_IS_NULL;
                    }else{
                        Collections.sort(mDuplAdCache);
                        Iterator<NativeAd> mDupleAditerator = mDuplAdCache.iterator();
                        while (mDupleAditerator.hasNext()){
                            INativeAd ad = mDupleAditerator.next();
                            if(null == ad || ad.hasExpired()){
                                mDupleAditerator.remove();
                                if(null == ad){
                                    errorCode = ERROR_AD_IS_NULL;
                                }else if(ad.hasExpired()){
                                    errorCode = ERROR_CACHE_IS_EXPIRED;
                                    doGetFeedAdSuccessReport(ad, Const.Event.DELETE_EXPIRED_AD);
                                }
                                continue;
                            }
                            if(!isSameTitle(ad.getAdTitle())){
                                mDupleAditerator.remove();
                                Logger.i(TAG, "getAd from dupleAd cache - after remove cache size :" + mDuplAdCache.size());
                                doGetFeedAdSuccessReport(ad);
                                doGetFeedAdSuccessReport(ad, Const.Event.GET_FEED_AD_SUCCESS_FROM_DUPLE_CACHE);
                                return ad;
                            }else{
                                errorCode = ERROR_DUPLE_AD;
                            }
                        }
                    }
                    Logger.i(TAG, "getAd from duple cache failed - errorCode :" + errorCode);
                    doGetFeedAdFailReport(Const.Event.GET_FEED_AD_FAIL_FROM_DUPLE_CACHE, String.valueOf(errorCode));

                    if(!mIsFirstLoadFailed.get()){
                        if(!mIsFirstLoadSuccess.get()){
                            Logger.i(TAG, mPosid + "get ad fail,because load not complete");
                            NativeReportUtil.doNativeAdSuccessReport(Const.Event.GET_FEED_AD_NOT_REQUEST_COMPLETE, mPosid);
                            return null;
                        }
                    }
                    doGetFeedAdFailReport(Const.Event.GET_FEED_AD_FAIL, String.valueOf(errorCode));
                    Logger.i(TAG, "getAd from cache failed...... ");
                    return null;
                }
            });
        }else{
            doGetFeedAdFailReport(Const.Event.GET_FEED_AD_FAIL, String.valueOf(ERROR_NOT_CALL_LOAD));
            Logger.i(TAG, "getAd from cache failed, because not call load...... ");
        }
        return null;
    }

    private void addAd2DupleCache(INativeAd tempAd){
        if(null != tempAd && tempAd instanceof NativeAd) {
            Logger.i(TAG, "addAd2DupleCache before size :" + mDuplAdCache.size());
            mDuplAdCache.add((NativeAd)tempAd);
            if(mDuplAdCache.size() > DUPL_AD_CACHE_NUM){
                INativeAd deleteAd = mDuplAdCache.remove(0);
                doGetFeedAdSuccessReport(deleteAd, Const.Event.DELETE_AD_FROM_DUPL_AD_CACHE);
                Logger.i(TAG, "addAd2DupleCache delete first ad :");
            }
            Logger.i(TAG, "addAd2DupleCache after size :" + mDuplAdCache.size());
        }
    }

    private boolean isSameTitle(String title){
        if(!mIsFilterDuplAd){
            return false;
        }
        boolean flag = true;
        if(TextUtils.isEmpty(mLastAdTitle) || !mLastAdTitle.equals(title)){
            flag = false;
            mLastAdTitle = title;
        }
        return flag;
    }

    private void updateRetryTime() {
        // Backoff time calculations
        mRetryTimeMilliseconds = (int) (mRetryTimeMilliseconds * EXPONENTIAL_BACKOFF_FACTOR);
        if (mRetryTimeMilliseconds > MAXIMUM_RETRY_TIME_MILLISECONDS) {
            mRetryTimeMilliseconds = MAXIMUM_RETRY_TIME_MILLISECONDS;
        }
    }

    private void resetRetryTime() {
        mRetryTimeMilliseconds = DEFAULT_RETRY_TIME_MILLISECONDS;
    }


    public void setOpenPriority(boolean isOpen){
        mIsSupportPriority = isOpen;
    }

    public void setFilterDuplicateAd(boolean isFilterDuplAd){
        this.mIsFilterDuplAd = isFilterDuplAd;
    }

    private void doGetFeedAdSuccessReport(INativeAd nativeAd) {
        doGetFeedAdSuccessReport(nativeAd, Const.Event.GET_FEED_AD_SUCCESS);
    }
    private void doGetFeedAdSuccessReport(INativeAd nativeAd, Const.Event event) {
        if (nativeAd != null) {
            String adTypeName = nativeAd.getAdTypeName();
            int adIndex = -1;
            if (mNativeAdManagerInternal != null) {
                adIndex = mNativeAdManagerInternal.getAdTypeNameIndex(adTypeName);
            }
            NativeReportUtil.doGetAdReport(event, mPosid, adTypeName, adIndex);
        }
    }

    private void doGetFeedAdFailReport(Const.Event event, String errorCode) {
        NativeReportUtil.doGetAdFailReport(event, mPosid, errorCode);
    }

    public void setRequestOrionAdNum(int num){
        mRequestOrionAdNum = num;
    }

    public interface FeedListListener{
        public void onAdsAvailable();
        public void onAdClick(INativeAd ad);
    }
}
