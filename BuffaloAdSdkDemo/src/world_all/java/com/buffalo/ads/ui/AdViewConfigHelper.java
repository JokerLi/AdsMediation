package com.buffalo.ads.ui;

import com.buffalo.ads.ui.impls.AdmobRenderAdapter;
import com.buffalo.ads.ui.impls.FacebookRenderAdapter;
import com.buffalo.ads.ui.impls.MopubRenderAdapter;
import com.buffalo.ads.ui.impls.YahooRenderAdapter;
import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.Const;

/**
 * Created by Li Guoqing on 2016/11/21.
 */
public class AdViewConfigHelper {
    public static void setRenderAdapter() {
        AdManager.addRenderAdapter(Const.KEY_FB, new FacebookRenderAdapter(AdManager.getContext()));
        AdManager.addRenderAdapter(Const.KEY_AB, new AdmobRenderAdapter(AdManager.getContext()));
        AdManager.addRenderAdapter(Const.KEY_MP, new MopubRenderAdapter(AdManager.getContext()));
        AdManager.addRenderAdapter(Const.KEY_YH, new YahooRenderAdapter(AdManager.getContext()));
    }
}
