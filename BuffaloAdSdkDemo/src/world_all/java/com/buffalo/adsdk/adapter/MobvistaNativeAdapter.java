package com.buffalo.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.buffalo.baseapi.ads.INativeAd;
import com.mobvista.msdk.MobVistaConstans;
import com.mobvista.msdk.out.Campaign;
import com.mobvista.msdk.out.Frame;
import com.mobvista.msdk.out.MvNativeHandler;
import com.mobvista.msdk.out.MvNativeHandler.NativeAdListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MobvistaNativeAdapter extends NativeloaderAdapter {
    Context mContext;
    private String mPlacementId;
    private int mLoadSize;
    public static final int MOBVISTA_PRELOAD_SIZE = 10;
    private static final long MOBVISTA_CACHE_TIME = 60 * 60 * 1000l;

    @Override
    public void loadNativeAd(@NonNull Context context, @NonNull Map<String, Object> extras) {
        mContext = context;
        mPlacementId = (String) extras.get(BaseNativeAd.KEY_PLACEMENT_ID);
        mLoadSize = (Integer) extras.get(BaseNativeAd.KEY_LOAD_SIZE);
        mLoadSize = Math.max(mLoadSize, MOBVISTA_PRELOAD_SIZE);
        MobVistaConstans.NATIVE_SHOW_LOADINGPAGER = true;
        new MobvistaNativeAd().loadAd();
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.mv;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        return Const.pkgName.mv;
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_MV;
    }

    @Override
    public long getDefaultCacheTime() {
        return MOBVISTA_CACHE_TIME;
    }

    private class MobvistaNativeAd extends BaseNativeAd implements NativeAdListener {
        Campaign mCampaign;
        private View mAdView;
        MvNativeHandler mNativeHandle;

        public MobvistaNativeAd() {
        }

        public void loadAd() {
            mNativeHandle = createMobVistaNativeHandler();
            mNativeHandle.setAdListener(this);
            mNativeHandle.load();
        }

        public MvNativeHandler createMobVistaNativeHandler() {
            Map<String, Object> properties = MvNativeHandler
                    .getNativeProperties(mPlacementId);
            //设置获取的广告个数，1-10个
            properties.put(MobVistaConstans.PROPERTIES_AD_NUM, mLoadSize);
            MvNativeHandler nativeHandle = new MvNativeHandler(properties, mContext);
            nativeHandle.addTemplate(new MvNativeHandler.Template(MobVistaConstans.TEMPLATE_BIG_IMG, mLoadSize));
            return nativeHandle;
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_MV;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            if (view == null) {
                return false;
            }
            mAdView = view;
            mNativeHandle.registerView(mAdView, collectChildView(mAdView), mCampaign);
            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
            return true;
        }

        private List<View> collectChildView(View view) {
            List<View> viewList = new ArrayList<View>();
            if (view instanceof ViewGroup) {
                int childViewCount = ((ViewGroup) view).getChildCount();
                for (int i = 0; i < childViewCount; i++) {
                    View childView = ((ViewGroup) view).getChildAt(i);
                    List<View> list = collectChildView(childView);
                    viewList.addAll(list);
                }
            } else {
                viewList.add(view);
            }
            return viewList;
        }

        @Override
        public void unregisterView() {
            if (mAdView != null) {
                mNativeHandle.unregisterView(mAdView, collectChildView(mAdView), mCampaign);
                mAdView = null;
                mNativeHandle.release();
            }
        }

        @Override
        public Object getAdObject() {
            return mCampaign;
        }

        @Override
        public void onAdLoaded(List<Campaign> list, int template) {
            List<INativeAd> mReusltPool = new ArrayList<INativeAd>();
            for (Campaign campaign : list) {
                if (campaign != null) {
                    MobvistaNativeAd ad = this.clone();
                    ad.mCampaign = campaign;
                    ad.mNativeHandle = createMobVistaNativeHandler();
                    ad.mNativeHandle.setAdListener(ad);
                    ad.updateData();
                    mReusltPool.add(ad);
                }
            }

            if (mReusltPool.isEmpty()) {
                notifyNativeAdFailed("MvNativeHandler.onAdLoaded.no.fill");
            } else {
                notifyNativeAdLoaded(mReusltPool);
            }
        }

        @Override
        public void onAdLoadError(String message) {
            notifyNativeAdFailed(message);
        }

        @Override
        public void onAdClick(Campaign campaign) {
            notifyNativeAdClick(this);
        }

        @Override
        public void onAdFramesLoaded(List<Frame> list) {

        }

        private void updateData() {
            setTitle(mCampaign.getAppName());
            setAdBody(mCampaign.getAppDesc());
            setAdCoverImageUrl(mCampaign.getImageUrl());
            setAdIconUrl(mCampaign.getIconUrl());
            setAdCallToAction(mCampaign.getAdCall());
            setAdStarRate(mCampaign.getRating());
        }

        public MobvistaNativeAd clone() {
            MobvistaNativeAd mobvistaNativeAd = new MobvistaNativeAd();
            mobvistaNativeAd.mNativeHandle = this.mNativeHandle;
            return mobvistaNativeAd;
        }
    }


}
