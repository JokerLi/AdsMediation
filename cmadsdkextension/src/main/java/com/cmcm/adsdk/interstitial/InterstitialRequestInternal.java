package com.cmcm.adsdk.interstitial;

import android.content.Context;

import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.nativead.NativeAdManagerInternal;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoader;
import com.cmcm.utils.Logger;

/**
 * Created by chenhao on 2016/1/8.
 */
public class InterstitialRequestInternal extends NativeAdManagerInternal {


    private INativeAd mCachedAd;
    public InterstitialRequestInternal(Context context, String posId) {
        super(context, posId);
    }


    @Override
    public void loadAd() {
        Logger.i(TAG, mPositionId + " loadAd");
        if(mCachedAd != null && !mCachedAd.hasExpired()){
            notifyAdLoaded();
            return;
        }
        mIsOpenPriority = false;
        mIsPreload = true;
        mOptimizeEnabled = false;
        super.loadAd();
    }

    public boolean isReady() {
        if (mCachedAd != null && !mCachedAd.hasExpired()) {
            return true;
        }
        return false;
    }

    public String getCachelAdType() {
        if (isReady()) {
            return mCachedAd.getAdTypeName();
        }
        return null;
    }


    @Override
    protected int getLoadAdTypeSize() {
        return NativeAdManagerInternal.PRELOAD_REQUEST_SIZE;
    }


    @Override
    public void adLoaded(String adTypeName) {
        INativeAdLoader loader = mLoaderMap.getAdLoader(adTypeName);
        if (loader != null) {
            INativeAd ad = loader.getAd();
            if (ad != null && ad.getAdObject() != null) {
                mCachedAd = ad;
            }
        }
        super.adLoaded(adTypeName);
    }


    @Override
    protected void checkIfAllfinished() {
        Logger.i(Const.TAG, "check finish");

        if (mIsFinished) {
            Logger.w(Const.TAG, "already finished");
            return;
        }

        if (mCachedAd != null) {
            notifyAdLoaded();
            return;
        }

        if (isAllLoaderFinished()) {
            notifyAdFailed(CMAdError.NO_FILL_ERROR);
        }
    }

    public void showAd(){
        if(mCachedAd != null){
            mCachedAd.registerViewForInteraction(null);
            mCachedAd = null;
        }
    }
}
