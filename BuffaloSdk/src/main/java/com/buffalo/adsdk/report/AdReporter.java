package com.buffalo.adsdk.report;

import android.text.TextUtils;

import java.util.Map;

public class AdReporter {
    public static void report(String type, String posid, Map<String, String> reportParams, String placementID) {
        if (TextUtils.isEmpty(type)) {
            return;
        }
        ReportFactory.reportNetworkAdLog(type, posid, reportParams, placementID, 0);
    }
}
