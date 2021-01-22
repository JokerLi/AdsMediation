package com.buffalo.adsdk.adapter;

import android.content.Context;

import com.buffalo.adsdk.RequestParams;
import com.buffalo.adsdk.base.INativeRequestCallBack;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoader;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;

import java.util.Iterator;
import java.util.List;

public abstract class BaseNativeLoaderAdapter implements INativeAdLoader {
    protected Context mContext;
    protected String mPositionId;
    protected INativeRequestCallBack mNativeAdListener;
    protected INativeAd.IAdOnClickListener mNativeAdClickListener = null;
    protected String mAdTypeName;
    protected RequestParams requestParams;

    protected BaseNativeLoaderAdapter(Context context, String posId, String adTypeName) {
        mContext = context;
        mPositionId = posId;
        mAdTypeName = adTypeName;
    }

    public abstract void loadAds(int num);

    public String getAdTypeName() {
        return mAdTypeName;
    }

    public void setRequestParams(RequestParams requestParams) {
        this.requestParams = requestParams;
    }

    public RequestParams getRequestParams() {
        return requestParams;
    }

    public void setLoadCallBack(INativeRequestCallBack adListener) {
        mNativeAdListener = adListener;
    }

    public void setAdListener(INativeAdLoaderListener adListener) {
        //空实现
    }

    public void setAdClickListener(INativeAd.IAdOnClickListener adClickListener) {
        mNativeAdClickListener = adClickListener;
    }

    protected void removeExpiredAds(List<INativeAd> list) {
        if (null == list || list.size() == 0) {
            return;
        }
        Iterator<INativeAd> iterator = list.iterator();
        INativeAd ad = null;
        while (iterator.hasNext()) {
            ad = iterator.next();
            if (ad == null || ad.hasExpired()) {
                iterator.remove();
            }
        }
    }
}
