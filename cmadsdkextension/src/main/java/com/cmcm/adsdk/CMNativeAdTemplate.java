package com.cmcm.adsdk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cmcm.adsdk.view.CMMediaView;
import com.cmcm.adsdk.view.CMViewRender;
import com.cmcm.baseapi.ads.INativeAd;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Li Guoqing on 2016/11/15.
 */
public class CMNativeAdTemplate {
    public interface ICMNativeAdViewAdapter {
        View onPostProcessAdView(INativeAd ad, CMNativeAdTemplate.ViewHolder viewHolder);
    }

    public static final String SPONSORED_TEXT = "sponsoredtext";
    public static final String SPONSORED_IMAGE = "sponsoredimage";
    public static final String AD_CORNER = "adcorner";
    public static final String BRAND_LOGO = "brandlogo";

    /**
     * check id , check view , save id
     */
    public final int mLayoutId;
    public final int mTitleId;
    public final int mTextId;
    public final int mSocialContextId;
    public final int mCallToActionId;
    public final int mMainImageId;
    public final int mIconImageId;
    public final int mStarRatingId;
    public final int mSponsoredId;
    public final int mAdCornerId;
    @NonNull
    final Map<String, Integer> extras;
    private CMViewRender mRender;

    private CMNativeAdTemplate(Builder builder){
        if(checkBuilderIllegal(builder)){
            throwException("input resource id is illegal");
        }
        mLayoutId = builder.mLayoutId;
        mTitleId = builder.mTitleId;
        mTextId = builder.mTextId;
        mSocialContextId = builder.mSocialContextId;
        mCallToActionId = builder.mCallToActionId;
        mMainImageId = builder.mMainImageId;
        mIconImageId = builder.mIconImageId;
        mStarRatingId = builder.mStarRatingId;
        mSponsoredId = builder.mSponsoredId;
        mAdCornerId = builder.mAdCornerId;
        extras = builder.extras;
    }

    private void throwException(String s){
        if(TextUtils.isEmpty(s)){
            return;
        }
        throw new RuntimeException(s);
    }

    private boolean checkBuilderIllegal(Builder builder) {
        if(builder == null){
            return true;
        }
        return false;
    }

    public View getBindedView(INativeAd ad) {
        if (mRender == null) {
            mRender = new CMViewRender(this);
        }

        return mRender.getBindedView(ad);
    }

    public <T extends View> T findViewById(int id){
        try{
            return (T)mRender.getMainView().findViewById(id);
        }catch (Exception e){

        }
        return null;
    }

    public static class ViewHolder {
        private Context mContext;
        private LayoutInflater mInflater;
        private CMNativeAdTemplate mViewBinder;

        private View mView;
        public View mLayoutView;
        public TextView mTitleView;
        public TextView mBodyView;
        public TextView mSocialContextView;
        public TextView mCallToActionView;
        public TextView mSponsoredView;
        public CMMediaView mMainImageView;
        public ImageView mIconImageView;
        public RatingBar mStarRatingView;
        public ViewGroup mAdCornerView;
        public Map<String, Integer> extras;

        public ViewHolder(Context context, CMNativeAdTemplate viewBinder) {
            mContext = context;
            mViewBinder = viewBinder;
            mInflater = LayoutInflater.from(mContext);
            inflateView();
        }

        public void resetView() {
            mView = mLayoutView;
        }

        public void setView(View view) {
            mView = view;
        }

        public View getView() {
            return mView;
        }

        private void inflateView() {
            try {
                mView = mLayoutView = mInflater.inflate(mViewBinder.mLayoutId, null);
                mTitleView = noExceptionFindView(mLayoutView, mViewBinder.mTitleId, TextView.class, "mTitleView");
                mBodyView = noExceptionFindView(mLayoutView, mViewBinder.mTextId, TextView.class, "mBodyView");
                mSocialContextView = noExceptionFindView(mLayoutView, mViewBinder.mSocialContextId, TextView.class, "mSocialContextView");
                mCallToActionView = noExceptionFindView(mLayoutView, mViewBinder.mCallToActionId, TextView.class, "mCallToActionView");
                mSponsoredView = noExceptionFindView(mLayoutView, mViewBinder.mSponsoredId, TextView.class, "mSponsoredView");
                mMainImageView = noExceptionFindView(mLayoutView, mViewBinder.mMainImageId, CMMediaView.class, "mMainImageView");
                mIconImageView = noExceptionFindView(mLayoutView, mViewBinder.mIconImageId, ImageView.class, "mIconImageView");
                mStarRatingView = noExceptionFindView(mLayoutView, mViewBinder.mStarRatingId, RatingBar.class, "mStarRatingView");
                mAdCornerView = noExceptionFindView(mLayoutView, mViewBinder.mAdCornerId, ViewGroup.class, "mAdCornerView");
                extras = mViewBinder.extras;
            } catch (Exception e) {
            }
        }

        private <T extends View> T noExceptionFindView(View layoutView, int id, Class<T> tClass, String type) {
            if (id == 0) {
                return null;
            }

            try {
                View view = layoutView.findViewById(id);
                if(tClass.isInstance(view)){
                    return (T) layoutView.findViewById(id);
                }else{
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

    }

    public static class Builder{
        private final int mLayoutId;
        private int mTitleId;
        private int mTextId;
        private int mSocialContextId;
        private int mCallToActionId;
        private int mMainImageId;
        private int mIconImageId;
        private int mSponsoredId;
        private int mStarRatingId;
        private int mAdCornerId;

        @NonNull
        private Map<String, Integer> extras = Collections.emptyMap();

        public Builder(int layoutId) {
            this.mLayoutId = layoutId;
            this.extras = new HashMap();
        }

        @NonNull
        public final Builder titleId(int titleId) {
            this.mTitleId = titleId;
            return this;
        }

        @NonNull
        public final Builder textId(int textId) {
            this.mTextId = textId;
            return this;
        }

        @NonNull
        public final Builder callToActionId(int callToActionId) {
            this.mCallToActionId = callToActionId;
            return this;
        }

        @NonNull
        public final Builder mainImageId(int mainImageId) {
            this.mMainImageId = mainImageId;
            return this;
        }

        @NonNull
        public final Builder iconImageId(int iconImageId) {
            this.mIconImageId = iconImageId;
            return this;
        }

        @NonNull
        public final Builder socialContextId(int socialContextId) {
            this.mSocialContextId = socialContextId;
            return this;
        }

        @NonNull
        public final Builder starRatingId(int starRatingId) {
            this.mStarRatingId = starRatingId;
            return this;
        }

        @NonNull
        public final Builder sponsoredId(int sponsoredId) {
            this.mSponsoredId = sponsoredId;
            return this;
        }

        @NonNull
        public final Builder adCornerId(int adCornerId) {
            this.mAdCornerId = adCornerId;
            return this;
        }

        @NonNull
        public final Builder addExtras(Map<String, Integer> resourceIds) {
            this.extras = new HashMap(resourceIds);
            return this;
        }

        @NonNull
        public final Builder addExtra(String key, int resourceId) {
            this.extras.put(key, Integer.valueOf(resourceId));
            return this;
        }

        public final CMNativeAdTemplate build() {
            return new CMNativeAdTemplate(this);
        }

    }
}
