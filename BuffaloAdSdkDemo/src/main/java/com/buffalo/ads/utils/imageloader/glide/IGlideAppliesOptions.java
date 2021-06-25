package com.buffalo.ads.utils.imageloader.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;

public interface IGlideAppliesOptions {

    /**
     * 配置 的自定义参数,此方法在初始化时执行在第一次被调用时初始化,只会执行一次
     *
     * @param context
     * @param builder {@link GlideBuilder} 此类被用来创建 Glide
     */
    void applyGlideOptions(@NonNull Context context, @NonNull GlideBuilder builder);
}