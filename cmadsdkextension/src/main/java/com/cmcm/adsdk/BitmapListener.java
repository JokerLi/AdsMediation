package com.cmcm.adsdk;

import android.graphics.Bitmap;

/**
 * Created by i on 2015/12/1.
 */
public interface BitmapListener {
    public void onFailed(String errorCode);
    public void onSuccessed(Bitmap bitmap);
}
