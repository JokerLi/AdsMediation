package com.buffalo.ads.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.buffalo.ads.ListAdsAdapter;
import com.buffalo.ads.R;
import com.buffalo.adsdk.nativead.FeedListAdManager;
import com.buffalo.baseapi.ads.INativeAd;

public class FeedListCaseView extends RelativeLayout implements ListAdsAdapter.IFeedAdFetch {
    private static final String LIST_POSID = "1094100";
    private Context mContext;
    private View mRootView;
    private ListView mListView;
    private ListAdsAdapter mAdapter;
    private FeedListAdManager mFeedListAdManager;
    private AdClickListener mAdapterListener;

    public FeedListCaseView(Context context) {
        this(context, null);
    }

    public FeedListCaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FeedListCaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mRootView = inflater.inflate(R.layout.view_feedlist_ad_show_case, null);
        addView(mRootView);
        mListView = (ListView) mRootView.findViewById(R.id.list_lv);
        mAdapter = new ListAdsAdapter(mContext, this);
        mListView.setAdapter(mAdapter);
        loadListAd();
    }

    private void loadListAd() {
        mFeedListAdManager = new FeedListAdManager(mContext, LIST_POSID);
        mFeedListAdManager.setFilterDuplicateAd(true);
        mFeedListAdManager.setFeedListener(new FeedListAdManager.FeedListListener() {
            @Override
            public void onAdsAvailable() {

            }

            @Override
            public void onAdClick(INativeAd ad) {
                if (mAdapterListener != null) {
                    mAdapterListener.onAdClicked(ad);
                }

            }
        });
        mFeedListAdManager.loadAds();
    }

    @Override
    public INativeAd getAd() {
        if (mFeedListAdManager != null) {
            return mFeedListAdManager.getAd();
        }
        return null;
    }

    public void onDestroy() {
        mFeedListAdManager = null;
    }

    public void setAdapterListener(ShowCasePagerAdapter listener) {
        this.mAdapterListener = listener;
    }

}
