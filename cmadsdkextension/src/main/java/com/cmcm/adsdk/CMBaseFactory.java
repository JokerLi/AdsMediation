package com.cmcm.adsdk;



import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by shimiaolei on 16/1/2.
 */
public abstract class CMBaseFactory {

    public final Map<String, String> mNativeAdLoaderClassMap = new HashMap<>();
    public final Map<String, CMNativeAdTemplate.ICMNativeAdViewAdapter> mNativeAdRenderMap = new HashMap<>();

    public abstract void initConfig();

    public CMBaseFactory() {
    }


    public boolean addLoaderClass(String loaderKey, String loaderClass) {
        if (mNativeAdLoaderClassMap.containsKey(loaderKey))
            return false;

        mNativeAdLoaderClassMap.put(loaderKey, loaderClass);
        return true;
    }


    public void addRenderAdapter(String loaderKey, CMNativeAdTemplate.ICMNativeAdViewAdapter adapter) {
        if(TextUtils.isEmpty(loaderKey) || adapter == null){
            return;
        }
        mNativeAdRenderMap.put(loaderKey, adapter);
    }

    public CMNativeAdTemplate.ICMNativeAdViewAdapter getRenderAdapter(String loaderKey){
        if(TextUtils.isEmpty(loaderKey)){
            return null;
        }

        return mNativeAdRenderMap.get(loaderKey);
    }

    public abstract Object createAdLoader(Context context, Object posBean);


    public abstract void  doNativeReport(Const.Event event,
                                         String posid,
                                         String adTypeName,
                                         long loadTime,
                                         String error,
                                         Map<String,String> extras);


    public abstract void doNetworkingReport(String pos, String source, String error);

}
