package com.cmcm.ads;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcm.ads.utils.VolleyUtil;
import com.cmcm.baseapi.ads.INativeAd;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenhao on 2015/8/26.
 */
public class ListAdsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private static final int NATIVE_ITEM = 0;
    private static final int OTHERS_ITEM = 1;
    private IFeedAdFetch iFeedAdFetch;
    private Map<Integer, INativeAd> mPositonAdMap = new HashMap<>();
    public ListAdsAdapter(Context context,IFeedAdFetch adFetch) {
        this.mContext = context;
        this.iFeedAdFetch = adFetch;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return 200;
    }

    @Override
    public Object getItem(int position) {
        if (getItemViewType(position) == NATIVE_ITEM) {
            return mPositonAdMap.get(position);
        }
        return new NewsDataModel(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public int getItemViewType(int position) {
        if (position > 0 && position % 6 == 0) {
            INativeAd iNativeAd = mPositonAdMap.get(position);
            if(iNativeAd == null) {
                iNativeAd = iFeedAdFetch.getAd();
                mPositonAdMap.put(position, iNativeAd);
            }
            if(iNativeAd != null) {
                return NATIVE_ITEM;
            }
        }
        return OTHERS_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (getItemViewType(position) == NATIVE_ITEM) {
                convertView = mInflater.inflate(R.layout.native_ad_layout, null, false);
                NativeViewHolder viewHolder = new NativeViewHolder();
                viewHolder.mImageIcon = (ImageView) convertView.findViewById(R.id.native_icon_image);
                viewHolder.mImageCover = (ImageView) convertView.findViewById(R.id.native_main_image);
                viewHolder.mNativeTitle = (TextView) convertView.findViewById(R.id.native_title);
                viewHolder.mNativeText = (TextView) convertView.findViewById(R.id.native_text);
                viewHolder.mCallToAction = (Button) convertView.findViewById(R.id.native_cta);
                INativeAd ad  = (INativeAd) getItem(position);
                viewHolder.setNativeAd(ad);
                ad.unregisterView();
                ad.registerViewForInteraction(convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = mInflater.inflate(R.layout.news_item_layout, null, false);
                NewsViewHolder newsViewHolder = new NewsViewHolder();
                newsViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.main_image_view);
                newsViewHolder.mTitleView = (TextView) convertView.findViewById(R.id.title_text);
                newsViewHolder.mTitleView.setText("测试数据Item#"+position);
                convertView.setTag(newsViewHolder);
            }

        }else {
            switch (getItemViewType(position)){
                case NATIVE_ITEM:
                    NativeViewHolder nativeViewHolder = (NativeViewHolder) convertView.getTag();
                    INativeAd nativeAd = (INativeAd) getItem(position);
                    nativeViewHolder.setNativeAd(nativeAd);
                    nativeAd.unregisterView();
                    nativeAd.registerViewForInteraction(convertView);
                    break;
                case OTHERS_ITEM:
                    NewsViewHolder newsViewHolder = (NewsViewHolder)convertView.getTag();
                    newsViewHolder.mTitleView.setText("测试数据Item#"+position);
                    break;
            }
        }
        return convertView;
    }

    static class NativeViewHolder {
        ImageView mImageIcon;
        ImageView mImageCover;
        TextView mNativeTitle;
        TextView mNativeText;
        Button mCallToAction;

        public void setNativeAd(INativeAd ad){
            String iconUrl = ad.getAdIconUrl();
            Log.i("Test", "iconUrl:" + iconUrl);
            mImageIcon.setImageResource(R.drawable.default_bg);
            if (!TextUtils.isEmpty(iconUrl)) {
                VolleyUtil.loadImage(mImageIcon, iconUrl);
            }
            String coverImageUrl = ad.getAdCoverImageUrl();
            Log.i("Test", "iconUrl:" + coverImageUrl);
           mImageCover.setImageResource(R.drawable.default_bg);
            if (!TextUtils.isEmpty(coverImageUrl)) {
                VolleyUtil.loadImage(mImageCover, coverImageUrl);
            }
            mNativeTitle.setText(ad.getAdTypeName() + " " + ad.getAdTitle());
            mNativeText.setText(ad.getAdBody());
            mCallToAction.setText(ad.getAdCallToAction());
        }
    }

    static class NewsViewHolder {
        ImageView mImageView;
        TextView mTitleView;
    }

    public interface IFeedAdFetch{
        INativeAd getAd();
    }

}


