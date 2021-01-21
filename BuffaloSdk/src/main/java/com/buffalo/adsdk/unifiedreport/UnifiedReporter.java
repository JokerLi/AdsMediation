package com.buffalo.adsdk.unifiedreport;

import android.content.Context;
import android.text.TextUtils;

import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.CMBaseFactory;
import com.buffalo.adsdk.InternalAdError;
import com.buffalo.utils.Commons;
import com.buffalo.utils.NetworkUtil;
import com.buffalo.utils.Networking;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;

public class UnifiedReporter {
    private static final int AC_SHOW = 1;
    private static final int AC_CLICK = 2;
    private static UnifiedReporter sSelf;
    private String mReportUrl = "";
    private String mConstantParam = "";
    private Context mContext = null;
    private volatile boolean mIsInit = false;

    public static UnifiedReporter getInstance() {
        if (sSelf == null) {
            synchronized (UnifiedReporter.class) {
                if (sSelf == null) {
                    sSelf = new UnifiedReporter();
                }
            }
        }
        return sSelf;
    }

    public UnifiedReporter() {
        if (mContext != null) {
            init();
        }
    }

    private void init() {
        if (!mIsInit) {
            mContext = AdManager.getContext().getApplicationContext();
            mReportUrl = getReportUrl();
            mConstantParam = getConstantParam();
            mIsInit = true;
        }
    }


    //上报展示
    public void reportShow(int posid) {
        reportShow(posid, "");
    }

    //上报展示+包名
    public synchronized void reportShow(int posid, String extra) {
        report(posid, extra, AC_SHOW);
    }

    //上报点击
    public void reportClick(int posid) {
        reportClick(posid, "");
    }

    //上报点击+包名
    public synchronized void reportClick(int posid, String extra) {
        report(posid, extra, AC_CLICK);
    }

    private void report(final int posid, String extra, int type) {
        if (!mIsInit) {
            init();
        }
        if (mIsInit) {
            final StringBuffer reportUrl = new StringBuffer(mReportUrl);
            reportUrl.append("ac=" + type)
                    .append("&posid=" + posid)
                    .append("&" + getVariabletParam())
                    .append("&" + mConstantParam);
            if (!TextUtils.isEmpty(extra)) {
                reportUrl.append("&").append("extra=").append(extra);
            }
            Networking.get(reportUrl.toString(), new Networking.HttpListener() {
                @Override
                public void onResponse(int responseCode, HashMap<String, String> headers, InputStream result, String encode, int contentLength) {
                    //do nothing
                }

                @Override
                public void onError(int responseCode, InternalAdError error) {
                    CMBaseFactory factory = AdManager.createFactory();
                    if (factory != null) {
                        factory.doNetworkingReport(posid + "", "2", error.getErrorCode() + "");
                    }
                }
            });
        }
    }

    private String getReportUrl() {
        if (!AdManager.sIsCnVersion) {
            return "http://ud.adkmob.com/r/?";
        } else {
            return "http://ud.mobad.ijinshan.com/r/?";
        }
    }

    private String getConstantParam() {
        String model = android.os.Build.MODEL;
        StringBuffer ret = new StringBuffer();
        try {
            model = URLEncoder.encode(model, "utf-8");
            ret.append("pid=" + AdManager.getMid())
                    .append("&intl=2")
                    .append("&aid=" + Commons.getAndroidId())
                    .append("&resolution=" + Commons.getResolution(mContext))
                    .append("&brand=" + android.os.Build.BRAND)
                    .append("&model=" + model)
                    .append("&vercode=" + Commons.getAppVersionCode(mContext))
                    .append("&mcc=" + Commons.getMCC(mContext))
                    .append("&cn=" + AdManager.getChannelId())
                    .append("&os=" + android.os.Build.VERSION.RELEASE);
        } catch (Exception e) {
        }

        return ret.toString();
    }

    private String getVariabletParam() {
        StringBuffer ret = new StringBuffer();
        ret.append("cl=" + Commons.getLocale(mContext))
                .append("&nt=" + getNetType());
        return ret.toString();
    }

    private int getNetType() {
        int nRet = 0;
        if (NetworkUtil.isWiFiActive(mContext)) {
            nRet = 1;
        } else if (NetworkUtil.isMobileNetWork(mContext)) {
            nRet = 2;
        }
        return nRet;
    }
}
