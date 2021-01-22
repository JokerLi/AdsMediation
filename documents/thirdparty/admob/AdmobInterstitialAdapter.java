package com.cmcm.adsdk.adapter;


import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;

import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.cmcm.adsdk.interstitial.InterstitialAdCallBack;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.utils.Logger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Map;

public class AdmobInterstitialAdapter extends NativeloaderAdapter {
    private static final String TAG = AdmobInterstitialAdapter.class.getSimpleName();
    private InterstitialAd mInterstitialAd;
    private AdmobInterstitialAd mAdmobInterstitialAd;
    private InterstitialAdCallBack mInterstitialAdCallBack;

    @Override
    public void loadNativeAd(@NonNull Context context, @NonNull Map<String, Object> extras) {
        if(!extrasAreValid(extras)){
            notifyNativeAdFailed(String.valueOf(CMAdError.PARAMS_ERROR));
            return;
        }
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        if(extras.containsKey(CMBaseNativeAd.KEY_EXTRA_OBJECT)){
            Object object = extras.get(CMBaseNativeAd.KEY_EXTRA_OBJECT);
            if(object instanceof InterstitialAdCallBack){
                mInterstitialAdCallBack = (InterstitialAdCallBack) object;
            }
        }
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId((String)extras.get(CMBaseNativeAd.KEY_PLACEMENT_ID));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Logger.d(TAG, "admobInterstitialAd is dismiss");
                if(mInterstitialAdCallBack  != null){
                    mInterstitialAdCallBack.onAdDismissed();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Logger.d(TAG, "admobInterstitialAd is fail to loaded");
                notifyNativeAdFailed(errorCode + "");
            }

            @Override
            public void onAdLeftApplication() {
                Logger.d(TAG, "admobInterstitialAd is leftApplication");
                if (mAdmobInterstitialAd != null) {
                    mAdmobInterstitialAd.notifyNativeAdClick(mAdmobInterstitialAd);
                }
            }

            @Override
            public void onAdOpened() {
                Logger.d(TAG, "admobInterstitialAd is display");
                mAdmobInterstitialAd.onLoggingImpression();
                if(mInterstitialAdCallBack != null){
                    mInterstitialAdCallBack.onAdDisplayed();
                }
            }

            @Override
            public void onAdLoaded() {
                Logger.d(TAG, "admobInterstitialAd is loaded");
                mAdmobInterstitialAd = new AdmobInterstitialAd();
                notifyNativeAdLoaded(mAdmobInterstitialAd);
            }
        });

        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    @Override
    public int getReportRes(int type, String adTypeName) {
        if(type == RES_TYPE_RCV) {
            return Const.res.admob;
        }else{
            return Const.res.pega_admob_interstitial;
        }
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        return Const.pkgName.admob_interstitial;
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_AB_INTERSTITIAL;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.admob;
    }

    class AdmobInterstitialAd extends CMBaseNativeAd implements INativeAd.ImpressionListener {
        @Override
        public String getAdTypeName() {
            return Const.KEY_AB_INTERSTITIAL;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            return true;
        }

        @Override
        public void unregisterView() {
            if (mInterstitialAd != null) {
                mInterstitialAd = null;
            }
        }

        @Override
        public Object getAdObject() {
            return mInterstitialAd;
        }

        @Override
        public void handleClick() {

        }

        @Override
        public boolean isNativeAd() {
            return false;
        }

        @Override
        public void onLoggingImpression() {
            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
        }
    }
}
