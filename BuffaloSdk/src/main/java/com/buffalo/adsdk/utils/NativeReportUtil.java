package com.buffalo.adsdk.utils;

import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.BaseFactory;
import com.buffalo.adsdk.Const;

import java.util.HashMap;
import java.util.Map;

public class NativeReportUtil {

    public static void doNativeAdSuccessReport(Const.Event event, String id) {
        doNativeAdSuccessReport(event, id, 0L);
    }

    public static void doNativeAdSuccessReport(Const.Event event, String mid, long time) {
        doNativeAdSuccessReport(event, mid, null, time, null);
    }

    public static void doGetAdFailReport(Const.Event event, String posid, String errorCode) {
        doNativeAdReport(event, posid, null, 0L, errorCode, null);
    }

    public static void doGetAdFailReport(Const.Event event, String posid, String errorCode, Map<String, String> extras) {
        doNativeAdReport(event, posid, null, 0L, errorCode, extras);
    }

    public static void doGetAdReport(Const.Event event, String posid, String adTypeName,
                                     int adIndex) {
        Map<String, String> data = new HashMap<>();
        data.put(ReportProxy.KEY_AD_INDEX, "" + adIndex);
        doNativeAdReport(event, posid, adTypeName, 0L, null, data);
    }

    public static void doNativeAdFailReport(Const.Event event, String mid, long time, String error) {
        doNativeAdFailReport(event, mid, null, time, error, null);
    }

    public static void doNativeAdFailReport(Const.Event event, String posid, String error,
                                            boolean isPreload) {
        Map<String, String> data = new HashMap<>();
        data.put(ReportProxy.KEY_IS_RELOAD, String.valueOf(isPreload));
        doNativeAdFailReport(event, posid, null, 0L, error, data);
    }


    public static void doNativeAdSuccessReport(Const.Event event, String posid, String adTypeName,
                                               long time, Map<String, String> extras) {
        doNativeAdReport(event, posid, adTypeName, time, null, extras);
    }

    public static void doNativeAdFailReport(Const.Event event, String posid, String adTypeName,
                                            long time, String error, Map<String, String> extras) {
        doNativeAdReport(event, posid, adTypeName, time, error, extras);
    }

    private static void doNativeAdReport(Const.Event event, String posid, String adTypeName,
                                         long time, String error, Map<String, String> extras) {
        BaseFactory factory = AdManager.createFactory();
        if (factory != null) {
            factory.doNativeReport(event, posid, adTypeName, time, error, extras);
        }
    }
}