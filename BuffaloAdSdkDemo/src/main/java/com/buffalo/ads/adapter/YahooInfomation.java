package com.buffalo.ads.adapter;

import com.buffalo.utils.Commons;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class YahooInfomation {
    private static final String TAG = "YahooInfomation";
    private static final String AD_TITLE = "headline";
    private static final String AD_SEC_HQIMAGE = "secHqImage";
    private static final String AD_SEC_IMAGE = "secImage";
    private static final String CALL_TO_ACTION = "callToAction";
    private static final String SUMMARY = "summary";
    private static final String APP_RATING = "appRating";
    private static final String AD_ASSET_CATEGORY = "appCategory";
    private static final String AD_ASSET_SOURCE = "source";

    /**
     * 获取yahoo Native广告 offer信息
     *
     * @param flurryAdNative
     * @return
     */
    public static String getYahooNativeOfferJsonV112(int operation, String requestId, FlurryAdNative flurryAdNative) {
        JSONObject jsonObject = new JSONObject();
        try {
            FlurryAdNativeAsset adTitle = flurryAdNative.getAsset(AD_TITLE);
            FlurryAdNativeAsset adAdCoverImageAsset = flurryAdNative.getAsset(AD_SEC_HQIMAGE);
            FlurryAdNativeAsset adAdIconImageAsset = flurryAdNative.getAsset(AD_SEC_IMAGE);
            FlurryAdNativeAsset adCallToAction = flurryAdNative.getAsset(CALL_TO_ACTION);
            FlurryAdNativeAsset adBody = flurryAdNative.getAsset(SUMMARY);
            String yahooOrgin = getYahooOriginData(flurryAdNative);
            Commons.putValueIntoJson(jsonObject, "operation", operation + "");
            Commons.putValueIntoJson(jsonObject, "ad_id", requestId);
            Commons.putValueIntoJson(jsonObject, "icon_url", adAdCoverImageAsset.getValue());
            Commons.putValueIntoJson(jsonObject, "cover_url", adAdIconImageAsset.getValue());
            Commons.putValueIntoJson(jsonObject, "title", adTitle.getValue());
            Commons.putValueIntoJson(jsonObject, "body", adBody.getValue());
            Commons.putValueIntoJson(jsonObject, "social_context", "");
            Commons.putValueIntoJson(jsonObject, "call2action", adCallToAction.getValue());
            Commons.putValueIntoJson(jsonObject, "fbad", yahooOrgin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getYahooOriginData(FlurryAdNative flurryAdNative) {
        try {
            Field Field = flurryAdNative.getClass().getDeclaredField("fAdObject");
            Field.setAccessible(true);
            Object object = Field.get(flurryAdNative);
            if (object instanceof com.flurry.sdk.h) {
                com.flurry.sdk.h c = (com.flurry.sdk.h) object;
                Field bvFile = c.getClass().getSuperclass().getDeclaredField("d");
                bvFile.setAccessible(true);
                Object bvObject = bvFile.get(c);
                if (bvObject instanceof com.flurry.sdk.bv) {
                    com.flurry.sdk.bv bv = (com.flurry.sdk.bv) bvObject;
                    Field cyFile = bv.getClass().getDeclaredField("a");
                    cyFile.setAccessible(true);
                    Object cyObject = cyFile.get(bv);
                    if (cyObject instanceof com.flurry.sdk.cy) {
                        com.flurry.sdk.cy cy = (com.flurry.sdk.cy) cyObject;
                        return cy.toString();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
