package com.buffalo.ads.ui;

import com.buffalo.ads.ui.impls.AdmobRenderAdapter;
import com.buffalo.ads.ui.impls.FacebookRenderAdapter;
import com.buffalo.ads.ui.impls.MopubRenderAdapter;
import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.Const;

public class AdViewConfigHelper {
    public static void setRenderAdapter() {
        AdManager.addRenderAdapter(Const.KEY_FB, new FacebookRenderAdapter(AdManager.getContext()));
        AdManager.addRenderAdapter(Const.KEY_AB, new AdmobRenderAdapter(AdManager.getContext()));
        AdManager.addRenderAdapter(Const.KEY_MP, new MopubRenderAdapter(AdManager.getContext()));
    }
}
