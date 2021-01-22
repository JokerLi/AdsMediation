package com.buffalo.ads.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.NativeAdError;
import com.buffalo.adsdk.adapter.NativeloaderAdapter;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.Map;

public class AdmobNativeAdapter extends NativeloaderAdapter {
    @Override
    public void loadNativeAd(@NonNull Context context,
                             @NonNull Map<String, Object> extras) {
        if (!extrasAreValid(extras)) {
            notifyNativeAdFailed(String.valueOf(NativeAdError.PARAMS_ERROR));
            return;
        }
        new AdmobNativeAd(context, extras).loadAd();
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.admob;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        //如果配置的是Admob 这个key保持和之前一致，如果配置的是ab_h这种就拼接在后面
        if (adTypeName.equals(Const.KEY_AB)) {
            return Const.pkgName.admob;
        } else {
            return String.format("%s.%s", Const.pkgName.admob, adTypeName);
        }
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_AB;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.admob;
    }

    private class AdmobNativeAd extends BaseNativeAd implements
            NativeAppInstallAd.OnAppInstallAdLoadedListener,
            NativeContentAd.OnContentAdLoadedListener {
        private NativeAd mNativeAd;
        private Map<String, Object> mExtras;
        private Context mContext;

        public AdmobNativeAd(@NonNull Context context,
                             @Nullable Map<String, Object> extras) {
            this.mContext = context.getApplicationContext();
            this.mExtras = extras;
        }

        public void loadAd() {
            String mUnitId = (String) mExtras.get(KEY_PLACEMENT_ID);
            boolean filterInstallAd = false;
            if (mExtras.containsKey(BaseNativeAd.KEY_FILTER_ADMOB_INSTALL_AD)) {
                filterInstallAd = (boolean) mExtras.get(BaseNativeAd.KEY_FILTER_ADMOB_INSTALL_AD);
            }
            boolean filterContentAd = false;
            if (mExtras.containsKey(BaseNativeAd.KEY_FILTER_ADMOB_CONTENT_AD)) {
                filterContentAd = (boolean) mExtras.get(BaseNativeAd.KEY_FILTER_ADMOB_CONTENT_AD);
            }
            if (filterInstallAd && filterContentAd) {
                notifyNativeAdFailed("all admob native type be filter");
                return;
            }
            AdLoader.Builder loaderBuilder = new AdLoader.Builder(mContext, mUnitId);
            if (!filterInstallAd) {
                loaderBuilder.forAppInstallAd(this);
            }
            if (!filterContentAd) {
                loaderBuilder.forContentAd(this);
            }
            loaderBuilder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    notifyNativeAdFailed(String.valueOf(errorCode));
                }

                @Override
                public void onAdOpened() {
                    notifyNativeAdClick(AdmobNativeAd.this);
                }
            }).withNativeAdOptions(new NativeAdOptions.Builder().setReturnUrlsForImageAssets(true).build());
            AdLoader adLoader = loaderBuilder.build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_AB;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            if (view instanceof NativeContentAdView && mNativeAd instanceof NativeContentAd) {
                ((NativeContentAdView) view).setNativeAd(mNativeAd);
            } else if (view instanceof NativeAppInstallAdView && mNativeAd instanceof NativeAppInstallAd) {
                ((NativeAppInstallAdView) view).setNativeAd(mNativeAd);
            }

            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
            return true;
        }

        @Override
        public void unregisterView() {
        }

        @Override
        public Object getAdObject() {
            return mNativeAd;
        }

        @Override
        public void handleClick() {
        }

        @Override
        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
            setUpData(nativeAppInstallAd);
            mNativeAd = nativeAppInstallAd;

            notifyNativeAdLoaded(AdmobNativeAd.this);
        }

        @Override
        public void onContentAdLoaded(NativeContentAd nativeContentAd) {
            setUpData(nativeContentAd);
            mNativeAd = nativeContentAd;

            notifyNativeAdLoaded(AdmobNativeAd.this);
        }

        private void setUpData(@NonNull NativeAd admobAd) {
            if (admobAd instanceof NativeContentAd) {
                NativeContentAd ad = (NativeContentAd) admobAd;
                setTitle(ad.getHeadline().toString());
                setAdBody(ad.getBody().toString());
                if (ad.getImages() != null && ad.getImages().get(0) != null
                        && ad.getImages().get(0).getUri() != null) {
                    setAdCoverImageUrl(ad.getImages().get(0).getUri().toString());
                }
                if (ad.getLogo() != null && ad.getLogo().getUri() != null) {
                    setAdIconUrl(ad.getLogo().getUri().toString());
                }
                setAdCallToAction(ad.getCallToAction().toString());
                setIsDownloadApp(false);
                setAdStarRate(0.0d);
            } else if (admobAd instanceof NativeAppInstallAd) {
                NativeAppInstallAd ad = (NativeAppInstallAd) admobAd;
                setTitle(ad.getHeadline().toString());
                setAdBody(ad.getBody().toString());
                if (ad.getImages() != null && ad.getImages().get(0) != null
                        && ad.getImages().get(0).getUri() != null) {
                    setAdCoverImageUrl(ad.getImages().get(0).getUri().toString());
                }
                if (ad.getIcon() != null && ad.getIcon().getUri() != null) {
                    setAdIconUrl(ad.getIcon().getUri().toString());
                }
                setAdCallToAction(ad.getCallToAction().toString());
                setIsDownloadApp(true);
                try {
                    //此方法内部可能抛出NullPointException
                    setAdStarRate(ad.getStarRating());
                } catch (Exception e) {
                    setAdStarRate(0.0d);
                }
            }
        }
    }


    public static boolean isAdMobAd(Object obj) {
        if (obj == null) {
            return false;
        }

        try {
            return (obj instanceof NativeContentAd)
                    || (obj instanceof NativeAppInstallAd);
        } catch (Throwable e) {
        }
        return false;
    }

}
