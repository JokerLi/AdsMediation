package com.cmcm.adsdk;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenhao on 2015/7/27.
 */
public class CMRequestParams {

    public static final String KEY_REPORT_SHOW_IGNORE_VIEW = "report_show_ignore_view";
    public static final String KEY_BANNER_VIEW_SIZE = "key_banner_view_size";
    public static final String KEY_SELECT_ALL_PRIORITYAD = "key_select_all_priority";
    public static final String KEY_PICKS_LOAD_NUM = "key_picks_load_num";
    public static final String KEY_FILTER_ADMOB_INSTALL_AD = "filer_admob_install_ad";
    public static final String KEY_FILTER_ADMOB_CONTENT_AD = "filer_admob_content_ad";
    public static final String KEY_EXTRA_OBJECT = "key_extra_object";
    public static final String KEY_TAB_ID = "key_tab_id";
    public static final String KEY_IS_TOP = "key_is_top";

    protected Map<String, Object> mParams = null;
    public CMRequestParams() {
        mParams = new HashMap<String, Object>();
    }

    public void setExposeExtra(String key, Object value) {
        if (mParams != null && !TextUtils.isEmpty(key) && value != null) {
            mParams.put(key, value);
        }
    }

    public Object getExposeExtra(String key) {
        if (mParams != null && !TextUtils.isEmpty(key)) {
            if (mParams.containsKey(key)) {
                return mParams.get(key);
            }
        }
        return null;
    }


    /**
     *
     * @param ignoreView 上报的时候忽略View是否真的展示
     */
    public void setReportShowIgnoreView(boolean ignoreView){
        if(mParams != null) {
            mParams.put(KEY_REPORT_SHOW_IGNORE_VIEW, ignoreView);
        }
    }


    /**
     *
     * @return 是否忽略View真正展示就上报，默认不忽略
     */
    public boolean getReportShowIgnoreView(){
        if(mParams != null) {
            Object isIgnore = mParams.get(KEY_REPORT_SHOW_IGNORE_VIEW);
            if (null != isIgnore) {
                return Boolean.valueOf(isIgnore.toString());
            }
        }
        //默认不忽略
        return false;
    }



    /**
     */
    public void setSelectAllPriorityAd(boolean select){
        if(mParams != null) {
            mParams.put(KEY_SELECT_ALL_PRIORITYAD, select);
        }
    }


    /**
     *
     */
    public boolean isSelectAllPriorityAd(){
        if(mParams != null) {
            if(mParams.containsKey(KEY_SELECT_ALL_PRIORITYAD)){
                return (boolean) mParams.get(KEY_SELECT_ALL_PRIORITYAD);
            }
        }
        return true;
    }


    /**
     */
    public void setPicksLoadNum(int num){
        if(mParams != null) {
            mParams.put(KEY_PICKS_LOAD_NUM, num);
        }
    }


    /**
     *默认返回0
     */
    public int getPicksLoadNum(){
        if(mParams != null) {
            if(mParams.containsKey(KEY_PICKS_LOAD_NUM)){
                return (Integer) mParams.get(KEY_PICKS_LOAD_NUM);
            }
        }
        return 0;
    }

    public void setFilterAdmobInstallAd(boolean filter){
        if(mParams != null){
            mParams.put(KEY_FILTER_ADMOB_INSTALL_AD, filter);
        }
    }

    //默认不过滤
    public boolean isFilterAdmobInstallAd(){
        if(mParams != null && mParams.containsKey(KEY_FILTER_ADMOB_INSTALL_AD)){
            return (Boolean)mParams.get(KEY_FILTER_ADMOB_INSTALL_AD);
        }
        return false;
    }


    public void setFilterAdmobContentAd(boolean filter){
        if(mParams != null){
            mParams.put(KEY_FILTER_ADMOB_CONTENT_AD, filter);
        }
    }

    //默认不过滤
    public boolean isFilterAdmobContentAd(){
        if(mParams != null && mParams.containsKey(KEY_FILTER_ADMOB_CONTENT_AD)){
            return (Boolean)mParams.get(KEY_FILTER_ADMOB_CONTENT_AD);
        }
        return false;
    }



    public Object getExtraObject(){
        if(mParams != null) {
            if(mParams.containsKey(KEY_EXTRA_OBJECT)){
                return mParams.get(KEY_EXTRA_OBJECT);
            }
        }
        return null;
    }

    public void setExtraObject(Object object){
        if(mParams != null){
            mParams.put(KEY_EXTRA_OBJECT, object);
        }
    }

    /**
     * for orion brand
     */
    public void setTabId(String tabId) {
        if (mParams != null) {
            mParams.put(KEY_TAB_ID, tabId);
        }
    }

    public String getTabId(){
        if(mParams != null) {
            if(mParams.containsKey(KEY_TAB_ID)){
                return ((String) mParams.get(KEY_TAB_ID));
            }
        }
        return null;
    }

    /**
     * for orion brand
     */
    public boolean getIsTop() {
        if (mParams != null) {
            if (mParams.containsKey(KEY_IS_TOP)) {
                return (boolean) mParams.get(KEY_IS_TOP);
            }
        }
        return false;
    }

    public void setIsTop(boolean value) {
        if (mParams != null) {
            mParams.put(KEY_IS_TOP, value);
        }
    }
}
