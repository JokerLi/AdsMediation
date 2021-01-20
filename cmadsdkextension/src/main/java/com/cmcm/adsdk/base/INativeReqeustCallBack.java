package com.cmcm.adsdk.base;


/**
 * Created by chenhao on 2015/7/30.
 */
public interface INativeReqeustCallBack {

    public void adLoaded(String adTypeName);

    public void adFailedToLoad(String adTypeName, String errorInfo);

}
