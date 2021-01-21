package com.buffalo.adsdk.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.NativeAdError;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;
import com.inmobi.sdk.InMobiSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class InmobiNativeAdapter extends NativeloaderAdapter {
    @Override
    public void loadNativeAd(@NonNull Context context,
                             @NonNull Map<String, Object> extras) {
        if (!extrasAreValid(extras)) {
            notifyNativeAdFailed(String.valueOf(NativeAdError.PARAMS_ERROR));
            return;
        }
        //接入时需要将此处ACCOUNT_ID替换
        InMobiSdk.init(context, "6dc25f2ddda844bb96d691d0698952e7");
        new InmobiNativeAd(context, extras).loadAd();
    }

    @Override
    public int getReportRes(String adTypeName) {
        return Const.res.inmobi;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        return Const.pkgName.imobi;
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_IM;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.pubmatic;
    }

    private class InmobiNativeAd extends BaseNativeAd {
        private InMobiNative mNativeAd;
        private Map<String, Object> mExtras;
        private String mUrl;
        private Context mContext;

        public InmobiNativeAd(@NonNull Context context,
                              @Nullable Map<String, Object> extras) {
            this.mContext = context.getApplicationContext();
            this.mExtras = extras;
        }

        public void loadAd() {
            long placementId = Long.valueOf((String) mExtras.get(KEY_PLACEMENT_ID));
            mNativeAd = new InMobiNative(placementId, new InMobiNative.NativeAdListener() {

                @Override
                public void onAdLoadSucceeded(InMobiNative inMobiNative) {
                    setUpData(inMobiNative);
                    mNativeAd = inMobiNative;
                    notifyNativeAdLoaded(InmobiNativeAd.this);
                }

                @Override
                public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
                    notifyNativeAdFailed(inMobiAdRequestStatus.getMessage());
                }

                @Override
                public void onAdDismissed(InMobiNative inMobiNative) {

                }

                @Override
                public void onAdDisplayed(InMobiNative inMobiNative) {

                }

                @Override
                public void onUserLeftApplication(InMobiNative inMobiNative) {

                }
            });
            mNativeAd.load();
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_IM;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
            if (mNativeAd != null) {
                mNativeAd.bind(view, mNativeAd);
            }
            return false;
        }

        @Override
        public void unregisterView() {
            if (mNativeAd != null) {
                mNativeAd = null;
            }
        }

        @Override
        public Object getAdObject() {
            return mNativeAd;
        }

        @Override
        public void handleClick() {
            if (mNativeAd != null) {
                mNativeAd.reportAdClick(null);
            }
            if (!TextUtils.isEmpty(mUrl)) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
                List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(appIntent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list != null && list.size() > 0) {
                    mContext.startActivity(appIntent);
                }
            }
        }

        private void setUpData(@NonNull InMobiNative inMobiNative) {
            String nativeContent = inMobiNative.getAdContent().toString();
            JSONObject adObject;
            try {
                adObject = new JSONObject(nativeContent);
                String title = adObject.optString("title");
                String description = adObject.optString("description");
                String callToAction = adObject.optString("cta");
                String iconUrl = adObject.optJSONObject("icon") != null ?
                        adObject.optJSONObject("icon").getString("url") : null;
                String mainImageUrl = adObject.optJSONObject("screenshots") != null ?
                        adObject.optJSONObject("screenshots").getString("url") : null;
                double rating = adObject.has("rating") ? adObject.getDouble("cta") : 0.0d;
                mUrl = adObject.optString("landingURL");

                setTitle(title);
                setAdBody(description);
                setAdCoverImageUrl(mainImageUrl);
                setAdIconUrl(iconUrl);
                setAdCallToAction(callToAction);
                setAdStarRate(rating);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
