package com.buffalo.adsdk.view;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.buffalo.adsdk.BitmapListener;
import com.buffalo.adsdk.ImageDownloadListener;
import com.buffalo.adsdk.NativeAdManagerFactory;
import com.buffalo.baseapi.ads.INativeAd;

public class RenderViewHelper {
    public static void setImageView(final ImageView imageView, String url) {
        if (imageView == null || TextUtils.isEmpty(url)) {
            return;
        }

        // TODO: 2016/11/15 default image
        imageView.setVisibility(View.VISIBLE);
        ImageDownloadListener listener = NativeAdManagerFactory.getImageDownloadListener();
        if (listener != null) {
            listener.getBitmap(url, false, new BitmapListener() {
                @Override
                public void onFailed(String errorCode) {

                }

                @Override
                public void onSuccessed(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }
            });
        }
    }

    public static void setStarRating(RatingBar ratingBar, float num) {
        if (ratingBar == null || num < 0f) {
            return;
        }

        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setRating(num);
    }

    public static void setTextView(TextView textView, CharSequence inputText, CharSequence defaultText) {
        if (textView == null) {
            return;
        }
        CharSequence txt = inputText;
        if (TextUtils.isEmpty(txt)) {
            if (TextUtils.isEmpty(defaultText)) {
                textView.setVisibility(View.GONE);
                return;
            } else {
                txt = defaultText;
            }
        }
        textView.setVisibility(View.VISIBLE);
        textView.setText(txt);
    }

    public static void setBigCard(NativeMediaView mainImageView, INativeAd ad, View view) {
        if (mainImageView == null || ad == null) {
            return;
        }
        mainImageView.setAd(ad, view);
    }

}
