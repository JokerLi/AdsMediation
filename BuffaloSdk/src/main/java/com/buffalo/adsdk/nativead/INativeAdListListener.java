package com.buffalo.adsdk.nativead;

import com.buffalo.baseapi.ads.INativeAdLoaderListener;

/**
 * Created by chenhao on 2016/1/26.
 */
public interface INativeAdListListener  extends INativeAdLoaderListener {
    abstract void onLoadProcess();
}
