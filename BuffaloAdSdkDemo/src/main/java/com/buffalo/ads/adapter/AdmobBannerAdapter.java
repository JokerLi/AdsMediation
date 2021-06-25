package com.buffalo.ads.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.NativeAdError;
import com.buffalo.adsdk.adapter.NativeloaderAdapter;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.Map;

public class AdmobBannerAdapter extends NativeloaderAdapter {
    private static final String TAG = "AdmobBannerAdapter";

    Context mContext;
    Map<String, Object> mExtras;

    public AdmobBannerAdapter() {
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
        new AdmobBannerHookAd().loadAd();
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.admob;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        //保持和原有一致
        if (adTypeName.equals(Const.KEY_AB_BANNER)) {
            return String.format("%s.%s", Const.pkgName.admob_banner, Const.REPORT_BANNER_SUFFIX);
        } else {
            return String.format("%s.%s.%s", Const.pkgName.admob_banner, Const.REPORT_BANNER_SUFFIX, adTypeName);
        }
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_AB_BANNER;
    }

    @Override
    public long getDefaultCacheTime() {
        return 0;
    }


    class AdmobBannerHookAd extends BaseNativeAd {
        private AdView mAdView;

        public void loadAd() {
            if (mAdView != null) {
                mAdView.destroy();
                mAdView = null;
            }
            mAdView = new AdView(mContext);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId((String) mExtras.get(KEY_PLACEMENT_ID));
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    notifyNativeAdLoaded(AdmobBannerHookAd.this);
//                    initCloseView();
//                    showAd();
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    super.onAdFailedToLoad(adError);
                    Log.e("MainBannerView", "onAdFailedToLoad = " + adError.getResponseInfo());
                    notifyNativeAdFailed(adError.getMessage());
                    unregisterView();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    notifyNativeAdClick(AdmobBannerHookAd.this);
                }
            });
            mAdView.loadAd(adRequest);
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_AB_BANNER;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
            return true;
        }

        @Override
        public void unregisterView() {
            if (mAdView != null) {
                mAdView.pause();
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
