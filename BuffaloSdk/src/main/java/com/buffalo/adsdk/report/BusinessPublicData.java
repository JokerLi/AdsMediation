package com.buffalo.adsdk.report;

import android.text.TextUtils;

import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.Const;
import com.buffalo.utils.Commons;
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

    private String mPos;
    private int mMid;
    private int mAc;
    private String mAid;
    private String mLan;
    private String mExt = "";
    /**
     * 如果为null，不需要最后传出去
     */
    private String mRf = null;
    /**
     * 等于-1不上报
     */
    private String mMcc = "";
    private String mGaid = "";
    private String mMnc = "";
    private String mChannelid;
    private int mLp = 0;
    private Map<String, String> mReportParam;

    public static BusinessPublicData CREATE(String posid, int ac) {
        BusinessPublicData data = new BusinessPublicData();
        data.mPos = posid;
        data.mMid = Integer.parseInt(AdManager.getMid());
        data.mAc = ac;
        data.mAid = Commons.getAndroidId();

        String language = Commons.getLanguage(AdManager.getContext());
        String country = Commons.getCountry(AdManager.getContext());
        data.mLan = String.format("%s_%s", language, country);

        data.mMcc = Commons.getMCC(AdManager.getContext());
        data.mGaid = AdvertisingIdHelper.getInstance().getGAId();
        data.mMnc = Commons.getMNC(AdManager.getContext());
        data.mChannelid = AdManager.getChannelId();
        return data;
    }

    public void setLpCode(int code) {
        this.mLp = code;
    }

    public void setReportParam(Map<String, String> reportParam) {
        this.mReportParam = reportParam;
    }

    public BusinessPublicData rf(String rf) {
        mRf = rf;
        return this;
    }

    // ac=50&pos=10&mid=101&aid=c3c4d512f16d9d5c&lan=en_us
    // FIXME: 2016/7/28
    public String toReportString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ac=" + mAc)
                .append("&pos=" + mPos)
                .append("&mid=" + mMid)
                .append("&aid=" + mAid)
                .append("&lan=" + mLan)
                .append("&ext=" + mExt)
                .append("&mcc=" + (TextUtils.isEmpty(mMcc) ? "" : mMcc))
                .append("&mnc=" + (TextUtils.isEmpty(mMnc) ? "" : mMnc))
                .append("&gaid=" + mGaid)
                .append("&pl=2")
                // FIXME: 2016/7/28
                //.append("&v=" + CmMarketHttpClient.PROTOCAL_VERSION)
                .append("&channelid=" + mChannelid)
                .append("&lp=" + mLp)
                .append("&sdkv=" + Const.VERSION)
                .append("&at=" + System.currentTimeMillis());
        if (mRf != null) {
            sb.append("&rf=" + mRf);
        }
        if (mReportParam != null && !mReportParam.isEmpty()) {
            for (String key : mReportParam.keySet()) {
                sb.append("&").append(key).append("=").append(mReportParam.get(key));
            }
        }
        return sb.toString();
    }
}

