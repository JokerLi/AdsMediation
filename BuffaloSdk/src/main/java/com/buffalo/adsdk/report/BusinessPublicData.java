package com.buffalo.adsdk.report;

import com.buffalo.adsdk.AdManager;
import com.buffalo.utils.gaid.AdvertisingIdHelper;

import java.util.Map;

public class BusinessPublicData {
    public static final int AC_REQUEST_AD = 3;
    public static final int AC_VIEW = 50;
    public static final int AC_CLICK = 60;
    public static final int AC_SUCCESS = 36;
    public static final int AC_INSTALL = 38;
    public static final int DETAIL_CLICK = 61;
    public static final int FAILED_CLICK = 62;
    public static final int CANCEL_CLICK = 101;
    public static final int VAST_CLICK = 64;
    public static final int VAST_PLAY = 54;
    public static final int DETAIL_SHOW = 51;
    public static final int JUMP_DETAIL = 71;
    public static final int VAST_PARSE_START = 110;
    public static final int VAST_PARSE_END = 111;
    public static final int AC_USER_IMPRESSION = 502;

    private int mMid;
    private String mPos;
    private int mAction;
    private String mExt = "";
    private String mGaid = "";
    private String mPlacementId;

    private Map<String, String> mReportParam;

    public static BusinessPublicData CREATE(String posId, String placementId, int action) {
        BusinessPublicData data = new BusinessPublicData();
        data.mPos = posId;
        data.mPlacementId = placementId;
        data.mMid = Integer.parseInt(AdManager.getMid());
        data.mAction = action;
        data.mGaid = AdvertisingIdHelper.getInstance().getGAId();
        return data;
    }

    public int getMid() {
        return mMid;
    }

    public String getPos() {
        return mPos;
    }

    public String getPlacementId() {
        return mPlacementId;
    }

    public int getAction() {
        return mAction;
    }

    public String getExt() {
        return mExt;
    }

    public String getGaid() {
        return mGaid;
    }

    public void setReportParam(Map<String, String> reportParam) {
        this.mReportParam = reportParam;
    }

    public Map<String, String> getReportParam() {
        return mReportParam;
    }
}

