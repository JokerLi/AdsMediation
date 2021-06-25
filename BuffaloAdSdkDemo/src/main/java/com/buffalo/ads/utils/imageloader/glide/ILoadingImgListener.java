package com.buffalo.ads.utils.imageloader.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public interface ILoadingImgListener {

    void onLoadSuccess(Bitmap resource);

    void onLoadFailed(Exception e, Drawable errorDrawable);

    void onLoadStarted(Drawable placeholder);
}
