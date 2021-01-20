package com.cmcm.adsdk.nativead;

import com.cmcm.baseapi.ads.INativeAdLoaderListener;

/**
 * Created by chenhao on 2016/1/26.
 */
public interface INativeAdListListener  extends INativeAdLoaderListener {
    abstract void onLoadProcess();
}
