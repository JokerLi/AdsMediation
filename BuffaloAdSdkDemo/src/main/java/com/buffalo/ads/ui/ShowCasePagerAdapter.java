package com.buffalo.ads.ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.buffalo.baseapi.ads.INativeAd;

import java.util.ArrayList;
import java.util.List;

public class ShowCasePagerAdapter extends PagerAdapter implements AdClickListener {
    private List<View> mList = new ArrayList<View>();
    private Context mContext;
    private AdClickListener mAdClickListner;

    public ShowCasePagerAdapter(Context context) {
        this.mContext = context;
        NativeAdCaseView nativeAdCaseView = new NativeAdCaseView(mContext);
        FeedListCaseView feedListCaseView = new FeedListCaseView(mContext);
        mList.add(nativeAdCaseView);
        mList.add(feedListCaseView);
        nativeAdCaseView.setAdapterListener(this);
        feedListCaseView.setAdapterListener(this);

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mList.get(position), 0);
        return mList.get(position);
    }

    public void setAdClickLisener(AdClickListener lisener) {
        this.mAdClickListner = lisener;
    }

    @Override
    public void onAdClicked(INativeAd ad) {
        if (mAdClickListner != null) {
            mAdClickListner.onAdClicked(ad);
        }
    }


}
