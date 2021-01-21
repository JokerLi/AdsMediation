package com.buffalo.utils.internal.report;

import com.buffalo.adsdk.report.ReportFactory;
import com.buffalo.utils.UniReport;

import java.util.Map;

public class Reporter {
    public static void reportClick(String posId, String pkgName, int res, String offerInfo, boolean isOrionAd, String placementId, boolean isNativeAd, Map<String, String> extra) {
        UniReport.report(ReportFactory.CLICK, pkgName, posId, res, extra,
                placementId, isNativeAd, offerInfo, isOrionAd);
    }

    public static void reportCallbackImpression(String posId, String pkgName, int res, String offerInfo, boolean isOrionAd, String placementId, boolean isNativeAd, Map<String, String> extra) {
        UniReport.report(ReportFactory.VIEW, pkgName, posId, res, extra,
                placementId, isNativeAd, offerInfo, isOrionAd);
    }
}
