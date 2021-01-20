package com.cmcm.ads;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cmcm.adsdk.banner.CMAdView;
import com.cmcm.adsdk.banner.CMBannerAdListener;
import com.cmcm.adsdk.banner.CMBannerAdSize;

public class BannerAdSampleActivity extends Activity implements CMBannerAdListener {

    private String mPlacementId = BuildConfig.IS_CN_VERSION ? "": "1094108";

    private CMAdView mAdView;
    private RelativeLayout bannerView;
    private EditText refresh_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        initView();
    }

    private void initView() {
        bannerView = (RelativeLayout) findViewById(R.id.banner_view);
        refresh_time = (EditText) findViewById(R.id.refresh_time);
        findViewById(R.id.btn_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });
        findViewById(R.id.btn_destroy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAdView != null){
                    mAdView.onDestroy();
					mAdView = null;
                }
            }
        });
        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerView.removeAllViews();
                bannerView.addView(mAdView);
                mAdView.prepare();
            }
        });
    }

    private void load(){
        String time = refresh_time.getText().toString();
        mAdView =  new CMAdView(this, mPlacementId, CMBannerAdSize.BANNER_300_250);
        mAdView.setAdListener(this);
        if(!TextUtils.isEmpty(time)){
            mAdView.setBannerAutorefreshTime(Integer.parseInt(time));
        }
        mAdView.loadAd();
    }

    @Override
    public void onAdLoaded(CMAdView adView) {
        Log.e("TAG", "Activity adLoaded :" + mAdView.hashCode() + ", adView:" +adView.hashCode());
        Toast.makeText(this, "onAdLoaded : "+ adView.getAdType(), Toast.LENGTH_SHORT).show();
        bannerView.removeAllViews();
        bannerView.addView(adView);
        mAdView.prepare();
    }

    @Override
    public void adFailedToLoad(CMAdView adView, int errorCode) {
        Toast.makeText(this, "adFailedToLoad,and errorCode = " + errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdClicked(CMAdView adView) {
        Toast.makeText(this, "onAdClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if(mAdView != null){
            mAdView.onDestroy();
        }
        super.onDestroy();
    }
}
