package com.cmcm.utils.internal.report;

import com.cmcm.adsdk.report.ReportFactory;
import com.cmcm.utils.UniReport;

import java.util.Map;

/**
 * Created by peiboning on 2016/11/21.
 */
public class Reporter {
    public static void reportClick(String posId, String pkgName,int res, String offerInfo, boolean isOrionAd, String placementId,boolean isNativeAd, Map<String, String> extra) {
        UniReport.report(ReportFactory.CLICK, pkgName, posId, res, extra,
                placementId, isNativeAd, offerInfo, isOrionAd);
    }

    public static void reportCallbackImpression(String posId, String pkgName,int res, String offerInfo, boolean isOrionAd, String placementId,boolean isNativeAd, Map<String, String> extra) {
        UniReport.report(ReportFactory.VIEW, pkgName, posId, res, extra,
                placementId, isNativeAd, offerInfo, isOrionAd);
    }
}
