package com.cmcm.ads.ui.impls;

import android.content.Context;
import android.view.View;

import com.cmcm.adsdk.CMNativeAdTemplate;
import com.cmcm.baseapi.ads.INativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAdView;

/**
 * Created by Li Guoqing on 2016/11/19.
 */
public class AdmobRenderAdapter implements CMNativeAdTemplate.ICMNativeAdViewAdapter {
    private Context mContext;
    public AdmobRenderAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View onPostProcessAdView(INativeAd ad, CMNativeAdTemplate.ViewHolder  viewHolder) {
        return addAdmobBrandLogoView(viewHolder, ad);
    }

    private View addAdmobBrandLogoView(CMNativeAdTemplate.ViewHolder viewHolder, INativeAd ad) {
        if (ad == null || viewHolder == null) {
            return null;
        }

        if (ad.isDownLoadApp()) {
            NativeAppInstallAdView installAdView = new NativeAppInstallAdView(mContext);
            installAdView.setBodyView(viewHolder.mBodyView);
            installAdView.setCallToActionView(viewHolder.mCallToActionView);
            installAdView.setHeadlineView(viewHolder.mTitleView);
            installAdView.setIconView(viewHolder.mIconImageView);
            installAdView.setImageView(viewHolder.mMainImageView);
            installAdView.setStarRatingView(viewHolder.mStarRatingView);
            installAdView.setStoreView(viewHolder.mSocialContextView);

            installAdView.addView(viewHolder.mLayoutView);
            return installAdView;
        } else {
            NativeContentAdView contentAdView = new NativeContentAdView(mContext);
            contentAdView.setHeadlineView(viewHolder.mTitleView);
            contentAdView.setCallToActionView(viewHolder.mCallToActionView);
            contentAdView.setAdvertiserView(viewHolder.mSocialContextView);
            contentAdView.setLogoView(viewHolder.mIconImageView);
            contentAdView.setBodyView(viewHolder.mBodyView);
            contentAdView.setImageView(viewHolder.mMainImageView);

            contentAdView.addView(viewHolder.mLayoutView);
            return contentAdView;
        }
    }

}
