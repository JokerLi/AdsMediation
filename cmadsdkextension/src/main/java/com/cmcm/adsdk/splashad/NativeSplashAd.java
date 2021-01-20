package com.cmcm.adsdk.splashad;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.cmcm.adsdk.BitmapListener;
import com.cmcm.adsdk.CMAdManagerFactory;
import com.cmcm.adsdk.CMNativeAdTemplate;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.view.CMViewRender;
import com.cmcm.adsdk.R;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.utils.Logger;

public class NativeSplashAd {
    private String mPosid;
    private Context mContext;
    private SplashAdListener mListener;
    private NativeSplashLoader mAdloader;
    private Runnable mTimeOutTask;
    private boolean mIsRequested;
    private boolean mIsTimeOut;
    private Handler mHandler;
    private int mAdShowSecond = SplashConst.SPLASH_SHOW_TIME_MILLS;
    private int mLoadTimeoutMilliSecond = SplashConst.SPLASH_LOAD_TIMEOUT_MILLS;
    private boolean mIsShowSpreadSign = true;
    private boolean mIsShowCountDownTime = true;
    private NativeSplashAdView mSplashView;
    private INativeAd mAd;

    public NativeSplashAd(Context context, String posid, SplashAdListener listener) {
        this.mPosid = posid;
        if (null == context) {
            return;
        }
        if(context instanceof Activity){
            this.mContext = context.getApplicationContext();
        }else {
            this.mContext = context;
        }
        mHandler = new Handler(Looper.getMainLooper());
        mListener = listener;
    }

    public void load() {
        if(mIsRequested){
            onError(SplashConst.ERROR_ONLY_CAN_LOAD_ONCE);
            return;
        }
        mIsRequested = true;
        startTimeoutTask();
        initLoader();
        mAdloader.loadAd();
    }

    private NativeSplashLoader initLoader() {
        if (mAdloader == null) {
            mAdloader = new NativeSplashLoader(mContext, mPosid);
            mAdloader.setAdListener(new NativeSplashLoader.AdListener() {
                @Override
                public void onAdLoaded() {
                    Logger.i(Const.TAG, "native splash ad loaded");
                    Logger.i(Const.TAG, "native splash get ad");
                    INativeAd tempAd = mAdloader.getAd();
                    //先get一次ad，如果ad为空或者ad的大图为空则再get一次，因为拉取广告调用的是native的load接口(并发两个)
                    if (tempAd == null || TextUtils.isEmpty(tempAd.getAdCoverImageUrl())) {
                        tempAd = mAdloader.getAd();
                    }
                    if(tempAd == null){
                        Logger.i(Const.TAG, "native splash get ad is null");
                        onError(SplashConst.ERROR_SPALSH_AD_IS_NULL);
                        return;
                    }
                    final INativeAd ad = tempAd;
                    if (ad.getAdTypeName() != null && ad.getAdTypeName().startsWith(Const.KEY_FB)) {
                        callbackLoadSuccess(ad);
                        return;
                    }
                    CMAdManagerFactory.getImageDownloadListener().getBitmap(ad.getAdCoverImageUrl(), false, new BitmapListener(){

                        @Override
                        public void onFailed(String errorCode) {
                            mAd = null;
                            onError(SplashConst.ERROR_GET_AD_IMGE_IS_NULL);
                        }

                        @Override
                        public void onSuccessed(Bitmap bitmap) {
                            Logger.i(Const.TAG, "native splash ad image is ready." + bitmap.hashCode());
                            callbackLoadSuccess(ad);
                        }
                    });
                }

                @Override
                public void onAdLoadFailed(final int errorcode) {
                    Logger.i(Const.TAG, "native splash ad load fail: " + errorcode);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAd = null;
                            onError(errorcode);
                        }
                    });
                }

                @Override
                public void onAdClick() {
                    Logger.i(Const.TAG, "native splash ad click");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onClick();
                            }
                        }
                    });
                }
            });
        }
        return mAdloader;
    }

    private void callbackLoadSuccess(final INativeAd ad) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mAd = ad;
                    if (!mIsTimeOut) {
                        stopTimeOutTask();
                        mListener.onLoadSuccess();
                    }
                }
            }
        });
    }

    public boolean isValid() {
        if (mAd != null && !mAd.hasExpired()) {
            return true;
        }
        return false;
    }

    public NativeSplashAdView createNativeSplashView() {
        if (!isValid()) {
            mAd = null;
            return null;
        }
        CMNativeAdTemplate binder = new CMNativeAdTemplate.Builder(R.layout.cmadsdk_native_splash_ad)
                .mainImageId(R.id.iv_image_cover)
                .titleId(R.id.native_splash_title)
                .callToActionId(R.id.native_splash_btn_cta)
                .adCornerId(R.id.ad_corner)
                .build();
        View adView = new CMViewRender(binder).getBindedView(mAd);
        mSplashView = new NativeSplashAdView(mContext, new SplashAdListener() {
            @Override
            public void onAdImpression() {
                Logger.i(Const.TAG, "native splash ad start impression.");
                mAd = null;
                if (mListener != null) {
                    mListener.onAdImpression();
                }
            }

            @Override
            public void onEndAdImpression() {
                Logger.i(Const.TAG, "native splash ad end impression.");
                if (mListener != null) {
                    mListener.onEndAdImpression();
                }
            }

            @Override
            public void onClick() {
                if (mListener != null) {
                    mListener.onClick();
                }
            }

            @Override
            public void onSkipClick() {
                Logger.i(Const.TAG, "native splash ad skip click.");
                if (mListener != null) {
                    mListener.onSkipClick();
                }
            }

            @Override
            public void onFailed(int resultCode) {
            }

            @Override
            public void onLoadSuccess() {

            }

        });
        mSplashView.setShowMills(mAdShowSecond);
        Logger.i(Const.TAG, "native splash ad show time: " + mAdShowSecond);
        mSplashView.setShowSpreadSign(mIsShowSpreadSign);
        Logger.i(Const.TAG, "native splash ad is show spread sign: " + mIsShowSpreadSign);
        mSplashView.setShowCountDownTime(mIsShowCountDownTime);
        Logger.i(Const.TAG, "native splash ad is show count down time: " + mIsShowCountDownTime);
        return mSplashView.build(adView, mAd) ? mSplashView : null;
    }

    private void onError(int errorCode) {
        if (!mIsTimeOut) {
            stopTimeOutTask();
            if(null != mListener){
                mListener.onFailed(errorCode);
            }
        }
    }

    private void startTimeoutTask(){
        if(null == mTimeOutTask){
            mTimeOutTask = new Runnable() {
                @Override
                public void run() {
                    Logger.i(Const.TAG, "native splash timeout.");
                    onError(SplashConst.ERROR_LOAD_DATA_TIME_OUT);
                    mIsTimeOut = true;
                }
            };
        }
        mHandler.postDelayed(mTimeOutTask, mLoadTimeoutMilliSecond);
    }

    private void stopTimeOutTask(){
        if(null != mTimeOutTask){
            mHandler.removeCallbacks(mTimeOutTask);
            mTimeOutTask = null;
        }
    }

    public NativeSplashAd setIsShowCountDownTime(boolean flag) {
        mIsShowCountDownTime = flag;
        return this;
    }

    public NativeSplashAd setLoadTimeOutMilliSecond(int time) {
        if (time > 0 && time < 5000) {
            mLoadTimeoutMilliSecond = time;
        }
        return this;
    }

    public NativeSplashAd setAdShowTimeSecond(int timeSecond) {
        if(timeSecond > 0 && timeSecond < 8){
            mAdShowSecond = timeSecond;
        }
        return this;
    }

    public NativeSplashAd setShowSpreadSign(boolean isShowSpreadSign) {
        mIsShowSpreadSign = isShowSpreadSign;
        return this;
    }


    public  interface SplashAdListener {
        void onLoadSuccess();
        void onAdImpression();
        void onEndAdImpression();
        void onClick();
        void onSkipClick();
        void onFailed(int resultCode);
    }
}
