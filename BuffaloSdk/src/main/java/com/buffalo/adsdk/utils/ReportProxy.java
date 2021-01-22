package com.buffalo.adsdk.utils;

import com.buffalo.adsdk.Const;

import java.util.Map;

public interface ReportProxy {

    String KEY_POSID = "posid";
    String KEY_ADTYPE_NAME = "adTypeName";
    String KEY_LOAD_TIME = "loadTime";
    String KEY_ERROR_CODE = "errorcode";
    String KEY_ERROR_MESSAGE = "error_message";

    String KEY_IS_RELOAD = "is_preload";
    String KEY_AD_INDEX = "ad_index";
    String KEY_AD_LOAD_TIMES = "ad_load_times";


    void doNativeReport(Const.Event event, Map<String, String> extras);

    //TODO:此接口不确定是否有效,想删除
    void doNetworkingReport(Map<String, String> extras);
}
