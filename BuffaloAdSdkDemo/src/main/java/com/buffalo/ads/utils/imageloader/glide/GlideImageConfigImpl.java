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

import android.widget.ImageView;

import com.buffalo.ads.utils.imageloader.GlideImageConfig;


/**
 * ================================================
 * 这里存放图片请求的配置信息,可以一直扩展字段,如果外部调用时想让图片加载框架
 * 做一些操作,比如清除缓存或者切换缓存策略,则可以定义一个 int 类型的变量,内部根据 switch(int) 做不同的操作
 * 其他操作同理
 * <p>
 * Created by JessYan on 8/5/16 15:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class GlideImageConfigImpl extends GlideImageConfig {
    // 0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
    private int cacheStrategy;
    // 请求 url 为空,则使用此图片作为占位符
    private int fallback;
    // 图片每个圆角的大小
    private int imageRadius;
    //是否使用淡入淡出过渡动画
    private boolean isCrossFade;
    //是否将图片剪切为 CenterCrop
    private boolean isCenterCrop;
    //是否将图片剪切为圆形
    private boolean isCircle;
    //清理内存缓存
    private boolean isClearMemory;
    //清理本地缓存
    private boolean isClearDiskCache;
    // 移除动画
    private boolean isRemoveAnim;

    public GlideImageConfigImpl(Builder builder) {
        this.url = builder.url;
        this.imageView = builder.imageView;
        this.placeholder = builder.placeholder;
        this.errorPic = builder.errorPic;
        this.fallback = builder.fallback;
        this.cacheStrategy = builder.cacheStrategy;
        this.imageRadius = builder.imageRadius;
        this.isCrossFade = builder.isCrossFade;
        this.isCenterCrop = builder.isCenterCrop;
        this.isCircle = builder.isCircle;
        this.isClearMemory = builder.isClearMemory;
        this.isClearDiskCache = builder.isClearDiskCache;
        this.requestWidth = builder.requestWidth;
        this.requestHeight = builder.requestHeight;
        this.isRemoveAnim = builder.isRemoveAnim;
        this.mSimpleTarget = builder.mSimpleTarget;
    }

    public int getCacheStrategy() {
        return cacheStrategy;
    }

    public boolean isClearMemory() {
        return isClearMemory;
    }

    public boolean isClearDiskCache() {
        return isClearDiskCache;
    }

    public int getFallback() {
        return fallback;
    }

    public int getImageRadius() {
        return imageRadius;
    }

    public boolean isImageRadius() {
        return imageRadius > 0;
    }

    public boolean isCrossFade() {
        return isCrossFade;
    }

    public boolean isRemoveAnim() {
        return isRemoveAnim;
    }

    public boolean isCenterCrop() {
        return isCenterCrop;
    }

    public boolean isCircle() {
        return isCircle;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String url;
        private ImageView imageView;
        private CustomImageTarget mSimpleTarget;

        private int placeholder;
        private int errorPic;
        private int fallback; //请求 url 为空,则使用此图片作为占位符
        private int cacheStrategy;//0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
        private int imageRadius;//图片每个圆角的大小

        private boolean isCrossFade;//是否使用淡入淡出过渡动画
        private boolean isCenterCrop;//是否将图片剪切为 CenterCrop
        private boolean isCircle;//是否将图片剪切为圆形
        private boolean isClearMemory;//清理内存缓存
        private boolean isClearDiskCache;//清理本地缓存
        private boolean isRemoveAnim;
        private int requestWidth;
        private int requestHeight;

        public Builder() {
        }

        public Builder target(CustomImageTarget target) {
            this.mSimpleTarget = target;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder placeholder(int placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder errorPic(int errorPic) {
            this.errorPic = errorPic;
            return this;
        }

        public Builder fallback(int fallback) {
            this.fallback = fallback;
            return this;
        }

        public Builder imageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder cacheStrategy(int cacheStrategy) {
            this.cacheStrategy = cacheStrategy;
            return this;
        }

        public Builder imageRadius(int imageRadius) {
            this.imageRadius = imageRadius;
            return this;
        }

        public Builder requestWidth(int requestWidth) {
            this.requestWidth = requestWidth;
            return this;
        }

        public Builder requestHeight(int requestHeight) {
            this.requestHeight = requestHeight;
            return this;
        }

        public Builder isCrossFade(boolean isCrossFade) {
            this.isCrossFade = isCrossFade;
            return this;
        }

        public Builder isCenterCrop(boolean isCenterCrop) {
            this.isCenterCrop = isCenterCrop;
            return this;
        }

        public Builder isRemoveAnim(boolean isRemoveAnim) {
            this.isRemoveAnim = isRemoveAnim;
            return this;
        }

        public Builder isCircle(boolean isCircle) {
            this.isCircle = isCircle;
            return this;
        }

        public Builder isClearMemory(boolean isClearMemory) {
            this.isClearMemory = isClearMemory;
            return this;
        }

        public Builder isClearDiskCache(boolean isClearDiskCache) {
            this.isClearDiskCache = isClearDiskCache;
            return this;
        }

        public GlideImageConfigImpl build() {
            return new GlideImageConfigImpl(this);
        }
    }
}
