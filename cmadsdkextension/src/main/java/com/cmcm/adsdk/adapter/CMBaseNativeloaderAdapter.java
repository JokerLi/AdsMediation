
package com.cmcm.adsdk.adapter;

import android.content.Context;

import com.cmcm.adsdk.CMRequestParams;
import com.cmcm.adsdk.base.INativeReqeustCallBack;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoader;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;

import java.util.Iterator;
import java.util.List;

public abstract class CMBaseNativeloaderAdapter implements INativeAdLoader {

    protected  Context mContext;
    public String mPositionId = null;
    protected INativeReqeustCallBack mNativeAdListener;
    protected INativeAd.IAdOnClickListener mNativeAdClickListener = null;
    protected String mAdTypeName;
    protected CMRequestParams requestParams;

    protected CMBaseNativeloaderAdapter(Context context, String posId, String adTypeName) {
        mContext = context;
        mPositionId = posId;
        mAdTypeName = adTypeName;
    }

    public abstract void loadAds(int num);

    public String getAdTypeName() {
        return mAdTypeName;
    }

    public void setRequestParams( CMRequestParams requestParams){
        this.requestParams = requestParams;
    }

    public CMRequestParams getRequestParams(){
        return requestParams;
    }


    public void setLoadCallBack(INativeReqeustCallBack adListener) {
        mNativeAdListener = adListener;
    }

    public void setAdListener(INativeAdLoaderListener adListener){
        //空实现
    }

    public void setAdClickListener(INativeAd.IAdOnClickListener adClickListener) {
        mNativeAdClickListener = adClickListener;
    }

    protected  void removeExpiredAds(List<INativeAd> list){
        if(null == list || list.size()==0){
            return ;
        }
        Iterator<INativeAd> iterator = list.iterator();
        INativeAd ad = null;
        while(iterator.hasNext()){
            ad = iterator.next();
            if(ad == null || ad.hasExpired()){
                iterator.remove();
            }
        }
    }
}
