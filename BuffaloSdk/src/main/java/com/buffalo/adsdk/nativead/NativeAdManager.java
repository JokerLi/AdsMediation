package com.buffalo.adsdk.nativead;

import android.app.Activity;
import android.content.Context;

import com.buffalo.adsdk.RequestParams;
import com.buffalo.adsdk.config.PosBean;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;
import com.buffalo.utils.ThreadHelper;

import java.util.List;
import java.util.concurrent.Callable;

public class NativeAdManager implements LifeCycleDelegate {
    public RequestParams requestParams;
    private Context mContext;
    NativeAdManagerInternal requestAd = null;

    public NativeAdManager(Context context, String posid) {
        if (context instanceof Activity) {
            this.mContext = context.getApplicationContext();
        } else {
            this.mContext = context;
        }
        requestAd = new NativeAdManagerInternal(mContext, posid);

    }

    public void setRequestParams(RequestParams params) {
        this.requestParams = params;
    }

    public void setNativeAdListener(INativeAdLoaderListener listener) {
        if (requestAd != null) {
            requestAd.setAdListener(listener);
        }
    }

    public void preloadAd() {
        requestAd(true);
    }

    public void loadAd() {
        requestAd(false);
    }

    protected void requestAd(boolean isPreload) {
        if (requestParams != null) {
            requestAd.setRequestParams(requestParams);
        }
        requestAd.setPreload(isPreload);
        requestAd.loadAd();
    }

    public INativeAd getAd() {
        return ThreadHelper.runOnUiThreadBlockingNoException(new Callable<INativeAd>() {
            @Override
            public INativeAd call() throws Exception {

                if (requestAd != null) {
                    return requestAd.getAd();
                }
                return null;
            }

        });
    }

    public List<PosBean> getPosBeans() {
        return requestAd.getPosBeans();
    }

    public String getRequestLastError() {
        if (requestAd != null) {
            return requestAd.mRequestLogger.getLastResult();
        }
        return null;
    }

    public String getRequestErrorInfo() {
        if (requestAd != null) {
            return requestAd.mRequestLogger.getRequestErrorInfo();
        }
        return null;
    }

    public void enableVideoAd() {
        if (requestAd != null) {
            requestAd.enableVideoAd();
        }
    }

    public void enableBannerAd() {
        if (requestAd != null) {
            requestAd.enableBannerAd();
        }
    }

    @Override
    public void onPause() {
        if (requestAd != null) {
            requestAd.onPause();
        }
    }

    @Override
    public void onResume() {
        if (requestAd != null) {
            requestAd.onResume();
        }
    }

    @Override
    public void onDestroy() {
        if (requestAd != null) {
            requestAd.onDestroy();
        }
    }

    public void disableAdType(List<String> adTypes) {
        if (adTypes == null) {
            return;
        }
        requestAd.setDisableAdType(adTypes);
    }

    public void setCheckPointNum(int num) {
        requestAd.setCheckPointNum(num);
    }

    public void setFirstCheckTime(int time) {
        requestAd.setFirstCheckTime(time);
    }

    public void setCheckPointIntervalTime(int time) {
        requestAd.setCheckPointIntervalTime(time);
    }
}
