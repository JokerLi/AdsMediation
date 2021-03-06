package com.buffalo.adsdk.interstitial;


import android.content.Context;

import com.buffalo.adsdk.RequestParams;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;

public class InterstitialAdManager implements INativeAdLoaderListener {
    private Context mContext;
    private InterstitialRequestInternal interstitialRequest;
    private InterstitialAdCallBack mCallBack;

    public InterstitialAdManager(Context context, String posId) {
        this.mContext = context;
        this.interstitialRequest = new InterstitialRequestInternal(mContext, posId);
    }

    // 加载广告
    public void loadAd() {
        interstitialRequest.setAdListener(this);
        interstitialRequest.loadAd();
    }

    // 展示广告
    public void showAd() {
        if (interstitialRequest != null) {
            interstitialRequest.showAd();
        }
    }

    // 缓存中是否有广告
    public boolean isReady() {
        if (interstitialRequest != null) {
            return interstitialRequest.isReady();
        }
        return false;
    }

    // 返回缓存中广告的广告类型
    public String getCacheAdType() {
        if (interstitialRequest != null) {
            return interstitialRequest.getCachelAdType();
        }
        return null;
    }

    public void setInterstitialCallBack(InterstitialAdCallBack callBack) {
        mCallBack = callBack;
        if (interstitialRequest != null) {
            RequestParams requestParams = new RequestParams();
            requestParams.setExtraObject(callBack);
            interstitialRequest.setRequestParams(requestParams);
        }
    }

    @Override
    public void adLoaded() {
        if (mCallBack != null) {
            mCallBack.onAdLoaded();
        }
    }

    @Override
    public void adFailedToLoad(int errorcode) {
        if (mCallBack != null) {
            mCallBack.onAdLoadFailed(errorcode);
        }
    }

    @Override
    public void adClicked(INativeAd nativeAd) {
        if (mCallBack != null) {
            mCallBack.onAdClicked();
        }
    }

    public void destroy() {
        interstitialRequest = null;
        mCallBack = null;
    }

}
