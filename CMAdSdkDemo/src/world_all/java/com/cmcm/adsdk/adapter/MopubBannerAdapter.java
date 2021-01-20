package com.cmcm.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import java.util.Map;

/**
 * Created by cm on  2016/1/5 14:54.
 */
public class MopubBannerAdapter extends NativeloaderAdapter {
    private static final String TAG = "MopubBannerAdapter";
    private static final String UNITID = "252412d5e9364a05ab77d9396346d73d";
    Context mContext;
    Map<String, Object> mExtras;

    public MopubBannerAdapter() {
        mContext = null;
    }

    @Override
    public void loadNativeAd(@NonNull Context context, @NonNull Map<String, Object> extras) {

        mContext = context;
        mExtras = extras;
        if(!extrasAreValid(extras)){
            notifyNativeAdFailed(String.valueOf(CMAdError.PARAMS_ERROR));
            return;
        }
        new MopubBannerHookAd().loadAd();
    }

    @Override
    public int getReportRes( String adTypeName) {
        return Const.res.mopub;
    }

    @Override
    public int getAdErrorCode(Object errorInstance) {
        if (errorInstance == null || !(errorInstance instanceof MoPubErrorCode))
            return 0;
        switch((MoPubErrorCode)errorInstance){
            case NO_FILL:
                return CMAdError.NO_FILL;
            case WARMUP:
                return CMAdError.WARMUP;
            case SERVER_ERROR:
                return CMAdError.SERVER_ERROR;
            case NO_CONNECTION:
                return CMAdError.NO_CONNECTION;
            case CANCELLED:
                return CMAdError.CANCELLED;
            case ADAPTER_NOT_FOUND:
                return CMAdError.ADAPTER_NOT_FOUND;
            case ADAPTER_CONFIGURATION_ERROR:
                return CMAdError.ADAPTER_CONFIGURATION_ERROR;
            case MRAID_LOAD_ERROR:
                return CMAdError.MRAID_LOAD_ERROR;
            case VIDEO_CACHE_ERROR:
                return CMAdError.VIDEO_CACHE_ERROR;
            case VIDEO_DOWNLOAD_ERROR:
                return CMAdError.VIDEO_DOWNLOAD_ERROR;
            case VIDEO_NOT_AVAILABLE:
                return CMAdError.VIDEO_NOT_AVAILABLE;
            case VIDEO_PLAYBACK_ERROR:
                return CMAdError.VIDEO_PLAYBACK_ERROR;
            case LOADIMAGE_ERROR:
                return CMAdError.LOADIMAGE_ERROR;
            case BANNER_CREATE_ERROR:
                return CMAdError.BANNER_CREATE_ERROR;
            default:
                break;
        }
        return 0;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        //保持和原有一致
        if(adTypeName.equals(Const.KEY_MP_BANNER)) {
            return String.format("%s.%s", Const.pkgName.mopub, Const.REPORT_BANNER_SUFFIX);
        }else {
            return String.format("%s.%s.%s", Const.pkgName.mopub, Const.REPORT_BANNER_SUFFIX, adTypeName);
        }
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_MP_BANNER;
    }

    @Override
    public long getDefaultCacheTime() {
        return 0;
    }


    class MopubBannerHookAd extends CMBaseNativeAd implements MoPubView.BannerAdListener {

        private MoPubView moPubView;

        public void loadAd(){
            MoPubView.setLoadImageSwitch(false);
            // error:mopubView not unregisterReceiver
            if(moPubView != null){
                moPubView.destroy();
                moPubView = null;
            }
            moPubView = new MoPubView(mContext);
            moPubView.setAutorefreshEnabled(false);
            moPubView.setBannerAdListener(this);

            String mUnitId = (String)mExtras.get(KEY_PLACEMENT_ID);
            moPubView.setAdUnitId(mUnitId);

            moPubView.loadAd();
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_MP_BANNER;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
            return true;
        }

        @Override
        public void unregisterView() {
            if(moPubView != null){
                Log.d(TAG, "mopubview banner unregisterView");
                moPubView.destroy();
            }
        }

        @Override
        public boolean isNativeAd() {
            return false;
        }

        @Override
        public Object getAdObject() {
            return moPubView;
        }

        @Override
        public void handleClick() {
        }


        @Override
        public void onBannerLoaded(MoPubView moPubView) {
            Log.d(TAG, "mopub banner loaded");
            notifyNativeAdLoaded(this);
        }

        @Override
        public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
            Log.d(TAG, "mopub banner failed");
            //ErrorCode 转换成int
            notifyNativeAdFailed(getAdErrorCode(moPubErrorCode)+"");
            unregisterView();
        }

        @Override
        public void onBannerClicked(MoPubView moPubView) {
            Log.d(TAG, "mopub banner clicked");
            notifyNativeAdClick(this);
        }

        @Override
        public void onBannerExpanded(MoPubView moPubView) {

        }

        @Override
        public void onBannerCollapsed(MoPubView moPubView) {

        }
    }

    @Override
    public Const.AdType getAdType() {
        return Const.AdType.BANNER;
    }
}
