package com.buffalo.ads;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.buffalo.adsdk.banner.BannerAdListener;
import com.buffalo.adsdk.banner.BannerAdSize;
import com.buffalo.adsdk.banner.BannerAdView;

public class BannerAdSampleActivity extends Activity implements BannerAdListener {
    private String mPlacementId = "1094108";

    private BannerAdView mAdView;
    private RelativeLayout bannerView;
    private EditText refresh_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        initView();
    }

    private void initView() {
        bannerView = findViewById(R.id.banner_view);
        refresh_time = findViewById(R.id.refresh_time);
        findViewById(R.id.btn_load).setOnClickListener(view -> load());

        findViewById(R.id.btn_destroy).setOnClickListener(view -> {
            if (mAdView != null) {
                mAdView.onDestroy();
                mAdView = null;
            }
        });
    }

    private void load() {
        String time = refresh_time.getText().toString();
        mAdView = new BannerAdView(this, mPlacementId, BannerAdSize.BANNER_300_250);
        mAdView.setAdListener(this);
        if (!TextUtils.isEmpty(time)) {
            mAdView.setBannerAutorefreshTime(Integer.parseInt(time));
        }
        mAdView.loadAd();
    }

    @Override
    public void onAdLoaded(BannerAdView adView) {
        Log.e("TAG", "Activity adLoaded :" + mAdView.hashCode() + ", adView:" + adView.hashCode());
        Toast.makeText(this, "onAdLoaded : " + adView.getAdType(), Toast.LENGTH_SHORT).show();
        bannerView.removeAllViews();
        bannerView.addView(adView);
        mAdView.prepare();
    }

    @Override
    public void onAdFailed(BannerAdView adView, int errorCode) {
        Toast.makeText(this, "adFailedToLoad,and errorCode = " + errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdClicked(BannerAdView adView) {
        Toast.makeText(this, "onAdClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.onDestroy();
        }
        super.onDestroy();
    }
}
