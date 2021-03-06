package com.cmcm.adsdk.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.utils.BackgroundThread;
import com.cmcm.utils.Commons;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.ImpressionListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import java.util.ArrayList;
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

        mContext = context;
        mExtras = extras;
        if(!extrasAreValid(extras)){
            notifyNativeAdFailed(String.valueOf(CMAdError.PARAMS_ERROR));
            return;
        }
        mPlacementId = (String)mExtras.get(CMBaseNativeAd.KEY_PLACEMENT_ID);
        if (mExtras.containsKey(CMBaseNativeAd.KEY_LOAD_SIZE)) {
            try {
                mRequestAdSize = (Integer)mExtras.get(CMBaseNativeAd.KEY_LOAD_SIZE);
                mRequestAdSize = Commons.range(mRequestAdSize, 1, MAX_LOAD_ITEM);
            } catch (Exception e) {
                mRequestAdSize = 1;
            }
        }

        BackgroundThread.postOnIOThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mRequestAdSize > 1) {
                        new FacebookAdsAdapter().loadNativeAd();
                    } else {
                        new FacebookNativeAd().loadAd();
                    }
                }catch (Throwable e){
                    notifyNativeAdFailed("facebook load error");
                }
            }
        });
    }

    @Override
    public int getReportRes(int type ,String adTypeName) {
        if(type == RES_TYPE_RCV) {
            return Const.res.facebook;
        }else{
            if(adTypeName.contains("_h")){
                return Const.res.pega_fb_h;
            }else if(adTypeName.contains("_b")){
                return Const.res.pega_fb_b;
            }else if(adTypeName.contains("_l")){
                return Const.res.pega_fb_l;
            }else{
                return Const.res.facebook;
            }
        }
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        return FaceBookInfomation.getFBReportPkg(adTypeName);
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_FB;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.facebook;
    }

    private class FacebookNativeAd extends CMBaseNativeAd implements AdListener, ImpressionListener {
        private NativeAd mNativeAd;

        public FacebookNativeAd(){
        }

        public FacebookNativeAd(NativeAd nativeAd) {
            mNativeAd = nativeAd;

            if (mNativeAd != null) {
                mNativeAd.setAdListener(this);
                mNativeAd.setImpressionListener(this);
                updateData();
            }
        }

        public void loadAd(){
            mNativeAd = new NativeAd(mContext, mPlacementId);
            mNativeAd.setAdListener(this);
            mNativeAd.setImpressionListener(this);
            mNativeAd.loadAd();
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_FB;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            if(view == null){
                return false;
            }
            mNativeAd.registerViewForInteraction(view);
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
            notifyNativeAdFailed(adError.getErrorCode()+"");
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
            setTitle(mNativeAd.getAdTitle());
            setAdBody(mNativeAd.getAdBody());
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
        public String getRawString(int operation) {
            return FaceBookInfomation.getRawJson(operation, mNativeAd);
        }
    }

    public class FacebookAdsAdapter implements NativeAdsManager.Listener {
        private NativeAdsManager mNativeAdsMgr;

        public void loadNativeAd() {
            mNativeAdsMgr = new NativeAdsManager(mContext, mPlacementId, mRequestAdSize);
            mNativeAdsMgr.setListener(this);
            mNativeAdsMgr.loadAds();
        }

        @Override
        public void onAdsLoaded() {
            int adCount = mNativeAdsMgr.getUniqueNativeAdCount();
            List<INativeAd> mReusltPool = new ArrayList<INativeAd>();
            for(int i = 0; i < adCount; i++) {
                NativeAd fbNativeAd = mNativeAdsMgr.nextNativeAd();
                if(fbNativeAd == null || !fbNativeAd.isAdLoaded()){
                    continue;
                }
                mReusltPool.add(new FacebookNativeAd(fbNativeAd));
            }

            if (mReusltPool.isEmpty()) {
                notifyNativeAdFailed("fbAdsManager.onAdsLoaded.no.fill");
            } else {
                notifyNativeAdLoaded(mReusltPool);
            }
        }

        @Override
        public void onAdError(AdError adError) {
            notifyNativeAdFailed(adError.getErrorMessage());
        }

    }
}
