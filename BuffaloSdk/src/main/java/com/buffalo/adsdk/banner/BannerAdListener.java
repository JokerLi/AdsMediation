package com.buffalo.adsdk.banner;

/**
 * Created by chenhao on 2015/8/14.
 */
public interface BannerAdListener {
    public void onAdLoaded(BannerAdView ad);
    public void adFailedToLoad(BannerAdView ad, int errorCode);
    public void onAdClicked(BannerAdView ad);
}
