package com.cmcm.adsdk.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.CMBaseFactory;
import com.cmcm.adsdk.CMNativeAdTemplate;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.utils.Logger;

/**
 * Created by Li Guoqing on 2016/11/15.
 */
public class CMViewRender {
    private static final String TAG = "CMViewRenderLog";

    private Context mContext;
    private CMNativeAdTemplate.ViewHolder mViewHolder;

    public CMViewRender(CMNativeAdTemplate cmViewBinder) {
        mContext = CMAdManager.getContext();
        if(cmViewBinder == null){
            throw new RuntimeException("CMNativeAdTemplate is null");
        }
        mViewHolder = new CMNativeAdTemplate.ViewHolder(mContext, cmViewBinder);
    }

    public View getMainView(){
        if(null != mViewHolder){
            return mViewHolder.mLayoutView;
        }
        return null;
    }

    public View getBindedView(INativeAd ad) {
        if (ad == null) {
            log("ad is null, return null view!");
            return null;
        }

        CMNativeAdTemplate.ICMNativeAdViewAdapter adapter = getAdapter(ad);
        renderView(adapter, ad);
        View view = mViewHolder.getView();
        return view;
    }

    private CMNativeAdTemplate.ICMNativeAdViewAdapter getAdapter(INativeAd ad) {
        if (ad == null) {
            return null;
        }
        String adTypeName = ad.getAdTypeName();
        CMBaseFactory factory = CMAdManager.createFactory();
        if(factory == null){
            return null;
        }
        return factory.getRenderAdapter(getTypeName(adTypeName));
    }

    private String getTypeName(String key){
        if(TextUtils.isEmpty(key)){
            return null;
        }

        String str[] = key.split("_");
        return str[0];
    }

    private void renderView(CMNativeAdTemplate.ICMNativeAdViewAdapter adapter, INativeAd ad) {
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
        if(mViewHolder.mLayoutView == null){
            return;
        }

        ViewParent parent = mViewHolder.mLayoutView.getParent();
        if(parent == null || !(parent instanceof ViewGroup)){
            return;
        }

        ((ViewGroup)parent).removeAllViews();
    }

    private void setCustomView(CMNativeAdTemplate.ICMNativeAdViewAdapter adapter, INativeAd ad) {
        if(adapter == null || ad == null){
            return;
        }

        View view =  adapter.onPostProcessAdView(ad, mViewHolder);
        if(view != null) {
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
