package com.cmcm.adsdk.banner;

/**
 * Created by chenhao on 2015/8/14.
 */
public interface CMBannerAdListener {
    public void onAdLoaded(CMAdView ad);
    public void adFailedToLoad(CMAdView ad, int errorCode);
    public void onAdClicked(CMAdView ad);
}
