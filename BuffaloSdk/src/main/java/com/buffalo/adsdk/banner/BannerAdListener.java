package com.buffalo.adsdk.banner;

public interface BannerAdListener {
    public void onAdLoaded(BannerAdView ad);

    public void adFailedToLoad(BannerAdView ad, int errorCode);

    public void onAdClicked(BannerAdView ad);
}
