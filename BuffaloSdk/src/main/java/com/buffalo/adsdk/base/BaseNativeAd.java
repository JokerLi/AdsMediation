package com.buffalo.adsdk.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.buffalo.baseapi.ads.INativeAd;

import java.util.List;
import java.util.Map;

public abstract class BaseNativeAd implements INativeAd {
    public static final String KEY_APP_ID = "appid";
    public static final String KEY_LOAD_SIZE = "load_size";
    public static final String KEY_PLACEMENT_ID = "placementid";
    public static final String KEY_CACHE_TIME = "cache_time";
    public static final String KEY_JUHE_POSID = "juhe_posid";
    public static final String KEY_RCV_REPORT_RES = "rcv_report_res";
    public static final String KEY_REPORT_PKGNAME = "report_pkg_name";
    public static final String KEY_LOAD_LIST = "ad_load_list";
    public static final String KEY_CHECK_VIEW = "check_view";
    public static final String KEY_AD_TYPE_NAME = "ad_type_name";
    public static final String KEY_IS_FEED = "is_feed";
    public static final String KEY_FILTER_ADMOB_INSTALL_AD = "FILTER_ADMOB_INSTALL_AD";
    public static final String KEY_FILTER_ADMOB_CONTENT_AD = "FILTER_ADMOB_CONTENT_AD";
    public static final String KEY_EXTRA_OBJECT = "extra_object";
    //banner
    public static final String KEY_BANNER_VIEW_SIZE = "banner_view_size";
    //orion brand tab id for classify
    public static final String KEY_TAB_ID = "key_tab_id";
    public static final String KEY_IS_TOP = "key_is_top";

    // Basic fields
    @NonNull
    private String mMainImageUrl;
    @NonNull
    private String mIconImageUrl;
    @NonNull
    private String mTitle;
    @Nullable
    private String mCallToAction;
    @Nullable
    private String mAdSocialContext;
    @Nullable
    private boolean mIsDownloadApp = false;
    @Nullable
    private String mAdDescription;
    @Nullable
    private double mAdStartRate;
    @Nullable
    private boolean mIsPriority;
    @Nullable
    protected ImpressionListener mImpressionListener;
    @Nullable
    protected IAdOnClickListener mAdOnClickListener;
    @Nullable
    public OpenDegBrowserListener openDegBrowserListener;  //设置跳转至猎豹浏览器

    private String mSource;
    private boolean mIsHasDetailPage = false;

    protected long mCreateTime;
    protected long mCacheTime;
    protected Map<String, String> mExtraReportParams;
    protected String mAdType = "";

    public BaseNativeAd() {
        mCreateTime = System.currentTimeMillis();
    }

    protected IAdClickDelegate mAdClickDelegate;

    @Override
    public void setAdClickDelegate(IAdClickDelegate l) {
        mAdClickDelegate = l;
    }

    public void setAdOnClickListener(IAdOnClickListener adOnClickListener) {
        this.mAdOnClickListener = adOnClickListener;
    }

    public void notifyNativeAdClick(final INativeAd nativeAd) {
        if (mAdOnClickListener != null) {
            mAdOnClickListener.onAdClick(nativeAd);
        }
    }

    public interface IAdClickDelegate {
        public boolean handleClick(boolean isDetailPageClick);
    }


    public IAdOnClickListener getAdOnClickListener() {
        return mAdOnClickListener;
    }

    public void setImpressionListener(@Nullable ImpressionListener impressionListener) {
        this.mImpressionListener = impressionListener;
    }

    public void setOnClickToLBListener(@Nullable OpenDegBrowserListener openDegBrowserListener) {
        this.openDegBrowserListener = openDegBrowserListener;
    }

    public interface OpenDegBrowserListener {
        void toGetADUrl(String uri);
    }

    public boolean isNativeAd() {
        return true;
    }

    public String getAdTitle() {
        return mTitle;
    }

    public String getAdCoverImageUrl() {
        return mMainImageUrl;
    }

    public String getAdIconUrl() {
        return mIconImageUrl;
    }

    public String getAdSocialContext() {
        return mAdSocialContext;
    }

    public String getAdCallToAction() {
        return mCallToAction;
    }

    @Override
    public boolean isDownLoadApp() {
        return mIsDownloadApp;
    }

    public void setIsDownloadApp(@Nullable boolean isDownloadApp) {
        this.mIsDownloadApp = isDownloadApp;
    }

    public void setTitle(@NonNull String mTitle) {
        this.mTitle = mTitle;
    }

    public void setAdCoverImageUrl(@NonNull String mainImageUrl) {
        this.mMainImageUrl = mainImageUrl;
    }

    public void setAdIconUrl(@NonNull String iconImageUrl) {
        this.mIconImageUrl = iconImageUrl;
    }

    public void setAdCallToAction(@Nullable String callToAction) {
        this.mCallToAction = callToAction;
    }

    public void setAdSocialContext(@Nullable String adSocialContext) {
        this.mAdSocialContext = adSocialContext;
    }

    public void setAdBody(@Nullable String adDescription) {
        this.mAdDescription = adDescription;
    }

    public String getAdBody() {
        return this.mAdDescription;
    }

    public double getAdStarRating() {
        return mAdStartRate;
    }

    public void setAdStarRate(@Nullable double starRate) {
        this.mAdStartRate = starRate;
    }

    public boolean isPriority() {
        return mIsPriority;
    }

    public void setIsPriority(@Nullable boolean isPriority) {
        this.mIsPriority = isPriority;
    }

    public void setCacheTime(long cacheTime) {
        mCacheTime = cacheTime;
    }

    public void setExtraReportParams(Map<String, String> reportParams) {
        mExtraReportParams = reportParams;
    }

    public Map<String, String> getExtraReportParams() {
        return mExtraReportParams;
    }

    public void setAdTypeId(String id) {
        mAdType = id;
    }

    @Override
    public String getTypeId() {
        return mAdType;
    }

    @Override
    public boolean registerViewForInteraction(View view, View mediaView, @Nullable View adIconView, @Nullable List<View> clickableViews) {
        return false;
    }

    @Override
    public boolean hasExpired() {
        return (System.currentTimeMillis() - mCreateTime) >= mCacheTime;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public String getSource() {
        return mSource;
    }

    public void setIsHasDetailPage(boolean isHasDetailPage) {
        mIsHasDetailPage = isHasDetailPage;
    }

    @Override
    public View createDetailPage(INativeAd ad) {
        return null;
    }

    @Override
    public View createDetailPage() {
        return null;
    }

    @Override
    public boolean isHasDetailPage() {
        return mIsHasDetailPage;
    }

    @Override
    public void handleDetailClick() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void handleClick() {

    }
}
