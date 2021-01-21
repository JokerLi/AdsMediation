package com.buffalo.adsdk;


import android.content.Context;
import android.text.TextUtils;

import com.buffalo.adsdk.adapter.NativeloaderAdapter;
import com.buffalo.adsdk.config.PosBean;
import com.buffalo.adsdk.config.RequestConfig;
import com.buffalo.adsdk.nativead.NativeAdLoader;
import com.buffalo.adsdk.utils.ReportProxy;
import com.buffalo.utils.Logger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class CMAdManagerFactory extends CMBaseFactory {

    private static ImageDownloadListener sImageDownloadListener;
    private static ReportProxy sReportProxy;
    public CMAdManagerFactory() {
    }

    @Override
    public void initConfig() {
        RequestConfig.getInstance().init(AdManager.getContext(), AdManager.getMid());
        RequestConfig.getInstance().requestConfig(false);
    }

    public static void setDefaultConfig(String defaultConfig, boolean force){
        RequestConfig.getInstance().setDefaultConfig(defaultConfig, force);
    }


    @Override
    public Object createAdLoader(Context context, Object posBean) {
        if(!(posBean instanceof  PosBean)){
            return null;
        }
        PosBean bean = (PosBean) posBean;
        if(posBean == null || TextUtils.isEmpty(bean.name)){
            return null;
        }
        try {
            String str[] = bean.name.split("_");
            if(str.length == 0){
                Logger.i(Const.TAG, "config type:" + bean.name + ",has error");
                return null;
            }

            String loaderName = str[0].toLowerCase();
            String posid = String.valueOf(bean.placeid);
            String params = bean.parameter;
            String adTypeName = bean.name;

            Object nativeloaderAdapter = null;
            if(mNativeAdLoaderClassMap.containsKey(loaderName)) {
                Logger.i(Const.TAG, "create NativeAdapter:" + adTypeName + " [ loaderName:" + loaderName + "]" );
                nativeloaderAdapter = createObject(mNativeAdLoaderClassMap.get(loaderName));
            } else {
                Logger.w(Const.TAG, "unmatched native adtype:" + adTypeName);
            }
            if (nativeloaderAdapter != null) {
                return new NativeAdLoader(context, posid, adTypeName, params, bean, (NativeloaderAdapter) nativeloaderAdapter);
            }
        } catch (Exception e) {
            Logger.w(Const.TAG, e.toString());
        }
        return null;
    }

    public static void setImageDownloadListener(ImageDownloadListener listener){
        sImageDownloadListener = listener;
    }

    public static ImageDownloadListener getImageDownloadListener(){
        return sImageDownloadListener;
    }

    private static Object createObject(String className) {
        Object loader = null;
        try {
            Class loaderClass = Class.forName(className);
            Class[] paramTypes = {};
            Object[] params = {};
            Constructor constructor = loaderClass.getConstructor(paramTypes);
            loader = constructor.newInstance(params);
        } catch (Exception e) {
            Logger.w(Const.TAG, e.toString());
        }
        return loader;
    }

    public static void setReportProxy(ReportProxy reportProxy){
        sReportProxy = reportProxy;
    }



    //区分native, banner, video埋点，否则不方便查数据
    @Override
    public void doNativeReport(Const.Event event, String posid,
                               String adTypeName,long loadTime, String error, Map<String,String> extras) {
        if(sReportProxy != null){
            Map<String, String> map = createReportMap(posid, adTypeName, loadTime);
            map.put(ReportProxy.KEY_ERROR_CODE, error);
            if (extras != null && extras.size() > 0) {
                map.putAll(extras);
            }
            sReportProxy.doNativeReport(event, map);
        }
    }


    private Map<String, String> createReportMap(String posid, String adTypeName, long time){
        Map<String, String> extras = new HashMap<String, String>();
        extras.put(ReportProxy.KEY_POSID, posid);
        extras.put(ReportProxy.KEY_ADTYPE_NAME, adTypeName);
        extras.put(ReportProxy.KEY_LOAD_TIME, String.valueOf(time));
        return extras;
    }


    @Override
    public void doNetworkingReport(String pos, String source, String error){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("pos", pos);
        map.put("source", source);
        map.put("error", error);
        if(sReportProxy != null){
            sReportProxy.doNetworkingReport(map);
        }
    }
}
