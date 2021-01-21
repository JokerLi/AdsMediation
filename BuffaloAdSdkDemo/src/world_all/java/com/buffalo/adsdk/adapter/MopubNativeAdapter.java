package com.buffalo.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.buffalo.adsdk.CMAdError;
import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.buffalo.utils.Commons;
import com.mopub.common.ClientMetadata;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.NativeResponse;

import java.util.Map;

/**
 * Created by chenhao on 15/12/1.
 */
public class MopubNativeAdapter extends NativeloaderAdapter {
    private final String PREFS_NAME = "cmcmadsdk_config";

    @Override
    public void loadNativeAd(@NonNull Context context,
                             @NonNull Map<String, Object> extras) {
        if (!extrasAreValid(extras)) {
            notifyNativeAdFailed(String.valueOf(CMAdError.PARAMS_ERROR));
            return;
        }
        new MopubNativeAd(context, extras).loadAd();
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.mopub;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        return String.format("%s.%s", Const.pkgName.mopub, adTypeName);
    }

    @Override
    public int getAdErrorCode(Object errorInstance) {
        if (errorInstance == null || !(errorInstance instanceof NativeErrorCode))
            return 0;
        switch ((NativeErrorCode) errorInstance) {
            case EMPTY_AD_RESPONSE:
                return CMAdError.EMPTY_AD_RESPONSE;
            case INVALID_JSON:
                return CMAdError.INVALID_JSON;
            case IMAGE_DOWNLOAD_FAILURE:
                return CMAdError.IMAGE_DOWNLOAD_FAILURE;
            case INVALID_REQUEST_URL:
                return CMAdError.INVALID_REQUEST_URL;
            case UNEXPECTED_RESPONSE_CODE:
                return CMAdError.UNEXPECTED_RESPONSE_CODE;
            case SERVER_ERROR_RESPONSE_CODE:
                return CMAdError.SERVER_ERROR_RESPONSE_CODE;
            case CONNECTION_ERROR:
                return CMAdError.CONNECTION_ERROR;
            case UNSPECIFIED:
                return CMAdError.UNSPECIFIED;
            case NETWORK_INVALID_REQUEST:
                return CMAdError.NETWORK_INVALID_REQUEST;
            case NETWORK_TIMEOUT:
                return CMAdError.NETWORK_TIMEOUT;
            case NETWORK_NO_FILL:
                return CMAdError.NETWORK_NO_FILL;
            case NETWORK_INVALID_STATE:
                return CMAdError.NETWORK_INVALID_STATE;
            case NATIVE_ADAPTER_CONFIGURATION_ERROR:
                return CMAdError.NATIVE_ADAPTER_CONFIGURATION_ERROR;
            case NATIVE_ADAPTER_NOT_FOUND:
                return CMAdError.NATIVE_ADAPTER_NOT_FOUND;
            default:
                break;
        }
        return 0;
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_MP;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.mopub;
    }

    private class MopubNativeAd extends BaseNativeAd implements MoPubNative.MoPubNativeListener {
        private NativeResponse mNativeResponse;
        private Map<String, Object> mExtras;
        private Context mContext;
        private View mAdView;

        public MopubNativeAd(@NonNull Context context,
                             @Nullable Map<String, Object> extras) {
            this.mContext = context;
            this.mExtras = extras;
        }

        public void loadAd() {
            String mUnitId = (String) mExtras.get(KEY_PLACEMENT_ID);
            if (TextUtils.isEmpty(mUnitId)) {
                Log.e("MopubNativeAdAdapter", "unit id is null");
                notifyNativeAdFailed("unit id is null");
                return;
            }
            MoPubNative moPubNative = new MoPubNative(mContext, mUnitId, MopubNativeAd.this);
            String gaId = Commons.getGAId();
            gaId.trim();
            boolean dnt = Commons.getTrackFlag();
            ClientMetadata clientMetadata = ClientMetadata.getInstance(mContext);
            if (!TextUtils.isEmpty(gaId)) {
                clientMetadata.setAdvertisingInfo(gaId, dnt);
            }

//            RequestParameters.Builder builder = new RequestParameters.Builder();
//            String keyWords = RequestUFS.getInstance().getUFSInfo();
//            if(!TextUtils.isEmpty(keyWords)) {
//                builder.keywords(keyWords);
//                moPubNative.makeRequest(builder.build());
//            }else {
//                moPubNative.makeRequest();
//            }
            moPubNative.makeRequest();
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_MP;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            mAdView = view;
            mNativeResponse.recordImpression(view);
            return false;
        }

        @Override
        public void unregisterView() {
            if (null != mAdView) {
                mNativeResponse.clear(mAdView);
            }
        }

        @Override
        public Object getAdObject() {
            return mNativeResponse;
        }

        @Override
        public void handleClick() {
            mNativeResponse.handleClick(mAdView);
        }

        @Override
        public void onNativeLoad(NativeResponse nativeResponse) {
            mNativeResponse = nativeResponse;
            setTitle(nativeResponse.getTitle());
            setAdBody(nativeResponse.getText());
            setAdCallToAction(nativeResponse.getCallToAction());
            setAdCoverImageUrl(nativeResponse.getMainImageUrl());
            setAdIconUrl(nativeResponse.getIconImageUrl());
            String starRating = nativeResponse.getStarRating() + "";
            if (starRating.equals("null")) {
                starRating = "0";
            }
            setAdStarRate(Double.parseDouble(starRating));
            notifyNativeAdLoaded(this);
        }

        @Override
        public String getRawString(int operation) {
            return MopubInfomation.getMopubNativeAdOfferJsonV313(operation, mNativeResponse);
        }

        @Override
        public void onNativeFail(NativeErrorCode nativeErrorCode) {
            //改ErrorCode值为int
            notifyNativeAdFailed(getAdErrorCode(nativeErrorCode) + "");
        }

        @Override
        public void onNativeImpression(View view) {
            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
        }

        @Override
        public void onNativeClick(View view) {

        }
    }

}
