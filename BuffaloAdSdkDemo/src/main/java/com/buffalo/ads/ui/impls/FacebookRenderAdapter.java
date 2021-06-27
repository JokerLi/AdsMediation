package com.buffalo.ads.ui.impls;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.buffalo.adsdk.NativeAdTemplate;
import com.buffalo.baseapi.ads.INativeAd;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;

public class FacebookRenderAdapter implements NativeAdTemplate.INativeAdViewAdapter {
    private Context mContext;

    public FacebookRenderAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View onPostProcessAdView(INativeAd ad, NativeAdTemplate.ViewHolder viewHolder) {
        if (ad == null || viewHolder == null || !(viewHolder.mLayoutView instanceof NativeAdLayout)) {
            return null;
        }
        viewHolder.mMainImageView.setAd(ad, createFacebookMediaView(ad));
        if (viewHolder.mAdCornerView == null) {
            return createDefaultBrandLogoView(viewHolder, getFacebookBrandLogoView(ad, (NativeAdLayout) viewHolder.mLayoutView));
        } else {
            viewHolder.mAdCornerView.removeAllViews();
            viewHolder.mAdCornerView.addView(getFacebookBrandLogoView(ad, (NativeAdLayout) viewHolder.mLayoutView));
            viewHolder.mAdCornerView.bringToFront();
            return null;
        }
    }

    private View createFacebookMediaView(INativeAd ad) {
        if (ad == null) {
            return null;
        }

        Object object = ad.getAdObject();
        if (object == null || !(object instanceof NativeAd)) {
            return null;
        }
        MediaView nativeAdMedia = new MediaView(mContext);
        return nativeAdMedia;
    }

    private View getFacebookBrandLogoView(INativeAd ad, NativeAdLayout nativeAdLayout) {
        if (ad == null || nativeAdLayout == null) {
            return null;
        }

        Object ob = ad.getAdObject();
        if (!(ob instanceof NativeAd)) {
            return null;
        }

        return new AdOptionsView(mContext, (NativeAd) ob, nativeAdLayout);
    }

    private View createDefaultBrandLogoView(NativeAdTemplate.ViewHolder viewHolder, View brandLogoView) {
        if (brandLogoView == null || viewHolder == null) {
            return null;
        }
        FrameLayout outLayout = new FrameLayout(mContext);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        outLayout.addView(viewHolder.mLayoutView);
        viewHolder.mLayoutView.setLayoutParams(params);

        RelativeLayout relativeLL = new RelativeLayout(mContext);
        outLayout.addView(relativeLL);
        relativeLL.setLayoutParams(params);

        brandLogoView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams rllparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rllparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rllparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        brandLogoView.setLayoutParams(rllparams);
        relativeLL.addView(brandLogoView);
        return outLayout;
    }

}
