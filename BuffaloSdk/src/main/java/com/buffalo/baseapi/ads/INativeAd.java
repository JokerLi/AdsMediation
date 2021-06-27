package com.buffalo.baseapi.ads;

import android.view.View;

import androidx.annotation.Nullable;

import com.buffalo.adsdk.base.BaseNativeAd;

import java.util.List;

public interface INativeAd {
    /**
     * 广告展示回调接口
     */
    interface ImpressionListener {
        void onLoggingImpression();
    }

    /**
     * 广告点击回调接口
     */
    interface IAdOnClickListener {
        void onAdClick(INativeAd nativeAd);
    }

    /**
     * @return 返回广告类型
     */
    String getAdTypeName();

    /**
     * @return 返回广告Title
     */
    String getAdTitle();

    /**
     * @return 返回广告背景大图
     */
    String getAdCoverImageUrl();

    /**
     * @return 广告Icon的url
     */
    String getAdIconUrl();

    /**
     * @return 广告的下载量或网址等小标题信息
     */
    String getAdSocialContext();

    /**
     * @return 广告按钮的文案
     */
    String getAdCallToAction();

    /**
     * @return 返回广告的详细描述
     */
    String getAdBody();


    /**
     * @return 返回评分条的值
     */
    double getAdStarRating();

    /**
     * 特供facebook
     *
     * @param view
     * @param mediaView
     * @param adIconView
     * @param clickableViews
     * @return
     */
    boolean registerViewForInteraction(View view, View mediaView, @Nullable View adIconView, @Nullable List<View> clickableViews);

    /**
     * 将显示广告的View和广告解除绑定
     */
    void unregisterView();

    /**
     * @return 是否过期
     */
    boolean hasExpired();

    /**
     * @return true 下载类型，false 非下载，null 未知
     */
    boolean isDownLoadApp();

    void setImpressionListener(ImpressionListener impressionListener);

    void setAdOnClickListener(IAdOnClickListener adOnClickListener);

    IAdOnClickListener getAdOnClickListener();

    boolean isNativeAd();

    boolean isPriority();

    Object getAdObject();

    //国内下载
    void handleClick();

    boolean isHasDetailPage();

    View createDetailPage(INativeAd ad);

    View createDetailPage();

    void handleDetailClick();

    void setAdClickDelegate(BaseNativeAd.IAdClickDelegate l);


    String getSource();

    //video
    void onPause();

    void onResume();

    void onDestroy();

    String getTypeId();//供内容类使用，标示内容的类别

}
