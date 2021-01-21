package com.buffalo.baseapi.ads;


import java.util.List;

public interface INativeAdLoader {

    void setAdListener(INativeAdLoaderListener listener);

    void loadAd();

    INativeAd getAd();

    List<INativeAd> getAdList(int num);



}
