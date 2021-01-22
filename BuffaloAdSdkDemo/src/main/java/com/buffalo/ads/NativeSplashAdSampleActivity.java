package com.buffalo.ads;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.buffalo.adsdk.splashad.NativeSplashAd;
import com.buffalo.adsdk.splashad.NativeSplashAdView;

public class NativeSplashAdSampleActivity extends Activity implements View.OnClickListener {
    private String mAdPosid = "1094101";
    private static final String TAG = "NativeSplashAd";
    private RelativeLayout mContainer;
    private RelativeLayout mSetContainer;
    private CheckBox mIsSetShowTime;
    private CheckBox mIsShowCount;
    private CheckBox mIsShowAdTag;
    private CheckBox mIsSetTimeout;
    private EditText mShowTime;
    private EditText mTimeout;
    private Button mLoadAd;
    private boolean mHasJump = false;
    private NativeSplashAd mNativeSplashAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_native_splash_ad);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSetContainer = (RelativeLayout) findViewById(R.id.native_splash_top);
        mContainer = (RelativeLayout) findViewById(R.id.splash_brand_container);
        mIsSetShowTime = (CheckBox) findViewById(R.id.is_show_time);
        mIsShowCount = (CheckBox) findViewById(R.id.is_show_countdown);
        mIsShowAdTag = (CheckBox) findViewById(R.id.is_show_ad);
        mIsSetTimeout = (CheckBox) findViewById(R.id.is_show_out_time);
        mShowTime = (EditText) findViewById(R.id.tx_show_time);
        mTimeout = (EditText) findViewById(R.id.tx_show_out_time);
        mLoadAd = (Button) findViewById(R.id.btn_splash_load);
        mLoadAd.setOnClickListener(this);
    }

    private void loadAd() {
        mContainer.removeAllViews();
        if (mNativeSplashAd != null && mNativeSplashAd.isValid()) {
            NativeSplashAdView splashView = mNativeSplashAd.createNativeSplashView();
            mContainer.removeAllViews();
            mContainer.addView(splashView);
        } else {
            doLoadAd();
        }
    }

    private void doLoadAd() {
        if (mNativeSplashAd == null) {
            mNativeSplashAd = new NativeSplashAd(this, mAdPosid, new NativeSplashAd.SplashAdListener() {
                @Override
                public void onLoadSuccess() {
                    Log.e("SplashBrandActivity", "brands splash onLoadSuccess");
                    if (mNativeSplashAd.isValid()) {
                        NativeSplashAdView splashView = mNativeSplashAd.createNativeSplashView();
                        mContainer.removeAllViews();
                        mContainer.addView(splashView);
                    }
                }

                @Override
                public void onAdImpression() {
                    Log.e("SplashBrandActivity", "brands splash onAdImpression");
                }

                @Override
                public void onEndAdImpression() {
                    Log.e("SplashBrandActivity", "brands splash onEndAdImpression");
                    jump();
                }

                @Override
                public void onClick() {
                    Log.e("SplashBrandActivity", "brands splash onClick");
                    jump();
                }

                @Override
                public void onSkipClick() {
                    Log.e("SplashBrandActivity", "brands splash onSkipClick");
                    jump();
                }

                @Override
                public void onFailed(int resultCode) {
                    Log.e("SplashBrandActivity", "brands splash onFailed resultCode = " + resultCode);
                    jump();
                }
            });
        }
        if (mIsSetShowTime.isChecked()) {
            String showTime = mShowTime.getText().toString().trim();
            if (!TextUtils.isEmpty(showTime)) {
                Log.d(TAG, "showTime: " + showTime);
                mNativeSplashAd.setAdShowTimeSecond(Integer.valueOf(showTime));
            }
        }
        mNativeSplashAd.setIsShowCountDownTime(mIsShowCount.isChecked());
        if (mIsSetTimeout.isChecked()) {
            String timeout = mTimeout.getText().toString().trim();
            if (!TextUtils.isEmpty(timeout)) {
                Log.d(TAG, "timeout: " + timeout);
                mNativeSplashAd.setLoadTimeOutMilliSecond(Integer.valueOf(timeout));
            }
        }
        mNativeSplashAd.setShowSpreadSign(mIsShowAdTag.isChecked());
        mNativeSplashAd.load();
        mHasJump = false;
    }

    private void jump() {
//        if (!mHasJump) {
//            mHasJump = true;
//            Log.e("SplashNativeActivity","splash  jump");
//            this.startActivity(new Intent(this, WelComeActivity.class));
//            this.finish();
//        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_splash_load:
                loadAd();
                if (mSetContainer != null) {
                    mSetContainer.setVisibility(View.GONE);
                }
                break;
        }
    }
}
