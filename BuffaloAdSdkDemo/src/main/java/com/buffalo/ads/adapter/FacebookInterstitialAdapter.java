//package com.buffalo.ads.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//
//import com.buffalo.adsdk.Const;
//import com.buffalo.adsdk.NativeAdError;
//import com.buffalo.adsdk.adapter.NativeloaderAdapter;
//import com.buffalo.adsdk.base.BaseNativeAd;
//import com.buffalo.adsdk.interstitial.InterstitialAdCallBack;
//import com.buffalo.baseapi.ads.INativeAd;
//import com.facebook.ads.Ad;
//import com.facebook.ads.AdError;
//import com.facebook.ads.InterstitialAd;
//import com.facebook.ads.InterstitialAdListener;
//
//import java.util.Map;
//
//public class FacebookInterstitialAdapter extends NativeloaderAdapter implements InterstitialAdListener {
//    private static final String TAG = "FacebookInterstitialAdapter";
//    private InterstitialAd interstitialAd;
//    private Context mContext;
//    private FacebookInterstatialAd mFacebookInterstatialAd;
//    private InterstitialAdCallBack mInterstitialAdCallBack;
//    private Map<String, Object> mExtras;
//
//    @Override
//    public void loadNativeAd(@NonNull Context context, @NonNull Map<String, Object> extras) {
//        this.mContext = context;
//        mExtras = extras;
//        if (!extrasAreValid(extras)) {
//            notifyNativeAdFailed(String.valueOf(NativeAdError.PARAMS_ERROR));
//            return;
//        }
//        if (interstitialAd != null) {
//            interstitialAd.destroy();
//            interstitialAd = null;
//        }
//        if (extras.containsKey(BaseNativeAd.KEY_EXTRA_OBJECT)) {
//            Object object = extras.get(BaseNativeAd.KEY_EXTRA_OBJECT);
//            if (object instanceof InterstitialAdCallBack) {
//                mInterstitialAdCallBack = (InterstitialAdCallBack) object;
//            }
//        }
//
//        interstitialAd = new InterstitialAd(context, (String) extras.get(BaseNativeAd.KEY_PLACEMENT_ID));
//        interstitialAd.setAdListener(this);
//        interstitialAd.loadAd();
//    }
//
//
//    @Override
//    public void onInterstitialDisplayed(Ad ad) {
//        mFacebookInterstatialAd.onLoggingImpression();
//        if (mInterstitialAdCallBack != null) {
//            mInterstitialAdCallBack.onAdDisplayed();
//        }
//    }
//
//    @Override
//    public void onInterstitialDismissed(Ad ad) {
//        Log.d(TAG, "facebookInterstitial is dismiss");
//        if (mInterstitialAdCallBack != null) {
//            mInterstitialAdCallBack.onAdDismissed();
//        }
//    }
//
//    @Override
//    public void onError(Ad ad, AdError adError) {
//        //改ErrorCode值为int
//        notifyNativeAdFailed(adError.getErrorCode() + "");
//    }
//
//    @Override
//    public void onAdLoaded(Ad ad) {
//        mFacebookInterstatialAd = new FacebookInterstatialAd(ad);
//        notifyNativeAdLoaded(mFacebookInterstatialAd);
//    }
//
//    @Override
//    public void onAdClicked(Ad ad) {
//        if (mFacebookInterstatialAd != null) {
//            mFacebookInterstatialAd.notifyNativeAdClick(mFacebookInterstatialAd);
//        }
//    }
//
//    @Override
//    public int getReportRes(String adTypeName) {
//        return Const.res.facebook;
//    }
//
//    @Override
//    public String getReportPkgName(String adTypeName) {
//        String reportPkgName = String.format("%s.%s", Const.pkgName.facebook, Const.REPORT_INTERSTITIAL_SUFFIX);
//        return reportPkgName;
//    }
//
//    @Override
//    public String getAdKeyType() {
//        return Const.KEY_FB_INTERSTITIAL;
//    }
//
//    @Override
//    public long getDefaultCacheTime() {
//        return Const.cacheTime.facebook;
//    }
//
//
//    class FacebookInterstatialAd extends BaseNativeAd implements INativeAd.ImpressionListener {
//        private Ad mAd;
//
//        public FacebookInterstatialAd(Ad ad) {
//            mAd = ad;
//        }
//
//        @Override
//        public String getAdTypeName() {
//            return Const.KEY_FB_INTERSTITIAL;
//        }
//
//        @Override
//        public boolean registerViewForInteraction(View view) {
//            if (interstitialAd != null && interstitialAd.isAdLoaded()) {
//                interstitialAd.show();
//            }
//            return true;
//        }
//
//        @Override
//        public void unregisterView() {
//            if (interstitialAd != null) {
//                interstitialAd.destroy();
//                interstitialAd = null;
//            }
//        }
//
//        @Override
//        public Object getAdObject() {
//            return mAd;
//        }
//
//        @Override
//        public void handleClick() {
//
//        }
//
//        @Override
//        public boolean isNativeAd() {
//            return false;
//        }
//
//        @Override
//        public void onLoggingImpression() {
//            if (mImpressionListener != null)
//                mImpressionListener.onLoggingImpression();
//        }
//    }
//
//}
