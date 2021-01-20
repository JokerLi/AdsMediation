package com.cmcm.ads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcm.ads.ui.AdViewHelper;
import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.CMRequestParams;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.cmcm.adsdk.config.PosBean;
import com.cmcm.adsdk.nativead.CMNativeAd;
import com.cmcm.adsdk.nativead.NativeAdManager;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NativeAdSampleActivity extends Activity implements OnClickListener {
    private CheckBox mReportModeRCV;
    private CheckBox mReportModeUNI;
    private CheckBox mReportExtra;
    private CheckBox mOfferInsert;
    private CheckBox mOfferCallback;
    private CheckBox mOfferClick;
    private EditText mEditKey;
    private EditText mEditValue;
    private boolean mIsReportExtra = false;
    /* 广告 Native大卡样式 */
    private NativeAdManager nativeAdManager;
    private FrameLayout nativeAdContainer;
    /* 广告 Native大卡样式 */
    private Button loadAdButton;
    CheckBox mCheckboxPriority;
    private String mAdPosid = BuildConfig.IS_CN_VERSION ? "1096101" : "10000100";

    private View mAdView = null;
    //用户记录功能页面的PV的ID，可以自定义
    public static final int PAGE_UNITID = 10001;
    CMRequestParams params = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manager);
        params = new CMRequestParams();
        nativeAdManager = new NativeAdManager(this, mAdPosid);
        nativeAdManager.setRequestParams(params);
        nativeAdContainer = (FrameLayout) findViewById(R.id.big_ad_container);
        loadAdButton = (Button) findViewById(R.id.btn_load);
        loadAdButton.setOnClickListener(this);
        findViewById(R.id.getad).setOnClickListener(this);
        findViewById(R.id.btn_load_seq).setOnClickListener(this);
        findViewById(R.id.btn_load_service).setOnClickListener(this);
        mReportModeRCV = (CheckBox)findViewById(R.id.report_mode_rcv);
        mReportModeUNI = (CheckBox)findViewById(R.id.report_mode_uni);
        mReportExtra = (CheckBox)findViewById(R.id.report_extra);
        mOfferInsert = (CheckBox)findViewById(R.id.offer_info_insert);
        mOfferCallback = (CheckBox)findViewById(R.id.offer_info_callback);
        mOfferClick = (CheckBox)findViewById(R.id.offer_info_click);

        mEditKey = (EditText)findViewById(R.id.edit_text_key);
        mEditValue = (EditText)findViewById(R.id.edit_text_value);

        mCheckboxPriority = (CheckBox) findViewById(R.id.checkbox_priority);
        mCheckboxPriority.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (nativeAdManager != null) {
                    nativeAdManager.setOpenPriority(isChecked);
                }
            }
        });

        initNativeAd();
        //使用此类可以记录功能页面PV，注意：使用前确保聚合是已经初始化的
        CMAdManager.reportPV(PAGE_UNITID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load:
            case R.id.btn_load_seq:
                requestNativeAd(v.getId());
                break;
            case R.id.getad:
                updateConfig();
                getAd();
                break;
            case R.id.btn_load_service:
                Intent service = new Intent(this, TestService.class);
                startService(service);
            default:
                break;
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
                    "Ad index is: " + ((CMNativeAd)ad).getAdPriorityIndex(), Toast.LENGTH_SHORT).show();

            //如果只要实现某个类型的广告，需要判断广告类型通过ad.getAdTypeName()
            ad.setAdClickDelegate(new CMBaseNativeAd.IAdClickDelegate() {
                @Override
                public boolean handleClick(boolean isDetailPageClick) {
                    //true表示外部不做处理，false表示外部处理
                    return !isDetailPageClick;
                }
            });

            ad.setAdOnClickListener(new INativeAd.IAdOnClickListener() {
                @Override
                public void onAdClick(INativeAd nativeAd) {
                    Toast.makeText(NativeAdSampleActivity.this, "setAdOnClickListener",
                            Toast.LENGTH_SHORT).show();
                }
            });

            ad.setImpressionListener(new INativeAd.ImpressionListener() {
                @Override
                public void onLoggingImpression() {
                    Toast.makeText(NativeAdSampleActivity.this, "onLoggingImpression",
                            Toast.LENGTH_SHORT).show();
                }
            });
            if (mAdView != null) {
                // 把旧的广告view从广告容器中移除
                nativeAdContainer.removeView(mAdView);
            }
            mAdView = AdViewHelper.createAdView(getApplicationContext(), ad);
            nativeAdContainer.addView(mAdView);
            if(mIsReportExtra) {
                Map<String,String> extra = new HashMap<String,String>();
                String key = mEditKey.getText().toString();
                String value = mEditValue.getText().toString();
                extra.put(key, value);
                ad.registerViewForInteraction_withExtraReportParams(mAdView, extra);
            }else {
                ad.registerViewForInteraction(mAdView);
            }
        }
    }

    private void requestNativeAd(int id) {
        if (id == R.id.btn_load) {
            nativeAdManager.loadAd();
        } else {
            nativeAdManager.preloadAd();
        }
    }

    private void updateConfig(){
        int offerInsert = mOfferInsert.isChecked()?1:0;
        int offerCallback = mOfferCallback.isChecked()?2:0;
        int offerClick = mOfferClick.isChecked()?4:0;
        int switcher = offerInsert | offerCallback | offerClick;
        mIsReportExtra = mReportExtra.isChecked();
        CMAdManager.setReportSwitcher(switcher);
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