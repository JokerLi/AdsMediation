package com.cmcm.adsdk.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.ViewGroup;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.splashad.SplashBaseAdapter;
import com.cmcm.utils.Logger;

public class BaiduSplashAdapter extends SplashBaseAdapter {

    private static final java.lang.String TAG = "SplashAdManager";
    private ViewGroup adsParent;
    private OnSplashAdapterResultListener mOnSplashAdapterResultListener;
    private boolean AdCanClick = true;
    private SplashAd mSplashAd;

    public void setAdCanClick(boolean adCanClick) {
        AdCanClick = adCanClick;
    }

    @Override
    public String getAdType() {
        return Const.KEY_BD;
    }

    @Override
    public void onDestroy() {
        mSplashAd = null;
    }

    @Override
    protected void loadSplashAd(@NonNull Activity context, @NonNull OnSplashAdapterResultListener onSplashAdapterResultListener, @NonNull ViewGroup container) {
        this.adsParent = container;
        this.mOnSplashAdapterResultListener = onSplashAdapterResultListener;
        if (context != null && adsParent != null && mParamters != null) {
            Logger.i(TAG, "baidu load splash ad,and the placeid = " + mParamters);
            mSplashAd = new SplashAd(context, adsParent, new MyBaiduAdListener(), mParamters, AdCanClick);
            //TODO：外部回调
//            doLoadReport(getAdType());
        } else {
            if (null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onAdFailed(Const.KEY_BD, CMAdError.ERROR_CONFIG);
//                doLoadFailReport(getAdType(), CMAdError.ERROR_CONFIG);
            }
        }
    }

    @Override
    protected void reportImpression() {
        String reportPkgName = String.format("%s.%s", Const.pkgName.baidu, Const.REPORT_SPLASH_SUFFIX);
        super.reportImpression(Const.res.baidu, reportPkgName);
    }

    @Override
    protected void reportClick() {
        String reportPkgName = String.format("%s.%s", Const.pkgName.baidu, Const.REPORT_SPLASH_SUFFIX);
        super.reportClick(Const.res.baidu, reportPkgName);
    }


    class MyBaiduAdListener implements SplashAdListener {

        @Override
        public void onAdPresent() {
            Logger.i(TAG, "baidu load splash ad,and onAdPresent");
            reportImpression();
            if (null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onAdPresent(Const.KEY_BD);
            }
            //TODO:外部埋点
//            doLoadSuccReport(getAdType());
        }

        @Override
        public void onAdDismissed() {
            Logger.i(TAG, "baidu load splash ad,and onAdDismissed");
            if (null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onAdDismissed(Const.KEY_BD);
            }
        }

        @Override
        public void onAdFailed(String errorMessage) {
            Logger.i(TAG, "baidu load splash ad,and onAdFailed");
            if (null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onAdFailed(Const.KEY_BD, errorMessage);
            }
//            doLoadFailReport(getAdType(), errorMessage);
        }

        @Override
        public void onAdClick() {
            Logger.i(TAG, "baidu load splash ad,and onAdClick");
            reportClick();
            if (null != mOnSplashAdapterResultListener) {
                mOnSplashAdapterResultListener.onClicked(Const.KEY_BD);
            }
        }
    }


}
