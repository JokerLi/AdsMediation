package com.buffalo.adsdk.splashad;

import android.content.Context;

import com.buffalo.adsdk.nativead.NativeAdManager;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;

public class NativeSplashLoader implements INativeAdLoaderListener {
    private AdListener mAdListener;
    private NativeAdManager mNativeAdManager;

    public NativeSplashLoader(Context context, String posid) {
        mNativeAdManager = new NativeAdManager(context, posid);
        mNativeAdManager.setNativeAdListener(this);
    }

    public void loadAd() {
        if (mNativeAdManager != null) {
            mNativeAdManager.loadAd();
        }
    }

    @Override
    public void adLoaded() {
        if (mAdListener != null) {
            mAdListener.onAdLoaded();
        }
    }

    @Override
    public void adFailedToLoad(int errorcode) {
        if (mAdListener != null) {
            mAdListener.onAdLoadFailed(errorcode);
        }
    }

    @Override
    public void adClicked(INativeAd nativeAd) {
        if (mAdListener != null) {
            mAdListener.onAdClick();
        }
    }

    public INativeAd getAd() {
        //TODO:judge the ad if avaliable
        INativeAd nativeAd = mNativeAdManager.getAd();
        return nativeAd;
    }

    public void destroy() {
        mNativeAdManager = null;
        mAdListener = null;
    }

    public void setAdListener(AdListener listener) {
        this.mAdListener = listener;
    }

    public interface AdListener {
        void onAdLoaded();

        void onAdLoadFailed(int errorcode);

        void onAdClick();
    }
}
