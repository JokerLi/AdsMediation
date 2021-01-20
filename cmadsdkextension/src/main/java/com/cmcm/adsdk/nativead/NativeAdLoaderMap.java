package com.cmcm.adsdk.nativead;

import android.content.Context;
import android.text.TextUtils;

import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.config.PosBean;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shimiaolei on 16/1/1.
 */
public class NativeAdLoaderMap {

    final private Map<String, CMNativeAdLoader> mLoaderCacheMap = new HashMap<String, CMNativeAdLoader>();
    final List<String> mFailedLoaderNames = new ArrayList<>();
    List<PosBean> oldBeanList = new ArrayList<>();
    boolean enableVideo = false;
    boolean enableBanner = false;

    private boolean assurePosbeanSame(List<PosBean> newPosbean, List<PosBean> oldPosbean){
        if(newPosbean.size() != oldPosbean.size()){
            return false;
        }
        for(int i = 0; i < oldPosbean.size(); i++ ){
            PosBean newBean = newPosbean.get(i);
            PosBean oldBean = oldPosbean.get(i);
            if((null == newBean.name) || (null == newBean.parameter)){
                return false;
            }
            if(!newBean.name.equalsIgnoreCase(oldBean.name) || !newBean.parameter.equalsIgnoreCase(oldBean.parameter)){
                return false;
            }
        }
        return true;
    }

    public void updateLoaders(Context context, List<PosBean> posBeans, INativeAd.IAdOnClickListener onClickListener) {
        if(!assurePosbeanSame(posBeans, oldBeanList)){
            oldBeanList = posBeans;
            mLoaderCacheMap.clear();
        }
        mFailedLoaderNames.clear();

        for (PosBean bean : posBeans) {
            CMNativeAdLoader loader = getAdLoader(context, bean, onClickListener);
            mLoaderCacheMap.put(bean.name, loader);

            if (loader == null ||
                    (!enableBanner && loader.getAdType() == Const.AdType.BANNER)
                    || (!enableVideo && loader.getAdType() == Const.AdType.VIDEO)) {
                mFailedLoaderNames.add(bean.name);
            }
        }
        Logger.i(Const.TAG, "mConfigBeans size:" + posBeans.size() + " mLoaderCacheMap size:" + mLoaderCacheMap.size());
    }

    public CMNativeAdLoader getAdLoader(Context context, PosBean posBean, INativeAd.IAdOnClickListener onClickListener) {
        if (posBean == null || TextUtils.isEmpty(posBean.name) || !posBean.isValidInfo()) {
            return null;
        }
        //从NativeAdLoader的cache里面拿
        if (mLoaderCacheMap.containsKey(posBean.name)) {
            return mLoaderCacheMap.get(posBean.name);
        } else {
            CMNativeAdLoader loader = (CMNativeAdLoader) (CMAdManager.createFactory().createAdLoader(context, posBean));
            if (loader != null) {
                loader.setAdClickListener(onClickListener);
                mLoaderCacheMap.put(posBean.name, loader);
            }
            return loader;
        }
    }

    public CMNativeAdLoader getAdLoader(Object key) {
        if (mLoaderCacheMap.containsKey(key)) {
            return mLoaderCacheMap.get(key);
        }
        return null;
    }

    public boolean containsKey(Object key) {
        return mLoaderCacheMap.containsKey(key);
    }

    public void enableVideo(boolean enable){
        this.enableVideo = enable;
    }


    public void enableBanner(boolean enable){
        this.enableBanner = enable;
    }


}
