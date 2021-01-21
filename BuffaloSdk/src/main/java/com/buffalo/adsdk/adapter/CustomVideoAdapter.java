package com.buffalo.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.nativead.LifeCycleDelegate;

import java.util.Map;

/**
 * Created by chenhao on 16/3/31.
 * for video custom
 */
public abstract class CustomVideoAdapter extends NativeloaderAdapter implements LifeCycleDelegate{
    public abstract void initVideoSDK(@NonNull final Context context,
                                      @NonNull final Map<String, Object> extras);


    @Override
    public Const.AdType getAdType() {
        return Const.AdType.VIDEO;
    }

}
