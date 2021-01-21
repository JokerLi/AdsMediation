package com.buffalo.ads.utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class LocaleConfig {
    public static final String TYPE_NATIVE = "type_native";
    public static final String TYPE_BANNER = "type_banner";
    public static final String reqUrl = "";
    public static HashMap<String, String> data = new HashMap<String, String>();
    public static JSONObject mNativeKeyValue;

    public static void init() {
        VolleyUtil.getStream(reqUrl, new VolleyUtil.CallBack() {
            @Override
            public void onResponse(final InputStream in) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuffer sb = new StringBuffer();

                try {
                    String str = null;
                    while ((str = br.readLine()) != null) {
                        sb.append(str);
                    }
                    JSONObject jo = new JSONObject(sb.toString());
                    JSONArray jr = jo.getJSONArray("data");
                    for (int i = 0; i < jr.length(); i++) {
                        JSONObject jo1 = jr.getJSONObject(i);
                        mNativeKeyValue = jo1.getJSONObject("data");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(String response) {
                int a;
            }
        });

    }

    public static String getString(String type, String key, String defvalue) {
        JSONObject jo = getJo(type);
        return jo == null ? defvalue : jo.optString(key, defvalue);
    }

    public static int getInt(String type, String key, int defvalue) {
        JSONObject jo = getJo(type);
        return jo == null ? defvalue : jo.optInt(key, defvalue);
    }

    private static JSONObject getJo(String type) {
        JSONObject jo = null;
        if (TYPE_NATIVE.equals(type)) {
            jo = mNativeKeyValue;
        }
        return jo;
    }


}
