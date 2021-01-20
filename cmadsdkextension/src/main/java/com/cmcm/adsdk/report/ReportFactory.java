package com.cmcm.adsdk.report;

import android.text.TextUtils;

import com.cmcm.utils.BackgroundThread;

import java.util.Map;

public class ReportFactory {
    public static final String VIEW = "view";
    public static final String CLICK = "click";
    public static final String INSERTVIEW = "insertview";
    public static final String USER_IMPRESSION = "user_impression";
    public static final String REQUEST_AD = "request_ad";
    public static final String DETAIL_CLICK = "detail_click";
    public static final String DOWN_SUCCESS = "down_success";
    public static final String INSTALL_SUCCESS = "install_success";
    public static final String CLICK_FAILED = "click_failed";
    public static final String VAST_PLAY = "vast_play";
    public static final String VAST_CLICK = "vast_click";
    public static final String MPA_SHOW= "mpa_show";
    public static final String MPA_CLICK = "mpa_click";
    public static final String VAST_PARSE_START = "vast_parse_start";
    public static final String VAST_PARSE_END = "vast_parse_end";
    public static final String JUMP_DETAIL_PAGE = "jump_detail_page";
    public static final String DETAIL_PAGE_SHOW = "detail_page_show";
    public static final String DETAIL_PAGE_CLOSE = "detail_page_close";

    public static final String EXTRA_KEY_DUPLICATE = "duple_status";
    public static final String EXTRA_VALUE_DEFAULT = "0";
    public static final String EXTRA_VALUE_NEW = "1";
    public static final String EXTRA_VALUE_DUPLICATE = "2";
    public static final String EXTRA_VALUE_REUSED = "3";

    private static BuinessDataItem toBuinessDataItem(String pkgName, int res) {
        return toBuinessDataItem(pkgName, res, -1, -1, -1);
    }

    private static BuinessDataItem toBuinessDataItem(String pkgName, int res,int duration, int playtime, int event) {
        BuinessDataItem item = new BuinessDataItem(pkgName, res, null, duration, playtime, event);
        return item;
    }

    private static BuinessPublicData parsePublicData(String type, String rf, String posid,int ac) {
        if(TextUtils.isEmpty(rf)){
            rf = null;
        }
        if(VIEW.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.AC_VIEW).rf(rf);
        }else if(CLICK.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.AC_CLICK).rf(rf);
        }else if(DETAIL_CLICK.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.DETAIL_CLICK).rf(rf);
        }else if(INSTALL_SUCCESS.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.AC_INSTALL).rf(rf);
        }else if(DOWN_SUCCESS.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.AC_SUCCESS).rf(rf);
        }else if(CLICK_FAILED.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.FAILED_CLICK).rf(rf);
        }else if(VAST_PLAY.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.VAST_PLAY).rf(rf);
        }else if (VAST_CLICK.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.VAST_CLICK).rf(rf);
        }else if(MPA_SHOW.equals(type)){
            return BuinessPublicData.CREATE(posid,ac).rf(rf);
        }else if (MPA_CLICK.equals(type)){
            return BuinessPublicData.CREATE(posid,ac).rf(rf);
        }else if(VAST_PARSE_START.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.VAST_PARSE_START).rf(rf);
        }else if(VAST_PARSE_END.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.VAST_PARSE_END).rf(rf);
        }else if(JUMP_DETAIL_PAGE.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.JUMP_DETAIL).rf(rf);
        }else if(DETAIL_PAGE_SHOW.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.DETAIL_SHOW).rf(rf);
        }else  if(DETAIL_PAGE_CLOSE.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.CANCEL_CLICK).rf(rf);
        } else if (REQUEST_AD.equals(type)) {
            return BuinessPublicData.CREATE(posid, BuinessPublicData.AC_REQUEST_AD).rf(rf);
        } else if(USER_IMPRESSION.equals(type)){
            return BuinessPublicData.CREATE(posid, BuinessPublicData.AC_USER_IMPRESSION).rf(rf);
        }
        return null;
    }

    // NOTE: Only used for thirdparty network report to rcv
    public static void reportNetworkAdLog(String type, String pkg, int reportRes,final String posid, String rf, Map<String, String > extraReportParams, String placementId, String rawJson,int ac, boolean isTest){
        if (TextUtils.isEmpty(type)){
            return ;
        }

        final BuinessPublicData publicData = parsePublicData(type, rf, posid, ac);
        if(publicData == null){
            return ;
        }
        publicData.setReportParam(extraReportParams);
        final BuinessDataItem bdi = toBuinessDataItem(pkg, reportRes);
        bdi.setExtraBuinessData(placementId, rawJson);
        if(isTest){
            bdi.setTestMode(isTest);
        }
        try {
            BuinessDataReporter bdr = new BuinessDataReporter();
            bdr.setData(bdi, publicData);
            BackgroundThread.executeAsyncTask(bdr);
        } catch (Throwable e) {
        }
    }
}
