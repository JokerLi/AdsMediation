package com.buffalo.adsdk.interstitial;


public interface InterstitialAdCallBack {
    void onAdLoadFailed(int errorCode);

    void onAdLoaded();

    void onAdClicked();

    void onAdDisplayed();

    void onAdDismissed();
}
