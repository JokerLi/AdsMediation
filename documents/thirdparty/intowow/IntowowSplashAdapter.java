package com.cmcm.adsdk.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.cmcm.adsdk.BuildConfig;
import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.splashad.SplashBaseAdapter;
import com.cmcm.utils.Logger;
import com.intowow.sdk.Ad;
import com.intowow.sdk.AdError;
import com.intowow.sdk.AdListener;
import com.intowow.sdk.DisplayAd;
import com.intowow.sdk.I2WAPI;


/**
 * Created by xzl on  2015/11/30 16:58.
 */
public class IntowowSplashAdapter extends SplashBaseAdapter {

    private static final String TAG = "SplashAdManager";
    private static final int DELAYE_TIME = 750;//毫秒
    private ViewGroup adsParent;
    private OnSplashAdapterResultListener mOnSplashAdapterResultListener;
    private DisplayAd mDisplayAd;
    private static final String ADERROR = "iclick inner error";
    
    @Override
    public String getAdType() {
        return Const.KEY_ICLICK;
    }

    @Override
    public void onDestroy() {
        if(mDisplayAd != null) {
            mDisplayAd.stop();
            mDisplayAd.destroy();
            mDisplayAd = null;
        }
    }

    @Override
    public void onStop() {
        if(mDisplayAd != null) {
            mDisplayAd.stop();
        }
    }

    @Override
    public void onPause() {
        if (mContext != null) {
            I2WAPI.onActivityPause(mContext);
        }
    }

    @Override
    public void onResume() {
        if(mContext != null) {
            I2WAPI.onActivityResume(mContext);
        }
    }

    @Override
    protected void loadSplashAd(@NonNull Activity context, @NonNull OnSplashAdapterResultListener onSplashAdapterResultListener, @NonNull ViewGroup container) {
        this.mContext = context;
        this.adsParent = container;
        this.mOnSplashAdapterResultListener = onSplashAdapterResultListener;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        Log.i(TAG, "SplashMainActivity onCreate ++++++++++++++ width=" + width);
        if(mContext != null && mParamters != null && width != 0){
            Logger.i(TAG, "iclick load splash ad,and the placeid =" + mParamters + ",and width = " + width);
            try {
                if(BuildConfig.DEBUG){
                    I2WAPI.init(mContext, true);
                }else {
                    I2WAPI.init(mContext);
                }
                mDisplayAd = new DisplayAd(context, mParamters);
                mDisplayAd.setAdListener(new MyIntowowListener());
                mDisplayAd.setWidth(width);
                mDisplayAd.setAutoplay(true);
                mDisplayAd.loadAd();
                //TODO:外部回调
//                doLoadReport(getAdType());
            }catch (Exception e){
                if(null != mOnSplashAdapterResultListener) {
                    mOnSplashAdapterResultListener.onAdFailed(Const.KEY_ICLICK, ADERROR);
//                    doLoadFailReport(getAdType(), ADERROR);
                }
            }
        }else{
            if(null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onAdFailed(Const.KEY_ICLICK, CMAdError.ERROR_CONFIG);
//                doLoadFailReport(getAdType(), CMAdError.ERROR_CONFIG);
            }
        }
    }

    @Override
    protected void reportImpression() {
        String reportPkgName = String.format("%s.%s", Const.pkgName.iclick, Const.REPORT_SPLASH_SUFFIX);
        super.reportImpression(Const.res.iclick, reportPkgName);
    }

    @Override
    protected void reportClick() {
        String reportPkgName = String.format("%s.%s", Const.pkgName.iclick, Const.REPORT_SPLASH_SUFFIX);
        super.reportClick(Const.res.iclick, reportPkgName);
    }

    class MyIntowowListener implements AdListener{

        private boolean mIsFirstVideoClick = false;
        private boolean mIsFirstImgClick = false;

        @Override
        public void onError(Ad ad, AdError adError) {
            Logger.i(TAG, "iclick load splash ad,and onError");
            if(null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onAdFailed(Const.KEY_ICLICK, adError.getErrorMessage());
            }
//            doLoadFailReport(getAdType(), adError.getErrorMessage());
        }

        @Override
        public void onAdLoaded(Ad ad) {
            Logger.i(TAG, "iclick load splash ad,and onAdPresent");
            if (mDisplayAd != null && mDisplayAd == ad) {
                View view = mDisplayAd.getView();
                if (view != null && adsParent != null) {
                    adsParent.addView(view);
                    //mDisplayAd.play();
                }
                if (null != mOnSplashAdapterResultListener) {
                    mOnSplashAdapterResultListener.onAdPresent(mDisplayAd.hasVideoContent() ? Const.KEY_ICLICK_VIDEO  : Const.KEY_ICLICK);
                }
//                doLoadSuccReport(getAdType());
            }else{
                if(null != mOnSplashAdapterResultListener) {
                    mOnSplashAdapterResultListener.onAdFailed(Const.KEY_ICLICK, ADERROR);
                }
            }
        }

        @Override
        public void onAdClicked(Ad ad) {
            Logger.i(TAG, "iclick load splash ad,and onAdClicked");
            if(null != mOnSplashAdapterResultListener && !mIsFirstImgClick) {
                mIsFirstImgClick = true;
                mOnSplashAdapterResultListener.onClicked(Const.KEY_ICLICK);
                reportClick();
            }
        }

        @Override
        public void onAdImpression(Ad ad) {
            if(mDisplayAd != null){
                Logger.i(TAG, "iclick load splash ad,and onAdImpression，is video" + mDisplayAd.hasVideoContent());
            }
            reportImpression();
        }

        @Override
        public void onAdMute(Ad ad) {
            Logger.i(TAG, "iclick load splash ad,and onAdMute");
            if(null != mOnSplashAdapterResultListener && !mIsFirstVideoClick) {
                mIsFirstVideoClick = true;
                mOnSplashAdapterResultListener.onClicked(Const.KEY_ICLICK_VIDEO);
            }
        }

        @Override
        public void onAdUnmute(Ad ad) {
            Logger.i(TAG, "iclick load splash ad,and onAdUnmute");
            if(null != mOnSplashAdapterResultListener && !mIsFirstVideoClick) {
                mIsFirstVideoClick = true;
                mOnSplashAdapterResultListener.onClicked(Const.KEY_ICLICK_VIDEO);
            }
        }

        @Override
        public void onVideoStart(Ad ad) {
            Logger.i(TAG, "iclick load splash ad,and onVideoStart");
        }

        @Override
        public void onVideoEnd(Ad ad) {
            Logger.i(TAG, "cm load splash ad,and onVideoEnd");
        }

        @Override
        public void onVideoProgress(Ad ad, int totoalDuration, int currentPosition) {
            Logger.i(TAG, "iclick load splash ad,and onVideoProgress,and totalDuration = " + totoalDuration + ",the currentPosition = " + currentPosition);
            if(totoalDuration - currentPosition < DELAYE_TIME){
                if(null != mOnSplashAdapterResultListener) {
                    mOnSplashAdapterResultListener.onAdDismissed(Const.KEY_ICLICK_VIDEO);
                }
            }
        }
    }

}
