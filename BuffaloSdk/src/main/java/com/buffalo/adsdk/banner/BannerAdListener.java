package com.buffalo.adsdk.banner;

public interface BannerAdListener {
    void onAdLoaded(BannerAdView ad);

    void onAdFailed(BannerAdView ad, int errorCode);

    void onAdClicked(BannerAdView ad);
}
