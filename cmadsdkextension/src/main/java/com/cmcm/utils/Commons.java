/**
 * @filename: Commons.java
 * @author: kebo<iamkebo@gmail.com>
 * @time: Dec 9, 2009
 * @description: 
 */
package com.cmcm.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.cmcm.adsdk.CMAdManager;
import com.cmcm.utils.gaid.AdvertisingIdHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author amas
 *
 */
public final class Commons {
	public static String getIMEI(Context context) {
		if (context == null)
			return null;

		try {
			final TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getDeviceId();
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isHasPackage(Context c, String packageName) {
		if (null == c || null == packageName)
			return false;

        List<String> pkgNames = PackageManagerWrapper.getInstance().getPkgNameList(Build.VERSION.SDK_INT>20?true:false);
        if(null != pkgNames && pkgNames.size() > 0){
            return pkgNames.contains(packageName);
        }

		boolean bHas = true;
		try {
			c.getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS);
		} catch (/* NameNotFoundException */Exception e) {
			// 抛出找不到的异常，说明该程序已经被卸载
			bHas = false;
		}
		return bHas;
	}


	public static boolean isUserApp(ApplicationInfo info) {
		return !((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}

	public static final boolean isWebViewProbablyCorrupt(Context context) {
		SQLiteDatabase cacheDb = null;
		// only do dirty check when the cache db are not existed!!!
		try {
			File cachedb = new File("/data/data/" + context.getPackageName() + "/databases/" + "webviewCache.db");
			if (cachedb.exists()) {
				return false;
			}
			cacheDb = context.openOrCreateDatabase("webviewCache.db", 0, null);
			if (cacheDb != null) {
				cacheDb.close();
				cacheDb = null;
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cacheDb != null) {
				cacheDb.close();
			}
		}
		return true;
	}

	// 必须都使用此方法打开外部activity,避免外部activity不存在而造成崩溃，
	public static boolean startActivity(Context context, Intent intent) {
		boolean bResult = true;
		try {
			if(!(context instanceof Activity)){
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
		} catch (Exception e) {
			bResult = false;
		}
		return bResult;
	}

    public static void openGooglePlayByUrl(String url, Context context) {
		if (!TextUtils.isEmpty(url)){
			Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			Commons.startActivity(context, it);
		}
    }

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	public static void openApp(Context context, String packageName) {
		PackageManager pm = CMAdManager.getContext().getPackageManager();
		Intent intent = null;
		try {
			intent = pm.getLaunchIntentForPackage(packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null != intent){
			Commons.startActivity(context, intent);
		}
	}
	
	/**
	 * 获取手机分辨率  (height x width)
	 * @param context
	 * @return
	 */
	public static String getResolution(Context context) {
		try {
			return String.format(Locale.US,"%d*%d",getScreenHeight(context),getScreenWidth(context));
		}catch (Exception e){
			return "";
		}
	}

	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/********************************* cm_act_active **************************************/

	private static String MCC;
	private static String MNC;
	public static void initMNC_MNC(Context context){
		if (context != null){
			final TelephonyManager tm = (TelephonyManager)context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String mcc_mnc = tm.getSimOperator();
			if(!TextUtils.isEmpty(mcc_mnc)){
				if (mcc_mnc.length() >= 3) {
					MCC = mcc_mnc.substring(0, 3);
				}
				if(mcc_mnc.length() >= 5){
					MNC = mcc_mnc.substring(3, 5);
				}
			}
		}
	}

	public static String getMCC(Context context) {
		if(TextUtils.isEmpty(MCC)){
			initMNC_MNC(context);
		}
		return MCC;
	}

	public static String getMNC(Context context) {
		if(TextUtils.isEmpty(MNC)){
			initMNC_MNC(context);
		}
		return MNC;
	}

	public static String getLanguage(Context context) {
		Locale locale = getLocale(context);
		return locale != null ? locale.getLanguage() : null;
	}

	public static String getCountry(Context context){
		Locale locale = getLocale(context);
		return locale != null ? locale.getCountry() : null;
	}

	public static Locale getLocale(Context context) {
		Locale locale = null ;
		if(null == context){
			locale= Locale.getDefault();
		}else{
			Resources rs = context.getResources();
			if(null != rs){
				Configuration config = rs.getConfiguration();
				if(null != config){
					locale = config.locale;
				}
				if(null == locale){
					locale= Locale.getDefault();
				}
			}else{
				locale= Locale.getDefault();
			}
		}
		return locale;
	}


    /**
     * 獲取系統屬性
     *
     * @param key
     *            鍵值
     * @param fail
     *            失敗或為空時返回此值
     * @return
     */
    public static String SP2(String key, String fail) {
        String value = getSystemProperties(key);
        if(TextUtils.isEmpty(value)){
            value = Build.MODEL;
        }
        return !TextUtils.isEmpty(value) ? value : fail;
    }

	public static boolean isScreenOn(Context context){
		if (context == null) {
			return false;
		}
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		return (pm != null) ? pm.isScreenOn() : false;
	}

	/**
	 * 获取安卓ID
	 * @param context
	 * @return
	 */
	private static String sAndroidID = "";
	private static Object sAndroidIDLock = new Object();
	public static String getAndroidId() {
		Context context = CMAdManager.getContext();
		if (TextUtils.isEmpty(sAndroidID)) {
			synchronized (sAndroidIDLock) {
				if (TextUtils.isEmpty(sAndroidID)) {
					String androidId = "";
					try {
						androidId = Settings.System.getString(
								context.getContentResolver(),
								Settings.System.ANDROID_ID);
						if(!TextUtils.isEmpty(androidId)){
							sAndroidID = androidId;
						}
					} catch (Exception e) {
					}
				}
			}
		}
		return sAndroidID;
	}

	public static int range(int n, int min, int max) {
		if (n <= min) {
			return min;
		} else if (n >= max) {
			return max;
		}
		return n;
	}


	public static boolean openAppByDeeplink(Context context, String pkgName, String link){
		if (TextUtils.isEmpty(link)){
			openApp(context, pkgName);
		}else{
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
			return startActivity(context, intent);
		}
		return true;
	}

	private static final String PKG_NAME_SYSTEMPROPERTIES = "android.os.SystemProperties";

	public static String getSystemProperties(String key){
		try {
			Method method = Class.forName(PKG_NAME_SYSTEMPROPERTIES).getMethod("get", String.class);
			return (String)method.invoke(null, key);
		} catch (Exception e) {
		}
		return "";
	}

	public static boolean isMiui() {
		String ver = getSystemVariable("ro.miui.ui.version.name", "UNKNOWN");
		if(ver.equals("V5") || (ver.equalsIgnoreCase("V6")) || ver.equalsIgnoreCase("V7")) {
			return true;
		}
		return false;
	}

	public static String getSystemVariable(String key, String defValue){
		try {
			Method method = Class.forName(PKG_NAME_SYSTEMPROPERTIES).getMethod("get", String.class, String.class);
			return (String)method.invoke(null, key, defValue);
		} catch (Exception e) {
		}
		return defValue;
	}

	public static int getAppVersionCode(Context context){
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (Exception e){
			e.printStackTrace();
		}
		return  0;
	}
	public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";
	public static byte[] encrypt(byte[] key, byte[] data) throws Exception {
		Key deskey = keyGenerator(new String(key));
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] bOut = cipher.doFinal(data);
		return bOut;
	}

	private static Key keyGenerator(String keyStr) throws Exception {
		byte input[] = hexString2Bytes(keyStr);
		DESKeySpec desKeySpec = new DESKeySpec(input);
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
		Key convertKey = secretKeyFactory.generateSecret(desKeySpec);
		return convertKey;
	}

	public static byte[] hexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}

	private static int parse(char c) {
		if (c >= 'a') return (c - 'a' + 10) & 0x0f;
		if (c >= 'A') return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}

	public static String toHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	// FIXME: 2016/7/12 
	/*public static int getAdAppShowType(INativeAd ad){
		if(ad != null){
			Object adObject = ad.getAdObject();
			if(adObject instanceof OrionNativeAd){
				return (((OrionNativeAd) adObject).getRawAd()).getAppShowType();
			}
		}
		return -1;
	}*/

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static boolean isMainProcess(Context context){
		if(context == null){
			return false;
		}
		String pkgName = context.getPackageName();
		try {
			String currentProcessName = getCurProcessName(context);
			return pkgName.equals(currentProcessName);
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		if(mActivityManager == null || mActivityManager.getRunningAppProcesses() == null){
			return null;
		}
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {

				return appProcess.processName;
			}
		}
		return null;
	}

	public final static boolean isNumeric(String s) {
		if (s != null && !"".equals(s.trim())) {
			return s.matches("^[0-9]*$");
		}
		return false;
	}

	// FIXME: 2016/7/12
	/*public static String getPkgUrl(INativeAd ad){
		if(ad != null){
			Object adObject = ad.getAdObject();
			if(adObject instanceof OrionNativeAd){
				return (((OrionNativeAd) adObject).getRawAd()).getPkgUrl();
			}
		}

		return null;
	}

	public static String getAdPkg(INativeAd ad){
		if(ad != null){
			Object adObject = ad.getAdObject();
			if(adObject instanceof OrionNativeAd){
				return (((OrionNativeAd) adObject).getRawAd()).getPkg();
			}
		}
		return null;
	}*/


    public static void putValueIntoJson(JSONObject object, String key, String value) throws JSONException {
        if(TextUtils.isEmpty(key) || object == null){
            return;
        }
        if(TextUtils.isEmpty(value)){
            object.put(key, "");
        }else {
            object.put(key, value);
        }
    }

	public static String getGAId(){
		return AdvertisingIdHelper.getInstance().getGAId();
	}

	public static boolean getTrackFlag(){
		return AdvertisingIdHelper.getInstance().getTrackFlag();
	}
}
