package com.buffalo.ads.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.NativeAdError;
import com.buffalo.adsdk.adapter.NativeloaderAdapter;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.util.List;
import java.util.Map;

public class FacebookBannerAdapter extends NativeloaderAdapter {
    private static final String TAG = "FacebookBannerAdapter";

    Context mContext;
    Map<String, Object> mExtras;

    public FacebookBannerAdapter() {
        mContext = null;
    }

    @Override
    public void loadNativeAd(@NonNull Context context, @NonNull Map<String, Object> extras) {
        mContext = context;
        mExtras = extras;
        if (!extrasAreValid(extras)) {
            notifyNativeAdFailed(String.valueOf(NativeAdError.PARAMS_ERROR));
            return;
        }
        new FacebookBannerHookAd().loadAd();
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.facebook;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        //保持和原有一致
        if (adTypeName.equals(Const.KEY_FB_BANNER)) {
            return Const.pkgName.facebook_banner;
        } else {
            return String.format("%s.%s", Const.pkgName.facebook_banner, adTypeName);
        }
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_FB_BANNER;
    }

    @Override
    public long getDefaultCacheTime() {
        return 0;
    }


    class FacebookBannerHookAd extends BaseNativeAd {
        private AdView mAdView;

        public void loadAd() {
            if (mAdView != null) {
                mAdView.destroy();
                mAdView = null;
            }
            mAdView = new AdView(mContext, (String) mExtras.get(KEY_PLACEMENT_ID), AdSize.BANNER_HEIGHT_50);
            AdListener adListener = new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    notifyNativeAdFailed(adError.getErrorCode() + "");
                    unregisterView();
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    notifyNativeAdLoaded(FacebookBannerHookAd.this);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    notifyNativeAdClick(FacebookBannerHookAd.this);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    if (mImpressionListener != null) {
                        mImpressionListener.onLoggingImpression();
                    }
                }
            };
            mAdView.loadAd(mAdView.buildLoadAdConfig().withAdListener(adListener).build());
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_FB_BANNER;
        }

        @Override
        public boolean registerViewForInteraction(View view, View mediaView, @Nullable View adIconView, @Nullable List<View> clickableViews) {
            return true;
        }

        @Override
        public void unregisterView() {
            if (mAdView != null) {
                mAdView.destroy();
                mAdView = null;
            }
        }

        @Override
        public boolean isNativeAd() {
            return false;
        }

        @Override
        public Object getAdObject() {
            return mAdView;
        }

        @Override
        public void handleClick() {
        }
    }

    @Override
    public Const.AdType getAdType() {
        return Const.AdType.BANNER;
    }
}
