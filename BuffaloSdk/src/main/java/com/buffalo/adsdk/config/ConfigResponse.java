package com.buffalo.adsdk.config;

import android.text.TextUtils;

import com.buffalo.utils.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigResponse {
    private static final String TAG = "ConfigResponse";
    private static final String KEY_CONFIG_POSLIST = "poslist";
    private static final String KEY_AD_TYPE = "adtype";
    private static final String KEY_PLACE_ID = "placeid";
    private static final String KEY_INFO = "info";
    private static final String KEY_NAME = "name";
    private static final String KEY_PARAMETER = "parameter";
    private static final String KEY_WEIGHT = "weight";

    public static class AdPosInfo {
        public int adType;
        public String placementId;
        public List<PosBean> orders = new ArrayList<>();
    }

    final private Map<String, AdPosInfo> mAdPosConfigMap = new HashMap<>();

    public Map<String, AdPosInfo> getPosConfigMap() {
        return mAdPosConfigMap;
    }

    public static boolean isValidResponse(String json) {
        try {
            JSONObject configJson = new JSONObject(json);
            return configJson.has(KEY_CONFIG_POSLIST);
        } catch (Exception e) {
            Logger.e(TAG, "valid " + e);
        }
        return false;
    }

    public static ConfigResponse createFrom(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        ConfigResponse instance = null;
        try {
            instance = new ConfigResponse();
            JSONObject obj = new JSONObject(json);
            JSONArray posListJsonArray = obj.getJSONArray(KEY_CONFIG_POSLIST);
            for (int i = 0; i < posListJsonArray.length(); ++i) {
                AdPosInfo adPos = new AdPosInfo();
                JSONObject posListObject = posListJsonArray.getJSONObject(i);
                adPos.adType = posListObject.optInt(KEY_AD_TYPE);
                adPos.placementId = posListObject.optString(KEY_PLACE_ID);
                JSONArray infoArray = posListObject.getJSONArray(KEY_INFO);
                for (int j = 0; j < infoArray.length(); j++) {
                    JSONObject infoObject = infoArray.getJSONObject(j);

                    int weight = infoObject.optInt(KEY_WEIGHT);
                    if (weight > 0) {
                        PosBean bean = new PosBean(infoObject.optString(KEY_NAME), adPos.placementId, weight, adPos.adType, infoObject.optString(KEY_PARAMETER));
                        adPos.orders.add(bean);
                    }
                }
                Collections.sort(adPos.orders);
                instance.mAdPosConfigMap.put(adPos.placementId, adPos);
            }
        } catch (Exception e) {
            Logger.e(TAG, "ConfigResponse create error..." + e.getMessage());
        }
        return instance;
    }

    public AdPosInfo findAdPosInfo(String placementId) {
        if (mAdPosConfigMap.isEmpty()) {
            return null;
        }

        return mAdPosConfigMap.get(placementId);
    }

    @Override
    public String toString() {
        if (mAdPosConfigMap.isEmpty()) {
            return "{null}";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, AdPosInfo> entry : mAdPosConfigMap.entrySet()) {
            AdPosInfo pos = entry.getValue();

            sb.append("pos:").append(pos.placementId).append(" adtype:").append(pos.adType);
            sb.append(":poslist{");
            for (PosBean bean : pos.orders) {
                sb.append(bean.toString());
                sb.append(",");
            }
            sb.append("}\n");
        }
        return sb.toString();
    }

}
