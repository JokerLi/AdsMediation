package com.cmcm.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.utils.ThreadHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by chenhao on 15/12/1.
 */
public abstract class NativeloaderAdapter {
    public static final int DEFAULT_LOAD_SIZE = 1;
    public static final int RES_TYPE_RCV = 0;
    public static final int RES_TYPE_PEG = 1;

    public interface NativeAdapterListener{
        void onNativeAdLoaded(INativeAd nativeAd);
        void onNativeAdLoaded(List<INativeAd> list);
        void onNativeAdFailed(String errorInfo);
    }

    //对Adapter传入的参数进行检查，默认检查方式是placementId不为null
    // 需要特殊检查的复写此方法
    public boolean extrasAreValid(Map<String, Object> extras) {
        try {
            if (extras != null && extras.containsKey(CMBaseNativeAd.KEY_PLACEMENT_ID)) {
                String object = (String) extras.get(CMBaseNativeAd.KEY_PLACEMENT_ID);
                return !TextUtils.isEmpty(object);
            }
        }catch (Exception e){

        }
        return false;
    }

    public abstract void loadNativeAd(@NonNull final Context context,

                                         @NonNull final Map<String, Object> extras);

    public int getDefaultLoadNum() {
        return DEFAULT_LOAD_SIZE;
    }
    public abstract int getReportRes(String adTypeName);
    public abstract String getReportPkgName(String adTypeName);
    public abstract String getAdKeyType();
    public abstract long getDefaultCacheTime();


    private NativeAdapterListener mListener;

    public void setAdapterListener(NativeAdapterListener listener) {
        mListener = listener;
    }

    protected void notifyNativeAdLoaded(final INativeAd nativeAd) {
        callBack(ADLOAD_AD, nativeAd, null, "");
    }

    protected void notifyNativeAdLoaded(final List<INativeAd> list) {
        callBack(ADLOAD_ADS, null, list, "");
    }

    protected void notifyNativeAdFailed(final String errorInfo) {
        callBack(FAILED, null, null, errorInfo);
    }

    private final String ADLOAD_ADS = "adload_ads";
    private final String ADLOAD_AD = "adload_ad";
    private final String FAILED = "failed";
    private void callBack(String type, final INativeAd nativeAd, final List<INativeAd> list, final String errorInfo) {
        ThreadHelper.runOnUiThread(new CallBackRunnable(type, nativeAd, list, errorInfo));
    }

    private class CallBackRunnable implements Runnable{
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
                if(ADLOAD_ADS.equals(type)){
                    mListener.onNativeAdLoaded(list);
                }else if(ADLOAD_AD.equals(type)){
                    mListener.onNativeAdLoaded(nativeAd);
                }else if(FAILED.equals(type)){
                    mListener.onNativeAdFailed(errorInfo);
                }
            }
        }
    }

    public Const.AdType getAdType(){
        return Const.AdType.NATIVE;
    }

    //返回广告请求的错误码,如果返回不是int型，则子类中实现该方法转换，提供给上报统计用
    public int getAdErrorCode(Object errorInstance){
        return 0;
    }
}
