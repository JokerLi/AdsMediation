package com.buffalo.baseapi.ads;

import android.view.View;
import android.view.ViewGroup;

import com.buffalo.adsdk.base.BaseNativeAd;

import java.util.List;
import java.util.Map;

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
    public String getAdTypeName();

    /**
     * @return 返回广告Title
     */
    public String getAdTitle();

    /**
     * @return 返回广告背景大图
     */
    public String getAdCoverImageUrl();

    /**
     * @return 广告Icon的url
     */
    public String getAdIconUrl();

    /**
     * @return 广告的下载量或网址等小标题信息
     */
    public String getAdSocialContext();

    /**
     * @return 广告按钮的文案
     */
    public String getAdCallToAction();

    /**
     * @return 返回广告的详细描述
     */
    public String getAdBody();


    /**
     * @return 返回评分条的值
     */
    public double getAdStarRating();

    /**
     * @param view 将显示广告的View和广告绑定
     */
    public boolean registerViewForInteraction(View view);

    public boolean registerViewForInteraction_withExtraReportParams(View view, Map<String, String> reportParam);

    /**
     * 针对视频接口使用
     */
    public boolean registerViewForInteraction_withListView(IVideoAdapter adapter, View listView, ViewGroup viewGroup);

    /**
     * 将显示广告的View和广告解除绑定
     */
    public void unregisterView();

    /**
     * @return 是否过期
     */
    public boolean hasExpired();

    /**
     * @return true 下载类型，false 非下载，null 未知
     */
    public boolean isDownLoadApp();

    public void setImpressionListener(ImpressionListener impressionListener);

    public void setAdOnClickListener(IAdOnClickListener adOnClickListener);

    public IAdOnClickListener getAdOnClickListener();

    public boolean isNativeAd();

    public boolean isPriority();

    public Object getAdObject();

    //国内下载
    public void handleClick();

    public List<String> getExtPics();

    // FIXME: 2016/7/12 liguoqing
//    public MpaModule getMpaModule();

    public boolean isHasDetailPage();

    public View createDetailPage(INativeAd ad);

    public View createDetailPage();

    public void handleDetailClick();

    public void setAdClickDelegate(BaseNativeAd.IAdClickDelegate l);


    public String getSource();

    //video
    public void onPause();

    public void onResume();

    public void onDestroy();

    public String getTypeId();//供内容类使用，标示内容的类别

}
