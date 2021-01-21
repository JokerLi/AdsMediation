package com.buffalo.adsdk;

import android.graphics.Bitmap;

public interface BitmapListener {
    public void onFailed(String errorCode);

    public void onSuccessed(Bitmap bitmap);
}
