package com.buffalo.ads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WelComeActivity extends Activity {
    public static final String FROM = "from";
    public static final String CLASS_NAME = "WelComeActivity";

    class AdEntryItem {
        boolean isGroupHeader;
        String title;
        String activty;
        String placementId;

        public AdEntryItem(boolean groupHeader, String title, String activity, String placementId) {
            this.isGroupHeader = groupHeader;
            this.title = title;
            this.activty = activity;
            this.placementId = placementId;
        }
    }

    List<AdEntryItem> mAdEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initData();

        ListView listView = (ListView) findViewById(R.id.listAdEntries);
        AdEntryListAdapter listAdapter = new AdEntryListAdapter();
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(listAdapter);
    }


    void initData() {
        mAdEntries.clear();

//        addAdEntry(true, "猎户 - Orion", "", "");
//        addAdEntry(false, "优先级穿插 - HybridAd List", "CustomAdViewActivity", "");

        addAdEntry(true, "广告列表", "", "");
        addAdEntry(false, "原生广告 - NativeAdManager", "NativeAdSampleActivity", "");
//        addAdEntry(false, "原生广告 - NativeAdManagerEx", "NativeAdSampleActivityEx", "");
        addAdEntry(false, "原生广告 - NativeAdListManager", "ListAdSimpleActivity", "");
        addAdEntry(false, "插屏广告 - InterstitialAdManager", "InterstitalAdSampleActivity", "");
        addAdEntry(false, "原生开屏广告 - NativeSplashAd", "NativeSplashAdSampleActivity", "");
        addAdEntry(false, "场景 - 屏保（Activity）", "ScreenSaverEnterActivity", "");
        addAdEntry(false, "场景 - AppLock(WindowManager)", "WindowManagerSimpleActivity", "");
//        if(!BuildConfig.IS_CN_VERSION) {
//            addAdEntry(false, "IAB Banner广告(Mopub/Orion) - IAB Banner", "BannerAdSampleActivity", "");
//        }
    }

    void addAdEntry(boolean groupHeader, String title, String activity, String placementId) {
        mAdEntries.add(new AdEntryItem(groupHeader, title, activity, placementId));
    }

    class AdEntryListAdapter extends BaseAdapter implements ListView.OnItemClickListener {

        @Override
        public int getCount() {
            return mAdEntries.size();
        }

        @Override
        public Object getItem(int position) {
            return mAdEntries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            return !mAdEntries.get(position).isGroupHeader;
        }

        @Override
        public int getItemViewType(int position) {
            return mAdEntries.get(position).isGroupHeader ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            AdEntryItem entry = mAdEntries.get(position);
            int layout_res = entry.isGroupHeader ? R.layout.list_ad_entry_group_header : R.layout.list_ad_entry_item;
            if (view == null) {
                view = LayoutInflater.from(getApplicationContext()).inflate(layout_res, null);
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView descriptionView = (TextView) view.findViewById(R.id.description);

            if (titleView != null) {
                titleView.setText(entry.title);
            }
            if (descriptionView != null) {
                String description = entry.activty;
                if (!TextUtils.isEmpty(entry.placementId)) {
                    description = description + " placementId:" + entry.placementId;
                }
                descriptionView.setText(description);
            }

            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AdEntryItem entry = mAdEntries.get(position);
            startAdEntry(entry);
        }
    }

    public void startAdEntry(AdEntryItem entry) {
        if (entry.isGroupHeader)
            return;

        try {
            Class activityClass = Class.forName("com.buffalo.ads." + entry.activty);
            Intent intent = new Intent(WelComeActivity.this, activityClass);
            intent.putExtra(FROM, CLASS_NAME);
            intent.putExtra("placementId", entry.placementId);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
