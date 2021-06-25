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

import android.content.Context;

import androidx.annotation.NonNull;

import com.buffalo.ads.utils.imageloader.FileUtils;
import com.buffalo.ads.utils.imageloader.GlideDataHelper;
import com.buffalo.ads.utils.imageloader.GlideImageLoader;
import com.buffalo.ads.utils.imageloader.IImageLoaderStrategy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * ================================================
 * {@link AppGlideModule} 的默认实现类
 * 用于配置缓存文件夹,切换图片请求框架等操作
 * <p>
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
@GlideModule(glideName = "CustomGlideApp")
public class GlideConfiguration extends AppGlideModule {
    public static final int IMAGE_DISK_CACHE_MAX_SIZE = 100 * 1024 * 1024;//图片缓存文件最大值为100Mb

    private static final String SUB_DIRECTORY_NAME = "glide";

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {

        builder.setDiskCache(() -> {
            // Careful: the external cache directory doesn't enforce permissions
            return DiskLruCacheWrapper.create(GlideDataHelper.makeDirs(FileUtils.getSubCacheDirectory(context, SUB_DIRECTORY_NAME)), IMAGE_DISK_CACHE_MAX_SIZE);
        });

        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);

        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));

        //将配置 Glide 的机会转交给 GlideImageLoaderStrategy,如你觉得框架提供的 GlideImageLoaderStrategy
        //并不能满足自己的需求,想自定义 BaseImageLoaderStrategy,那请你最好实现 GlideAppliesOptions
        //因为只有成为 GlideAppliesOptions 的实现类,这里才能调用 applyGlideOptions(),让你具有配置 Glide 的权利
        IImageLoaderStrategy loadImgStrategy = new GlideImageLoader().getLoadImgStrategy();
        if (loadImgStrategy instanceof IGlideAppliesOptions) {
            ((IGlideAppliesOptions) loadImgStrategy).applyGlideOptions(context, builder);
        }
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        //Glide 默认使用 HttpURLConnection 做网络请求,在这切换成 Okhttp 请求
        registry.replace(GlideUrl.class, InputStream.class, new GlideOkHttpUrlLoader.Factory(new OkHttpClient()));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
