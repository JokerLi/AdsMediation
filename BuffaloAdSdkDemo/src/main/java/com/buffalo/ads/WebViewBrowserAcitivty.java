package com.buffalo.ads;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.buffalo.ads.R;
import com.buffalo.ads.utils.PackageUtils;


/**
 * Created by chenhao on 16/11/19.
 */

public class WebViewBrowserAcitivty extends Activity implements View.OnClickListener {
    public static final String EXTRA_URL = "browser.intent.extra.URL";
    public static final String EXTRA_FROM = "extra_from";
    private WebView webView;
    private TextView mWebTitle;
    private TextView mBackTv;
    private String mOriginUrl;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_webviewbrowser);
        this.webView = (WebView) findViewById(R.id.web_view);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            mOriginUrl = extras.getString(EXTRA_URL);
        }
        mBackTv = (TextView) findViewById(R.id.webview_back);
        mWebTitle = (TextView) findViewById(R.id.title);
        mBackTv.setOnClickListener(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(!isMultitouchSupported());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebviewClient());
        webView.loadUrl(mOriginUrl);
    }

    protected void onPause() {
        super.onPause();
        pauseWebView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void pauseWebView() {
        if (null != webView) {
            try {
                webView.onPause();
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                    webView.pauseTimers();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onResume() {
        super.onResume();
        resumeWebView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void resumeWebView() {
        if(null != webView) {
            try {
                webView.onResume();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    webView.resumeTimers();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        destroyWebView();
    }

    private void destroyWebView() {
        if (null != webView) {
            try {
                ViewGroup viewGroup = (ViewGroup) webView.getParent();
                if (null != viewGroup) {
                    viewGroup.removeView(webView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    private boolean isMultitouchSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH);
    }

    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.webview_back){
            finish();
        }
    }

    class MyWebviewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            super.onPageStarted(webview, url, favicon);
        }

        @Override
        public void onPageFinished(WebView webview, String url) {
            super.onPageFinished(webview, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(PackageUtils.isGooglePlayUrl(url)){
                PackageUtils.go2GooglePlay(WebViewBrowserAcitivty.this, url);
                finish();
            }else {
                return super.shouldOverrideUrlLoading(view, url);
            }
            return true;

        }
    }

    class MyWebChromeClient extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title){
            if(mWebTitle != null){
                mWebTitle.setText(title);
            }

        }
    }

    /**
     * 将cookie同步到WebView
     * @param url WebView要加载的url
     * @param cookie 要同步的cookie
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public boolean syncCookie(String url,String cookie) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this.getApplicationContext());
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, cookie);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
        String newCookie = cookieManager.getCookie(url);
        return !TextUtils.isEmpty(newCookie);
    }

}
