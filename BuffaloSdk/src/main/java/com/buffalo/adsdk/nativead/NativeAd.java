package com.buffalo.adsdk.nativead;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.buffalo.adsdk.report.ReportFactory;
import com.buffalo.adsdk.utils.DownloadCheckDialog;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAd.ImpressionListener;
import com.buffalo.baseapi.ads.IVideoAdapter;
import com.buffalo.utils.Logger;
import com.buffalo.utils.UniReport;
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
    private int mRcvReportRes;
    private int mPegReportRes;
    private String mReportPkgName;
    private String mPlacementId;
    private String mAdTypeName;
    private boolean mIsOrionAd;
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
        if (extras.containsKey(KEY_RCV_REPORT_RES)) {
            setRcvReportRes((Integer) extras.get(KEY_RCV_REPORT_RES));
        }
//        if(extras.containsKey(KEY_PEG_REPORT_RES)){
//            setPegReportRes((Integer) extras.get(KEY_PEG_REPORT_RES));
//        }
        if (extras.containsKey(KEY_REPORT_PKGNAME)) {
            setReportPkgName((String) extras.get(KEY_REPORT_PKGNAME));
        }
        if (extras.containsKey(KEY_PLACEMENT_ID)) {
            setPlacementId((String) extras.get(KEY_PLACEMENT_ID));
        }
        if (extras.containsKey(KEY_AD_TYPE_NAME)) {
            mAdTypeName = (String) extras.get(KEY_AD_TYPE_NAME);
        }
        if (extras.containsKey(KEY_IS_ORIONAD)) {
            mIsOrionAd = (boolean) extras.get(KEY_IS_ORIONAD);
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
        setExtPics(ad.getExtPics());
        // FIXME: 2016/7/12 
//        setMpaModule(ad.getMpaModule());
        setIsHasDetailPage(ad.isHasDetailPage());
        setSource(ad.getSource());
        setAdTypeId(ad.getTypeId());
        mAd.setImpressionListener(this);
    }

    public int getRcvReportRes() {
        return mRcvReportRes;
    }

    public int getPegReportRes() {
        return mPegReportRes;
    }

    public String getReportPkgName() {
        return mReportPkgName;
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

    // FIXME: 2016/7/12 
//    @Override
//    public MpaModule getMpaModule() {
//        return mAd.getMpaModule();
//    }

    @Override
    public String getSource() {
        return mAd.getSource();
    }

    @Override
    public String getRawString(int operation) {
        if (mAd != null) {
            return mAd.getRawString(operation);
        }
        return "";
    }

    @Override
    public boolean registerViewForInteraction_withExtraReportParams(View view, Map<String, String> reportParam) {
        mRegisterTimes++;
        mExtraReportParams = reportParam;
        mAd.setExtraReportParams(reportParam);

        //如果需要主动添加点击事件会返回false
//        if(Const.isPicksAd(mAd.getAdTypeName())){
//            mAd.registerViewForInteraction_withExtraReportParams(view, reportParam);
//            mAd.setAdOnClickListener(this);
//        }
        if (!mAd.registerViewForInteraction(view)) {
            mAdView = view;
            setListener(view, this, this);
        } else {
            mAd.setAdOnClickListener(this);
        }

        String rawString = getRawString(1);

        //FIXME 在UniReport中已经屏蔽了此次上报
        UniReport.report(ReportFactory.INSERTVIEW, mReportPkgName, mPosid, mRcvReportRes
                , addDupReportExtra(false, mHasReportInsertImpression, getExtraReportParams()),
                mPlacementId, isNativeAd(), rawString, mIsOrionAd);
        mHasReportInsertImpression = true;

        if (mReportmodel == null) {
            mReportmodel = new ViewShowReporter.Model(mReportPkgName, mPosid, mRcvReportRes, mPegReportRes
                    , getExtraReportParams(), mPlacementId, rawString, mIsOrionAd, this);
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
            boolean checkDownload = AdManager.sIsCnVersion && mAd.isDownLoadApp();
            if (checkDownload) {
                DownloadCheckDialog.showDialog(mContext, new DownloadCheckDialog.DownloadCheckListener() {

                    @Override
                    public void handleDownload() {
                        handleClick();
                    }

                    @Override
                    public void cancelDownload() {
                        //取消下载
                    }
                });
            } else {
                handleClick();
            }
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

        //picks 和fb上报需要Object字段
        String rawString = getRawString(1);
        UniReport.report(ReportFactory.VIEW, mReportPkgName, mPosid, mRcvReportRes, extraReportParams,
                mPlacementId, isNativeAd(), rawString, mIsOrionAd);

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

    //    //For report
    public void setRcvReportRes(@Nullable int res) {
        this.mRcvReportRes = res;
    }

    public void setPegReportRes(@Nullable int res) {
        this.mPegReportRes = res;
    }

    public void setReportPkgName(@Nullable String pkgName) {
        this.mReportPkgName = pkgName;
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
