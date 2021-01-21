package com.buffalo.adsdk.banner;

import android.content.Context;
import android.view.View;

import com.buffalo.adsdk.CMAdError;
import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.nativead.NativeAdManagerInternal;
import com.buffalo.adsdk.utils.Assure;
import com.buffalo.baseapi.ads.INativeAdLoader;
import com.buffalo.utils.Logger;
import com.buffalo.baseapi.ads.INativeAd;


/**
 * Created by xzl on  2016/1/5 10:45.
 */
public class BannerAdManagerRequest extends NativeAdManagerInternal {

    private INativeAd mSrcNativeAd;

    public Object getAdObject() {
        return mSrcNativeAd != null ?  mSrcNativeAd.getAdObject() : null;
    }

    public String getAdType(){
        return mSrcNativeAd != null ?  mSrcNativeAd.getAdTypeName() : null;
    }

    public BannerAdManagerRequest(Context context, String posId, BannerAdSize adSize) {
        super(context, posId);
        BannerParams mRequestParams = new BannerParams();
        mRequestParams.setBannerViewSize(adSize);
        setRequestParams(mRequestParams);
    }

    @Override
    protected int getLoadAdTypeSize() {
        return PRELOAD_REQUEST_SIZE;
    }

    public void loadAd(){
        Logger.i(TAG, mPositionId + " loadAd");
        mIsOpenPriority = false;
        mIsPreload = true;
        mOptimizeEnabled = false;
        mSrcNativeAd = null;
        enableBannerAd();
        super.loadAd();
    }

    @Override
    public void adLoaded(String adTypeName) {
        Logger.i(Const.TAG, "banner loaded type = " + adTypeName);
        INativeAdLoader loader = mLoaderMap.getAdLoader(adTypeName);
        if (loader != null) {
            mSrcNativeAd = loader.getAd();
        }
        super.adLoaded(adTypeName);
    }

    @Override
    protected void checkIfAllfinished() {
        Logger.i(Const.TAG, "check finish");
        Assure.checkRunningOnUIThread();
        if (mIsFinished) {
            Logger.w(Const.TAG, "already finished");
            return;
        }
        if (mSrcNativeAd != null) {
            notifyAdLoaded();
            return;
        }

        if (isAllLoaderFinished()) {
            notifyAdFailed(CMAdError.NO_FILL_ERROR);
        }
    }

    public void prepare(View view){
        if(mSrcNativeAd != null && view != null){
            mSrcNativeAd.registerViewForInteraction(view);
        }
    }

    public void destroy(){
        if(mSrcNativeAd != null){
            Logger.w(Const.TAG, "banner unregister view");
            mSrcNativeAd.unregisterView();
        }
    }

}
