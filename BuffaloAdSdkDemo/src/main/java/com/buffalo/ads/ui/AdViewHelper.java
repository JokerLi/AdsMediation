package com.buffalo.ads.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.buffalo.ads.R;
import com.buffalo.ads.utils.imageloader.GlideImageLoader;
import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.NativeAdTemplate;
import com.buffalo.baseapi.ads.INativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

public class AdViewHelper {
    final protected Context mContext;
    static AdViewHelper sHelper;

    public static View createAdView(Context context, INativeAd ad) {
        if (sHelper == null) {
            sHelper = new AdViewHelper(context);
        }

        return sHelper.createAdViewNew(ad);
    }

    public AdViewHelper(Context context) {
        mContext = context;
    }

    //    public void initAdView(INativeAd ad) {
//        View mNativeAdView = createAdView(ad);
//        if (mNativeAd != null) {
//            mNativeAd.unregisterView();
//        }
//
//        //保存广告对象
//        mNativeAd = ad;
//        //将广告View和广告对象绑定起来
//        mNativeAd.registerViewForInteraction(mNativeAdView);
//    }
    NativeAdTemplate mBinder;

    private View createAdViewNew(INativeAd ad) {
        if (ad == null) {
            return null;
        }

        View adView = null;
        mBinder = new NativeAdTemplate.Builder(R.layout.native_ad_mediaview_layout)
                .iconImageId(R.id.native_icon_image)
                .mainImageId(R.id.native_main_image)
                .titleId(R.id.native_title)
                .callToActionId(R.id.native_cta)
                .textId(R.id.native_text)
                .build();
        adView = mBinder.getBindedView(ad);
        return adView;
    }

    /**
     * NOTE: 接入Admob广告需要注意：如果广告是Admob的广告需要用Admob提供的根布局做广告的布局
     */
    private View createAdView(INativeAd ad) {
        if (ad == null) {
            return null;
        }

        View adView = null;
        if (ad.isNativeAd()) {
            if (isAdMobAd(ad)) {
                adView = createAdMobView(ad);
            } else {
                adView = createStandardAdView(ad);
            }
        } else {
            if (ad.getAdObject() instanceof View) {
                adView = (View) ad.getAdObject();
            }
        }

        return adView;
    }

    private boolean isAdMobAd(INativeAd ad) {
        if (ad == null) {
            return false;
        }

        try {
            Object obj = ad.getAdObject();
            return (obj instanceof NativeContentAd)
                    || (obj instanceof NativeAppInstallAd);
        } catch (Throwable e) {
        }
        return false;
    }


    private View createStandardAdView(INativeAd ad) {

        View adView = View.inflate(mContext, R.layout.native_ad_layout, null);

        String iconUrl = ad.getAdIconUrl();
        ImageView iconImageView = (ImageView) adView
                .findViewById(R.id.native_icon_image);
        if (iconUrl != null) {
            GlideImageLoader.getInstance().loadImage(mContext, iconUrl,
                    R.drawable.ic_launcher, iconImageView);
        }

        //获取大卡的背景图片的url
        String mainImageUrl = ad.getAdCoverImageUrl();
        if (!TextUtils.isEmpty(mainImageUrl)) {
            ImageView imageViewMain = (ImageView) adView
                    .findViewById(R.id.native_main_image);
            imageViewMain.setVisibility(View.VISIBLE);
            GlideImageLoader.getInstance().loadImage(mContext, mainImageUrl,
                    R.drawable.bg_default, imageViewMain);
        }

        Log.e("URL", mainImageUrl != null ? mainImageUrl : "mainImageUrl is null");

        TextView titleTextView = (TextView) adView.findViewById(R.id.native_title);
        Button bigButton = (Button) adView.findViewById(R.id.native_cta);
        TextView bodyTextView = (TextView) adView.findViewById(R.id.native_text);

        titleTextView.setText(ad.getAdTitle());
        bigButton.setText(ad.getAdCallToAction());
        bodyTextView.setText(ad.getAdBody());

        return adView;
    }

    private View createAdMobView(INativeAd ad) {
        String adTypeName = ad.getAdTypeName();
        if (!adTypeName.startsWith(Const.KEY_AB)) {
            return null;
        }

        View adView = null;

        if (ad.isDownLoadApp()) {
            adView = View.inflate(mContext, R.layout.admob_native_ad_layout_install, null).findViewById(R.id.admob_native_install_adview);
            setAdmobInstallAdView((NativeAppInstallAdView) adView);
        } else {
            adView = View.inflate(mContext, R.layout.admob_native_ad_layout_context, null).findViewById(R.id.admob_native_content_adview);
            setAdmobContentAdView((NativeContentAdView) adView);
        }

        String iconUrl = ad.getAdIconUrl();
        ImageView iconImageView = (ImageView) adView
                .findViewById(R.id.big_iv_icon);
        if (iconUrl != null) {
            GlideImageLoader.getInstance().loadImage(mContext, iconUrl,
                    R.drawable.ic_launcher, iconImageView);
        }

        //获取大卡的背景图片的url
        String mainImageUrl = ad.getAdCoverImageUrl();
        if (!TextUtils.isEmpty(mainImageUrl)) {
            ImageView imageViewMain = (ImageView) adView
                    .findViewById(R.id.iv_main);
            imageViewMain.setVisibility(View.VISIBLE);
            GlideImageLoader.getInstance().loadImage(mContext, mainImageUrl,
                    R.drawable.bg_default, imageViewMain);
        }

        Log.e("URL", mainImageUrl != null ? mainImageUrl : "mainImageUrl is null");

        TextView titleTextView = (TextView) adView.findViewById(R.id.big_main_title);
        Button bigButton = (Button) adView.findViewById(R.id.big_btn_install);
        TextView bodyTextView = (TextView) adView.findViewById(R.id.text_body);

        titleTextView.setText(ad.getAdTitle());
        bigButton.setText(ad.getAdCallToAction());
        bodyTextView.setText(ad.getAdBody());

        return adView;
    }

    private void setAdmobContentAdView(NativeContentAdView nativeContentAdView) {
        if (nativeContentAdView == null) {
            return;
        }
        nativeContentAdView.setBodyView(nativeContentAdView.findViewById(R.id.iv_main));
        //title  textview
        nativeContentAdView.setHeadlineView(nativeContentAdView.findViewById(R.id.big_main_title));
        //icon imageview
        nativeContentAdView.setLogoView(nativeContentAdView.findViewById(R.id.big_iv_icon));
        //body textview
        nativeContentAdView.setAdvertiserView(nativeContentAdView.findViewById(R.id.text_body));
        // download textview
        nativeContentAdView.setCallToActionView(nativeContentAdView.findViewById(R.id.big_btn_install));
    }

    private void setAdmobInstallAdView(NativeAppInstallAdView nativeAppInstallAdView) {
        if (nativeAppInstallAdView == null) {
            return;
        }
        nativeAppInstallAdView.setBodyView(nativeAppInstallAdView.findViewById(R.id.iv_main));
        //title  textview
        nativeAppInstallAdView.setHeadlineView(nativeAppInstallAdView.findViewById(R.id.big_main_title));
        //icon imageview
        nativeAppInstallAdView.setIconView(nativeAppInstallAdView.findViewById(R.id.big_iv_icon));
        //body textview
        nativeAppInstallAdView.setStoreView(nativeAppInstallAdView.findViewById(R.id.text_body));
        // download textview
        nativeAppInstallAdView.setCallToActionView(nativeAppInstallAdView.findViewById(R.id.big_btn_install));
        //title  textview
    }
}
