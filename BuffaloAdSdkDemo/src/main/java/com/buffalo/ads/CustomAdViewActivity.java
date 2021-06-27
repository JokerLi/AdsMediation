package com.buffalo.ads;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.buffalo.ads.ui.AdViewHelper;
import com.buffalo.adsdk.banner.BannerAdSize;
import com.buffalo.adsdk.banner.BannerParams;
import com.buffalo.adsdk.nativead.NativeAdManager;
import com.buffalo.baseapi.ads.INativeAd;
import com.buffalo.baseapi.ads.INativeAdLoaderListener;

public class CustomAdViewActivity extends FragmentActivity implements INativeAdLoaderListener, View.OnClickListener {
    private static final String POSID = "1094109";

    private NativeAdManager nativeAdManager;
    private INativeAd nativeAd = null;
//    private VideoAdapter adapter;

    private RelativeLayout mAdViewcontainer;//填充banner 或者native卡片或者video
    private ListView lv_video_container;
    private ViewGroup mSmallViewGroup;
    boolean isBindListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customad);
        initManager();
        initView();
    }

    private void initView() {
        lv_video_container = (ListView) findViewById(R.id.lv_video_container);
        mAdViewcontainer = (RelativeLayout) findViewById(R.id.rl_ad_container);
        mSmallViewGroup = (ViewGroup) findViewById(R.id.result_small_vast);
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_preload).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        ((CheckBox) findViewById(R.id.btn_bind_ListView)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isBindListView = isChecked;
            }
        });
        ((CheckBox) findViewById(R.id.btn_open_priority)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
//        adapter = new VideoAdapter(this);
//        lv_video_container.setAdapter(adapter);
//        lv_video_container.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {}
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if(nativeAd != null){
//                    nativeAd.registerViewForInteraction_withListView(adapter, lv_video_container, mSmallViewGroup);
//                }
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load:
                loadAd(false);
                break;
            case R.id.btn_preload:
                loadAd(true);
                break;
            case R.id.btn_show:
                showAd();
                break;
        }
    }

    private void initManager() {
        if (nativeAdManager == null) {
            nativeAdManager = new NativeAdManager(this, POSID);
        }
        nativeAdManager.enableVideoAd();//do not filter video ad
        nativeAdManager.enableBannerAd();//do not filter banner ad
        BannerParams mRequestParams = new BannerParams();//set banner ad size
        mRequestParams.setBannerViewSize(BannerAdSize.BANNER_300_250);
        nativeAdManager.setRequestParams(mRequestParams);
        nativeAdManager.setNativeAdListener(this);
    }

    private void loadAd(boolean preLoad) {
        if (nativeAdManager != null) {
            if (preLoad) {
                Log.i("CustomVideoAdapter", "video preload");
                nativeAdManager.preloadAd();
            } else {
                Log.i("CustomVideoAdapter", "video load");
                nativeAdManager.loadAd();
            }
        }
    }


    View adView;

    private void showAd() {
        if (nativeAdManager != null) {
            INativeAd ad = nativeAdManager.getAd();
            if (ad == null) {
                Toast.makeText(this, "no valid ad", Toast.LENGTH_SHORT).show();
                return;
            }
            nativeAd = ad;
//            nativeAd.setVideoAdListener(new MyVideoAdListener());
            adView = AdViewHelper.createAdView(this.getApplicationContext(), nativeAd);
            if (adView == null || nativeAd == null) {
                return;
            }
            Toast.makeText(this, "ad show", Toast.LENGTH_SHORT).show();
            if (isBindListView) {
                showAdViewBindListView(adView);
            } else {
                showAdView(adView);
            }
        }
    }

//    private class MyVideoAdListener implements INativeAd.IVideoAdListener {
//
//        @Override
//        public void onVideoStart(INativeAd nativeAd) {
//            Toast.makeText(CustomAdViewActivity.this, "video start", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onVideoViewShow(INativeAd nativeAd) {
//            Toast.makeText(CustomAdViewActivity.this, "video show", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onVideoViewHide(INativeAd nativeAd) {
//            Toast.makeText(CustomAdViewActivity.this, "video hide", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onVideoAdClicked(INativeAd nativeAd) {
//            Toast.makeText(CustomAdViewActivity.this, "video clicked", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onVideoAdMute(INativeAd nativeAd) {
//            Toast.makeText(CustomAdViewActivity.this, "video mute", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onVideoAdUnmute(INativeAd nativeAd) {
//            Toast.makeText(CustomAdViewActivity.this, "video unmute", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onVideoProgress(INativeAd nativeAd, int watchedMillis, int videoMillis) {
//            Log.i("CustomVideoAdapter","activity video progress,and watchedMillis = " + watchedMillis + ",videoMillis=" + videoMillis);
//        }
//
//        @Override
//        public void onVideoEnd(INativeAd nativeAd) {
//            Toast.makeText(CustomAdViewActivity.this, "video end", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onVideoShowError(INativeAd nativeAd, String errorMessage) {
//            Toast.makeText(CustomAdViewActivity.this, "video show error" + errorMessage, Toast.LENGTH_SHORT).show();
//        }
//
//    }

    private void showAdView(View view) {
        lv_video_container.setVisibility(View.GONE);
        mAdViewcontainer.setVisibility(View.VISIBLE);
        mAdViewcontainer.removeAllViews();
        nativeAd.registerViewForInteraction(view, null, null, null);
        mAdViewcontainer.addView(view);
    }

    private void showAdViewBindListView(View view) {
        mAdViewcontainer.removeAllViews();
        mAdViewcontainer.setVisibility(View.GONE);
        lv_video_container.setVisibility(View.VISIBLE);
        //lv_video_container.addHeaderView(view);
//        nativeAd.registerViewForInteraction_withListView(adapter, lv_video_container, mSmallViewGroup);
    }


    @Override
    protected void onStart() {
        super.onStart();
//        if(nativeAd != null && lv_video_container != null && mSmallViewGroup != null){
//            nativeAd.registerViewForInteraction_withListView(adapter, lv_video_container, mSmallViewGroup);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nativeAdManager != null) {
            nativeAdManager.onResume();
        }
        if (nativeAd != null) {
            nativeAd.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nativeAdManager != null) {
            nativeAdManager.onPause();
        }
        if (nativeAd != null) {
            nativeAd.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nativeAdManager != null) {
            nativeAdManager.onDestroy();
        }
        if (nativeAd != null) {
            nativeAd.onDestroy();
        }
    }

    @Override
    public void adLoaded() {
        Toast.makeText(this, "ad loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void adFailedToLoad(int errorcode) {
        Toast.makeText(this, "ad load fail, errorcode：" + errorcode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void adClicked(INativeAd nativeAd) {
        Toast.makeText(this, "ad click, nativead : " + nativeAd.getAdTitle(), Toast.LENGTH_SHORT).show();
    }


//    private class VideoAdapter extends BaseAdapter implements IVideoAdapter {
//
//        private static final int VIDEO_POSITION = 0;
//
//        private static final int VIDEO_ITEM = 0;
//        private static final int OTHERS_ITEM = 1;
//        private LayoutInflater mInflater = null;
//
//        private VideoAdapter(Context context) {
//            this.mInflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return 15;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            if( this.getItemViewType(position) == VIDEO_ITEM){
//                return adView;
//            }
//            return new NewsDataModel(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            switch (position) {
//                case VIDEO_POSITION :
//                    return VIDEO_ITEM;
//                default:
//                    return OTHERS_ITEM;
//            }
//        }
//
//        @Override
//        public int getViewTypeCount() {
//            return 2;
//        }
//
//        class NewsViewHolder {
//            TextView mTitleView;
//            public NewsViewHolder(){
//
//            }
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            NewsViewHolder holder = null;
//            if(convertView == null) {
//                if(getItemViewType(position) == VIDEO_ITEM && adView != null){
//                    if(adView != null){
//                        convertView = adView;
//                    }
//                }else {
//                    holder = new NewsViewHolder();
//                    convertView = mInflater.inflate(R.layout.news_item_layout, null);
//                    holder.mTitleView = (TextView) convertView.findViewById(R.id.title_text);
//                    convertView.setTag(holder);
//                }
//            } else {
//                holder = (NewsViewHolder)convertView.getTag();
//            }
//            if(getItemViewType(position) == OTHERS_ITEM){
//                holder.mTitleView = (TextView) convertView.findViewById(R.id.title_text);
//                NewsDataModel model = (NewsDataModel) getItem(position);
//                holder.mTitleView.setText(model.getTitle());
//
//            }
//            return convertView;
//        }
//
//        @Override
//        public boolean isAdShow(int i) {
//            return this.getItemViewType(i) == VIDEO_ITEM;
//        }
//    }


}
