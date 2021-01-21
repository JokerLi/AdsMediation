package com.buffalo.adsdk;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseFactory {

    public final Map<String, String> mNativeAdLoaderClassMap = new HashMap<>();
    public final Map<String, NativeAdTemplate.INativeAdViewAdapter> mNativeAdRenderMap = new HashMap<>();

    public abstract void initConfig();

    public BaseFactory() {
    }


    public boolean addLoaderClass(String loaderKey, String loaderClass) {
        if (mNativeAdLoaderClassMap.containsKey(loaderKey))
            return false;

        mNativeAdLoaderClassMap.put(loaderKey, loaderClass);
        return true;
    }


    public void addRenderAdapter(String loaderKey, NativeAdTemplate.INativeAdViewAdapter adapter) {
        if (TextUtils.isEmpty(loaderKey) || adapter == null) {
            return;
        }
        mNativeAdRenderMap.put(loaderKey, adapter);
    }

    public NativeAdTemplate.INativeAdViewAdapter getRenderAdapter(String loaderKey) {
        if (TextUtils.isEmpty(loaderKey)) {
            return null;
        }

        return mNativeAdRenderMap.get(loaderKey);
    }

    public abstract Object createAdLoader(Context context, Object posBean);


    public abstract void doNativeReport(Const.Event event,
                                        String posid,
                                        String adTypeName,
                                        long loadTime,
                                        String error,
                                        Map<String, String> extras);


    public abstract void doNetworkingReport(String pos, String source, String error);

}
