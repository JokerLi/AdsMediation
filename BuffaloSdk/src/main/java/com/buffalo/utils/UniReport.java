package com.buffalo.utils;

import android.text.TextUtils;

import com.buffalo.adsdk.report.MarketUtils;

import java.util.Map;

public class UniReport {
    public static void report(String type, String pkg, String posid, int rcvReportRes, Map<String, String> reportParams, String placementID,
                              boolean isNativeAd, String rawStr) {
        if (TextUtils.isEmpty(type)) {
            return;
        }
        MarketUtils.reportExtra(type, pkg, posid, rcvReportRes, reportParams, placementID, rawStr, false);
    }
}
