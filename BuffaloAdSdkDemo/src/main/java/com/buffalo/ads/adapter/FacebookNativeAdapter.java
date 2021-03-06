package com.buffalo.ads.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.buffalo.ads.NativeAdBaseContextWrapper;
import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.NativeAdError;
import com.buffalo.adsdk.adapter.NativeloaderAdapter;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.buffalo.utils.Commons;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;

import java.util.List;
import java.util.Map;

public class FacebookNativeAdapter extends NativeloaderAdapter {
    final public int MAX_LOAD_ITEM = 3;

    private Map<String, Object> mExtras;
    private Context mContext;
    String mPlacementId;
    int mRequestAdSize = 1;

    @Override
    public void loadNativeAd(@NonNull Context context,
                             @NonNull Map<String, Object> extras) {

        mContext = new NativeAdBaseContextWrapper(context);
        mExtras = extras;
        if (!extrasAreValid(extras)) {
            notifyNativeAdFailed(String.valueOf(NativeAdError.PARAMS_ERROR));
            return;
        }
        mPlacementId = (String) mExtras.get(BaseNativeAd.KEY_PLACEMENT_ID);
        if (mExtras.containsKey(BaseNativeAd.KEY_LOAD_SIZE)) {
            try {
                mRequestAdSize = (Integer) mExtras.get(BaseNativeAd.KEY_LOAD_SIZE);
                mRequestAdSize = Commons.range(mRequestAdSize, 1, MAX_LOAD_ITEM);
            } catch (Exception e) {
                mRequestAdSize = 1;
            }
        }

        //"new FacebookNativeAd().loadAd();" method must be invoked in background thread
//        try {
//            if (mRequestAdSize > 1) {
//                new FacebookAdsAdapter().loadNativeAd();
//            } else {
//                new FacebookNativeAd().loadAd();
//            }
//        } catch (Throwable e) {
//            notifyNativeAdFailed("facebook load error");
//        }
        try {
            new FacebookNativeAd().loadAd();
        } catch (Throwable e) {
            notifyNativeAdFailed("facebook load error");
        }
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.facebook;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        return "fb.com";
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_FB;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.facebook;
    }

    private class FacebookNativeAd extends BaseNativeAd implements NativeAdListener {
        private NativeAd mNativeAd;

        public FacebookNativeAd() {
        }

        public void loadAd() {
            mNativeAd = new NativeAd(mContext, mPlacementId);
            mNativeAd.loadAd(
                    mNativeAd.buildLoadAdConfig()
                            .withAdListener(this)
                            .build());
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_FB;
        }

        @Override
        public boolean registerViewForInteraction(View view, View mediaView, @Nullable View adIconView, @Nullable List<View> clickableViews) {
            if (view == null || !(mediaView instanceof MediaView)) {
                return false;
            }
            if (adIconView instanceof MediaView) {
                MediaView iconMediaView = (MediaView) adIconView;
                mNativeAd.registerViewForInteraction(view, (MediaView) mediaView, iconMediaView, clickableViews);
            } else if (adIconView instanceof ImageView) {
                ImageView iconImageView = (ImageView) adIconView;
                mNativeAd.registerViewForInteraction(view, (MediaView) mediaView, iconImageView, clickableViews);
            } else {
                mNativeAd.registerViewForInteraction(view, (MediaView) mediaView, clickableViews);
            }
            return true;
        }

        @Override
        public void unregisterView() {
            mNativeAd.unregisterView();
        }

        @Override
        public Object getAdObject() {
            return mNativeAd;
        }

        @Override
        public void handleClick() {
        }

        @Override
        public void onError(Ad ad, AdError adError) {
            //改ErrorCode值为int
            notifyNativeAdFailed(adError.getErrorCode() + "");
        }

        @Override
        public void onAdLoaded(Ad ad) {
            if (!mNativeAd.equals(ad) || !mNativeAd.isAdLoaded()) {
                notifyNativeAdFailed("response is null");
                return;
            }
            updateData();
            notifyNativeAdLoaded(this);
        }

        private void updateData() {
            setTitle(mNativeAd.getAdvertiserName());
            setAdBody(mNativeAd.getAdBodyText());
            setAdCoverImageUrl(mNativeAd.getAdCoverImage().getUrl());
            setAdIconUrl(mNativeAd.getAdIcon().getUrl());
            setAdCallToAction(mNativeAd.getAdCallToAction());
            setAdSocialContext(mNativeAd.getAdSocialContext());
            setAdStarRate(mNativeAd.getAdStarRating() != null ? mNativeAd.getAdStarRating().getValue() : 0.0d);
        }

        @Override
        public void onAdClicked(Ad ad) {
            notifyNativeAdClick(this);
        }

        @Override
        public void onLoggingImpression(Ad ad) {
            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
        }

        @Override
        public void onMediaDownloaded(Ad ad) {

        }
    }

//    public class FacebookAdsAdapter implements NativeAdsManager.Listener {
//        private NativeAdsManager mNativeAdsMgr;
//
//        public void loadNativeAd() {
//            mNativeAdsMgr = new NativeAdsManager(mContext, mPlacementId, mRequestAdSize);
//            mNativeAdsMgr.setListener(this);
//            mNativeAdsMgr.loadAds();
//        }
//
//        @Override
//        public void onAdsLoaded() {
//            int adCount = mNativeAdsMgr.getUniqueNativeAdCount();
//            List<INativeAd> mReusltPool = new ArrayList<INativeAd>();
//            for (int i = 0; i < adCount; i++) {
//                NativeAd fbNativeAd = mNativeAdsMgr.nextNativeAd();
//                if (fbNativeAd == null || !fbNativeAd.isAdLoaded()) {
//                    continue;
//                }
//                mReusltPool.add(new FacebookNativeAd(fbNativeAd));
//            }
//
//            if (mReusltPool.isEmpty()) {
//                notifyNativeAdFailed("fbAdsManager.onAdsLoaded.no.fill");
//            } else {
//                notifyNativeAdLoaded(mReusltPool);
//            }
//        }
//
//        @Override
//        public void onAdError(AdError adError) {
//            notifyNativeAdFailed(adError.getErrorMessage());
//        }
//
//    }
}
