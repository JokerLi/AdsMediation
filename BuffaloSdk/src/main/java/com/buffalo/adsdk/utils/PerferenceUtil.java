package com.buffalo.adsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import com.buffalo.adsdk.AdManager;
import com.buffalo.utils.Logger;

import java.util.Map;

public class PerferenceUtil {
	
	private static SharedPreferences sSharePreference;
	private static String spfName = String.format("%s_%s", "adsdk", AdManager.getMid());;
	private static final String key = "config_cache";
	private static final String TAG = "PerferenceUtil";



	public synchronized static String getCacheJsonStr(String defValue){
		try{
			if(sSharePreference == null) {
				sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
			}
			String value = sSharePreference.getString(key, defValue);
			return value;
		}catch(Exception e){
			Logger.d(TAG, "get cache json error..." + e.getMessage());
			return "";
		}
	}
	
	public synchronized static void saveCacheJsonStr(String value) {
		Editor editor = null;
		try {
			if(sSharePreference == null){
				sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
			}
			editor = sSharePreference.edit();
			editor.putString(key,value);
            applyEditor(editor);
		} catch (Exception e) {
			Logger.d(TAG, "save cache json error..." + e.getMessage());
		}
	}

    public static void applyEditor(Editor editor) {
        if(Build.VERSION.SDK_INT >= 9){
            editor.apply();
        }else {
            editor.commit();
        }
    }

	public static void putString(String key, String value){
		if(sSharePreference == null){
			sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
		}
		Editor editor = sSharePreference.edit();
		editor.putString(key, value);
		applyEditor(editor);
	}

	public static String getString(String key, String defaultValue){
		if(sSharePreference == null){
			sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
		}
		return sSharePreference.getString(key, defaultValue);
	}

	public static void putBoolean(String key, boolean value){
		if(sSharePreference == null){
			sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
		}
		Editor editor = sSharePreference.edit();
		editor.putBoolean(key, value);
		applyEditor(editor);
	}

	public static boolean getBoolean(String key, boolean defaultValue){
		if(sSharePreference == null){
			sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
		}
		return sSharePreference.getBoolean(key, defaultValue);
	}

	public static void putInt(String key, int value){
		if(sSharePreference == null){
			sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
		}
		Editor editor = sSharePreference.edit();
		editor.putInt(key, value);
		applyEditor(editor);
	}

	public static int getInt(String key, int defaultValue){
		if(sSharePreference == null){
			sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
		}
		return sSharePreference.getInt(key, defaultValue);
	}

	public static void putLong(String key, long value){
		if(sSharePreference == null){
			sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
		}
		Editor editor = sSharePreference.edit();
		editor.putLong(key, value);
		applyEditor(editor);
	}

	public static long getLong(String key, long defaultValue){
		if(sSharePreference == null){
			sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
		}
		return sSharePreference.getLong(key, defaultValue);
	}

	public static Map<String, ?> getAll(){
		if(sSharePreference == null){
			sSharePreference = AdManager.getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
		}
		return sSharePreference.getAll();
	}
}
