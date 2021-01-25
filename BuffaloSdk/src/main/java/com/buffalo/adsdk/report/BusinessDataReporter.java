package com.buffalo.adsdk.report;

import android.os.AsyncTask;
import android.os.Bundle;

import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.Const;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

/**
 * 商业数据上报
 */
public class BusinessDataReporter extends AsyncTask<Void, Void, Void> {
    private BusinessPublicData mPublic;
    private FirebaseAnalytics mFirebaseAnalytics;

    public BusinessDataReporter() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(AdManager.getContext());
    }

    public void setData(BusinessPublicData publicData) {
        mPublic = publicData;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        if (mPublic == null) {
            return null;
        }

        doReport();
        return null;
    }

    private void doReport() {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.REPORT_KEY.KEY_MID, mPublic.getMid());
        bundle.putString(Const.REPORT_KEY.KEY_POS, mPublic.getPos());
        bundle.putInt(Const.REPORT_KEY.KEY_ACTION, mPublic.getAction());
        bundle.putString(Const.REPORT_KEY.KEY_EXT, mPublic.getExt());
        bundle.putString(Const.REPORT_KEY.KEY_GAID, mPublic.getGaid());
        bundle.putString(Const.REPORT_KEY.KEY_PLACEMENT_ID, mPublic.getPlacementId());
        Map<String, String> reportParam = mPublic.getReportParam();
        if (reportParam != null && !reportParam.isEmpty()) {
            for (String key : reportParam.keySet()) {
                bundle.putString(key, reportParam.get(key));
            }
        }
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
