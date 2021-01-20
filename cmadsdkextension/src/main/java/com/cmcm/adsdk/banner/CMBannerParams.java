package com.cmcm.adsdk.banner;

import com.cmcm.adsdk.CMRequestParams;

/**
 * Created by cm on 2016/2/17.
 */
public class CMBannerParams extends CMRequestParams {


    public void setBannerViewSize(CMBannerAdSize mBannerAdSize) {
        if(mParams != null) {
            mParams.put(KEY_BANNER_VIEW_SIZE, mBannerAdSize);
        }
    }

    public CMBannerAdSize getCMBannerAdSize() {
        if(mParams != null) {
            Object bannerSize = mParams.get(KEY_BANNER_VIEW_SIZE);
            if (null != bannerSize) {
                return (CMBannerAdSize)bannerSize;
            }
        }
        return null;
    }
}
