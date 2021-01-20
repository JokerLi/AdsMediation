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
import com.mopub.common.UrlAction;
import com.mopub.common.UrlHandler;
import com.mopub.common.util.Drawables;
import com.mopub.nativeads.NativeResponse;

/**
 * Created by Li Guoqing on 2016/11/19.
 */
public class MopubRenderAdapter implements CMNativeAdTemplate.ICMNativeAdViewAdapter {
    private Context mContext;
    public MopubRenderAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View onPostProcessAdView(INativeAd ad, CMNativeAdTemplate.ViewHolder viewHolder) {
        if(ad == null || viewHolder == null){
            return null;
        }
        if(viewHolder.mAdCornerView == null){
            return createDefaultBrandLogoView(viewHolder, getMopubBrandLogoView(ad));
        }else{
            viewHolder.mAdCornerView.removeAllViews();
            viewHolder.mAdCornerView.addView(getMopubBrandLogoView(ad));
            viewHolder.mAdCornerView.bringToFront();
            return null;
        }
    }

    private View getMopubBrandLogoView(INativeAd ad) {
        if (ad == null) {
            return null;
        }

        Object ob = ad.getAdObject();
        if (ob == null || !(ob instanceof NativeResponse)) {
            return null;
        }

        NativeResponse response = (NativeResponse) ob;
        ImageView mopubAdCorner = new ImageView(mContext);

        final String daaIconClickThroughUrl = response.getDaaIconClickthroughUrl();
        mopubAdCorner.setImageDrawable(Drawables.NATIVE_DAA_ICON.createDrawable(CMAdManager.getContext()));
        mopubAdCorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new UrlHandler.Builder()
                        .withSupportedUrlActions(
                                UrlAction.IGNORE_ABOUT_SCHEME,
                                UrlAction.OPEN_NATIVE_BROWSER,
                                UrlAction.OPEN_IN_APP_BROWSER,
                                UrlAction.HANDLE_SHARE_TWEET,
                                UrlAction.FOLLOW_DEEP_LINK_WITH_FALLBACK,
                                UrlAction.FOLLOW_DEEP_LINK)
                        .build().handleUrl(CMAdManager.getContext(), daaIconClickThroughUrl);
            }
        });
        int dp_20 = Commons.dip2px(CMAdManager.getContext(), 20);
        int dp_5 = Commons.dip2px(CMAdManager.getContext(), 5);
        RelativeLayout.LayoutParams rllparams = new RelativeLayout.LayoutParams(dp_20, dp_20);
        rllparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rllparams.setMargins(0, dp_5, dp_5, 0);
        mopubAdCorner.setLayoutParams(rllparams);
        return mopubAdCorner;
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
