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

import android.widget.ImageView;

import com.buffalo.ads.utils.imageloader.glide.CustomImageTarget;


/**
 * ================================================
 * 这里是图片加载配置信息的基类,定义一些所有图片加载框架都可以用的通用参数
 * 每个 {@link IImageLoaderStrategy} 应该对应一个 {@link GlideImageConfig} 实现类
 * <p>
 * Created by JessYan on 8/5/16 15:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class GlideImageConfig {
    protected String url;
    // simpleTarget 和  ImageView 二者只可选其一 一个是带有加载监听的回调  一个是没有的 看需求来选
    protected CustomImageTarget mSimpleTarget;
    protected ImageView imageView;
    protected int placeholder;//占位符
    protected int errorPic;//错误占位符
    protected int requestWidth;
    protected int requestHeight;

    public String getUrl() {
        return url;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setSimpleTarget(CustomImageTarget simpleTarget) {
        mSimpleTarget = simpleTarget;
    }

    public CustomImageTarget getSimpleTarget() {
        return mSimpleTarget;
    }

    public int getPlaceholder() {
        return placeholder;
    }

    public int getErrorPic() {
        return errorPic;
    }

    public int getRequestWidth() {
        return requestWidth;
    }

    public void setRequestWidth(int requestWidth) {
        this.requestWidth = requestWidth;
    }

    public int getRequestHeight() {
        return requestHeight;
    }

    public void setRequestHeight(int requestHeight) {
        this.requestHeight = requestHeight;
    }
}
