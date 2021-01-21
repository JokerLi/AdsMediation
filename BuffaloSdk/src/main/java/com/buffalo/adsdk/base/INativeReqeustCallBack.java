package com.buffalo.adsdk.base;

public interface INativeReqeustCallBack {

    public void adLoaded(String adTypeName);

    public void adFailedToLoad(String adTypeName, String errorInfo);

}
