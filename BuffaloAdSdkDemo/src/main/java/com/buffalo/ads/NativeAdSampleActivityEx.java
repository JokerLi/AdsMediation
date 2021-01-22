package com.buffalo.ads;

import android.app.Activity;
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

import com.buffalo.ads.ui.AdViewHelper;
import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.RequestParams;
import com.buffalo.adsdk.base.BaseNativeAd;
import com.buffalo.adsdk.config.PosBean;
import com.buffalo.adsdk.nativead.NativeAdManagerEx;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;
import com.buffalo.utils.Commons;

import java.util.List;


public class NativeAdSampleActivityEx extends Activity implements OnClickListener {
    /* 广告 Native大卡样式 */
    private NativeAdManagerEx nativeAdManagerEx;
    private FrameLayout nativeAdContainer;
    /* 广告 Native大卡样式 */
    private Button loadAdButton;
    CheckBox mCheckboxPriority;
    private String mAdPosid = "1094101";

    private View mAdView = null;
    private EditText etPicksLoadNum;
    //用户记录功能页面的PV的ID，可以自定义
    public static final int PAGE_UNITID = 10001;
    RequestParams params = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etPicksLoadNum = (EditText) findViewById(R.id.et_picks_num);
        params = new RequestParams();
        nativeAdManagerEx = new NativeAdManagerEx(this, mAdPosid);
        nativeAdManagerEx.setRequestParams(params);
        nativeAdContainer = (FrameLayout) findViewById(R.id.big_ad_container);
        loadAdButton = (Button) findViewById(R.id.btn_load);
        loadAdButton.setOnClickListener(this);
        findViewById(R.id.getad).setOnClickListener(this);
        findViewById(R.id.getad_image).setOnClickListener(this);
        findViewById(R.id.btn_load_seq).setOnClickListener(this);
        mCheckboxPriority = (CheckBox) findViewById(R.id.checkbox_priority);
        mCheckboxPriority.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (nativeAdManagerEx != null) {
                    nativeAdManagerEx.setOpenPriority(isChecked);
                }
            }
        });
        showInfomation("posId: " + mAdPosid);
        initNativeAd();
        //使用此类可以记录功能页面PV，注意：使用前确保聚合是已经初始化的
        AdManager.reportPV(PAGE_UNITID);
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
                getAd(false);

                break;
            case R.id.getad_image:
                getAd(true);
                break;
            default:
                break;
        }
    }

    private void getAd(boolean forceImage) {
        if (nativeAdManagerEx != null) {
            INativeAd ad = nativeAdManagerEx.getAd(forceImage);
            if (ad == null) {
                Toast.makeText(NativeAdSampleActivityEx.this,
                        "no native ad loaded!", Toast.LENGTH_SHORT).show();
                return;
            }

            //如果只要实现某个类型的广告，需要判断广告类型通过ad.getAdTypeName()
            ad.setAdClickDelegate(new BaseNativeAd.IAdClickDelegate() {
                @Override
                public boolean handleClick(boolean isDetailPageClick) {
                    //true表示外部不做处理，false表示外部处理
                    return !isDetailPageClick;
                }
            });
            if (mAdView != null) {
                // 把旧的广告view从广告容器中移除
                nativeAdContainer.removeView(mAdView);
            }
            mAdView = AdViewHelper.createAdView(getApplicationContext(), ad);
            nativeAdContainer.addView(mAdView);
            ad.registerViewForInteraction(mAdView);
        }
    }

    private void requestNativeAd(int id) {
        String inputNum = etPicksLoadNum.getText().toString();
        if (Commons.isNumeric(inputNum)) {
            params.setPicksLoadNum(Integer.parseInt(inputNum));
            nativeAdManagerEx.setRequestParams(params);
        }

        if (id == R.id.btn_load) {
            nativeAdManagerEx.loadAd();
        } else {
            nativeAdManagerEx.preloadAd();
        }
    }

    private void initNativeAd() {
        nativeAdManagerEx.setNativeAdListener(new INativeAdLoaderListener() {
            @Override
            public void adLoaded() {
                updateInfo();
                Toast.makeText(NativeAdSampleActivityEx.this, "adLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void adFailedToLoad(int i) {
                updateInfo();
                Toast.makeText(NativeAdSampleActivityEx.this, "Ad failed to load errorCode:" + i,
                        Toast.LENGTH_SHORT).show();
            }


            @Override
            public void adClicked(INativeAd ad) {
                Toast.makeText(NativeAdSampleActivityEx.this, "adClicked",
                        Toast.LENGTH_SHORT).show();
            }

            public void updateInfo() {
                List<PosBean> posbeans = nativeAdManagerEx.getPosBeans();
                String lastError = nativeAdManagerEx.getRequestLastError();
                String error = nativeAdManagerEx.getRequestErrorInfo();

                String info = "";
                if (posbeans != null) {
                    info += "posbeans: " + posbeans.size() + " ";
                    for (PosBean posBean : posbeans) {
                        info += posBean.getAdName().toString() + ">";
                    }
                } else {
                    info = "posbeans: empty";
                }
                info += "\n\n requestInfo: " + lastError + "\n" + error;
                showInfomation(info);
            }

        });
    }

    private void showInfomation(String info) {
        TextView view = (TextView) findViewById(R.id.ad_pos_infomation);
        view.setText(info);
    }


}
