package com.buffalo.ads.utils.imageloader.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class CustomImageTarget extends CustomTarget<BitmapDrawable> {
    private ILoadingImgListener mImgListener;

    public CustomImageTarget(ILoadingImgListener imgListener) {
        mImgListener = imgListener;
    }

    public CustomImageTarget(int width, int height, ILoadingImgListener imgListener) {
        super(width, height);
        mImgListener = imgListener;
    }

    @Override
    public void onResourceReady(@NonNull BitmapDrawable resource, @Nullable Transition<? super BitmapDrawable> transition) {
        if (null != mImgListener) {
            mImgListener.onLoadSuccess(resource.getBitmap());
        }
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {

    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        if (null != mImgListener) {
            mImgListener.onLoadFailed(null, errorDrawable);
        }
    }

    @Override
    public void onLoadStarted(@Nullable Drawable placeholder) {
        super.onLoadStarted(placeholder);
        if (null != mImgListener) {
            mImgListener.onLoadStarted(placeholder);
        }
    }
}
