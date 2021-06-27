package com.buffalo.ads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.buffalo.ads.ui.AdViewHelper;
import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.RequestParams;
import com.buffalo.adsdk.nativead.NativeAd;
import com.buffalo.adsdk.nativead.NativeAdManager;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;


public class NativeAdSampleActivity extends Activity implements OnClickListener {
    /* 广告 Native大卡样式 */
    private NativeAdManager nativeAdManager;
    private FrameLayout nativeAdContainer;
    /* 广告 Native大卡样式 */
    private Button loadAdButton;
    private String mAdPosId = "10000100";

    private View mAdView = null;
    //用户记录功能页面的PV的ID，可以自定义
    public static final int PAGE_UNIT_ID = 10001;
    RequestParams params = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manager);
        params = new RequestParams();
        nativeAdManager = new NativeAdManager(this, mAdPosId);
        nativeAdManager.setRequestParams(params);
        nativeAdContainer = findViewById(R.id.big_ad_container);
        loadAdButton = findViewById(R.id.btn_load);
        loadAdButton.setOnClickListener(this);
        findViewById(R.id.getad).setOnClickListener(this);
        findViewById(R.id.btn_load_seq).setOnClickListener(this);

        initNativeAd();
        //使用此类可以记录功能页面PV，注意：使用前确保聚合是已经初始化的
        AdManager.reportPV(PAGE_UNIT_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load:
                requestNativeAd(false);
                break;
            case R.id.btn_load_seq:
                requestNativeAd(true);
                break;
            case R.id.getad:
                getAd();
                break;
            default:
                break;
        }
    }

    private void requestNativeAd(boolean isPreload) {
        if (isPreload) {
            nativeAdManager.preloadAd();
        } else {
            nativeAdManager.loadAd();
        }
    }

    private void getAd() {
        if (nativeAdManager != null) {
            INativeAd ad = nativeAdManager.getAd();
            if (ad == null) {
                Toast.makeText(NativeAdSampleActivity.this,
                        "no native ad loaded!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(NativeAdSampleActivity.this,
                    "Ad index is: " + ((NativeAd) ad).getAdPriorityIndex(), Toast.LENGTH_SHORT).show();

            //如果只要实现某个类型的广告，需要判断广告类型通过ad.getAdTypeName()
            ad.setAdClickDelegate(isDetailPageClick -> {
                //true表示外部不做处理，false表示外部处理
                return !isDetailPageClick;
            });

            ad.setAdOnClickListener(nativeAd ->
                    Toast.makeText(NativeAdSampleActivity.this, "setAdOnClickListener", Toast.LENGTH_SHORT).show());

            ad.setImpressionListener(() ->
                    Toast.makeText(NativeAdSampleActivity.this, "onLoggingImpression", Toast.LENGTH_SHORT).show());
            if (mAdView != null) {
                // 把旧的广告view从广告容器中移除
                nativeAdContainer.removeView(mAdView);
            }
            mAdView = AdViewHelper.createAdView(getApplicationContext(), ad);
            nativeAdContainer.addView(mAdView);

            ad.registerViewForInteraction(mAdView, null, null, null);
        }
    }

    private void initNativeAd() {
        nativeAdManager.setNativeAdListener(new INativeAdLoaderListener() {
            @Override
            public void adLoaded() {
                Toast.makeText(NativeAdSampleActivity.this, "adLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void adFailedToLoad(int i) {
                Toast.makeText(NativeAdSampleActivity.this, "Ad failed to load errorCode:" + i,
                        Toast.LENGTH_SHORT).show();
            }


            @Override
            public void adClicked(INativeAd ad) {
                Toast.makeText(NativeAdSampleActivity.this, "adClicked",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
