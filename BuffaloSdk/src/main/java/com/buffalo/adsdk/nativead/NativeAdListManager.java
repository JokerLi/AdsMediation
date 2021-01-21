package com.buffalo.adsdk.nativead;


import android.content.Context;

import com.buffalo.adsdk.RequestParams;
import com.buffalo.adsdk.config.PosBean;
import com.buffalo.utils.ThreadHelper;
import com.buffalo.baseapi.ads.INativeAd;

import java.util.List;
import java.util.concurrent.Callable;

public class NativeAdListManager {
    private NativeAdsManagerInternal mRequest;
    private RequestParams requestParams;
    public NativeAdListManager(Context context, String posid, INativeAdListListener listener) {
        mRequest = new NativeAdsManagerInternal(context, posid);
        mRequest.setAdListener(listener);
    }

    public void loadAds(int num){
        mRequest.setRequestParams(requestParams);
        mRequest.loadAds(num);
    }

    public void setOpenPriority(boolean openPriority){
        mRequest.setOpenPriority(openPriority);
    }

    public List<INativeAd> getAdList(){
        return ThreadHelper.runOnUiThreadBlockingNoException(new Callable<List<INativeAd>>() {
            @Override
            public List<INativeAd> call() throws Exception {
                if (mRequest != null) {
                    return mRequest.getAdList();
                }
                return null;
            }
        });
    }

    public List<PosBean> getPosBeans() {
        return mRequest.getPosBeans();
    }

    public String getRequestLastError() {
        if (mRequest != null) {
            return mRequest.mRequestLogger.getLastResult();
        }
        return null;
    }

    public String getRequestErrorInfo(){
        if(mRequest != null){
            return mRequest.mRequestLogger.getRequestErrorInfo();
        }
        return null;
    }


    public void setRequestParams(RequestParams params){
        this.requestParams = params;
    }


}
