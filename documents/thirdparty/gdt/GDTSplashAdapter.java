package com.cmcm.adsdk.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.splashad.SplashBaseAdapter;
import com.cmcm.utils.Logger;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

public class GDTSplashAdapter extends SplashBaseAdapter {

    private static final java.lang.String TAG = "SplashAdManager";
    private ViewGroup adsParent;
    private OnSplashAdapterResultListener mOnSplashAdapterResultListener;
    private String mTencentAppId;
    private String mTencentPosId;
    private SplashAD mSplashAD;

    private void initId(String params) {
        if (!TextUtils.isEmpty(params) && params.contains("_")) {
            String[] ids = params.split("_");
            if (ids.length >= 2) {
                mTencentAppId = ids[0];
                mTencentPosId = ids[1];
            }
        }
    }

    @Override
    public String getAdType() {
        return Const.KEY_GDT;
    }

    @Override
    public void onDestroy() {
        mSplashAD = null;
    }

    @Override
    protected void loadSplashAd(@NonNull Activity activity, @NonNull OnSplashAdapterResultListener onSplashAdapterResultListener, @NonNull ViewGroup container) {
        initId(mParamters);

        this.mContext = activity;
        this.adsParent = container;
        this.mOnSplashAdapterResultListener = onSplashAdapterResultListener;
        if (activity != null && adsParent != null && !TextUtils.isEmpty(mTencentAppId) && !TextUtils.isEmpty(mTencentPosId)) {
            Logger.i(TAG, "gdt load splash ad,and appid = " + mTencentAppId + ",and posid = " + mTencentPosId);
            mSplashAD = new SplashAD(activity, adsParent, mTencentAppId, mTencentPosId, new MyGDTListener());
            //TODO:注释掉请求上报
//            doLoadReport(getAdType());
        } else {
            if (null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onAdFailed(Const.KEY_GDT, CMAdError.ERROR_CONFIG);
//                doLoadFailReport(getAdType(), CMAdError.ERROR_CONFIG);
            }
        }
    }

    @Override
    protected void reportImpression() {
        String reportPkgName = String.format("%s.%s", Const.pkgName.gdt, Const.REPORT_SPLASH_SUFFIX);
        super.reportImpression(Const.res.gdt, reportPkgName);
    }

    @Override
    protected void reportClick() {
        String reportPkgName = String.format("%s.%s", Const.pkgName.gdt, Const.REPORT_SPLASH_SUFFIX);
        super.reportClick(Const.res.gdt, reportPkgName);
    }

    class MyGDTListener implements SplashADListener {

        @Override
        public void onADDismissed() {
            Logger.i(TAG, "gdt load splash ad,and onADDismissed");
            reportImpression();
            if (null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onAdDismissed(Const.KEY_GDT);
            }
        }

        @Override
        public void onNoAD(int errorCode) {
            Logger.i(TAG, "gdt load splash ad,and onNoAD");
            if (null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onAdFailed(Const.KEY_GDT, String.valueOf(errorCode));
            }
//            doLoadFailReport(getAdType(), String.valueOf(errorCode));
        }

        @Override
        public void onADPresent() {
            Logger.i(TAG, "gdt load splash ad,and onADPresent");
            if (null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onAdPresent(Const.KEY_GDT);
            }
//            doLoadSuccReport(getAdType());
        }
    }
}
