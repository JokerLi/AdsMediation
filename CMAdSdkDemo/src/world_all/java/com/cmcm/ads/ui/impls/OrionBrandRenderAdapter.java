package com.cmcm.ads.ui.impls;

import android.view.View;

import com.cmcm.adsdk.CMNativeAdTemplate;
import com.cmcm.baseapi.ads.INativeAd;

/**
 * Created by Li Guoqing on 2016/11/19.
 */
public class OrionBrandRenderAdapter implements CMNativeAdTemplate.ICMNativeAdViewAdapter {
    @Override
    public View onPostProcessAdView(INativeAd ad, CMNativeAdTemplate.ViewHolder viewHolder) {
        return getOrionBrandView(ad);
    }

    public View getOrionBrandView(INativeAd ad) {
        if (ad == null) {
            return null;
        }

        Object object = ad.getAdObject();
        if (object == null || !(object instanceof View)) {
            return null;
        }
        View view = (View) object;
        return view;
    }
}
