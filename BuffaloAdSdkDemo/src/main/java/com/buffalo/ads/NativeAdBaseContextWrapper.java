package com.buffalo.ads;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class NativeAdBaseContextWrapper extends ContextWrapper {
    private Context mBase;

    private static final String GP_PACKAGE_NAME = "com.android.vending";
    private static final String ANDROID_BROWSER = "com.android.browser";

    // 用于新屏保点击FB后 如有密码锁，需要延时开启内容
    private boolean mIsScreenSaver;
    private boolean mDelayJump;
    private boolean mIsWebView = false;
    Intent mJumpIntent;
    boolean mIsSplashAd = false;
    private static ScreenOnReceiver sScreenOnReceiver;

    public NativeAdBaseContextWrapper(Context base) {
        this(base, false);
    }

    public NativeAdBaseContextWrapper(Context base, boolean isScreenSaver) {
        super(base.getApplicationContext());
        mBase = base.getApplicationContext();
        mIsScreenSaver = isScreenSaver;
        if (sScreenOnReceiver == null) {
            sScreenOnReceiver = new ScreenOnReceiver();
            sScreenOnReceiver.registerReceiver(mBase);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        mIsWebView = false;
        mJumpIntent = intent;
        if (null == mJumpIntent) {
            super.startActivity(mJumpIntent);
            return;
        }
        // 用户点击广告时作初始化
        if (mIsScreenSaver) {
            mDelayJump = false;
        }
        Uri uri = mJumpIntent.getData();
        mJumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mJumpIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        Intent oriIntent = new Intent(mJumpIntent);


        try {
            if (uri == null || uri.toString() == null)
                throw new Exception("null uri");

            String url = uri.toString().toLowerCase(Locale.getDefault());
            if (!mJumpIntent.getAction().equals(Intent.ACTION_VIEW)) {
                throw new Exception("not view action");
            }

            if (url.startsWith("http://") || url.startsWith("https://")) {
                if (url.startsWith("https://play.google.com/store/apps/details") ||
                        url.startsWith("http://play.google.com/store/apps/details")) {
                    updateCustomIntent(mJumpIntent, GP_PACKAGE_NAME);
                } else {
                    //云端控制是否采用内部webview进行跳转
                    boolean isJumpInnerWebView = checkJumpInnerWebView(url);
                    if (isJumpInnerWebView) {
                        mIsWebView = true;
                        jumpToInnerWebView(url);
                        return;
                    } else {
                        updateCustomIntent(mJumpIntent, ANDROID_BROWSER);
                    }
                }
            } else if (url.startsWith("market://")) {
                updateCustomIntent(mJumpIntent, GP_PACKAGE_NAME);
            }
            //super.startActivity(intent);
        } catch (Exception e) {
            super.startActivity(oriIntent);
        }

        if (mIsScreenSaver && shouldDelayJump()) {
            mDelayJump = true;
            sScreenOnReceiver.addOberver(new Observer() {
                @Override
                public void update(Observable observable, Object data) {
                    if (mDelayJump && !mIsWebView) {
                        mBase.startActivity(mJumpIntent);
                        mDelayJump = false;
                    }
                    sScreenOnReceiver.removeObserver();
                }
            });
        } else {
            if (!mIsWebView) {
                super.startActivity(mJumpIntent);
            }
        }
    }

    private void updateCustomIntent(Intent intent, String defCutomPackage) {
        List<ResolveInfo> infos = getResolveInfo(intent);
        if (null == infos) {
            return;
        }

        ResolveInfo defaultInfo = getDefaultInfo(infos);
        if (null != defaultInfo) {
            intent.setClassName(defaultInfo.activityInfo.packageName, defaultInfo.activityInfo.name);
            return;
        }

        boolean defPkgFound = false;
        for (ResolveInfo info : infos) {
            if (info.activityInfo != null && info.activityInfo.packageName.equalsIgnoreCase(defCutomPackage)) {
                intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                defPkgFound = true;
                break;
            }
        }

        if (!defPkgFound) {
            ResolveInfo info = infos.get(0);
            intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
        }
    }

    private List<ResolveInfo> getResolveInfo(Intent intent) {
        PackageManager pkgMgr = getPackageManager();
        if (null == pkgMgr) {
            return null;
        }

        List<ResolveInfo> infos = null;
        try {
            infos = pkgMgr.queryIntentActivities(intent, 0);
            if (null == infos) {
                return null;
            }
        } catch (Exception e) {
        }

        return infos;
    }

    private ResolveInfo getDefaultInfo(List<ResolveInfo> infos) {
        if (null == infos) {
            return null;
        }

        for (ResolveInfo info : infos) {
            if (null != info && info.isDefault) {
                return info;
            }
        }

        return null;
    }

    public void setIsSplashAd() {
        mIsSplashAd = true;
    }

    private boolean shouldDelayJump() {
        return (Build.VERSION.SDK_INT >= 16) && isKeyGuardLockedAndSecure(mBase);
    }

    private boolean checkJumpInnerWebView(String url) {
        //1代表使用内部webview打开,非1代表不使用内部webview打开跳转到默认浏览器,默认采用内部webview打开
        //魔方控制
        boolean cloudSet = true;

        //判断是否是fb redirect广告
        boolean isFBRedirect = url.startsWith("https://www.facebook.com/ads/conv_redirect/") ||
                url.startsWith("http://www.facebook.com/ads/conv_redirect/");
        return !mIsSplashAd && cloudSet && (!isFBRedirect);
    }

    private void jumpToInnerWebView(String url) {
        Intent intent = new Intent(mBase, WebViewBrowserAcitivty.class);
        if (!(mBase instanceof android.app.Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(WebViewBrowserAcitivty.EXTRA_URL, (url != null) ? url.trim() : null);
        intent.putExtra(WebViewBrowserAcitivty.EXTRA_FROM, "");
        mBase.startActivity(intent);
    }

    // 检查当前是否需要解锁屏幕且需要输入密码
    private static boolean isKeyGuardLockedAndSecure(Context context) {
        try {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return (hasPassword(context) || hasPattern(context) || hasSecure(context)) && keyguardManager.inKeyguardRestrictedInputMode();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 4.1及以上系统提供检测锁屏是否为密码锁的方法，低版本通过反射调用
     */
    private static boolean hasSecure(Context context) {
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                ///< android m上不需要ACCESS_KEYGUARD_SECURE_STORAGE此权限
                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                Method isKeyguardSecure = keyguardManager.getClass().getMethod("isKeyguardSecure");
                return ((Boolean) isKeyguardSecure.invoke(keyguardManager));
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private static boolean hasPattern(Context context) {
        int hasPattern;
        try {
            ///< android m上需要ACCESS_KEYGUARD_SECURE_STORAGE此权限
            hasPattern = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.LOCK_PATTERN_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            return false;
        } catch (SecurityException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        return hasPattern != 0;
    }

    private static final String PASSWORD_TYPE_KEY = "lockscreen.password_type";

    private static boolean hasPassword(Context context) {
        try {
            long mode = Settings.Secure.getLong(
                    context.getContentResolver(), PASSWORD_TYPE_KEY, 0);
            if (mode == DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC
                    || mode == DevicePolicyManager.PASSWORD_QUALITY_NUMERIC
                    || mode == DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC) {
                return true;
            } else {
                return false;
            }
        } catch (SecurityException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void setIsScreenSaver() {
        mIsScreenSaver = true;
    }

    private class ScreenOnReceiver extends BroadcastReceiver {
        private Observer mObservers;

        void addOberver(Observer observer) {
            mObservers = observer;
        }

        void removeObserver() {
            mObservers = null;
        }

        void registerReceiver(Context context) {
            if (context != null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_USER_PRESENT);
                context.registerReceiver(this, filter);
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }

            if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
                if (mObservers != null) {
                    mObservers.update(null, null);
                }
            }
        }
    }
}
