package com.cmcm.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.cmcm.adsdk.nativead.CMNativeAd;
import com.flurry.android.FlurryInit;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;
import com.flurry.android.ads.FlurryAdNativeListener;

import java.util.Map;

/**
 * Created by chenhao on 15/12/1.
 */
public class YahooNativeAdapter extends NativeloaderAdapter {


    @Override
    public void loadNativeAd(@NonNull Context context,
                             @NonNull Map<String, Object> extras) {

        if(!extrasAreValid(extras)){
            notifyNativeAdFailed(String.valueOf(CMAdError.PARAMS_ERROR));
            return;
        }
        new YahooNativeAd(context, extras).loadAd();
    }


    @Override
    public boolean extrasAreValid(Map<String, Object> extras) {
        if(extras != null && extras.containsKey(CMNativeAd.KEY_PLACEMENT_ID)){
            String object = (String) extras.get(CMNativeAd.KEY_PLACEMENT_ID);
            if(!TextUtils.isEmpty(object)) {
                String params[] = object.split(";");
                //yahoo 必须要ApiKey和AdSpace
                return params != null && params.length > 1;
            }
        }
        return false;
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.yahoo;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        if(adTypeName.equals(Const.KEY_YH)) {
            return Const.pkgName.yahoo;
        }else {
            return String.format("%s.%s", Const.pkgName.yahoo, adTypeName);
        }
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_YH;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.yahoo;
    }

    private class YahooNativeAd extends CMBaseNativeAd implements FlurryAdNativeListener {
        private static final String AD_TITLE = "headline";
        private static final String AD_SEC_HQIMAGE = "secHqImage";
        private static final String AD_SEC_IMAGE = "secImage";
        private static final String CALL_TO_ACTION = "callToAction";
        private static final String SUMMARY = "summary";
        private static final String APP_RATING = "appRating";
        private static final String AD_ASSET_CATEGORY = "appCategory";
        private static final String AD_ASSET_SOURCE ="source";

        private Map<String, Object> mExtras;
        private Context mContext;
        private FlurryAdNative mFlurryAdNative;

        String mApiKey;
        String mAdSpace;
        String mPlacementId;
        private boolean mHasRegistView = false;

        public YahooNativeAd(@NonNull Context context,
                             @Nullable Map<String, Object> extras) {
            this.mContext = context;
            this.mExtras = extras;
        }

        public void loadAd() {
           if(mExtras.containsKey(KEY_PLACEMENT_ID)) {
               String placementid = (String) mExtras.get(KEY_PLACEMENT_ID);
               initParameters(placementid);
           }
            //Init Yahoo SDK
            FlurryInit.init(mContext, mApiKey);
            mFlurryAdNative = new FlurryAdNative(mContext, mAdSpace);
            mFlurryAdNative.setListener(this);
            mFlurryAdNative.fetchAd();
        }

        private void initParameters(String params) {
            try{
                mPlacementId = params;
                if (!TextUtils.isEmpty(params) && params.contains(";")) {
                    String[] ids = params.split(";");
                    if (ids.length >= 2) {
                        mApiKey = ids[0];
                        mAdSpace = ids[1];
                    }
                }
            }catch (Exception e){
                e.getMessage();
            }
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_YH;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            mHasRegistView = true;
            if(view == null){
                return false;
            }
            if(null != mFlurryAdNative){
                mFlurryAdNative.setLogControl(true);
                mFlurryAdNative.logImpression();
            }

            return false;
        }

        @Override
        public void unregisterView() {
            if(null != mFlurryAdNative && mHasRegistView) {
                mFlurryAdNative.setLogControl(false);
                mFlurryAdNative.destroy();
            }
        }

        @Override
        public Object getAdObject() {
            return mFlurryAdNative;
        }

        @Override
        public void handleClick() {
            if(null != mFlurryAdNative){
                mFlurryAdNative.logClick();
            }
        }

        @Override
        public void onFetched(FlurryAdNative flurryAdNative) {
            setUpData(flurryAdNative);
            notifyNativeAdLoaded(YahooNativeAd.this);
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
        }

        @Override
        public void onImpressionLogged(FlurryAdNative flurryAdNative) {
            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
        }

        @Override
        public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int i) {
            //原本ErrorCode为int型的 不需要转换
            notifyNativeAdFailed(String.valueOf(i));
        }

        @Override
        public String getRawString(int operation) {
            return YahooInfomation.getYahooNativeOfferJsonV112(operation, mPlacementId, mFlurryAdNative);
        }

        private void setUpData(@NonNull FlurryAdNative flurryAdNative) {
            FlurryAdNativeAsset adTitle = flurryAdNative.getAsset(AD_TITLE);
            if (adTitle != null) {
                setTitle(adTitle.getValue());
            }
            FlurryAdNativeAsset adAdCoverImageAsset = flurryAdNative.getAsset(AD_SEC_HQIMAGE);
            if (adAdCoverImageAsset != null) {
                setAdCoverImageUrl(flurryAdNative.getAsset(AD_SEC_HQIMAGE).getValue());
            }
            FlurryAdNativeAsset adAdIconImageAsset = flurryAdNative.getAsset(AD_SEC_IMAGE);
            if (adAdIconImageAsset != null) {
                setAdIconUrl(flurryAdNative.getAsset(AD_SEC_IMAGE).getValue());
            }
            FlurryAdNativeAsset adCallToAction = flurryAdNative.getAsset(CALL_TO_ACTION);
            if (adCallToAction != null) {
                setAdCallToAction(adCallToAction.getValue());
            }
            FlurryAdNativeAsset adBody = flurryAdNative.getAsset(SUMMARY);
            if (adBody != null) {
                setAdBody(adBody.getValue());
            }
            try {
                FlurryAdNativeAsset appRating = flurryAdNative.getAsset(APP_RATING);
                if (appRating != null && !TextUtils.isEmpty(appRating.getValue())) {
                    setAdStarRate(Double.parseDouble(appRating.getValue()));
                }
            } catch (Exception e) {
            }
            FlurryAdNativeAsset category = flurryAdNative.getAsset(AD_ASSET_CATEGORY);
            setIsDownloadApp(category != null && !TextUtils.isEmpty(category.getValue()));

            FlurryAdNativeAsset adSource = flurryAdNative.getAsset(AD_ASSET_SOURCE);
            if(adSource!= null){
                setSource(adSource.getValue());
            }
        }
    }
}
