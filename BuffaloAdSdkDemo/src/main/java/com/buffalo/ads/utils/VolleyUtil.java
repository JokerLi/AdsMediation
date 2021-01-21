package com.buffalo.ads.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.buffalo.adsdk.AdManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class VolleyUtil {

    public static ImageLoader sImageLoader = new ImageLoader(Volley.newRequestQueue(
            AdManager.getContext()), new BitmapLruCache());

    public static void loadImage(final ImageView view, String url) {
        doCleanAllCache();

        sImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (null != response.getBitmap()) {
                    view.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    public static void loadImage(String url, ImageLoader.ImageListener listener) {
        doCleanAllCache();

        sImageLoader.get(url, listener);
    }

    private static ImageLoader.ImageListener preloadImageListener;

    private static void doCleanAllCache() {
        /** Default on-disk cache directory. */
        final String DEFAULT_CACHE_DIR = "volley";
        File cacheDir = new File(AdManager.getContext().getCacheDir(), DEFAULT_CACHE_DIR);

        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }


    public static void preloadImage(final String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }

        doCleanAllCache();

        //预加载的复用同一个ImageListener
        if (null == preloadImageListener) {
            preloadImageListener = new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            };
        }
        sImageLoader.get(imageUrl, preloadImageListener);
    }

    public static class BitmapLruCache extends android.support.v4.util.LruCache<String, Bitmap> implements ImageLoader.ImageCache {
        public static int getDefaultLruCacheSize() {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;
            return cacheSize;
        }

        public BitmapLruCache() {
            this(getDefaultLruCacheSize());
        }

        public BitmapLruCache(int sizeInKiloBytes) {
            super(sizeInKiloBytes);
        }

        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            if (bitmap == null) {
                return 0;
            }
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
        }

        @Override
        public Bitmap getBitmap(String url) {

            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);

        }
    }

    public static void getStream(String url, final CallBack callBack) {
        RequestQueue queue = Volley.newRequestQueue(AdManager.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (callBack != null) {
                            if (!TextUtils.isEmpty(response)) {
                                InputStream in = new ByteArrayInputStream(response.getBytes());
                                callBack.onResponse(in);
                            } else {
                                callBack.onErrorResponse(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callBack != null) {
                    callBack.onErrorResponse(error.toString());
                }
            }
        });
        // 把这个请求加入请求队列
        queue.add(stringRequest);
    }

    public interface CallBack {
        void onResponse(InputStream in);

        void onErrorResponse(String response);
    }

}
