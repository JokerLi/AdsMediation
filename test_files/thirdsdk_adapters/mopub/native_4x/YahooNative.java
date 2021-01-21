package com.mopub.nativeads;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.flurry.android.FlurryInit;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;
import com.flurry.android.ads.FlurryAdNativeListener;

import java.util.Map;

public class YahooNative extends CustomEventNative {
    private static final String PLACEMENT_ID_KEY = "placement_id";
    private String mApiKey = null;
    private static String mAdSpace = null;
    private String mPlacementId;
    private CustomEventNativeListener mCustomEventListener;

    @Override
    protected void loadNativeAd(Activity activity, CustomEventNativeListener customEventNativeListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        //load yahoo native ad
        mCustomEventListener = customEventNativeListener;
        if (extrasAreValid(serverExtras)) {
            mPlacementId = serverExtras.get(PLACEMENT_ID_KEY);
        } else {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        initParameters(mPlacementId);
        FlurryInit.init(activity.getApplicationContext(), mApiKey);
        YahooStaticNativeAd yahooStaticNativeAd = new YahooStaticNativeAd(activity.getApplicationContext());
        yahooStaticNativeAd.loadNative();
    }

    private boolean extrasAreValid(final Map<String, String> serverExtras) {
        final String placementId = serverExtras.get(PLACEMENT_ID_KEY);
        return (placementId != null && placementId.length() > 0);
    }

    private void initParameters(String params) {
        try {
            if (!TextUtils.isEmpty(params) && params.contains(";")) {
                String[] ids = params.split(";");
                if (ids.length >= 2) {
                    mApiKey = ids[0];
                    mAdSpace = ids[1];
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    class YahooStaticNativeAd extends StaticNativeAd implements FlurryAdNativeListener {

        private FlurryAdNative mFlurryAdNative;
        private Context mContext;
        private static final String AD_TITLE = "headline";
        private static final String AD_SEC_HQIMAGE = "secHqImage";
        private static final String AD_SEC_IMAGE = "secImage";
        private static final String CALL_TO_ACTION = "callToAction";
        private static final String SUMMARY = "summary";
        private static final String APP_RATING = "appRating";
        private static final String AD_ASSET_CATEGORY = "appCategory";

        public YahooStaticNativeAd(Context context) {
            mContext = context;
        }

        public void loadNative() {
            mFlurryAdNative = new FlurryAdNative(mContext, mAdSpace);
            mFlurryAdNative.setListener(this);
            mFlurryAdNative.fetchAd();
        }

        @Override
        public void onFetched(FlurryAdNative flurryAdNative) {
            setUpData(flurryAdNative);
            mCustomEventListener.onNativeAdLoaded(this);
        }


        @Override
        public void onShowFullscreen(FlurryAdNative flurryAdNative) {

        }

        @Override
        public void onCloseFullscreen(FlurryAdNative flurryAdNative) {

        }

        @Override
        public void onAppExit(FlurryAdNative flurryAdNative) {

        }

        @Override
        public void onClicked(FlurryAdNative flurryAdNative) {
            notifyAdClicked();
        }

        @Override
        public void onImpressionLogged(FlurryAdNative flurryAdNative) {
            notifyAdImpressed();
        }

        @Override
        public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int i) {
            mCustomEventListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
        }

        private void setUpData(@NonNull FlurryAdNative flurryAdNative) {

            FlurryAdNativeAsset adTitle = flurryAdNative.getAsset(AD_TITLE);
            if (adTitle != null) {
                setTitle(adTitle.getValue());
            }
            FlurryAdNativeAsset adBody = flurryAdNative.getAsset(SUMMARY);
            if (adBody != null) {
                setText(adBody.getValue());
            }

            FlurryAdNativeAsset adAdCoverImageAsset = flurryAdNative.getAsset(AD_SEC_HQIMAGE);
            if (adAdCoverImageAsset != null) {
                setMainImageUrl(flurryAdNative.getAsset(AD_SEC_HQIMAGE).getValue());
            }
            FlurryAdNativeAsset adAdIconImageAsset = flurryAdNative.getAsset(AD_SEC_IMAGE);
            if (adAdIconImageAsset != null) {
                setIconImageUrl(flurryAdNative.getAsset(AD_SEC_IMAGE).getValue());
            }
            FlurryAdNativeAsset adCallToAction = flurryAdNative.getAsset(CALL_TO_ACTION);
            if (adCallToAction != null) {
                setCallToAction(adCallToAction.getValue());
            }

            FlurryAdNativeAsset appRating = flurryAdNative.getAsset(APP_RATING);
            if (appRating != null && !TextUtils.isEmpty(appRating.getValue())) {
                setStarRating(Double.parseDouble(appRating.getValue()));
            }
        }

        private void setOnClickListener(@NonNull final View view,
                                        @Nullable final View.OnClickListener onClickListener) {
            view.setOnClickListener(onClickListener);
            if ((view instanceof ViewGroup)) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                    setOnClickListener(viewGroup.getChildAt(i), onClickListener);
            }
        }


        @Override
        public void prepare(@NonNull View view) {

            if (view != null && null != mFlurryAdNative) {
                mFlurryAdNative.setLogControl(true);
                mFlurryAdNative.logImpression();
                setOnClickListener(view, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyAdClicked();
                        mFlurryAdNative.logClick();
                    }
                });
            }
        }

        @Override
        public void clear(@NonNull View view) {
            if (null != mFlurryAdNative) {
                mFlurryAdNative.setLogControl(false);
                mFlurryAdNative.destroy();
            }
        }
    }
}
