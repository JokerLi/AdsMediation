package com.cmcm.ads;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.cmcm.adsdk.interstitial.InterstitialAdCallBack;
import com.cmcm.adsdk.interstitial.InterstitialAdManager;

public class InterstitalAdSampleActivity extends Activity {
	private static final String TAG = "InterstitalAdSampleActivity";
	private InterstitialAdManager interstitialAdManager;
	private Button showBtn;
	private Button loadBtn;
	private String posid = BuildConfig.IS_CN_VERSION ? "1096105":"1094104";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interstitial);
		// 初始化插屏广告

		showBtn = (Button) findViewById(R.id.btn_show);
		loadBtn = (Button)findViewById(R.id.btn_load);
//		((CheckBox)findViewById(R.id.cb_over_enable_click)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (interstitialAdManager != null) {
//					interstitialAdManager.setInterstialOverClickEnable(isChecked);
//				}
//			}
//		});
		loadBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requestInstitialAd();
			}
		});
		showBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(interstitialAdManager != null && interstitialAdManager.isReady()) {
					interstitialAdManager.showAd();
				}
			}
		});
	}


	//	// 请求插屏广告
	private void requestInstitialAd() {
//		 第一个参数：activity
//		 第二个参数：插屏的广告位id
		if(interstitialAdManager == null) {
			interstitialAdManager = new InterstitialAdManager(this, posid);
		}
		// 需要回调可以设置回调接口，如果不需要可以不设置,展示的逻辑开发者可以根据需求自定义
		interstitialAdManager
				.setInterstitialCallBack(new InterstitialAdCallBack() {
					@Override
					public void onAdLoadFailed(int errorCode) {
						Toast.makeText(InterstitalAdSampleActivity.this, "interstitialAd load Failed errorcode:"+errorCode,Toast.LENGTH_LONG).show();
					}

					@Override
					public void onAdLoaded() {
						Toast.makeText(InterstitalAdSampleActivity.this, "interstitialAd load success",Toast.LENGTH_LONG).show();
					}

					@Override
					public void onAdClicked() {
						Toast.makeText(InterstitalAdSampleActivity.this, "interstitialAd click",Toast.LENGTH_LONG).show();
					}

					@Override
					public void onAdDisplayed() {
						Toast.makeText(InterstitalAdSampleActivity.this, "interstitialAd onAdDisplayed",Toast.LENGTH_LONG).show();
					}

					@Override
					public void onAdDismissed() {
						Toast.makeText(InterstitalAdSampleActivity.this, "interstitialAd onAdDismissed",Toast.LENGTH_LONG).show();
					}
				});
		interstitialAdManager.loadAd();
	}
}
