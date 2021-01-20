package com.cmcm.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;


public class NetworkUtil {

	public static final int NETWORK_TYPE_WIFI = 3;
	public static final int NETWORK_TYPE_3G = 2;
	public static final int NETWORK_TYPE_2G = 1;
	public static final int NETWORK_TYPE_UNKNOWN = 0;
	public static final int NETWORK_TYPE_NONE = 4;
    public static final int NETWORK_TYPE_4G = 5;


	public static boolean IsWifiNetworkAvailable(Context context) {
		// Monitor network connections (Wi-Fi, GPRS, UMTS, etc.)
		// mobile 3G Data Network
		ConnectivityManager conmgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(conmgr == null) {
			return false;
		}
		
		NetworkInfo info = null;
		try {
			info = conmgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (info == null) {
				return false;
			}
		} catch (NullPointerException e) {
			return false;
		}
		
		State wifi = info.getState(); // 显示wifi连接状态
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
			return true;
		
		return false;
	}

	/**
	 * @author lin
	 * @param context
	 * @return 返回5种网络类型，0-->未知网络，1-->2G网络，2-->3G网络，3-->wifi网络，4-->无网络,5-->4G
	 * 注意：只有wifi类型的判断是准确的，其他类型相对都有适配问题
	 */
	public static int getNetworkState(Context context){
		if (context == null)
			return NETWORK_TYPE_UNKNOWN;

		int networkType = NETWORK_TYPE_NONE;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				int type = info.getType();
				if(type == ConnectivityManager.TYPE_WIFI){
					if (isWiFiActive(context)) {
						networkType = NETWORK_TYPE_WIFI;
					} else {
						networkType = NETWORK_TYPE_NONE;
					}
				} else {
					int subType = info.getSubtype();
					if (isMobile2G(subType)) {
						networkType = NETWORK_TYPE_2G;
					} else if (isMobile3G(subType)) {
						networkType = NETWORK_TYPE_3G;
					} else if (isMobile4G(subType)) {
						networkType = NETWORK_TYPE_4G;
					}
				}
			} else {
				networkType = NETWORK_TYPE_NONE;
			}
		} catch (Exception ex) {
			networkType = NETWORK_TYPE_UNKNOWN;
		}

		return networkType;
	}

    private static boolean isMobile4G(int subType) {
        if (subType == TelephonyManager.NETWORK_TYPE_LTE){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 判断Wifi是否可用
     * @return true:可用 false:不可用
     */
    public static boolean isWiFiActive(Context context) {
        if (context == null)
            return false;
        boolean bReturn = false;
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            bReturn = true;
        }
        return bReturn;
    }
    /**
     * 判断是否2G网络
     * @param type
     * @return
     */
    public static boolean isMobile2G(int type) {
		boolean is2G = false;
		switch (type) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_IDEN:
				is2G = true;
				break;
			default:
				is2G = false;
				break;
		}
		return is2G;
    }

	/**
	 * 判断是否是3G网络
	 *
	 * @param type
	 * @return
	 */
	private static boolean isMobile3G(int type) {
		boolean is3G = false;
		switch (type) {
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
			case TelephonyManager.NETWORK_TYPE_EHRPD:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				is3G = true;
				break;
			default:
				is3G = false;
				break;
		}
		return is3G;
	}

	public static boolean isMobileNetWork(Context context){
		int networkState = getNetworkState(context);
		if(networkState == NETWORK_TYPE_2G
				|| networkState == NETWORK_TYPE_3G
				|| networkState == NETWORK_TYPE_4G){
			return true;
		}
		return false;
	}

	/**
	 * 检查网络是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		try {
			if (context != null) {
				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
				if (connectivityManager != null) {
					NetworkInfo activeNetInfo = connectivityManager
							.getActiveNetworkInfo();// 获取网络的连接情况
					if (activeNetInfo != null && activeNetInfo.isAvailable()
							&& activeNetInfo.isConnected()) {
						return true;
					} else {
						return false;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

}
