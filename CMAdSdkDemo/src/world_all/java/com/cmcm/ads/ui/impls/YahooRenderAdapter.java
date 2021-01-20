package com.cmcm.ads.ui.impls;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.CMNativeAdTemplate;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.utils.Commons;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;

/**
 * Created by Li Guoqing on 2016/11/19.
 */
public class YahooRenderAdapter implements CMNativeAdTemplate.ICMNativeAdViewAdapter {
    public static final String AD_ASSET_SOURCE = "source";
    public static final String AD_ASSET_SPONSORED_MARKER = "secBrandingLogo";

    private Context mContext;
    public YahooRenderAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View onPostProcessAdView(INativeAd ad, CMNativeAdTemplate.ViewHolder  viewHolder) {
        return createDefaultBrandLogoView(viewHolder, getYahooBrandLogoView(viewHolder, ad));
    }

    private View getYahooBrandLogoView(CMNativeAdTemplate.ViewHolder viewHolder, INativeAd ad) {
        if (ad == null || viewHolder == null) {
            return null;
        }

        Object o = ad.getAdObject();
        if (o == null || !(o instanceof FlurryAdNative)) {
            return null;
        }
        FlurryAdNative flurryAdNative = (FlurryAdNative) o;

        if (viewHolder.mSponsoredView != null) {
            FlurryAdNativeAsset adSource = flurryAdNative.getAsset(AD_ASSET_SOURCE);
            if (adSource != null) {
                viewHolder.mSponsoredView.setText(adSource.getValue());
                viewHolder.mSponsoredView.setVisibility(View.VISIBLE);
            }
        }

        FlurryAdNativeAsset secBrandingLogo = flurryAdNative.getAsset(AD_ASSET_SPONSORED_MARKER);
        if (secBrandingLogo != null) {
            ImageView ivSponsoredMaker = new ImageView(mContext);
            secBrandingLogo.loadAssetIntoView(ivSponsoredMaker);
            ivSponsoredMaker.setVisibility(View.VISIBLE);
            return ivSponsoredMaker;
        }

        return null;
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
        int dp_20 = Commons.dip2px(CMAdManager.getContext(), 20);
        RelativeLayout.LayoutParams rllparams = new RelativeLayout.LayoutParams(dp_20, dp_20);
        rllparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rllparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        brandLogoView.setLayoutParams(rllparams);
        relativeLL.addView(brandLogoView);
        return outLayout;
    }

}
