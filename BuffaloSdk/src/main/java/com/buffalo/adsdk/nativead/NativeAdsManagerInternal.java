package com.buffalo.adsdk.nativead;

import android.content.Context;
import android.text.TextUtils;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.NativeAdError;
import com.buffalo.adsdk.config.PosBean;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAd.IAdOnClickListener;
import com.buffalo.baseapi.ads.INativeAdLoader;
import com.buffalo.utils.Logger;
import com.buffalo.utils.ThreadHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NativeAdsManagerInternal extends NativeAdManagerInternal implements IAdOnClickListener {
    private List<INativeAd> mAdPool = new ArrayList<INativeAd>();
    private List<String> mTitlePool = new ArrayList<String>();
    private int mExpectedSize = 0;
    INativeAdListListener mAdListListener = null;

    public NativeAdsManagerInternal(Context context, String posId) {
        super(context, posId);
    }

    public void loadAds(int num) {
        Logger.i(TAG, mPositionId + " loadAds num:" + num);
//        mIsPreload = true;
        mOptimizeEnabled = false;
        mTitlePool.clear();
        mAdPool.clear();
        mExpectedSize = num;

        loadAd();
    }

    public void setAdListener(INativeAdListListener adListener) {
        super.setAdListener(adListener);
        mAdListListener = adListener;
    }

    public List<INativeAd> getAdList() {
        return mAdPool;
    }

    @Override
    protected int getLoadAdTypeSize() {
        return PRELOAD_REQUEST_SIZE;
    }

    @Override
    protected boolean requestBean(PosBean bean) {
        // FIXME: 需要确保如果已经notify 成功后, 不应该再加装
        int requestSize = mExpectedSize - mAdPool.size();
        if (requestSize <= 0) {
            asyncCheckIfAllFinished("request bean");
            return false;
        }

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
            adLoader.loadAds(requestSize);
            return true;
        } else {
            adFailedToLoad(adName, String.valueOf(NativeAdError.NO_AD_TYPE_EROOR));
            return false;
        }
    }


    private void pushAdsToPool(List<INativeAd> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Iterator<INativeAd> iterator = list.iterator();
        while (iterator.hasNext()) {
            INativeAd ad = iterator.next();
            if (ad == null || checkPoolHasAd(ad)) {
                iterator.remove();
            }
        }
        mAdPool.addAll(list);
    }

    private boolean checkPoolHasAd(INativeAd ad) {
        for (String title : mTitlePool) {
            if (!TextUtils.isEmpty(title)) {
                if (title.equals(ad.getAdTitle())) {
                    Logger.i("ad :" + ad.getAdTitle() + " has in pool list");
                    return true;
                }
            }
        }
        mTitlePool.add(ad.getAdTitle());
        return false;
    }


    @Override
    public void adLoaded(String adTypeName) {
        super.adLoaded(adTypeName);
        int oldSize = mAdPool.size();
        INativeAdLoader loader = mLoaderMap.getAdLoader(adTypeName);
        if (loader != null) {
            int needAdNum = mExpectedSize - mAdPool.size();
            if (needAdNum > 0) {
                List<INativeAd> tempList = loader.getAdList(needAdNum);
                if (tempList != null && !tempList.isEmpty()) {
                    pushAdsToPool(tempList);
                }
            }
        }

        Logger.d(Const.TAG, "adLoaded pool size: " + oldSize + " -> " + mAdPool.size() + " expect:" + mExpectedSize);
        if (oldSize != mAdPool.size()) {
            notifyLoadProgress();
        }
    }

    @Override
    protected void checkIfAllfinished() {
        Logger.i(Const.TAG, "check finish");

        if (mIsFinished) {
            Logger.w(Const.TAG, "already finished");
            return;
        }

        if (mAdPool.size() >= mExpectedSize) {
            notifyAdLoaded();
        }

        if (!mIsFinished) {
            if (isAllLoaderFinished()) {
                if (mAdPool.isEmpty()) {
                    notifyAdFailed(NativeAdError.NO_FILL_ERROR);
                } else {
                    notifyAdLoaded();
                }
            }
        }
    }

    protected void notifyLoadProgress() {
        ThreadHelper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdListListener != null) {
                    mAdListListener.onLoadProcess();
                }
            }
        });
    }
}
