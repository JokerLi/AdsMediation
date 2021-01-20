package com.cmcm.adsdk.report;


import android.annotation.SuppressLint;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.text.DecimalFormat;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by i on 2015/5/29. 128位AES 加密
 */
public class AESUtils {
    protected IvParameterSpec mIv;
    protected SecretKeySpec mKey;
    private int mVersion = 1;

    public IvParameterSpec getmIv() {
        return mIv;
    }

    public SecretKeySpec getmKey() {
        return mKey;
    }

    public int getmVersion() {
        return mVersion;
    }

    public byte[] encrypt(byte[] origData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, this.mKey, this.mIv);
            return cipher.doFinal(origData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(byte[] crypted) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, this.mKey, this.mIv);
            return cipher.doFinal(crypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private final static byte[] SDEFAULTKEY = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16
    };
    private final static String sKeys = "liebao%^&()@Io=-";

    public AESUtils(IvParameterSpec iv, SecretKeySpec key) {
        mIv = iv;
        mKey = key;
    }

    public AESUtils(byte[] iv, byte[] key) {
        mIv = new IvParameterSpec(iv);
        mKey = new SecretKeySpec(key, "AES");
    }

    /**
     * iv 采用随机生成
     * 密码采用设置
     */

    public AESUtils() {
        byte[] keys = new byte[16];
        new SecureRandom().nextBytes(keys);
        mIv = new IvParameterSpec(keys);
        byte[] screty = new byte[16];
        byte[] liebao = null;
        try {
            liebao = sKeys.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < screty.length && i < liebao.length && liebao != null; i++) {
            screty[i] = liebao[i];
        }
        mKey = new SecretKeySpec(screty, "AES");
    }

    public AESUtils(byte[] iv) {
        this(iv, SDEFAULTKEY);
    }

    @SuppressLint({ "NewApi", "InlinedApi" })
	public String base64Encrypt(String content) {
        try {
            if (null != content) {
                String version = new String(new DecimalFormat("0000").format(mVersion));
                int ivLen = mIv.getIV().length;
                int verLen = version.getBytes("utf-8").length;
                byte[] encode = encrypt(content.getBytes("utf-8"));
                int enLen = encode.length;
                byte[] end = new byte[ivLen + verLen + enLen];
                System.arraycopy(mIv.getIV(), 0, end, 0, ivLen);
                System.arraycopy(version.getBytes("utf-8"), 0, end, ivLen, verLen);
                System.arraycopy(encode, 0, end, ivLen + verLen, enLen);
                return Base64.encodeToString(end, Base64.NO_WRAP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

