package com.buffalo.adsdk.splashad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.R;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.utils.Logger;

public class NativeSplashAdView extends RelativeLayout implements View.OnClickListener, CountdownView.OnCountdownListener {
    protected Context mContext;
    private NativeSplashAd.SplashAdListener mListener;
    private boolean mIsShowCountDownTime;
    private RelativeLayout mMainContainer;
    private CountdownView mCountDownView;
    private TextView mSkip;
    private View adView;
    private boolean mIsEndImpression;
    private boolean mIsShowSpreadSign;
    private int mShowMills;

    public NativeSplashAdView(Context context, NativeSplashAd.SplashAdListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
    }

    public boolean build(View adView, INativeAd ad) {
        boolean isRet = false;
        if (adView != null) {
            isRet = true;
            initLayout();
            addAdView(adView, ad);
        }
        return isRet;
    }

    private void initLayout() {
        //广告内容的容器
        LayoutInflater layoutIflater = LayoutInflater.from(mContext);
        View rootView = layoutIflater.inflate(R.layout.layout_native_splash, this, false);
        addView(rootView);
        mMainContainer = (RelativeLayout) rootView.findViewById(R.id.native_splash_ad_body);
        mMainContainer.setOnClickListener(this);
        if (mIsShowCountDownTime) {
            //倒计时
            LinearLayout skipViewContainer = (LinearLayout) rootView.findViewById(R.id.native_splash_ad_skip_view);
            skipViewContainer.setVisibility(VISIBLE);
            mCountDownView = (CountdownView) rootView.findViewById(R.id.count_down_view);
            mSkip = (TextView) rootView.findViewById(R.id.native_splash_skip);
            mSkip.setOnClickListener(this);
            mCountDownView.setCountNum(mShowMills);
            mCountDownView.setOnCountdownListener(this);
        } else {
            //虽然不可见，但是必须要启动，否则收不到倒计时结束回调
            mCountDownView = (CountdownView) rootView.findViewById(R.id.count_down_view);
            mCountDownView.setCountNum(mShowMills);
            mCountDownView.setOnCountdownListener(this);
        }

        if (mIsShowSpreadSign) {
            TextView spreadSignView = (TextView) rootView.findViewById(R.id.native_splash_ad_sponsored);
            spreadSignView.setVisibility(VISIBLE);
        }
    }

    public void addAdView(View view, INativeAd ad) {
        if (null != mMainContainer && null != ad) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            adView = view;
            if (ad.getAdTypeName().startsWith(Const.KEY_AB)) {
                mMainContainer.setOnClickListener(null);
            }
            mMainContainer.setBackgroundResource(R.drawable.bg_native_splash);
            mMainContainer.addView(view, lp);
            ad.registerViewForInteraction(view);
        }
    }

    public void setShowMills(int showTimeMills) {
        //TODO judge time save
        mShowMills = showTimeMills;
    }

    public void setShowSpreadSign(boolean isShow) {
        mIsShowSpreadSign = isShow;
    }

    public void setShowCountDownTime(boolean isShow) {
        mIsShowCountDownTime = isShow;
    }

    private boolean mHaveImpression = false;

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Logger.i(Const.TAG, "native splash adView VisibilityChanged: " + visibility);
        if (mHaveImpression) {
            return;
        }
        if (visibility == VISIBLE) {
            if (!mIsEndImpression) {
                if (mCountDownView != null) {
                    mCountDownView.start();
                }
                if (mListener != null) {
                    Logger.i(Const.TAG, "native splash adView VisibilityChanged: onAdImpression");
                    mListener.onAdImpression();
                    mHaveImpression = true;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.native_splash_skip) {
            if (mListener != null) {
                mListener.onSkipClick();
            }
        } else if (id == R.id.native_splash_ad_body) {
            if (adView != null) {
                adView.performClick();
            }
        }
        mIsEndImpression = true;
        if (mCountDownView != null) {
            mCountDownView.stop();
        }
    }

    @Override
    public void onCountdownFinish() {
        if (mListener != null && !mIsEndImpression) {
            mIsEndImpression = true;
            Logger.i(Const.TAG, "native splash adView onCountdownFinish: onEndAdImpression");
            mListener.onEndAdImpression();
        }
    }

    public void destory() {
        if (mMainContainer != null) {
            mMainContainer.removeAllViews();
            mMainContainer = null;
        }
        if (mCountDownView != null) {
            mCountDownView.stop();
        }
    }
}
