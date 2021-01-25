package com.buffalo.adsdk.report;

import android.text.TextUtils;

import com.buffalo.utils.BackgroundThread;

import java.util.Map;

public class ReportFactory {
    public static final String PAGE_VIEW = "page_view";
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
    public static final String MPA_SHOW = "mpa_show";
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

    private static BusinessPublicData parsePublicData(String type, String posid, int ac, String placementId) {
        if (VIEW.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.AC_VIEW);
        } else if (CLICK.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.AC_CLICK);
        } else if (DETAIL_CLICK.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.DETAIL_CLICK);
        } else if (INSTALL_SUCCESS.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.AC_INSTALL);
        } else if (DOWN_SUCCESS.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.AC_SUCCESS);
        } else if (CLICK_FAILED.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.FAILED_CLICK);
        } else if (VAST_PLAY.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.VAST_PLAY);
        } else if (VAST_CLICK.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.VAST_CLICK);
        } else if (MPA_SHOW.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, ac);
        } else if (MPA_CLICK.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, ac);
        } else if (VAST_PARSE_START.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.VAST_PARSE_START);
        } else if (VAST_PARSE_END.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.VAST_PARSE_END);
        } else if (JUMP_DETAIL_PAGE.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.JUMP_DETAIL);
        } else if (DETAIL_PAGE_SHOW.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.DETAIL_SHOW);
        } else if (DETAIL_PAGE_CLOSE.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.CANCEL_CLICK);
        } else if (REQUEST_AD.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.AC_REQUEST_AD);
        } else if (USER_IMPRESSION.equals(type)) {
            return BusinessPublicData.CREATE(posid, placementId, BusinessPublicData.AC_USER_IMPRESSION);
        }
        return null;
    }

    public static void reportNetworkAdLog(String type, final String posId, Map<String, String> extraReportParams, String placementId, int action) {
        if (TextUtils.isEmpty(type)) {
            return;
        }

        final BusinessPublicData publicData = parsePublicData(type, posId, action, placementId);
        if (publicData == null) {
            return;
        }
        publicData.setReportParam(extraReportParams);
        try {
            BusinessDataReporter bdr = new BusinessDataReporter();
            bdr.setData(publicData);
            BackgroundThread.executeAsyncTask(bdr);
        } catch (Throwable e) {
        }
    }
}
