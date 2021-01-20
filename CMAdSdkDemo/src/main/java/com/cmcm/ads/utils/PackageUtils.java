/**
 * @filename: Commons.java
 * @author: kebo<iamkebo@gmail.com>
 * @time: Dec 9, 2009
 * @description: 
 */
package com.cmcm.ads.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

/**
 * @author amas
 *
 */
public final class PackageUtils {

	public static boolean isHasPackage(Context c, String packageName) {
		if (null == c || null == packageName)
			return false;

		boolean bHas = false;
		try {
			PackageInfo packageInfo = c.getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS);
			if(packageInfo!=null&&packageInfo.applicationInfo!=null){
				bHas = packageInfo.applicationInfo.enabled;
			}
		} catch (/* NameNotFoundException */Exception e) {
			// 抛出找不到的异常，说明该程序已经被卸载
			bHas = false;
		}
		return bHas;
	}

	public static boolean isHasLoginGoogle(Context context) {
		AccountManager manager = AccountManager.get(context);
		Account[] accounts = manager.getAccounts();
		for (Account account : accounts) {
			if (account.type != null && account.type.equalsIgnoreCase("com.google")) {
				return true;
			}
		}
		return false;
	}

	public static boolean getPkgAvailable(Context c, String packageName) {
		if (null == c || TextUtils.isEmpty(packageName))
			return false;
		boolean flag = false;
		try {
			ApplicationInfo info = c.getPackageManager().getApplicationInfo(
				packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			flag = (info == null) ? false : info.enabled;
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static boolean isMarketInstalled(Context ctx){
		if (!isHasPackage(ctx, "com.android.vending")) {
			return false;
		}
		return true;
	}

	public static boolean isGPAvailable(Context ctx) {
		if (!isHasPackage(ctx, "com.android.vending")) {
			return false;
		}

		// 判断GP服务包
		PackageInfo gsfInfo = getPackageInfo(ctx, "com.google.android.gsf");
		if (null == gsfInfo || ((gsfInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1)) {
			return false;
		}

		return true;
	}

	public static PackageInfo getPackageInfo(Context c, String packageName) {
		if (null == c || null == packageName)
			return null;

		PackageInfo info = null;
		try {
			info = c.getPackageManager().getPackageInfo(packageName, 0);
		} catch (/* NameNotFoundException */Exception e) {
			// 抛出找不到的异常，说明该程序已经被卸载
			return null;
		}
		return info;
	}
	
	public static ApplicationInfo getApplicationInfo(Context context, String pkgName) {
		
		if (TextUtils.isEmpty(pkgName)) {
			return null;
		}
		
		try {
			PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
			if (pkgInfo != null) {
				ApplicationInfo appInfo = pkgInfo.applicationInfo;
				return appInfo;
			}
		} catch (Exception e) {
		}
		
		return null;
	}

	/**
	 * 获取APP版本号
	 *
	 * @param pContext Context
	 * @return AppVersionName
	 */
	public static String getAppVersionName(Context pContext) {
		String versionName = "";
		try {
			PackageManager pm = pContext.getPackageManager();
			PackageInfo info = pm.getPackageInfo(pContext.getPackageName(), 0);
			versionName = info.versionName;
			if(TextUtils.isEmpty(versionName)){
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

    /**
     * 获取VERSION号
     * @param pContext
     * @return
     */
    public static int getAppVersionCode(Context pContext) {
        int code = 0;
        try {
            PackageManager pm = pContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(pContext.getPackageName(), 0);
            code = info.versionCode;
            System.out.println("VERSION : " + code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

	/**
	 * 获取指定包的安装来源
	 * @param pm {@link PackageManager}
	 * @param packageName 包名
	 * @param fail 失败活取空后返回此值
	 * @return 安装来源(包名)
	 */
	public static String getInstallSource(PackageManager pm, final String packageName, final String fail) {
		String src = null;
		try {
			src = pm.getInstallerPackageName(packageName);
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return TextUtils.isEmpty(src) ? fail : src;
	}

	/**
	 * 获取自身安装来源
	 * @param context
	 * @param fail 失败后返回值
     * @return 安装来源
     */
	public static String getInstallSource(Context context, final String fail) {
		String source  = fail;
		try {
			PackageManager pm = context.getPackageManager();
			source = getInstallSource(pm, context.getPackageName(), fail);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return source;
	}

	public static boolean isSystemApp(ApplicationInfo info) {
		if (info == null) {
			return false;
		}
		return ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}


	// 必须都使用此方法打开外部activity,避免外部activity不存在而造成崩溃，
	public static boolean startActivity(Context context, Intent intent) {
		boolean bResult = true;
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			bResult = false;
		}
		return bResult;
	}


	public static Intent getGooglePalyIntent(String url, Context context) {
		if (url == null || url.length() == 0) {
			return null;
		}
		Intent it = new Intent(Intent.ACTION_VIEW);
		//   it.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
		it.setPackage("com.android.vending");
		if (!(context instanceof Activity)) {
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		} else {
			it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		}

		it.setData(Uri.parse(url));
		return it;
	}

	/**
	 * @param url
	 * @param context
	 * @return 某些需要知道source的跳转
	 */
	public static boolean startGooglePlayByUrl(String url, Context context) {

		try {
			Intent it = getGooglePalyIntent(url, context);
			if (it == null) {
				return false;
			}
			return startActivity(context, it);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isGooglePlayUrl(String url) {
		if (TextUtils.isEmpty(url))
			return false;
		if (url.startsWith("https://play.google.com") || url.startsWith("http://play.google.com") || url.startsWith("market:")) {
			return true;
		}
		return false;
	}

	public static void go2GooglePlay(Context context, String url) {
		if (context != null && !TextUtils.isEmpty(url)) {
			if (isGooglePlayUrl(url)) {
				openGooglePlayByUrl(url, context);
			} else {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(url);
				intent.setData(content_url);
				if(!(context instanceof Activity)){
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
				startActivity(context, intent);
			}

		}
	}


	/*
* 优先GP跳转，如果GP没有的话就让用户去选择跳转的方式
*/
	public static void openGooglePlayByUrl(String url, Context context) {
		boolean gpSuccess = false;
		if (PackageUtils.isGPAvailable(context))
		{
			if (!TextUtils.isEmpty(url))
				gpSuccess = startGooglePlayByUrl(url, context);
		}
		if (!gpSuccess)
		{
			if (!TextUtils.isEmpty(url))
			{
				Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(context, it);
			}
		}
	}
}
