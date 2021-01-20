package com.cmcm.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.cmcm.adsdk.interstitial.InterstitialAdCallBack;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.orion.picks.api.InterstitialAdListener;
import com.cmcm.orion.picks.api.OrionInterstitialAd;

import java.util.Map;

/**
 * Created by chenhao on 2016/1/8.
 * 主要逻辑：请求猎户广告, 请求猎户广告的图片,打开插屏页面
 */
public class PicksInterstatialAdapter extends NativeloaderAdapter {

    private OrionInterstitialAd mInterstitialAd;
    private PicksInterstatialAd mPicksInterstatialAd;
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
        mInterstitialAd = new OrionInterstitialAd(context, (String)extras.get(CMBaseNativeAd.KEY_PLACEMENT_ID));
        mInterstitialAd.setInterstitialAdListener(new InterstitialAdListener() {
            @Override
            public void onAdLoadFailed(int errorCode) {
                notifyNativeAdFailed(errorCode + "");
            }

            @Override
            public void onAdLoaded() {
                mPicksInterstatialAd = new PicksInterstatialAd();
                boolean flag = mInterstitialAd.loadNativeInterstitialImage();
                if (flag) {
                    notifyNativeAdLoaded(mPicksInterstatialAd);
                } else {
                    notifyNativeAdFailed("load ad image error");
                }
            }

            @Override
            public void onAdClicked() {
                if (mPicksInterstatialAd != null) {
                    mPicksInterstatialAd.notifyNativeAdClick(mPicksInterstatialAd);
                }
            }

            @Override
            public void onAdDisplayed() {
                mPicksInterstatialAd.onLoggingImpression();
                if(mInterstitialAdCallBack != null){
                    mInterstitialAdCallBack.onAdDisplayed();
                }
            }

            @Override
            public void onAdDismissed() {
                if(mInterstitialAdCallBack  != null){
                    mInterstitialAdCallBack.onAdDismissed();
                }
            }
        });

        if (!mInterstitialAd.isReady()) {
            mInterstitialAd.loadAd();
        }
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.pega_picks_interstitial;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        String reportPkgName = String.format("%s.%s", Const.pkgName.cm, Const.REPORT_INTERSTITIAL_SUFFIX);
        return reportPkgName;
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_CM_INTERSTITIAL;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.cm;
    }

    class PicksInterstatialAd extends CMBaseNativeAd implements INativeAd.ImpressionListener {

        @Override
        public String getAdTypeName() {
            return Const.KEY_CM_INTERSTITIAL;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            if (mInterstitialAd != null && mInterstitialAd.isReady()) {
                mInterstitialAd.showAd();
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
