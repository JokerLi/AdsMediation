package com.buffalo.baseapi.ads;

public interface INativeAdLoaderListener {
    void adLoaded();

    void adFailedToLoad(int errorcode);

    void adClicked(INativeAd nativeAd);
}
