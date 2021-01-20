package com.cmcm.baseapi.ads;

/**
 * Created by chenhao on 2016/1/26.
 */
public interface INativeAdLoaderListener {
    void adLoaded();

    void adFailedToLoad(int errorcode);

    void adClicked(INativeAd nativeAd);
}
