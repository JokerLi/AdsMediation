package com.buffalo.utils;

import android.text.TextUtils;

import com.buffalo.adsdk.report.ReportFactory;
import com.buffalo.adsdk.report.MarketUtils;

import java.util.Map;

/**
 * Created by Li Guoqing on 2016/5/19.
 */
public class UniReport {
    public static void report(String type, String pkg, String posid, int rcvReportRes, Map<String, String> reportParams, String placementID,
                              boolean isNativeAd, String rawStr, boolean isOrionAd) {
        if (TextUtils.isEmpty(type)) {
            return;
        }
        if(isOrionAd || ReportFactory.INSERTVIEW.equals(type)) {
            return;
        }
        MarketUtils.reportExtra(type, pkg, posid, rcvReportRes, reportParams, placementID, rawStr, false);
    }
}
