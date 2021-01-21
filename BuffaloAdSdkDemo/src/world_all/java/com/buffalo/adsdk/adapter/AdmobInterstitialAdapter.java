package com.buffalo.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.buffalo.adsdk.NativeAdError;
import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.buffalo.adsdk.interstitial.InterstitialAdCallBack;
import com.buffalo.baseapi.ads.INativeAd;
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
        if (!extrasAreValid(extras)) {
            notifyNativeAdFailed(String.valueOf(NativeAdError.PARAMS_ERROR));
            return;
        }
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        if (extras.containsKey(BaseNativeAd.KEY_EXTRA_OBJECT)) {
            Object object = extras.get(BaseNativeAd.KEY_EXTRA_OBJECT);
            if (object instanceof InterstitialAdCallBack) {
                mInterstitialAdCallBack = (InterstitialAdCallBack) object;
            }
        }
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId((String) extras.get(BaseNativeAd.KEY_PLACEMENT_ID));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "admobInterstitialAd is dismiss");
                if (mInterstitialAdCallBack != null) {
                    mInterstitialAdCallBack.onAdDismissed();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "admobInterstitialAd is fail to loaded");
                notifyNativeAdFailed(errorCode + "");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "admobInterstitialAd is leftApplication");
                if (mAdmobInterstitialAd != null) {
                    mAdmobInterstitialAd.notifyNativeAdClick(mAdmobInterstitialAd);
                }
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "admobInterstitialAd is display");
                mAdmobInterstitialAd.onLoggingImpression();
                if (mInterstitialAdCallBack != null) {
                    mInterstitialAdCallBack.onAdDisplayed();
                }
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "admobInterstitialAd is loaded");
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
    public int getReportRes(String adTypeName) {
        return Const.res.admob;
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

    class AdmobInterstitialAd extends BaseNativeAd implements INativeAd.ImpressionListener {
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
