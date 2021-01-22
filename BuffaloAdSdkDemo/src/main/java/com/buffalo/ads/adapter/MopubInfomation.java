package com.buffalo.ads.adapter;

import com.buffalo.utils.Commons;
import com.mopub.nativeads.NativeResponse;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class MopubInfomation {
    public static final String TAG = "MopubInfomation";

    /**
     * 获取Mopub Native广告 offer信息
     *
     * @param
     * @return
     */
    public static String getMopubNativeAdOfferJsonV313(int operation, NativeResponse nativeResponse) {
        JSONObject jsonObject = new JSONObject();
        try {
            String mainImageUrl = nativeResponse.getMainImageUrl();
            String iconImageUrl = nativeResponse.getIconImageUrl();
            String callToAction = nativeResponse.getCallToAction();
            String title = nativeResponse.getTitle();
            String text = nativeResponse.getText();
            String unitId = nativeResponse.getAdUnitId();
            String mOriginBody = getMopubOriginData(nativeResponse);
            Commons.putValueIntoJson(jsonObject, "operation", operation + "");
            Commons.putValueIntoJson(jsonObject, "ad_id", unitId);
            Commons.putValueIntoJson(jsonObject, "icon_url", iconImageUrl);
            Commons.putValueIntoJson(jsonObject, "cover_url", mainImageUrl);
            Commons.putValueIntoJson(jsonObject, "title", title);
            Commons.putValueIntoJson(jsonObject, "body", text);
            Commons.putValueIntoJson(jsonObject, "social_context", "");
            Commons.putValueIntoJson(jsonObject, "call2action", callToAction);
            Commons.putValueIntoJson(jsonObject, "fbad", mOriginBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getMopubOriginData(NativeResponse nativeResponse) {
        try {
            Field Field = nativeResponse.getClass().getDeclaredField("mNativeAd");
            Field.setAccessible(true);
            Object Object = Field.get(nativeResponse);
            if (Object instanceof com.mopub.nativeads.NativeAdInterface) {
                com.mopub.nativeads.MoPubCustomEventNative.MoPubForwardingNativeAd moPubForwardingNativeAd = (com.mopub.nativeads.MoPubCustomEventNative.MoPubForwardingNativeAd) Object;
                Field jsonField = moPubForwardingNativeAd.getClass().getDeclaredField("mJsonObject");
                jsonField.setAccessible(true);
                Object jsonObject = jsonField.get(moPubForwardingNativeAd);
                if (jsonObject instanceof org.json.JSONObject) {
                    JSONObject json = (JSONObject) jsonObject;
                    return json.toString();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
