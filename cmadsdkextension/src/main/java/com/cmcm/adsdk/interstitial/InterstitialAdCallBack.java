package com.cmcm.adsdk.interstitial;


public interface InterstitialAdCallBack {
	public void onAdLoadFailed(int errorCode);
	public void onAdLoaded();
	public void onAdClicked();
	public void onAdDisplayed();
	public void onAdDismissed();
}
