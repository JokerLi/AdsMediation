package com.buffalo.utils;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;

import com.buffalo.adsdk.AdManager;
import com.buffalo.adsdk.nativead.NativeAd;
import com.buffalo.adsdk.report.ReportFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by peiboning on 2016/8/9.
 */
public class ViewShowReporter implements Callable<Boolean>{
    private static String TAG = ViewShowReporter.class.getSimpleName();
    private static ViewCheckHelper checkHelper;
    private static Map<Model, WeakReference<View>> waitReportMap = new Hashtable<Model, WeakReference<View>>();
    private static AtomicBoolean mIsRunning;
    private static long mBeginRunTime = 0L;
    private static final float VIEW_ALPHA_VALUE = 0.9f;

    private ViewShowReporter(){}

    public synchronized static void add(Model model, View view){
        if(null == model || null == view){
            return;
        }
        if(!waitReportMap.containsKey(model)){
            waitReportMap.put(model, new WeakReference<View>(view));
            mBeginRunTime = System.currentTimeMillis();
            Logger.i(TAG, "add view size = " + waitReportMap.size());
        }
        if(null == mIsRunning){
            mIsRunning = new AtomicBoolean(false);
        }
        if(!mIsRunning.get()){
            mIsRunning.set(true);
            checkHelper = new ViewCheckHelper(AdManager.getContext(), new ViewShowReporter());
            Logger.i(TAG, "new ViewCheckHelper");
            checkHelper.startWork();
        }
    }

    private boolean shouldStop() {
        if(waitReportMap.size() <= 0){
            reset();
            Logger.i(TAG, "Stop cause map size <= 0 ");
            return true;
        }

        if(System.currentTimeMillis() - mBeginRunTime >= 3 * 60 * 1000){
            Logger.i(TAG, "Stop cause time out > 3 minutes");
            reset();
            return true;
        }

        return false;
    }

    private void reset(){
        mIsRunning.set(false);
        waitReportMap.clear();
        mBeginRunTime = 0;
        checkHelper = null;
    }

    public static void unRegister(Model model){
        if(waitReportMap != null && model != null){
            if(waitReportMap.containsKey(model)){
                waitReportMap.remove(model);
            }
        }
    }

    @Override
    public Boolean call() throws Exception {
        return check();
    }

    /**
     *
     * @return true --> need stop;
     */
    private boolean check(){
        Logger.i(TAG, "check");
        long startTime = System.currentTimeMillis();
        Set<Model> keys = waitReportMap.keySet();
        List<Model> removed = new ArrayList<Model>();
        for(Model model : keys){
            View view = waitReportMap.get(model).get();
            if(view != null && isViewOnScreen(view)){
                //report
                model.report();
                removed.add(model);
            }else if(view == null){
                //移除
                removed.add(model);
            }
        }
        for(Model model : removed){
            waitReportMap.remove(model);
            Logger.i(TAG, "remove view size = " + waitReportMap.size());
        }

        Logger.i(TAG, "check cost time = " + (System.currentTimeMillis() - startTime));
        return shouldStop();
    }

    private boolean isViewOnScreen(View view) {
        if ((view == null)
                || (view.getVisibility() != View.VISIBLE)
                || (view.getParent() == null) || (!isValidAlpha(view))) {
            return false;
        }
        Rect visibleRect = new Rect();
        if (!view.getGlobalVisibleRect(visibleRect)) {
            return false;
        }
        double visibleArea = visibleRect.width() * visibleRect.height();
        Logger.i(TAG, "visibleArea = " + visibleArea);
        return visibleArea >= 1;
    }

    @SuppressLint("NewApi")
    private boolean isValidAlpha(View view) {
        if (Build.VERSION.SDK_INT >= 11) {
            return view.getAlpha() > VIEW_ALPHA_VALUE;
        }
        return true;
    }

    public static class Model{
        private String pkgName;
        private String posid;
        private int rcvReportRes;
        private int pegReportRes;
        private Map<String, String> extraReportParams;
        private String placementId;
        private String rawString;
        private boolean isOrionAd;
        private NativeAd ad;

        public Model(String pkgName, String posid, int rcvReportRes, int pegReportRes,
                     Map<String, String> extraReportParams, String placementId, String rawString,
                     boolean isOrionAd, NativeAd ad) {
            this.pkgName = pkgName;
            this.posid = posid;
            this.rcvReportRes = rcvReportRes;
            this.extraReportParams = extraReportParams;
            this.placementId = placementId;
            this.rawString = rawString;
            this.isOrionAd = isOrionAd;
            this.ad = ad;
        }

        public void report(){
            Logger.i(TAG, "report title = " + ad.getAdTitle());
            extraReportParams = ad.addDupReportExtra(false, ad.hasReportUserImpression(), extraReportParams);
            ad.setHasReportUserImpression(true);
            UniReport.report(ReportFactory.USER_IMPRESSION, pkgName, posid, rcvReportRes, extraReportParams,
                    placementId, ad.isNativeAd(), rawString, isOrionAd);
        }
    }
}
