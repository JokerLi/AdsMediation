package com.buffalo.adsdk.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.BaseFactory;
import com.buffalo.adsdk.NativeAdTemplate;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.utils.Logger;

public class AdViewRender {
    private static final String TAG = "CMViewRenderLog";

    private Context mContext;
    private NativeAdTemplate.ViewHolder mViewHolder;

    public AdViewRender(NativeAdTemplate viewBinder) {
        mContext = AdManager.getContext();
        if (viewBinder == null) {
            throw new RuntimeException("NativeAdTemplate is null");
        }
        mViewHolder = new NativeAdTemplate.ViewHolder(mContext, viewBinder);
    }

    public View getMainView() {
        if (null != mViewHolder) {
            return mViewHolder.mLayoutView;
        }
        return null;
    }

    public View getBindedView(INativeAd ad) {
        if (ad == null) {
            log("ad is null, return null view!");
            return null;
        }

        NativeAdTemplate.INativeAdViewAdapter adapter = getAdapter(ad);
        renderView(adapter, ad);
        View view = mViewHolder.getView();
        return view;
    }

    private NativeAdTemplate.INativeAdViewAdapter getAdapter(INativeAd ad) {
        if (ad == null) {
            return null;
        }
        String adTypeName = ad.getAdTypeName();
        BaseFactory factory = AdManager.createFactory();
        if (factory == null) {
            return null;
        }
        return factory.getRenderAdapter(getTypeName(adTypeName));
    }

    private String getTypeName(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        String str[] = key.split("_");
        return str[0];
    }

    private void renderView(NativeAdTemplate.INativeAdViewAdapter adapter, INativeAd ad) {
        mViewHolder.resetView();
        if (ad == null) {
            return;
        }

        removeFromOldViewGroup();
        setCustomView(adapter, ad);
        RenderViewHelper.setTextView(mViewHolder.mTitleView, ad.getAdTitle(), "");
        RenderViewHelper.setTextView(mViewHolder.mBodyView, ad.getAdBody(), "");
        RenderViewHelper.setTextView(mViewHolder.mSocialContextView, "", "");
        RenderViewHelper.setTextView(mViewHolder.mCallToActionView, ad.getAdCallToAction(), "Detail");
        RenderViewHelper.setBigCard(mViewHolder.mMainImageView, ad, null);
        RenderViewHelper.setImageView(mViewHolder.mIconImageView, ad.getAdIconUrl());
        RenderViewHelper.setStarRating(mViewHolder.mStarRatingView, (float) ad.getAdStarRating());
    }

    private void removeFromOldViewGroup() {
        if (mViewHolder.mLayoutView == null) {
            return;
        }

        ViewParent parent = mViewHolder.mLayoutView.getParent();
        if (parent == null || !(parent instanceof ViewGroup)) {
            return;
        }

        ((ViewGroup) parent).removeAllViews();
    }

    private void setCustomView(NativeAdTemplate.INativeAdViewAdapter adapter, INativeAd ad) {
        if (adapter == null || ad == null) {
            return;
        }

        View view = adapter.onPostProcessAdView(ad, mViewHolder);
        if (view != null) {
            mViewHolder.setView(view);
        }
    }

    private static void log(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        Logger.e(TAG, message);
    }
}
