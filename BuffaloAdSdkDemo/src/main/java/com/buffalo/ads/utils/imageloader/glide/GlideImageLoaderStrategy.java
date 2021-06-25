/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buffalo.ads.utils.imageloader.glide;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.buffalo.ads.utils.imageloader.GlideImageConfig;
import com.buffalo.ads.utils.imageloader.GlidePreconditions;
import com.buffalo.ads.utils.imageloader.IImageLoaderStrategy;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * ================================================
 * 此类只是简单的实现了 Glide 加载的策略,方便快速使用,但大部分情况会需要应对复杂的场景
 * 这时可自行实现 {@link IImageLoaderStrategy} 和 {@link GlideImageConfig} 替换现有策略
 *
 * @see ( IImageLoaderStrategy )
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class GlideImageLoaderStrategy implements IImageLoaderStrategy<GlideImageConfigImpl>, IGlideAppliesOptions {
    @SuppressLint("CheckResult")
    @Override
    public void loadImage(@Nullable Context ctx, @Nullable GlideImageConfigImpl config) {
        GlidePreconditions.checkNotNull(ctx, "Context is required");
        GlidePreconditions.checkNotNull(config, "ImageConfigImpl is required");
        if (config.getImageView() == null && config.getSimpleTarget() == null) {
            throw new NullPointerException("ImageView is required or SimpleTarget is required");
        }

        RequestManager requestManager = CustomGlideApp.with(ctx);
        RequestBuilder requestBuilder = requestManager.load(config.getUrl());

        RequestOptions requestOptions = new RequestOptions();

        switch (config.getCacheStrategy()) {//缓存策略
            case 1:
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                break;
            case 2:
                requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                break;
            case 3:
                requestOptions.diskCacheStrategy(DiskCacheStrategy.DATA);
                break;
            case 4:
                requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                break;
            case 0:
            default:
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                break;
        }

        if (config.getRequestWidth() > 0 && config.getRequestHeight() > 0) {
            requestOptions.override(config.getRequestWidth(), config.getRequestHeight());
        }

        if (config.isCenterCrop()) {
            requestOptions.centerCrop();
        }

        if (config.isRemoveAnim()) {
            requestOptions.dontAnimate();
        } else if (config.isCrossFade()) {
            requestBuilder.transition(DrawableTransitionOptions.withCrossFade(300));
        }

        if (config.isCircle()) {
            requestOptions.circleCrop();
        }

        if (config.isImageRadius()) {
            requestOptions.transform(new RoundedCorners(config.getImageRadius()));
        }

        if (config.getPlaceholder() != 0)//设置占位符
            requestOptions.placeholder(config.getPlaceholder());

        if (config.getErrorPic() != 0)//设置错误的图片
            requestOptions.error(config.getErrorPic());

        if (config.getFallback() != 0)//设置请求 url 为空图片
            requestOptions.fallback(config.getFallback());

        if (config.isClearMemory()) {
            requestOptions.skipMemoryCache(true);
        }

        if (config.isClearDiskCache()) {
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        }

        if (config.getImageView() == null) {
            requestBuilder.apply(requestOptions).into(config.getSimpleTarget());
        } else {
            requestBuilder.apply(requestOptions).into(config.getImageView());
        }

    }

    @Override
    public void clear(@Nullable final Context ctx, @Nullable GlideImageConfigImpl config) {
        GlidePreconditions.checkNotNull(ctx, "Context is required");
        GlidePreconditions.checkNotNull(config, "ImageConfigImpl is required");

        if (config.getImageView() != null) {
            CustomGlideApp.get(ctx).getRequestManagerRetriever().get(ctx).clear(config.getImageView());
        }
        if (config.isClearDiskCache()) {//清除本地缓存
            Completable.fromAction(() -> CustomGlideApp.get(ctx).clearDiskCache()).subscribeOn(Schedulers.io()).subscribe();
        }
        if (config.isClearMemory()) {//清除内存缓存
            Completable.fromAction(() -> CustomGlideApp.get(ctx).clearMemory()).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
        }
    }

    @Override
    public void applyGlideOptions(@NonNull Context context, @NonNull GlideBuilder builder) {

    }
}
