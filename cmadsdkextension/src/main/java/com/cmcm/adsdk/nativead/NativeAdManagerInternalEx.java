package com.cmcm.adsdk.nativead;


import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.cmcm.adsdk.BitmapListener;
import com.cmcm.adsdk.CMAdManagerFactory;
import com.cmcm.adsdk.CMRequestParams;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.config.PosBean;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenhao on 16/3/10.
 * 目的：保证有图片缓存模块的情况下，尽量保证广告优先级顺序不变
 *
 * // FIXME: 1. 暂不支持超级抢量
 * // FIXME: 2. getAd 缓冲池空的时候, 不会内部去请求下一个广告, 需要外部触发load
 */
public class NativeAdManagerInternalEx extends NativeAdManagerInternal{
    private boolean mBreakRequest = false;
    private List<ImageINativeAd> mCacheAdList = new ArrayList<ImageINativeAd>();
    public NativeAdManagerInternalEx(Context context, String posId) {
        super(context, posId);
    }


    @Override
    public void setRequestParams(CMRequestParams requestParams) {
        super.setRequestParams(requestParams);
    }

    @Override
    public void loadAd() {
        removeExpiredAd();
        //顺序请求场景缓存中有最高优先级 或者 并发场景缓存中只要有广告 --> 回调成功。
        if((mIsPreload && hasHighPriorityAd()) || (!mIsPreload && !mCacheAdList.isEmpty())) {
            super.notifyAdLoaded();
            return;
        }
        mBreakRequest = false;
        super.loadAd();
    }

    private void removeExpiredAd(){
        if(mCacheAdList.isEmpty()){
            return;
        }
        Iterator<ImageINativeAd> iterator = mCacheAdList.iterator();
        while (iterator.hasNext()){
            ImageINativeAd imageINativeAd = iterator.next();
            if(imageINativeAd.getAdObject() == null || imageINativeAd.getAdObject().hasExpired()){
                iterator.remove();
            }
        }
    }

    public boolean hasHighPriorityAd(){
        if(mCacheAdList.isEmpty() || mConfigBeans.isEmpty()){
            return false;
        }
        INativeAd ad = mCacheAdList.get(0).getAdObject();
        if(ad.getAdTypeName().equals(mConfigBeans.get(0).name)){
            Logger.i(Const.TAG, "has high ad ,break load new ad");
            return true;
        }
        return false;
    }


    @Override
    protected boolean requestBean(PosBean bean) {
        if(mBreakRequest){
            return false;
        }
        //顺序请求，只请求第一个缓存广告前面优先级的广告。如果并发请求，说明没有缓存，所有的都去请求。
        if(mIsPreload && !mCacheAdList.isEmpty()) {
            INativeAd nativeAd = mCacheAdList.get(0).getAdObject();
            if (nativeAd != null && nativeAd.getAdTypeName().equalsIgnoreCase(bean.name)) {
                Logger.i(Const.TAG, "this ad type has cache ad, beak requestBean");
                mBreakRequest = true;
                //如果是preload 能跑到这说明高优先级的已经失败了，否则早就回调成功了也不会进行请求下一优先级的。
                super.notifyAdLoaded();
                return false;
            } else {
                return super.requestBean(bean);
            }
        }
        return super.requestBean(bean);
    }

    public INativeAd getAd(){
        return getAd(false);
    }


    public INativeAd getAd(boolean forceImage){
        removeExpiredAd();

        if(mCacheAdList.isEmpty()){
            return super.getAd();
        }
        if(forceImage) {
            Iterator<ImageINativeAd> iterator = mCacheAdList.iterator();
            while (iterator.hasNext()){
                ImageINativeAd ad = iterator.next();
                if(ad.hasCacheImage()){
                    iterator.remove();
                    return ad.getAdObject();
                }else {
                    preloadNativeAdImage(ad);
                    continue;
                }
            }
        }else {
            return mCacheAdList.remove(0).getAdObject();
        }
        return null;
    }

    @Override
    protected void notifyAdLoaded() {
        INativeAd nativeAd = super.getAd();
        if(nativeAd != null) {
            ImageINativeAd imageINativeAd = new ImageINativeAd(nativeAd, getAdTypeNameIndex(nativeAd.getAdTypeName()));
            if(nativeAd.isNativeAd()){
                preloadNativeAdImage(imageINativeAd);
            }
            mCacheAdList.add(imageINativeAd);
            Collections.sort(mCacheAdList);
        }
        super.notifyAdLoaded();
    }

    private boolean preloadNativeAdImage(final ImageINativeAd imageINativeAd){
        if(imageINativeAd == null){
            return false;
        }
        final String adCoverImageUrl = imageINativeAd.getAdObject().getAdCoverImageUrl();
        if(TextUtils.isEmpty(adCoverImageUrl)){
            return false;
        }
        Logger.i(Const.TAG, "preload image ad title:" + imageINativeAd.getAdObject().getAdTitle() + ",ad type is "+imageINativeAd.getAdObject().getAdTypeName());
        if(CMAdManagerFactory.getImageDownloadListener() != null){
            CMAdManagerFactory.getImageDownloadListener().getBitmap(imageINativeAd.getAdObject().getAdIconUrl(), false, null);
            CMAdManagerFactory.getImageDownloadListener().getBitmap(imageINativeAd.getAdObject().getAdCoverImageUrl(), false, new BitmapListener() {
                @Override
                public void onFailed(String errorCode) {

                }

                @Override
                public void onSuccessed(Bitmap bitmap) {
                    imageINativeAd.setHasCacheImage();
                }
            });
            return true;
        }
        return false;
    }


    static class ImageINativeAd implements  Comparable<ImageINativeAd>{
        private INativeAd mNativeAd;
        private boolean mHasImageCached = false;
        private int mAdLevelIndex;
        public ImageINativeAd(INativeAd nativeAd, int index){
            this.mAdLevelIndex = index;
            this.mNativeAd = nativeAd;
        }

        public INativeAd getAdObject(){
            return mNativeAd;
        }

        public void setHasCacheImage(){
            this.mHasImageCached = true;
        }

        public boolean hasCacheImage(){
            return mHasImageCached;
        }

        public int getAdLevelIndex(){
            return mAdLevelIndex;
        }

        @Override
        public int compareTo(ImageINativeAd another) {
            //数字越小越优先
            return Integer.valueOf(mAdLevelIndex).compareTo(another.getAdLevelIndex());
        }
    }
}
