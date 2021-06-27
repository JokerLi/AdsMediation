package com.buffalo.ads.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.buffalo.ads.R;
import com.buffalo.adsdk.nativead.NativeAdManager;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;

public class NativeAdCaseView extends RelativeLayout implements View.OnClickListener {
    private static final String NATIVE_POSID = "1094101";
    private Context mContext;
    private NativeAdManager mNativeAdManager;
    private RelativeLayout mNativeContainer;
    private View mRootView;
    private AdClickListener mAdapterListener;

    public NativeAdCaseView(Context context) {
        this(context, null);
    }

    public NativeAdCaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NativeAdCaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();

    }

    private void initView() {
        LayoutInflater layoutIflater = LayoutInflater.from(mContext);
        mRootView = layoutIflater.inflate(R.layout.view_native_ad_show_case, null);
        addView(mRootView);
        mNativeContainer = (RelativeLayout) mRootView.findViewById(R.id.native_container);
        mRootView.findViewById(R.id.btn_preload).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_load).setOnClickListener(this);
        initAdManager();
    }

    private void initAdManager() {
        mNativeAdManager = new NativeAdManager(mContext, NATIVE_POSID);
        mNativeAdManager.setNativeAdListener(new INativeAdLoaderListener() {
            @Override
            public void adLoaded() {
                INativeAd ad = mNativeAdManager.getAd();
                if (ad == null) {
                    return;
                }
                View adView = AdViewHelper.createAdView(mContext, ad);
                ad.registerViewForInteraction(adView, null, null, null);
                mNativeContainer.removeAllViews();
                mNativeContainer.addView(adView);
                Toast.makeText(mContext, "Native Ad adLoaded ",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void adFailedToLoad(int errorcode) {
                Toast.makeText(mContext, "Native Ad adFailedToLoad errorcode:" + errorcode,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void adClicked(INativeAd nativeAd) {
                Toast.makeText(mContext, "Native Ad adClicked ", Toast.LENGTH_SHORT).show();
                if (mAdapterListener != null) {
                    mAdapterListener.onAdClicked(nativeAd);
                }
            }
        });
    }


    private void loadNativeAd() {
        if (mNativeAdManager != null) {
            mNativeAdManager.loadAd();
        }
    }

    private void preloadNativeAd() {
        if (mNativeAdManager != null) {
            mNativeAdManager.preloadAd();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_preload) {
            preloadNativeAd();
        } else if (view.getId() == R.id.btn_load) {
            loadNativeAd();
        }
    }

    public void onDestroy() {
        mNativeAdManager = null;
        mNativeContainer = null;
    }


    public void setAdapterListener(AdClickListener listener) {
        this.mAdapterListener = listener;
    }
}
