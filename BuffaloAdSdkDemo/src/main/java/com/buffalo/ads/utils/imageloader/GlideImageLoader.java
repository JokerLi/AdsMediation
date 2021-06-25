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
package com.buffalo.ads.utils.imageloader;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.buffalo.ads.utils.imageloader.glide.CustomImageTarget;
import com.buffalo.ads.utils.imageloader.glide.GlideImageConfigImpl;
import com.buffalo.ads.utils.imageloader.glide.GlideImageLoaderStrategy;
import com.buffalo.ads.utils.imageloader.glide.ILoadingImgListener;


/**
 * ================================================
 * {@link GlideImageLoader} 使用策略模式和建造者模式,可以动态切换图片请求框架(比如说切换成 Picasso )
 * 当需要切换图片请求框架或图片请求框架升级后变更了 Api 时
 * 这里可以将影响范围降到最低,所以封装 {@link GlideImageLoader} 是为了屏蔽这个风险
 * <p>
 * ================================================
 */
public final class GlideImageLoader {

    private static volatile GlideImageLoader sImageLoader;

    public static GlideImageLoader getInstance() {
        if (sImageLoader == null) {
            synchronized (GlideImageLoader.class) {
                if (sImageLoader == null) {
                    sImageLoader = new GlideImageLoader();
                }
            }
        }
        return sImageLoader;
    }

    @Nullable
    private
    IImageLoaderStrategy mStrategy;

    public GlideImageLoader() {
        // 默认使用Glide加载
        mStrategy = new GlideImageLoaderStrategy();
    }

    /**
     * 加载图片
     */
    public <T extends GlideImageConfig> void loadImage(Context context, T config) {
        GlidePreconditions.checkNotNull(mStrategy, "Please implement BaseImageLoaderStrategy and call " +
                "GlobalConfigModule.Builder#imageLoaderStrategy(BaseImageLoaderStrategy) in the applyOptions method of ConfigModule");
        mStrategy.loadImage(context, config);
    }

    /**
     * 加载图片
     */
    public void loadImage(Context context, String url, @DrawableRes int placeHolder, ImageView imageView) {
        GlidePreconditions.checkNotNull(mStrategy, "Please implement BaseImageLoaderStrategy and call " +
                "GlobalConfigModule.Builder#imageLoaderStrategy(BaseImageLoaderStrategy) in the applyOptions method of ConfigModule");

        GlideImageConfig config = new GlideImageConfigImpl.Builder()
                .imageView(imageView)
                .url(url)
                .placeholder(placeHolder)
                .isRemoveAnim(true)
                .isClearDiskCache(false)
                .isClearMemory(false)
                .build();
        mStrategy.loadImage(context, config);
    }

    /**
     * 关闭缓存功能加载图片
     */
    public void loadImage(Context context, String url, @DrawableRes int placeHolder,
                          boolean isSupportCache, ImageView imageView) {
        GlidePreconditions.checkNotNull(mStrategy, "Please implement BaseImageLoaderStrategy and call " +
                "GlobalConfigModule.Builder#imageLoaderStrategy(BaseImageLoaderStrategy) in the applyOptions method of ConfigModule");

        GlideImageConfig config = new GlideImageConfigImpl.Builder()
                .imageView(imageView)
                .url(url)
                .placeholder(placeHolder)
                .isRemoveAnim(true)
                .isClearDiskCache(!isSupportCache)
                .isClearMemory(!isSupportCache)
                .build();
        mStrategy.loadImage(context, config);
    }

    /**
     * r
     * 加载图片   带有监听回调
     */
    public void loadImage(Context context, String url, @DrawableRes int placeHolder, ILoadingImgListener listener) {
        GlidePreconditions.checkNotNull(mStrategy, "Please implement BaseImageLoaderStrategy and call " +
                "GlobalConfigModule.Builder#imageLoaderStrategy(BaseImageLoaderStrategy) in the applyOptions method of ConfigModule");

        GlideImageConfig config = new GlideImageConfigImpl.Builder()
                .target(new CustomImageTarget(listener))
                .url(url)
                .placeholder(placeHolder)
                .isRemoveAnim(true)
                .isClearDiskCache(false)
                .isClearMemory(false)
                .build();
        mStrategy.loadImage(context, config);
    }

    /**
     * 停止加载或清理缓存
     */
    public <T extends GlideImageConfig> void clear(Context context, T config) {
        GlidePreconditions.checkNotNull(mStrategy, "Please implement BaseImageLoaderStrategy and " +
                "call GlobalConfigModule.Builder#imageLoaderStrategy(BaseImageLoaderStrategy) in the applyOptions method of ConfigModule");
        mStrategy.clear(context, config);
    }

    /**
     * 可在运行时随意切换 {@link IImageLoaderStrategy}
     */
    public void setLoadImgStrategy(IImageLoaderStrategy strategy) {
        GlidePreconditions.checkNotNull(strategy, "strategy == null");
        this.mStrategy = strategy;
    }

    @Nullable
    public IImageLoaderStrategy getLoadImgStrategy() {
        return mStrategy;
    }
}
