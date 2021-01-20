package com.cmcm.ads.ui.impls;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.cmcm.adsdk.CMNativeAdTemplate;
import com.cmcm.baseapi.ads.INativeAd;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

/**
 * Created by Li Guoqing on 2016/11/19.
 */
public class FacebookRenderAdapter implements CMNativeAdTemplate.ICMNativeAdViewAdapter {
    private Context mContext;
    public FacebookRenderAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View onPostProcessAdView(INativeAd ad, CMNativeAdTemplate.ViewHolder  viewHolder) {
        if(ad == null || viewHolder == null){
            return null;
        }
        viewHolder.mMainImageView.setAd(ad, createFacebookMediaView(ad));
        if(viewHolder.mAdCornerView == null){
            return createDefaultBrandLogoView(viewHolder, getFacebookBrandLogoView(ad));
        }else{
            viewHolder.mAdCornerView.removeAllViews();
            viewHolder.mAdCornerView.addView(getFacebookBrandLogoView(ad));
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
        NativeAd nativeAd = (NativeAd) object;
        MediaView nativeAdMedia = new MediaView(mContext);
        nativeAdMedia.setAutoplay(true);
        nativeAdMedia.setNativeAd(nativeAd);

        return nativeAdMedia;
    }

    private View getFacebookBrandLogoView(INativeAd ad) {
        if (ad == null) {
            return null;
        }

        Object ob = ad.getAdObject();
        if (ob == null || !(ob instanceof NativeAd)) {
            return null;
        }

        AdChoicesView choicesView = new AdChoicesView(mContext, (NativeAd)ob, true);
        return choicesView;
    }

    private View createDefaultBrandLogoView(CMNativeAdTemplate.ViewHolder viewHolder, View brandLogoView) {
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
