package com.cmcm.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.cmcm.orion.picks.api.OrionNativeAd;
import com.cmcm.orion.picks.api.OrionNativeAdsManager;


import java.util.Map;

/**
 * Created by chenhao on 15/12/1.
 */
public class PicksNativeAdapter extends NativeloaderAdapter {
    private static final int PICKS_DEFAULT_LOAD_NUM = 10;
    public static final int DOWNLOAD_MT_TYPE = 8;

    protected String mPlacementId;
    protected Map<String, Object> mExtras;
    protected Context mContext;
    protected int loadSize;
    protected boolean isFeedList = false;
    protected OrionNativeAdsManager orionNativeAdsManager;

    @Override
    public void loadNativeAd(@NonNull Context context,
                                @NonNull Map<String, Object> extras) {
        this.mContext = context;
        this.mExtras = extras;
        if(!extrasAreValid(extras)){
            notifyNativeAdFailed(String.valueOf(CMAdError.PARAMS_ERROR));
            return;
        }
        mPlacementId = (String) mExtras.get(CMBaseNativeAd.KEY_PLACEMENT_ID);
        if(mExtras.containsKey(CMBaseNativeAd.KEY_LOAD_SIZE)){
            loadSize = (int)mExtras.get(CMBaseNativeAd.KEY_LOAD_SIZE);
        }
        if(mExtras.containsKey(CMBaseNativeAd.KEY_IS_FEED)){
            isFeedList = (boolean)mExtras.get(CMBaseNativeAd.KEY_IS_FEED);
        }

        if (isFeedList) {
            new PicksFeedAdLoader().loadAd(loadSize);
        } else {
            new PicksNativeAdLoader().loadAd(loadSize);
        }
    }

    @Override
    public int getDefaultLoadNum() {
        return PICKS_DEFAULT_LOAD_NUM;
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.cm;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        return Const.pkgName.cm;
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_CM;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.cm;
    }

    protected class PicksNativeAdLoader implements OrionNativeAd.OrionNativeListener {

        public void loadAd(int num) {
            OrionNativeAd orionNativeAd = new OrionNativeAd(mPlacementId);
            orionNativeAd.setRequestAdNum(num);
            orionNativeAd.setListener(this);
            orionNativeAd.load();
        }

        @Override
        public void onAdLoaded(OrionNativeAd ad) {
            if (ad != null) {
                notifyNativeAdLoaded(new PicksNativeAd(ad));
            } else {
                onFailed(-1);
            }
        }

        @Override
        public void onFailed(int code) {
            notifyNativeAdFailed(String.valueOf(code));
        }
    }

    protected class PicksFeedAdLoader implements OrionNativeAdsManager.OrionNativeAdsListener {
        public void loadAd(int num){
            if(orionNativeAdsManager == null){
                orionNativeAdsManager = new OrionNativeAdsManager(mPlacementId);
            }
            orionNativeAdsManager.setRequestAdNum(1);
            orionNativeAdsManager.setListener(this);
            orionNativeAdsManager.load();
        }

        @Override
        public void onAdLoaded() {
            OrionNativeAd ad = orionNativeAdsManager.nextNativeAd();
            if (ad != null) {
                notifyNativeAdLoaded(new PicksNativeAd(ad));
            } else {
                onFailed(-1);
            }
        }

        @Override
        public void onFailed(int code) {
            notifyNativeAdFailed(String.valueOf(code));
        }
    }

    static class PicksNativeAd extends CMBaseNativeAd implements
            OrionNativeAd.OrionImpressionListener {
        final protected OrionNativeAd mAd;

        public PicksNativeAd(OrionNativeAd ad){
            mAd = ad;
            if (mAd != null) {
                setUpData(mAd);
            }
        }

        void setUpData(OrionNativeAd ad){
            if(mAd.getAppShowType()==com.cmcm.orion.adsdk.Const.SHOW_TYPE_NEWS_SMALL_PIC
                    || mAd.getAppShowType()== com.cmcm.orion.adsdk.Const.SHOW_TYPE_NEWS_THREE_PIC){
                Log.d("CMCMADSDK","70003|70002 pic size="+mAd.getExtPics().size());
                setExtPics(mAd.getExtPics());
            }
            setTitle(mAd.getTitle());
            setAdCoverImageUrl(mAd.getCoverImageUrl());
            setAdIconUrl(mAd.getIconUrl());
            setAdCallToAction(mAd.getButtonTxt());
            setAdBody(mAd.getAdBody());
            setAdStarRate(mAd.getRating());
            setAdSocialContext(mAd.getButtonTxt());
            setIsDownloadApp(mAd.getMtType() == DOWNLOAD_MT_TYPE);
            setIsPriority(this.mAd.getPriority() == 1);
//            setMpaModule(this.mAd.getMpaModule());
            setSource(this.mAd.getSource());
            ad.setImpressionListener(this);
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_CM;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            mAd.registerViewForInteraction(view);
            return true;
        }

        @Override
        public boolean registerViewForInteraction_withExtraReportParams(View view, Map<String, String> reportParam) {
            mAd.registerViewForInteraction(view, reportParam);
            return true;
        }

        @Override
        public void unregisterView() {
            mAd.unregisterView();
        }

        @Override
        public boolean hasExpired() {
            return !mAd.isAvailAble();
        }

        @Override
        public Object getAdObject() {
            return mAd;
        }

        @Override
        public void onAdImpression() {
            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
        }

        @Override
        public void onAdClick() {
            notifyNativeAdClick(this);
        }

    }
}
