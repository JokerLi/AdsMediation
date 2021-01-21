package com.mopub.nativeads;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.*;
import com.google.android.gms.ads.formats.NativeAd;

import java.util.Map;

public class AdmobNative extends CustomEventNative {

    private static final String PLACEMENT_ID_KEY = "placement_id";
    private String mPlacementId;
    private NativeContentAd mAd;
    private CustomEventNative.CustomEventNativeListener mCustomEventListener;

    public AdmobNative() {

    }

    @Override
    protected void loadNativeAd(Activity activity, CustomEventNative.CustomEventNativeListener customEventNativeListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        //load yahoo native ad
        mCustomEventListener = customEventNativeListener;
        if (!extrasAreValid(serverExtras)) {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        mPlacementId = serverExtras.get(PLACEMENT_ID_KEY);
        new AdmobStaticNativeAd(activity.getApplicationContext()).loadAd();

    }

    private boolean extrasAreValid(final Map<String, String> serverExtras) {
        final String placementId = serverExtras.get(PLACEMENT_ID_KEY);
        return (placementId != null && placementId.length() > 0);
    }

    public class AdmobStaticNativeAd extends StaticNativeAd implements NativeAppInstallAd.OnAppInstallAdLoadedListener,
            NativeContentAd.OnContentAdLoadedListener {

        private Context mContext;

        public AdmobStaticNativeAd(Context context) {
            mContext = context;
        }

        public void loadAd() {
            AdLoader adLoader = new AdLoader.Builder(mContext, mPlacementId)
                    .forContentAd(this)
                    .forAppInstallAd(this)
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            mCustomEventListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
                        }

                        @Override
                        public void onAdOpened() {
                            notifyAdImpressed();
                        }

                        @Override
                        public void onAdLeftApplication() {
                            notifyAdClicked();
                        }
                    }).withNativeAdOptions(new NativeAdOptions.Builder().setReturnUrlsForImageAssets(true).build())
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }


        @Override
        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
            setUpData(nativeAppInstallAd);
            mCustomEventListener.onNativeAdLoaded(this);
        }

        @Override
        public void onContentAdLoaded(NativeContentAd nativeContentAd) {
            setUpData(nativeContentAd);
            mCustomEventListener.onNativeAdLoaded(this);
        }

        private void setUpData(@NonNull com.google.android.gms.ads.formats.NativeAd admobAd) {
            mAd = (NativeContentAd) admobAd;
            if (mAd instanceof NativeContentAd) {
                NativeContentAd ad = (NativeContentAd) mAd;
                setTitle(ad.getHeadline().toString());
                setText(ad.getBody().toString());
                if (ad.getImages() != null && ad.getImages().get(0) != null
                        && ad.getImages().get(0).getUri() != null) {
                    setMainImageUrl(ad.getImages().get(0).getUri().toString());
                }
                if (ad.getLogo() != null && ad.getLogo().getUri() != null) {
                    setIconImageUrl(ad.getLogo().getUri().toString());
                } else {
                    setIconImageUrl(ad.getImages().get(0).getUri().toString());
                }
                setCallToAction(ad.getCallToAction().toString());
                setStarRating(0.0d);
            } else {
                NativeAppInstallAd ad = (NativeAppInstallAd) mAd;
                setTitle(ad.getHeadline().toString());
                setText(ad.getBody().toString());
                if (ad.getImages() != null && ad.getImages().get(0) != null
                        && ad.getImages().get(0).getUri() != null) {
                    setMainImageUrl(ad.getImages().get(0).getUri().toString());
                }
                if (ad.getIcon() != null && ad.getIcon().getUri() != null) {
                    setIconImageUrl(ad.getIcon().getUri().toString());
                } else {
                    setIconImageUrl(ad.getImages().get(0).getUri().toString());
                }
                setCallToAction(ad.getCallToAction().toString());
                setStarRating(0.0d);
            }
        }

        @Override
        public void prepare(@NonNull View view) {
            notifyAdImpressed();
            if (view instanceof NativeContentAdView && mAd instanceof NativeContentAd) {
                ((NativeContentAdView) view).setNativeAd(mAd);
            } else if (view instanceof NativeAppInstallAdView && mAd instanceof NativeAppInstallAd) {
                ((NativeAppInstallAdView) view).setNativeAd(mAd);
            }
        }

        @Override
        public void clear(final View view) {

        }

    }

}
