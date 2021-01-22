package com.buffalo.adsdk.report;

import android.text.TextUtils;

import org.json.JSONObject;

public class BusinessDataItem {
    private String mPkgName;
    private int mSug;
    private int mRes;
    private String mDes;
    private int mSeq;
    private String mPlacementId;
    private String mRawJson;
    private int mDuration;
    private int mPlaytime;
    private int mEvent;
    private boolean mIsTestMode = false;

    public BusinessDataItem(String pkgName, int res, String des, int duration, int playtime, int event) {
        if (!TextUtils.isEmpty(pkgName)) {
            mPkgName = pkgName.replace("&", "_");
        }
        mSug = -1;
        mRes = res;
        mDes = des;
        mDuration = duration;
        mPlaytime = playtime;
        mEvent = event;
    }

    public void setExtraBuinessData(String placementId, String rawJson) {
        mPlacementId = placementId;
        mRawJson = rawJson;
    }

    public void setTestMode(boolean isTestMode) {
        mIsTestMode = isTestMode;
    }

    // {“pkg”:”com.bandsintown”,”sug”:1,”res”:10,”des”:”fe1234560”}
    public String toReportString() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("pkg", mPkgName);
            jsonObj.put("sug", mSug);
            jsonObj.put("res", mRes);
            jsonObj.put("des", TextUtils.isEmpty(mDes) ? "" : mDes);
            if (!TextUtils.isEmpty(mPlacementId)) {
                jsonObj.put("fbpos", mPlacementId);
            }
            if (!TextUtils.isEmpty(mRawJson)) {
                jsonObj.put("fbmeta", mRawJson);
            }
            if (mIsTestMode) {
                jsonObj.put("fbmess", "1");
            }
            if (mSeq > 0) {
                jsonObj.put("seq", mSeq);
            }
            if (mDuration > 0) {
                jsonObj.put("duration", mDuration);
                jsonObj.put("playtime", mPlaytime);
                jsonObj.put("event", mEvent);
            }
            return jsonObj.toString();
        } catch (Exception e) {
        }
        return null;
    }
}
