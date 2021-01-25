package com.buffalo.adsdk.nativead;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.buffalo.adsdk.base.BaseNativeAd;
import com.buffalo.adsdk.report.AdReporter;
import com.buffalo.adsdk.report.ReportFactory;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAd.ImpressionListener;
import com.buffalo.baseapi.ads.IVideoAdapter;
import com.buffalo.utils.Logger;
import com.buffalo.utils.ViewShowReporter;

import java.util.HashMap;
import java.util.Map;

public class NativeAd extends BaseNativeAd implements
        View.OnClickListener, View.OnTouchListener, ImpressionListener, INativeAd.IAdOnClickListener, Comparable<NativeAd> {

    final Context mContext;
    final BaseNativeAd mAd;

    private IAdOnClickListener mAdClickListener = null;
    private IAdOnClickListener mAdapterAdClickListener = null;

    private String mPosid;
    private View mAdView;
    protected boolean mHasReportCallBackImpression = false;
    private boolean mHasReportUserImpression;
    private boolean mHasReportInsertImpression;
    private boolean mHasReportClick;
    private String mPlacementId;
    private String mAdTypeName;
    private int mRegisterTimes;
    private ViewShowReporter.Model mReportmodel;
    private int mAdPriorityIndex;

    /**
     * 设置此广告是复用
     */
    public void setReUseAd() {
        this.mHasReportCallBackImpression = false;
        this.mHasReportUserImpression = false;
        this.mHasReportInsertImpression = false;
//        this.mHasReportClick = false;
    }


    public NativeAd(Context context, IAdOnClickListener adClickListener,
                    Map<String, Object> extras, BaseNativeAd ad) {
        super();
        mContext = context;
        mAd = ad;
        mAdapterAdClickListener = adClickListener;
        mRegisterTimes = 0;
        if (extras.containsKey(KEY_CACHE_TIME)) {
            mAd.setCacheTime((Long) extras.get(KEY_CACHE_TIME));
            setCacheTime((Long) extras.get(KEY_CACHE_TIME));
        }
        if (extras.containsKey(KEY_JUHE_POSID)) {
            setJuhePosid((String) extras.get(KEY_JUHE_POSID));
        }
        if (extras.containsKey(KEY_PLACEMENT_ID)) {
            setPlacementId((String) extras.get(KEY_PLACEMENT_ID));
        }
        if (extras.containsKey(KEY_AD_TYPE_NAME)) {
            mAdTypeName = (String) extras.get(KEY_AD_TYPE_NAME);
        }

        setTitle(ad.getAdTitle());
        setAdCoverImageUrl(ad.getAdCoverImageUrl());
        setAdIconUrl(ad.getAdIconUrl());
        setAdSocialContext(ad.getAdSocialContext());
        setAdCallToAction(ad.getAdCallToAction());
        setAdBody(ad.getAdBody());
        setAdStarRate(ad.getAdStarRating());
        setIsDownloadApp(ad.isDownLoadApp());
        setIsPriority(ad.isPriority());
        setIsHasDetailPage(ad.isHasDetailPage());
        setSource(ad.getSource());
        setAdTypeId(ad.getTypeId());
        mAd.setImpressionListener(this);
    }

    @Override
    public boolean isHasDetailPage() {
        return mAd.isHasDetailPage();
    }

    @Override
    public String getAdTypeName() {

        return !TextUtils.isEmpty(mAdTypeName) ? mAdTypeName : mAd.getAdTypeName();
    }

    public void setAdPriorityIndex(int mAdPriorityIndex) {
        this.mAdPriorityIndex = mAdPriorityIndex;
    }

    public int getAdPriorityIndex() {
        return mAdPriorityIndex;
    }

    @Override
    public boolean registerViewForInteraction(View view) {
        return registerViewForInteraction_withExtraReportParams(view, null);
    }

    @Override
    public String getSource() {
        return mAd.getSource();
    }

    @Override
    public boolean registerViewForInteraction_withExtraReportParams(View view, Map<String, String> reportParam) {
        mRegisterTimes++;
        mExtraReportParams = reportParam;
        mAd.setExtraReportParams(reportParam);

        if (!mAd.registerViewForInteraction(view)) {
            mAdView = view;
            setListener(view, this, this);
        } else {
            mAd.setAdOnClickListener(this);
        }

        AdReporter.report(ReportFactory.INSERTVIEW, mPosid
                , addDupReportExtra(false, mHasReportInsertImpression, getExtraReportParams()),
                mPlacementId);
        mHasReportInsertImpression = true;

        if (mReportmodel == null) {
            mReportmodel = new ViewShowReporter.Model(mPosid, getExtraReportParams(), mPlacementId, this);
        }
        ViewShowReporter.add(mReportmodel, view);
        return true;
    }

    @Override
    public void unregisterView() {
        mAd.unregisterView();

        if (mAdView != null) {
            setListener(mAdView, null, null);
            mAdView = null;
        }
        if (mAd != null) {
            mAd.setAdOnClickListener(null);
        }
        ViewShowReporter.unRegister(mReportmodel);
    }

    public boolean hasReportUserImpression() {
        return mHasReportUserImpression;
    }

    public void setHasReportUserImpression(boolean value) {
        this.mHasReportUserImpression = value;
    }

    public boolean hasReportClick() {
        return mHasReportClick;
    }

    public void setHasReportClick(boolean value) {
        this.mHasReportClick = value;
    }

    @Override
    public Object getAdObject() {
        return mAd.getAdObject();
    }

    @Override
    public void handleClick() {
        mAd.handleClick();
        onAdClick(this);
    }

    @Override
    public void handleDetailClick() {
        if (null == mAdClickDelegate || mAdClickDelegate.handleClick(true)) {
            mAd.handleDetailClick();
        } else {
            //表示点击详情页行为外部已经处理完毕，不需要内部处理
            Logger.e("ClickDelegate", "handClickDetail has execute out of sdk");
        }
    }

    @Override
    public boolean hasExpired() {
        // boolean isExpired = (System.currentTimeMillis() - mAd.mCreateTime) >= mAd.mCacheTime;
        boolean isExpiredByAd = mAd.hasExpired();

        return isExpiredByAd;
    }

    @Override
    public void onClick(View view) {
        boolean isNeedExecuteClick = (null == mAdClickDelegate || mAdClickDelegate.handleClick(false));
        if (isNeedExecuteClick) {
            handleClick();
        } else {
            //表示点击行为外部已经处理完毕，不需要内部处理
            Logger.e("ClickDelegate", "handClick has execute out of sdk");
        }
    }

    @Override
    public void onAdClick(INativeAd nativeAd) {
        if (mAdapterAdClickListener != null) {
            mAdapterAdClickListener.onAdClick(this);
        }
        if (mAdClickListener != null) {
            mAdClickListener.onAdClick(this);
        }
    }

    @Override
    public void setAdOnClickListener(IAdOnClickListener adOnClickListener) {
        mAdClickListener = adOnClickListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onLoggingImpression() {
        recordImpression();
    }

    @Override
    public View createDetailPage() {
        return mAd.createDetailPage(this);
    }

    private void recordImpression() {
        Map<String, String> extraReportParams = addDupReportExtra(false, mHasReportCallBackImpression, getExtraReportParams());

        AdReporter.report(ReportFactory.VIEW, mPosid, extraReportParams, mPlacementId);

        if (mImpressionListener != null) {
            mImpressionListener.onLoggingImpression();
        }
        mHasReportCallBackImpression = true;
    }

    public Map<String, String> addDupReportExtra(boolean isClick, boolean isReported, Map<String, String> extraReportParams) {
        String dupState = ReportFactory.EXTRA_VALUE_NEW;

        if (!isClick && (mRegisterTimes > 1)) {
            dupState = ReportFactory.EXTRA_VALUE_REUSED;
        }

        if (extraReportParams == null) {
            extraReportParams = new HashMap<String, String>();
        }
        extraReportParams.put(ReportFactory.EXTRA_KEY_DUPLICATE, dupState);
        return extraReportParams;
    }

    public void setJuhePosid(@NonNull String posid) {
        mPosid = posid;
    }

    public void setPlacementId(@Nullable String placementId) {
        this.mPlacementId = placementId;
    }

    public void setListener(View view, View.OnClickListener onClickListener, @Nullable View.OnTouchListener touchListener) {
        if (view == null) {
            return;
        }
        view.setOnClickListener(onClickListener);
        view.setOnTouchListener(touchListener);
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View childView = vp.getChildAt(i);
                setListener(childView, onClickListener, touchListener);
            }
        }
    }

    @Override
    public boolean isNativeAd() {
        if (mAd != null) {
            return mAd.isNativeAd();
        }
        return false;
    }

    @Override
    public boolean registerViewForInteraction_withListView(IVideoAdapter adapter, View listView, ViewGroup viewGroup) {
        if (mAd != null) {
            // Fix：video adapter 无点击回调,不能上报rcv点击
            mAd.setAdOnClickListener(this);
            return mAd.registerViewForInteraction_withListView(adapter, listView, viewGroup);
        }
        return false;
    }


    @Override
    public void onResume() {
        if (mAd != null) {
            mAd.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mAd != null) {
            mAd.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mAd != null) {
            mAd.onDestroy();
        }
    }

    @Override
    public int compareTo(NativeAd another) {
        if (null != another) {
            return getAdPriorityIndex() - another.getAdPriorityIndex();
        }
        return 0;
    }
}
