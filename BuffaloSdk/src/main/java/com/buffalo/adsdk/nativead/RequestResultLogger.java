package com.buffalo.adsdk.nativead;

import android.text.TextUtils;

import com.buffalo.adsdk.Const;
import com.buffalo.utils.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by shimiaolei on 16/1/2.
 */
public class RequestResultLogger {

    public static class Model {
        public static final String KEY_ADTYPE = "Adtype";
        public static final String KEY_IS_SUCCESS = "IsSuccess";
        public static final String KEY_ERRORINFO = "ErrorInfo";
        public static final String KEY_loadtime = "time";

        private boolean mIsSuccess;
        private String mFailReason;

        boolean mFinished = false;
        long mRequestBegin = 0;
        long mRequestEnd = 0;

        public Model() {
            mFinished = false;
            mRequestBegin = System.currentTimeMillis();
        }

        public void update(boolean isSuccess, String failReason) {
            this.mIsSuccess = isSuccess;
            this.mFailReason = failReason;
            mFinished = true;
            mRequestEnd = System.currentTimeMillis();
        }

        public boolean isSuccess(){
            return mIsSuccess;
        }

        public String getFailReason(){
            return mFailReason;
        }
    }

    String mLastResult;
    private Map<String, Model> mRequestResultMap = new HashMap<String, Model>();

    void reset() {
        mLastResult = null;
        mRequestResultMap.clear();
    }

    public int getRequestResultMapSize() {
        if (mRequestResultMap == null) {
            return 0;
        } else {
            return mRequestResultMap.size();
        }
    }

    public Model getFinishedItem(Object key) {
        Model model = mRequestResultMap.get(key);
        if (model != null && model.mFinished) {
            return model;
        }
        return null;
    }

    public boolean requestBegin(String adTypeName) {
        if (TextUtils.isEmpty(adTypeName)) {
            return false;
        }

        if (mRequestResultMap.containsKey(adTypeName)) {
            Logger.e(Const.TAG, adTypeName + " has begin load");
            return false;
        } else {
            Logger.i(Const.TAG, "begin load " + adTypeName + " to result map");
            mRequestResultMap.put(adTypeName, new Model());
            return true;
        }
    }

    public boolean requestEnd(String adTypeName, boolean isSuccess, String errorString) {
        if (TextUtils.isEmpty(adTypeName)) {
            return false;
        }

        if (!mRequestResultMap.containsKey(adTypeName)) {
            Logger.e(Const.TAG, adTypeName + "not-begin-yet, fail");
            return false;
        } else {
            Logger.i(Const.TAG, "push " + adTypeName + " to result map ,is scuccess:" + isSuccess);
            Model model = mRequestResultMap.get(adTypeName);
            model.update(isSuccess, errorString);
            return true;
        }
    }

    public void setRequestResult(String result) {
        mLastResult = result;
    }

    public String getLastResult() {
        return mLastResult;
    }

    public String getRequestErrorInfo() {
        JSONArray errorArr = new JSONArray();
        Iterator<String> iterator = mRequestResultMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Model resultModel = mRequestResultMap.get(key);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Model.KEY_ADTYPE, key);
                if (resultModel.mFinished) {
                    jsonObject.put(Model.KEY_IS_SUCCESS, resultModel.isSuccess());
                    jsonObject.put(Model.KEY_ERRORINFO, resultModel.getFailReason());
                    jsonObject.put(Model.KEY_loadtime, (resultModel.mRequestEnd - resultModel.mRequestBegin));
                }
                errorArr.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return errorArr.toString();
    }

    public boolean checkIfHaveAdLoadFinish() {
        for (Model model : mRequestResultMap.values()) {
            if (model.mFinished) {
                return true;
            }
        }
        return false;
    }
}
