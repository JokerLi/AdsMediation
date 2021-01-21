package com.buffalo.adsdk.nativead;

import android.content.Context;

import com.buffalo.adsdk.config.PosBean;
import com.buffalo.adsdk.CMRequestParams;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.utils.ThreadHelper;

import java.util.List;
import java.util.concurrent.Callable;


/**
 * Created by chenhao on 16/3/10.
 */
public class NativeAdManagerEx extends NativeAdManager  {
    private NativeAdManagerInternalEx managerInternalEx;

    public NativeAdManagerEx(Context context, String posid) {
        super(context, posid);
        managerInternalEx = new NativeAdManagerInternalEx(context, posid);
        super.requestAd = managerInternalEx;
    }

    public void setRequestParams(CMRequestParams requestParams) {
        if (managerInternalEx != null) {
            managerInternalEx.setRequestParams(requestParams);
        }
    }

    public INativeAd getAd(final boolean forceImageSuccess) {
        return ThreadHelper.runOnUiThreadBlockingNoException(new Callable<INativeAd>() {
            @Override
            public INativeAd call() throws Exception {

                if (managerInternalEx != null) {
                    return managerInternalEx.getAd(forceImageSuccess);
                }
                return null;
            }

        });
    }

    public boolean hasHighPriorityAd(){
        return managerInternalEx.hasHighPriorityAd();
    }

    public String getHighPriorityType(){
        List<PosBean> posBeanList = managerInternalEx.getPosBeans();
        if(posBeanList == null || posBeanList.isEmpty()){
            return null;
        }
        return posBeanList.get(0).getAdName();
    }
}
