package com.cmcm.adsdk;

/**
 * Created by i on 2015/12/1.
 */
public interface ImageDownloadListener {
    /**
     *
     * @param imageUrl 图片url
     * @param isOnlyCache 图片只从缓存中读取
     * @param listener
     */
    public void getBitmap(String imageUrl, boolean isOnlyCache, BitmapListener listener);
}
