package com.buffalo.adsdk.config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.text.TextUtils;

import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.Const;
import com.buffalo.adsdk.InternalAdError;
import com.buffalo.utils.ThreadHelper;
import com.buffalo.utils.gaid.AdvertisingIdHelper;
import com.buffalo.utils.Commons;
import com.buffalo.utils.Logger;
import com.buffalo.utils.NetworkUtil;
import com.buffalo.utils.Networking;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RequestUFS {

    private static final String TAG = "RequestUFS";
    private static SharedPreferences sSharedPreferences;
    private final String PREFS_NAME = "cmcmadsdk_config";
    private static final int UFS_REQUESTTIME_DEFAULT = 24 * 60 * 60 * 1000;

    //用户性别，1表示男性，2表示女性，3表示未知
    public static final int GENDER_MAN = 1;
    public static final int GENDER_WOMAN = 2;
    public static final int GENDER_UNKNOW = 3;

    //age 用户年龄，1表示18~24岁，2表示25~30岁，3表示31~40岁，4表示41岁以上，5表示未知
    public static final int AGE_BETWEEN_18_24 = 1;
    public static final int AGE_BETWEEN_25_30 = 2;
    public static final int AGE_BETWEEN_31_40 = 3;
    public static final int AGE_ABOVE_41 = 4;
    public static final int AGE_UNKNOW = 5;

    private static final String SDK_SK1 = "26f65c14a3df9c62";
    private static final String SDK_SK2 = "2ba42a014f0c8e92";
    private static final String KEY_AGE = "age";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_INTERESTS = "interests";
    private static final String KEY_UFS_REQUEST_TIME = "ufs_request_time";

    private String mMid;
    private String mGaid;
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private static RequestUFS sInstance = null;
    private static int sAge = -1;
    private static int sGender = -1;
    private ConnectionChangeReceiver mConnectionChangeReceiver;


    public static RequestUFS getInstance() {
        if (sInstance == null) {
            sInstance = new RequestUFS();
        }
        return sInstance;
    }

    private RequestUFS() {
        mContext = AdManager.getContext();
        mGaid = AdvertisingIdHelper.getInstance().getGAId();
        mMid = AdManager.getMid();
        sSharedPreferences = mContext.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        mEditor = sSharedPreferences.edit();
    }

    public void requestUFSInfo() {
        if (!Commons.isMainProcess(mContext)) {
            Logger.d(TAG, "request error, please request ufs in main process");
            return;
        }

        ThreadHelper.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                requestUFSInfoInternal();
            }
        });
    }

    private void requestUFSInfoInternal() {
        if (!AdManager.isRequestUfs()) {
            Logger.d(TAG, "request error, please turn on switch");
            return;
        }

        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            Logger.d(TAG, "network is unavailable");
            registerConnChangeReveiver();
            return;
        }

        if (TextUtils.isEmpty(mGaid)) {
            Logger.d(TAG, "gaid is null, get gaid again");
            mGaid = AdvertisingIdHelper.getInstance().getGAId();
            if (TextUtils.isEmpty(mGaid)) {
                Logger.d(TAG, "gaid is null, cannot request ufs");
                return;
            }
        }

        Long lastTime = sSharedPreferences.getLong(KEY_UFS_REQUEST_TIME, 0L);
        Logger.d(TAG, "requestufs lasttime = " + lastTime);
        if (System.currentTimeMillis() - lastTime > UFS_REQUESTTIME_DEFAULT) {
            mEditor.putLong(KEY_UFS_REQUEST_TIME, System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= 9) {
                mEditor.apply();
            } else {
                mEditor.commit();
            }
            String androidId = Commons.getAndroidId();
            final String ufsParamsStr = buildParamsUFS(mMid, androidId, mGaid);
            Networking.get(Const.CONFIG_URL_UFS, ufsParamsStr, new Networking.HttpListener() {
                @Override
                public void onResponse(int responseCode, HashMap<String, String> headers,
                                       InputStream result, String encode, int contentLength) {
                    byte[] resultByte = new byte[0];
                    try {
                        resultByte = readInputSream(result);
                        if (resultByte != null && resultByte.length >= 0) {
                            String resultStr = decryptResult(resultByte);
                            saveResultInfo(resultStr);
                            unRegisterConnChangeReveiver();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int responseCode, InternalAdError error) {
                }
            });
        }
    }

    private byte[] readInputSream(InputStream inStream) throws IOException {
        if (inStream != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inStream.close();
            return outStream.toByteArray();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param result
     */
    private String decryptResult(byte[] result) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SDK_SK2.getBytes(), "AES");
            byte[] ivPadding = new byte[16];
            for (int i = 0, len = ivPadding.length; i < len; ++i) {
                ivPadding[i] = 0;
            }
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivPadding);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] data = cipher.doFinal(result);
            String json = new String(data);
            Logger.i(TAG, "resultJson=" + json);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String buildParamsUFS(String mid, String androidId, String gaId) {
        StringBuffer sb = new StringBuffer();
        sb.append("c=sdk");
        sb.append("&gaid=" + gaId);
        sb.append("&mid=" + mid);
        sb.append("&androidid=" + androidId);
        String sig = generateSigStr(sb.toString() + "&" + SDK_SK1);
        sb.append("&sig=" + sig);
        return sb.toString();
    }


    /**
     * @param 'sig=78b557c32da4937278fbafed4222d405 (md5串用16进制小写字母表示)'
     * @return
     */
    private String generateSigStr(String params) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(params.getBytes(), 0, params.length());
            String sb = new BigInteger(1, messageDigest.digest()).toString(16);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void saveResultInfo(String info) {
        Logger.i(TAG, "saveUFSInfo=" + info);
        try {
            JSONObject ufsInfoJson = new JSONObject(info);
            sAge = ufsInfoJson.optInt(KEY_AGE);
            sGender = ufsInfoJson.optInt(KEY_GENDER);
            JSONArray interestsArray = ufsInfoJson.getJSONArray(KEY_INTERESTS);
            mEditor.putInt(KEY_AGE, sAge);
            mEditor.putInt(KEY_GENDER, sGender);
            mEditor.putString(KEY_INTERESTS, interestsArray.toString());
            if (Build.VERSION.SDK_INT >= 9) {
                mEditor.apply();
            } else {
                mEditor.commit();
            }
        } catch (Exception e) {
        }
    }

    public static int getAgeRange() {
        if (sAge == -1) {
            try {
                sAge = sSharedPreferences.getInt(KEY_AGE, AGE_UNKNOW);
            } catch (Exception e) {
            }
        }
        return sAge;
    }

    public static int getGender() {
        if (sGender == -1) {
            try {
                sGender = sSharedPreferences.getInt(KEY_GENDER, GENDER_UNKNOW);
            } catch (Exception e) {
            }
        }
        return sGender;
    }

    private class ConnectionChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtil.isNetworkAvailable(mContext)) {
                Logger.d(TAG, "network changed : request again");
                requestUFSInfo();
            }
        }
    }

    private void unRegisterConnChangeReveiver() {
        try {
            if (mContext != null && mConnectionChangeReceiver != null) {
                mContext.getApplicationContext().unregisterReceiver(mConnectionChangeReceiver);
            }
        } catch (Exception e) {

        }
    }


    private void registerConnChangeReveiver() {
        try {
            if (mContext != null) {
                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                mConnectionChangeReceiver = new ConnectionChangeReceiver();
                mContext.getApplicationContext().registerReceiver(mConnectionChangeReceiver, filter);
            }
        } catch (Exception e) {

        }
    }

}


