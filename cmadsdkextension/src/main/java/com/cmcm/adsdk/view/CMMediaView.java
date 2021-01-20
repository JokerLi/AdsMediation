package com.cmcm.adsdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cmcm.baseapi.ads.INativeAd;

/**
 * Created by Li Guoqing on 2016/11/15.
 */
public class CMMediaView extends RelativeLayout {
    private int mAdHashCode = -1;

    public CMMediaView(Context context) {
        super(context);
    }

    public CMMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CMMediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAd(INativeAd ad, View view) {
        if (ad == null) {
            return;
        }

        if (mAdHashCode == ad.hashCode()) {
            return;
        }

        mAdHashCode = ad.hashCode();
        if(view == null){
            view = createDefaultImageView(ad);
        }
        if (view != null) {
            addViewToCMMediaView(view);
        }
    }

    private View createDefaultImageView(INativeAd ad) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        RenderViewHelper.setImageView(imageView, ad.getAdCoverImageUrl());
        return imageView;
    }

    private void addViewToCMMediaView(View view) {
        if (view == null) {
            return;
        }

        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(layoutParams);
        view.setVisibility(View.VISIBLE);
        removeAllViews();
        addView(view);
    }

}
