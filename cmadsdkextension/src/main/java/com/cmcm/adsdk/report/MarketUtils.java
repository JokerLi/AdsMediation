package com.cmcm.adsdk.report;

import android.text.TextUtils;
import com.cmcm.adsdk.Const;
import com.cmcm.baseapi.ads.INativeAd;

import java.net.URLEncoder;
import java.util.Map;


public class MarketUtils {
    private static AESUtils mAesUtils;

	public static void reportExtra(String type, String pkg, String posid, int reportRes, Map<String, String> reportParams, String placementID, String rawStr, boolean isTest){
		String encryRawJson = encryptRawJson(rawStr);
		ReportFactory.reportNetworkAdLog(type, pkg, reportRes, posid, null, reportParams, placementID, encryRawJson, 0, isTest);
	}

    public static String encryptRawJson(String rawJson) {
        String facebookData = "";
        if (!TextUtils.isEmpty(rawJson)) {
            try {
                if (mAesUtils == null) {
                    mAesUtils = new AESUtils();
                }
                String temp = mAesUtils.base64Encrypt(rawJson);
                facebookData = URLEncoder.encode(temp, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return facebookData;
    }
}