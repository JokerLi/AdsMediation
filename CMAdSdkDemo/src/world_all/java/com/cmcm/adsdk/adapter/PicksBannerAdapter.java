package com.cmcm.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.cmcm.adsdk.CMAdError;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.cmcm.orion.picks.api.OrionBannerView;

import java.util.Map;

/**
 * Created by cm on  2016/1/5 14:54.
 */
public class PicksBannerAdapter extends NativeloaderAdapter {
    private static final String TAG = "PicksBannerAdapter";
    Context mContext;
    Map<String, Object> mExtras;
    private View mView = null;

    public PicksBannerAdapter() {
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
        new PicksBannerAd().loadAd();
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.cmb;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        return String.format("%s.%s.%s", Const.pkgName.cmb, Const.KEY_CM,Const.REPORT_BANNER_SUFFIX);
    }


    @Override
    public String getAdKeyType() {
        return Const.KEY_CM_BANNER;
    }

    @Override
    public long getDefaultCacheTime() {
        return 0;
    }

    class PicksBannerAd extends CMBaseNativeAd implements OrionBannerView.OrionBannerListener {

        private OrionBannerView orionBannerView;

        public void loadAd() {
            if (orionBannerView != null) {
                orionBannerView.destroy();
                orionBannerView = null;
            }
            orionBannerView = new OrionBannerView(mContext);
            String posId = (String) mExtras.get(KEY_PLACEMENT_ID);
            orionBannerView.setPosId(posId);
            orionBannerView.setBannerAdListener(this);
            orionBannerView.loadAd();
        }

        @Override
        public void onBannerLoaded(OrionBannerView orionBannerView) {
            Log.d(TAG, "picks banner loaded");
            mView = orionBannerView;
            notifyNativeAdLoaded(this);
        }

        @Override
        public void onBannerFailed(OrionBannerView orionBannerView, int errorCode) {
            Log.d(TAG, "picks banner failed");
            notifyNativeAdFailed(errorCode + "");
        }

        @Override
        public void onBannerClicked(OrionBannerView orionBannerView) {
            Log.d(TAG, "picks banner clicked");
            notifyNativeAdClick(this);
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_CM_BANNER;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            return true;
        }

        @Override
        public void unregisterView() {
            if (orionBannerView != null) {
                orionBannerView.destroy();
            }
        }

        @Override
        public boolean isNativeAd() {
            return false;
        }

        @Override
        public Object getAdObject() {
            return mView;
        }
    }

    @Override
    public Const.AdType getAdType() {
        return Const.AdType.BANNER;
    }
}
