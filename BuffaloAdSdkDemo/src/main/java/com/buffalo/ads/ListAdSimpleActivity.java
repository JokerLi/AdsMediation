package com.buffalo.ads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.buffalo.adsdk.nativead.FeedListAdManager;
import com.buffalo.baseapi.ads.INativeAd;

public class ListAdSimpleActivity extends Activity implements ListAdsAdapter.IFeedAdFetch {
    private static String TAG = ListAdSimpleActivity.class.getSimpleName();
    private String POSID = BuildConfig.IS_CN_VERSION ? "1096100" : "1094100";
    private ListView mListNativeAd;
    private FeedListAdManager mFeedListAdManager;
    private ListAdsAdapter mAdapter;
    private CheckBox mCheckBox;
    private CheckBox mCheckBoxThread;
    private Button mLoadAd;
    private boolean mIsLoadInChildThread = false;

    //    private EditText mRequestNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_native_ad_list);
        init();
    }

    private void init() {
//        mRequestNum = (EditText) findViewById(R.id.et_picks_num);
        mListNativeAd = (ListView) findViewById(R.id.list_lv);
        mAdapter = new ListAdsAdapter(this, this);
        mListNativeAd.setAdapter(mAdapter);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox_priority);

        mCheckBoxThread = (CheckBox) findViewById(R.id.checkbox_thread);
        mLoadAd = (Button) findViewById(R.id.lv_load);

        mFeedListAdManager = new FeedListAdManager(this, POSID);
        //mFeedListAdManager.setRequestOrionAdNum();
        mFeedListAdManager.setFilterDuplicateAd(true);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mFeedListAdManager != null) {
                    mFeedListAdManager.setOpenPriority(isChecked);
                }
            }
        });

        mCheckBoxThread.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsLoadInChildThread = isChecked;
            }
        });

        mLoadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoadInChildThread) {
                    new Thread() {
                        @Override
                        public void run() {
//                            String num = mRequestNum.getText().toString();
//                            if(!TextUtils.isEmpty(num) &&TextUtils.isDigitsOnly(num)){
//                                mFeedListAdManager.setRequestOrionAdNum(Integer.parseInt(num));
//                            }
//                            mRequestNum.setCursorVisible(false);
                            mFeedListAdManager.loadAds();
                        }
                    }.start();
                } else {
//                    String num = mRequestNum.getText().toString();
//                    if(!TextUtils.isEmpty(num) &&TextUtils.isDigitsOnly(num)){
//                        mFeedListAdManager.setRequestOrionAdNum(Integer.parseInt(num));
//                    }
//                    mRequestNum.setCursorVisible(false);
                    mFeedListAdManager.loadAds();
                }
            }
        });

    }


    @Override
    public INativeAd getAd() {
        if (mFeedListAdManager != null) {
            return mFeedListAdManager.getAd();
        }
        return null;
    }
}
