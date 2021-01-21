package com.buffalo.picks.internal.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.buffalo.utils.ThreadHelper;
import com.buffalo.adsdk.AdManager;
import com.buffalo.picks.internal.AdWebViewUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MarketConfig {
    private static final int MIN_CACHE_TIME = 1800;//最小缓存时间，单位s
	private static final int DEFAULT_CACHE_TIME = 60 * 60;
	private static final int SPLASH_AD_DEFAULT_CACHE_TIME = 6 * DEFAULT_CACHE_TIME;
	public static final long EXPIRE_FOR_ONE_DAY = 1000L * 60 * 60 * 24;
	/**游戏盒子的图标作为一次有效展示的最短展现时间，单位毫秒*/
	public static final String KEY_MARKET_CONFIG = "config_last_save_time";
    public static final String POSID_EXPIRE_TIME_NAME = "_posid_expire_time";
    public static final String POSID_EXPIRE_DEF_TIME_NAME = "_posid_expire_def_time";
    public static final String POSID_EXPIRE_MIN_TIME_NAME = "_posid_expire_min_time";
    public static final String HTTPS_REQUEST = "https_request_url";
    public static final String HTTPS_REPORT = "https_report_url";
    public static final String SCHEME = "scheme";
    public static final String HOST = "host";
    public static final String USER_AGENT = "uer_agent";
    private static SharedPreferences mSp ;
    private static int mCacheTime;
	public static int getCacheTime() {
		if (mCacheTime < MIN_CACHE_TIME) {
            String value = getString("cache_time", "");
            mCacheTime = getFromSting(value);
            if(mCacheTime < MIN_CACHE_TIME){
                mCacheTime = DEFAULT_CACHE_TIME;
            }
		}
		return mCacheTime * 1000;
	}


	private static int getFromSting(String value) {
		if (TextUtils.isEmpty(value) || !TextUtils.isDigitsOnly(value)) {
			return -1;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static boolean saveFromJson(String json) {

		if (TextUtils.isEmpty(json)) {
			return false;
		}
		try {
			JSONObject jsonObject = new JSONObject(json);
            String cacheTime = jsonObject.optString("cm_softer_cache_time","");
            mCacheTime = getFromSting(cacheTime);
			putString("cache_time", cacheTime);
			putString("request_url", jsonObject.optString("request_url"));
			putString("req_timeout_ms", jsonObject.optString("req_timeout_ms"));
            String httpsRequestUrl = jsonObject.optString(HTTPS_REQUEST, "");
            if(!TextUtils.isEmpty(httpsRequestUrl)){
                URI uri = new URI(httpsRequestUrl);
                String scheme = uri.getScheme();
                String host = uri.getHost();
                if(!TextUtils.isEmpty(scheme)){
                    putString(SCHEME, scheme);
                }
                if(!TextUtils.isEmpty(host)){
                    putString(HOST, host);
                }
                putString(HTTPS_REQUEST, httpsRequestUrl);
            }

            String httpsReport = jsonObject.optString(HTTPS_REPORT, "");
            if(!TextUtils.isEmpty(httpsReport)){
                putString(HTTPS_REPORT, httpsReport);
            }
            // 获取并保存 pos_cache列表
            JSONArray posCacheList = jsonObject.getJSONArray("pos_cache");
            if(null == mPosCache){
                mPosCache = new HashMap<Long, Long>();
            }
            for(int i=0; i<posCacheList.length(); i++){
                JSONObject cacheItem = posCacheList.getJSONObject(i);
                long posid = cacheItem.getLong("posid");
                long cachetime = cacheItem.getLong("cache_time");
                mPosCache.put(posid, cachetime);
                String keyName = String.valueOf(posid) + POSID_EXPIRE_TIME_NAME;
                putLong(keyName, cachetime);
            }

            return true;
		} catch (Exception e) {
		}
		return false;
	}

    public static String getRequestScheme(){
        return getString(SCHEME, "");
    }
    public static String getRequestHost(){
        return getString(HOST, "");
    }

    public static String getRequestUrl(){
        return getString(HTTPS_REQUEST, "");
    }

    public static String getReportUrl(){
        return getString(HTTPS_REPORT, "");
    }

    private static Map<Long, Long> mPosCache;
    public static long getCacheTimeByPosid(Long posid){
        if(null == mPosCache){
            return getPosIdExpireTime(String.valueOf(posid));
        }

        long lMinTimeMS = getMinExpiredTimeS(String.valueOf(posid)) * 1000;
        long lTimeMS    = 0;
        Long cacheTime = mPosCache.get(posid);
        if(null == cacheTime || cacheTime.longValue()<=0){
            ///< get default
            lTimeMS = getDefaultExpiredTimeS(String.valueOf(posid)) * 1000L;
            if (lTimeMS <= 0) {
                lTimeMS = getCacheTime();
            }
        } else {
            lTimeMS = cacheTime.longValue() * 1000;
        }

        return Math.max(lMinTimeMS, lTimeMS);
    }

    public static long getPosIdExpireTime(String posid){
        // configuable cachetime
//        PicksConfig config = PicksMob.getInstance().getConfig();
//        if (config != null && config.enable_debug && config.cache_time > 0)
//            return config.cache_time * 1000;
        String keyName = posid + POSID_EXPIRE_TIME_NAME;
        long time = getLong(keyName, 0L)*1000;
        if(time <= 0){
            ///< get default
            time = getDefaultExpiredTimeS(String.valueOf(posid)) * 1000L;
            if (time <= 0) {
                time = getCacheTime();
            }
        }

        long lMinTime = getMinExpiredTimeS(posid) * 1000;
        return Math.max(lMinTime, time);
    }

    public static long getDefaultExpiredTimeS(String strPosId) {
        String keyName = strPosId + POSID_EXPIRE_DEF_TIME_NAME;
        return getLong(keyName, 0L);
    }

    public static void updateDefaultExpiredTime(String strPosId, long lDefaultExpiredTimeS) {
        if (lDefaultExpiredTimeS > DEFAULT_CACHE_TIME) {
            String keyName = strPosId + POSID_EXPIRE_DEF_TIME_NAME;
            long time = getLong(keyName, 0L);
            if (time == 0) {
                putLong(keyName, lDefaultExpiredTimeS);
            }
        }
    }

    public static long getMinExpiredTimeS(String strPosId) {
        String keyName = strPosId + POSID_EXPIRE_MIN_TIME_NAME;
        return getLong(keyName, 0L);
    }

    public static void updateMinExpiredTime(String strPosId, long lDefaultExpiredTimeS) {
        if (lDefaultExpiredTimeS > DEFAULT_CACHE_TIME) {
            String keyName = strPosId + POSID_EXPIRE_MIN_TIME_NAME;
            long time = getLong(keyName, 0L);
            if (time == 0) {
                putLong(keyName, lDefaultExpiredTimeS);
            }
        }
    }


	/**
	 * 谨慎使用此函数，调用后就不再过期了
	 * @param key
	 * @param expireTime
	 * @return
	 */
	public static boolean isExpired(String key,long expireTime){
		long lastSaveTime = getLong(key, 0L);

		if (System.currentTimeMillis() - lastSaveTime > expireTime) {
            putLong(key, System.currentTimeMillis());
			return true;
		}
		return false;
	}

    public static void commitOrApplyEditor(SharedPreferences.Editor editor){
        if(Build.VERSION.SDK_INT > 8){
            editor.apply();
        }else{
            editor.commit();
        }
    }

	public static boolean getDownloadServiceState(Context context){
		SharedPreferences sp = context.getSharedPreferences("market_request_download_service", Context.MODE_PRIVATE);
		return sp.getBoolean("is_resume", false);
	}

	public static void setDownloadServiceState(boolean isResume){
		SharedPreferences sp = AdManager.getContext().getSharedPreferences("market_request_download_service", Context.MODE_PRIVATE);
		sp.edit().putBoolean("is_resume", isResume).commit();
	}

    public static void putString(String key, String value){
        ensureSp();
        SharedPreferences.Editor e = mSp.edit();
        e.putString(key, value);
        commitOrApplyEditor(e);
    }

    public static void putLong(String key, Long value){
        ensureSp();
        SharedPreferences.Editor e = mSp.edit();
        e.putLong(key, value);
        commitOrApplyEditor(e);
    }

    public static void putInt(String key, int value){
        ensureSp();
        SharedPreferences.Editor e = mSp.edit();
        e.putInt(key, value);
        commitOrApplyEditor(e);
    }

    public static int getInt(String key, int defValue){
        ensureSp();
        return mSp.getInt(key, defValue);
    }

    public static String getString(String key, String defValue){
        ensureSp();
        return mSp.getString(key, defValue);
    }

    public static Long getLong(String key, Long defValue){
        ensureSp();
        return mSp.getLong(key, defValue);
    }

    private static void ensureSp() {
        if(null == mSp){
            mSp = AdManager.getContext().getSharedPreferences("market_config", Context.MODE_PRIVATE);
        }
    }
    private static String sUserAgent = null;

    private static int sInitUARetryTime = 2;

    public static String getCacheUserAgent(){
        if(TextUtils.isEmpty(sUserAgent)){
            sUserAgent = MarketConfig.getString(MarketConfig.USER_AGENT, "");
            if(TextUtils.isEmpty(sUserAgent) && sInitUARetryTime > 0){
                sInitUARetryTime --;
                ThreadHelper.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initUserAgent();
                    }
                });
            }
        }
        if(!TextUtils.isEmpty(sUserAgent)){
            return sUserAgent;
        }else{
            return System.getProperties().getProperty("http.agent");
        }
    }

    public static void initUserAgent(){
        if(TextUtils.isEmpty(sUserAgent)) {
            try {
                sUserAgent = MarketConfig.getString(MarketConfig.USER_AGENT, "");
                if(TextUtils.isEmpty(sUserAgent)) {
                    sUserAgent = AdWebViewUtils.getUserAgentString(AdManager.getContext());
                    MarketConfig.putString(MarketConfig.USER_AGENT, sUserAgent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(TextUtils.isEmpty(sUserAgent)){
                sUserAgent = System.getProperties().getProperty("http.agent");
            }
        }
    }
    private static final String CHINA_REQUEST_HOST = "sdk.mobad.ijinshan.com";
    private static final String WORLD_REQUEST_HOST = "ssdk.adkmob.com";

    public static String getHost() {
        if (AdManager.sIsCnVersion) {
            return CHINA_REQUEST_HOST;
        } else {
            return WORLD_REQUEST_HOST;
        }
    }

}
