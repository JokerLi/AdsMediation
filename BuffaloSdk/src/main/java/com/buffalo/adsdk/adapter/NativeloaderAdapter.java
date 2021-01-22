package com.buffalo.adsdk.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.utils.ThreadHelper;

import java.util.List;
import java.util.Map;

public abstract class NativeloaderAdapter {
    public static final int DEFAULT_LOAD_SIZE = 1;

    private static final String AD_LOAD_ADS = "ad_load_ads";
    private static final String AD_LOAD_AD = "ad_load_ad";
    private static final String FAILED = "failed";

    private NativeAdapterListener mListener;

    public interface NativeAdapterListener {
        void onNativeAdLoaded(INativeAd nativeAd);

        void onNativeAdLoaded(List<INativeAd> list);

        void onNativeAdFailed(String errorInfo);
    }

    //对Adapter传入的参数进行检查，默认检查方式是placementId不为null
    // 需要特殊检查的复写此方法
    public boolean extrasAreValid(Map<String, Object> extras) {
        if (extras == null || !extras.containsKey(BaseNativeAd.KEY_PLACEMENT_ID)) {
            return false;
        }

        Object stringValue = extras.get(BaseNativeAd.KEY_PLACEMENT_ID);
        if (stringValue instanceof String) {
            return !TextUtils.isEmpty((String) stringValue);
        }
        return false;
    }

    public abstract void loadNativeAd(@NonNull final Context context, @NonNull final Map<String, Object> extras);

    public int getDefaultLoadNum() {
        return DEFAULT_LOAD_SIZE;
    }

    public abstract int getReportRes(String adTypeName);

    public abstract String getReportPkgName(String adTypeName);

    public abstract String getAdKeyType();

    public abstract long getDefaultCacheTime();

    public void setAdapterListener(NativeAdapterListener listener) {
        mListener = listener;
    }

    protected void notifyNativeAdLoaded(final INativeAd nativeAd) {
        callBack(AD_LOAD_AD, nativeAd, null, "");
    }

    protected void notifyNativeAdLoaded(final List<INativeAd> list) {
        callBack(AD_LOAD_ADS, null, list, "");
    }

    protected void notifyNativeAdFailed(final String errorInfo) {
        callBack(FAILED, null, null, errorInfo);
    }

    private void callBack(String type, final INativeAd nativeAd, final List<INativeAd> list, final String errorInfo) {
        ThreadHelper.runOnUiThread(new CallBackRunnable(type, nativeAd, list, errorInfo));
    }

    private class CallBackRunnable implements Runnable {
        private String type;
        private INativeAd nativeAd;
        private List<INativeAd> list;
        private String errorInfo;

        public CallBackRunnable(String type, INativeAd nativeAd, List<INativeAd> list, String errorInfo) {
            this.type = type;
            this.nativeAd = nativeAd;
            this.list = list;
            this.errorInfo = errorInfo;
        }

        @Override
        public void run() {
            if (mListener != null) {
                if (AD_LOAD_ADS.equals(type)) {
                    mListener.onNativeAdLoaded(list);
                } else if (AD_LOAD_AD.equals(type)) {
                    mListener.onNativeAdLoaded(nativeAd);
                } else if (FAILED.equals(type)) {
                    mListener.onNativeAdFailed(errorInfo);
                }
            }
        }
    }

    public Const.AdType getAdType() {
        return Const.AdType.NATIVE;
    }

    //返回广告请求的错误码,如果返回不是int型，则子类中实现该方法转换，提供给上报统计用
    public int getAdErrorCode(Object errorInstance) {
        return 0;
    }
}
