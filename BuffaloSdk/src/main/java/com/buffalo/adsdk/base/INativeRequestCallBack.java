package com.buffalo.adsdk.base;

public interface INativeRequestCallBack {
    void adLoaded(String adTypeName);

    void adFailedToLoad(String adTypeName, String errorInfo);
}
