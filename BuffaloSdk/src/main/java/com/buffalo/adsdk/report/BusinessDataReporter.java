package com.buffalo.adsdk.report;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.buffalo.utils.Networking;

import java.util.ArrayList;
import java.util.List;

/**
 * 商业数据上报
 */
public class BusinessDataReporter extends AsyncTask<Void, Void, Void> {
    private static final String CHINA_REPORT_HOST = "http://rcv.mobad.ijinshan.com/rp/";
    static final String WORLD_REPORT_HOST = "https://ssdk.adkmob.com/rp/";

    private BusinessPublicData mPublic;
    private List<BusinessDataItem> mItems;

    // ---------------------功能接口---------------------------

    /**
     * 单条上报调用
     */
    public void setData(BusinessDataItem item, BusinessPublicData publicData) {
        mPublic = publicData;
        mItems = new ArrayList<BusinessDataItem>();
        mItems.add(item);
    }

    /**
     * 批量上报调用
     */
    public void setData(List<BusinessDataItem> items, BusinessPublicData publicData) {
        mPublic = publicData;
        mItems = items;
    }

    private String getUploadUrl() {
        return WORLD_REPORT_HOST;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        if (mPublic == null) {
            return null;
        }

        String publicString = mPublic.toReportString();
        String datasString = getDatas();

        doReport(publicString, datasString);

        return null;
    }

    private void doReport(String publicString, String datasString) {
        if (TextUtils.isEmpty(publicString) || TextUtils.isEmpty(datasString)) {
            return;
        }
        Networking.post(getUploadUrl(), publicString + datasString, null);
    }

    private String getDatas() {
        if (mItems == null || mItems.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("&attach=[");
        boolean first = true;
        for (BusinessDataItem idx : mItems) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(idx.toReportString());
        }
        sb.append("]");
        return sb.toString();
    }
}
