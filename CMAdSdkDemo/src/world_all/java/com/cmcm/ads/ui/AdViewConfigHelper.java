package com.cmcm.ads.ui;

import com.cmcm.ads.ui.impls.AdmobRenderAdapter;
import com.cmcm.ads.ui.impls.FacebookRenderAdapter;
import com.cmcm.ads.ui.impls.MopubRenderAdapter;
import com.cmcm.ads.ui.impls.PicksRenderAdapter;
import com.cmcm.ads.ui.impls.YahooRenderAdapter;
import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.Const;

/**
 * Created by Li Guoqing on 2016/11/21.
 */
public class AdViewConfigHelper {
    public static void setRenderAdapter(){
        CMAdManager.addRenderAdapter(Const.KEY_FB, new FacebookRenderAdapter(CMAdManager.getContext()));
        CMAdManager.addRenderAdapter(Const.KEY_AB, new AdmobRenderAdapter(CMAdManager.getContext()));
        CMAdManager.addRenderAdapter(Const.KEY_MP, new MopubRenderAdapter(CMAdManager.getContext()));
        CMAdManager.addRenderAdapter(Const.KEY_YH, new YahooRenderAdapter(CMAdManager.getContext()));
        CMAdManager.addRenderAdapter(Const.KEY_CM, new PicksRenderAdapter());
    }
}
