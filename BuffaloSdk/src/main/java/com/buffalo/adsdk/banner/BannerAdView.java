package com.buffalo.adsdk.banner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.buffalo.adsdk.CMAdError;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;
import com.buffalo.utils.Logger;
import com.buffalo.utils.NetworkUtil;

public class BannerAdView extends FrameLayout {
    private static final String TAG = "CMAdView";
    protected String posid;
    protected BannerAdSize adSize;
    protected BannerAdListener mBannerAdListener;
    private BannerAdManagerRequest managerRequest;
    protected Context mContext;

    private static final int DEFAULT_REFRESH_TIME_MILLISECONDS = 30000;  // 30s
    private Handler mHandler;
    private BroadcastReceiver mScreenStateReceiver;
    private final Runnable mRefreshRunnable;
    private long mRefreshTimeMillis;
    private int mScreenVisibility;
    private boolean mPreviousAutoRefreshSetting = true;
    private boolean mAutoRefreshEnabled = true;
    protected boolean isFirstLoaded = false;
    protected boolean mAdWasLoaded = false;
    protected boolean mIsViewDestroyed = false;
    private boolean mAdLoaded = false;

    public BannerAdView(Context context, String posid, BannerAdSize size) {
        super(context);
        this.posid = posid;
        this.adSize = size;
        this.mContext = context;

        mScreenVisibility = getVisibility();
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);

        registerScreenStateBroadcastReceiver();

        mRefreshRunnable = new Runnable() {
            public void run() {
                Logger.d(TAG, "banner refresh runnable execute :" + System.currentTimeMillis());
                internalLoadAd();
            }
        };
        mRefreshTimeMillis = DEFAULT_REFRESH_TIME_MILLISECONDS;
        mHandler = new Handler();

    }

    public String getAdType() {
        return managerRequest != null ? managerRequest.getAdType() : null;
    }

    public void setBannerAutorefreshTime(long refreshTimeMillis) {
        if (refreshTimeMillis < 10000 && refreshTimeMillis != 0) {
            refreshTimeMillis = 10000;
        }
        mRefreshTimeMillis = refreshTimeMillis;
        setBannerAutorefreshEnabled(refreshTimeMillis != 0);
    }

    public void setAdListener(BannerAdListener listener) {
        this.mBannerAdListener = listener;
    }

    public void loadAd() {
        Logger.i(TAG, "loadAd");
        if (mContext != null && !TextUtils.isEmpty(posid) && adSize != null) {
            internalLoadAd();
        } else {
            Logger.e(TAG, "params error ,context is null: " + (mContext == null)
                    + "or posid is empty:" + TextUtils.isEmpty(posid)
                    + "or banner adsize is null:" + (adSize == null));
            notifyFailed(CMAdError.PARAMS_ERROR);
        }
    }

    protected void internalLoadAd() {
        mAdWasLoaded = true;
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            Logger.d(TAG, "Can't load an ad because there is no network connectivity.");
            scheduleRefreshTimerIfEnabled();
            return;
        }
        //destroy the last view
        invalidateView();
        if (managerRequest == null) {
            managerRequest = new BannerAdManagerRequest(mContext, posid, adSize);
        }
        managerRequest.setAdListener(new MyBannerViewLoadListener());
        managerRequest.loadAd();
    }

    public void setBannerAutorefreshEnabled(boolean enabled) {
        setAutorefreshEnabled(enabled);
        mPreviousAutoRefreshSetting = enabled;
    }

    @Override
    protected void onWindowVisibilityChanged(final int visibility) {
        // Ignore transitions between View.GONE and View.INVISIBLE
        boolean flag = isScreenVisible(mScreenVisibility) != isScreenVisible(visibility);
        Logger.d(TAG, "window visibility:" + visibility + ",screen visibility:" + mScreenVisibility + ",flag:" + flag);
        if (flag) {
            mScreenVisibility = visibility;
            setAdVisibility(mScreenVisibility);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Logger.i(TAG, "on ad attach to window");
        scheduleRefreshTimerIfEnabled();
    }

    private boolean isScreenVisible(final int visibility) {
        return visibility == View.VISIBLE;
    }

    private void registerScreenStateBroadcastReceiver() {
        mScreenStateReceiver = new BroadcastReceiver() {
            private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
            private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
            private boolean isScreenOff = false;

            public void onReceive(final Context context, final Intent intent) {
                if (!isScreenVisible(mScreenVisibility) || intent == null) {
                    return;
                }

                String action = intent.getAction();
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    isScreenOff = false;
                    setAdVisibility(View.VISIBLE);
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    if (!isScreenOff) {
                        setAdVisibility(View.GONE);
                        isScreenOff = true;
                    }
                } else if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason) && !mAdLoaded) {
                    setAdVisibility(View.GONE);
                }
            }
        };

        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);//HomeKey
            mContext.getApplicationContext().registerReceiver(mScreenStateReceiver, filter);
        } catch (Exception IllegalArgumentException) {
            Logger.d(TAG, "Failed to register screen state broadcast receiver.");
        }
    }

    private void setAdVisibility(final int visibility) {
        if (isScreenVisible(visibility)) {
            unpauseRefresh();
        } else {
            pauseRefresh();
        }
    }

    private void pauseRefresh() {
        mPreviousAutoRefreshSetting = mAutoRefreshEnabled;
        setAutorefreshEnabled(false);
    }

    private void unpauseRefresh() {
        setAutorefreshEnabled(mPreviousAutoRefreshSetting);
    }

    private void setAutorefreshEnabled(boolean enabled) {
        final boolean autorefreshChanged = mAdWasLoaded && (mAutoRefreshEnabled != enabled);
        if (autorefreshChanged) {
            Logger.d(TAG, "Refresh " + ((enabled) ? "enabled" : "disabled") + " for posid :" + posid);
        }

        mAutoRefreshEnabled = enabled;
        if (mAdWasLoaded && mAutoRefreshEnabled) {
            scheduleRefreshTimerIfEnabled();
        } else if (!mAutoRefreshEnabled) {
            cancelRefreshTimer();
        }
    }

    private void cancelRefreshTimer() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRefreshRunnable);
        }
    }

    protected void scheduleRefreshTimerIfEnabled() {
        cancelRefreshTimer();
        if (mAutoRefreshEnabled && !mIsViewDestroyed && mRefreshTimeMillis > 0) {
            Logger.d(TAG, "banner record refresh time :" + System.currentTimeMillis());
            mHandler.postDelayed(mRefreshRunnable, mRefreshTimeMillis);
        }
    }

    private void unregisterScreenStateBroadcastReceiver() {
        try {
            mContext.getApplicationContext().unregisterReceiver(mScreenStateReceiver);
        } catch (Exception IllegalArgumentException) {
            Logger.d(TAG, "Failed to unregister screen state broadcast receiver (never registered).");
        }
    }

    public void prepare() {
        if (managerRequest != null) {
            managerRequest.prepare(this);
        }
    }

    class MyBannerViewLoadListener implements INativeAdLoaderListener {

        @Override
        public void adLoaded() {
            mAdLoaded = true;
            notifyLoaded();
            if (isFirstLoaded) {
                scheduleRefreshTimerIfEnabled();
            }
        }

        @Override
        public void adFailedToLoad(int errorCode) {
            Logger.i(TAG, "onAdLoadFailed");
            mAdLoaded = false;
            notifyFailed(errorCode);
            scheduleRefreshTimerIfEnabled();
        }

        @Override
        public void adClicked(INativeAd nativeAd) {
            Logger.i(TAG, "onAdClicked");
            if (mBannerAdListener != null) {
                mBannerAdListener.onAdClicked(BannerAdView.this);
            }
        }
    }

    private void notifyLoaded() {
        if (managerRequest != null) {
            Object obj = managerRequest.getAdObject();
            if (obj != null && obj instanceof View) {
                BannerAdView.this.removeAllViews();
                BannerAdView.this.addView((View) obj);
                if (mBannerAdListener != null) {
                    mBannerAdListener.onAdLoaded(BannerAdView.this);
                    return;
                }
            }
        }
        notifyFailed(CMAdError.NO_VALID_DATA_ERROR);
    }

    private void notifyFailed(final int errorCode) {
        if (mBannerAdListener != null) {
            mBannerAdListener.adFailedToLoad(BannerAdView.this, errorCode);
        }
    }

    protected void invalidateView() {
        if (managerRequest != null) {
            isFirstLoaded = true;
            managerRequest.destroy();
        }
    }

    public void onDestroy() {
        Logger.i(TAG, "onDestroy");
        unregisterScreenStateBroadcastReceiver();
        mBannerAdListener = null;
        invalidateView();
        mIsViewDestroyed = true;
    }

}

