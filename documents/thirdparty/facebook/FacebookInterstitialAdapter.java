package com.cmcm.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.cmcm.adsdk.interstitial.InterstitialAdCallBack;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.utils.Logger;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import java.util.Map;

public class FacebookInterstitialAdapter extends NativeloaderAdapter implements InterstitialAdListener {
    private static final String TAG = "FacebookInterstitialAdapter";
    private InterstitialAd interstitialAd;
    private Context mContext;
    private FacebookInterstatialAd mFacebookInterstatialAd;
    private InterstitialAdCallBack mInterstitialAdCallBack;
    private Map<String, Object> mExtras;

    @Override
    public void loadNativeAd(@NonNull Context context, @NonNull Map<String, Object> extras) {
        this.mContext = context;
        mExtras = extras;
        if (!extrasAreValid(extras)) {
            notifyNativeAdFailed(String.valueOf(CMAdError.PARAMS_ERROR));
            return;
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
        if (extras.containsKey(CMBaseNativeAd.KEY_EXTRA_OBJECT)) {
            Object object = extras.get(CMBaseNativeAd.KEY_EXTRA_OBJECT);
            if (object instanceof InterstitialAdCallBack) {
                mInterstitialAdCallBack = (InterstitialAdCallBack) object;
            }
        }

        interstitialAd = new InterstitialAd(context, (String) extras.get(CMBaseNativeAd.KEY_PLACEMENT_ID));
        interstitialAd.setAdListener(this);
        interstitialAd.loadAd();
    }


    @Override
    public void onInterstitialDisplayed(Ad ad) {
        mFacebookInterstatialAd.onLoggingImpression();
        if (mInterstitialAdCallBack != null) {
            mInterstitialAdCallBack.onAdDisplayed();
        }
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        Logger.d(TAG, "facebookInterstitial is dismiss");
        if (mInterstitialAdCallBack != null) {
            mInterstitialAdCallBack.onAdDismissed();
        }
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        //改ErrorCode值为int
        notifyNativeAdFailed(adError.getErrorCode() + "");
    }

    @Override
    public void onAdLoaded(Ad ad) {
        mFacebookInterstatialAd = new FacebookInterstatialAd(ad);
        notifyNativeAdLoaded(mFacebookInterstatialAd);
    }

    @Override
    public void onAdClicked(Ad ad) {
        // FIXME: 如果这里存在adLoad 多次, 则Click 的Ad 回调会有问题
        // LATER
        if (mFacebookInterstatialAd != null) {
            mFacebookInterstatialAd.notifyNativeAdClick(mFacebookInterstatialAd);
        }
    }

    @Override
    public int getReportRes(int type, String adTypeName) {
        if (type == RES_TYPE_RCV) {
            return Const.res.facebook;
        } else {
            return Const.res.pega_fb_interstitial;
        }
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        String reportPkgName = String.format("%s.%s", Const.pkgName.facebook, Const.REPORT_INTERSTITIAL_SUFFIX);
        return reportPkgName;
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_FB_INTERSTITIAL;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.facebook;
    }


    class FacebookInterstatialAd extends CMBaseNativeAd implements INativeAd.ImpressionListener {
        private Ad mAd;

        public FacebookInterstatialAd(Ad ad) {
            mAd = ad;
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_FB_INTERSTITIAL;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                interstitialAd.show();
            }
            return true;
        }

        @Override
        public void unregisterView() {
            if (interstitialAd != null) {
                interstitialAd.destroy();
                interstitialAd = null;
            }
        }

        @Override
        public Object getAdObject() {
            return mAd;
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
            if (mImpressionListener != null)
                mImpressionListener.onLoggingImpression();
        }

        @Override
        public String getRawString(int operation) {
            return FaceBookInfomation.getFacebookInterstitialOfferStringV482(mAd);
        }
    }

}
