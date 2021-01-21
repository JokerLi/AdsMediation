package com.buffalo.adsdk.nativead;

import com.buffalo.baseapi.ads.INativeAdLoaderListener;

public interface INativeAdListListener extends INativeAdLoaderListener {
    abstract void onLoadProcess();
}
