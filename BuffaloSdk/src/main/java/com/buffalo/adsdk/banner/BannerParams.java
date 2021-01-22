package com.buffalo.adsdk.banner;

import com.buffalo.adsdk.RequestParams;

public class BannerParams extends RequestParams {
    public void setBannerViewSize(BannerAdSize mBannerAdSize) {
        if (mParams != null) {
            mParams.put(KEY_BANNER_VIEW_SIZE, mBannerAdSize);
        }
    }

    public BannerAdSize getBannerAdSize() {
        if (mParams != null) {
            Object bannerSize = mParams.get(KEY_BANNER_VIEW_SIZE);
            if (null != bannerSize) {
                return (BannerAdSize) bannerSize;
            }
        }
        return null;
    }
}
