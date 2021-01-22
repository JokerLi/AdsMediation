package com.buffalo.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AdWebViewUtils {
    private static String browserUserAgentString = null;

    public static String getUserAgentString(Context context) {

        if (browserUserAgentString == null) {
            if (Build.VERSION.SDK_INT >= 17) {
                try {
                    browserUserAgentString = getDefaultUserAgentL17(context);
                } catch (Exception e) {
                    browserUserAgentString = getStringWhenException(context);
                }
            } else {
                browserUserAgentString = getStringWhenException(context);
            }
        }
        return browserUserAgentString;
    }

    private static String getStringWhenException(Context context) {
        String tempString = "";
        try {
            tempString = getUserAgentStringByReflection(context, "android.webkit.WebSettings", "android.webkit.WebView");
        } catch (Exception ea) {
            try {
                tempString = getUserAgentStringByReflection(context, "android.webkit.WebSettingsClassic", "android.webkit.WebViewClassic");
            } catch (Exception ex) {
                WebView webView = new WebView(context.getApplicationContext());
                tempString = webView.getSettings().getUserAgentString();
                webView.destroy();
            }
        }

        return tempString;
    }

    private static String getUserAgentStringByReflection(Context context, String webSettingsClassName, String webViewClassName) throws Exception {
        Class webSettingsClass = Class.forName(webSettingsClassName);
        Constructor constructor = webSettingsClass.getDeclaredConstructor(new Class[]{Context.class, Class.forName(webViewClassName)});
        constructor.setAccessible(true);
        Method method = webSettingsClass.getMethod("getUserAgentString", new Class[0]);
        try {
            return (String) method.invoke(constructor.newInstance(new Object[]{context, null}), new Object[0]);
        } finally {
            constructor.setAccessible(false);
        }
    }

    @TargetApi(17)
    private static String getDefaultUserAgentL17(Context context) {
        return WebSettings.getDefaultUserAgent(context);
    }
}