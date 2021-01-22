package com.buffalo.picks.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import com.buffalo.utils.PackageManagerWrapper;


public class ReceiverUtils extends BroadcastReceiver {
    private static ReceiverUtils mInstance;
    //添加action，请现在这里声明
    private static final String INSTALL_APP = Intent.ACTION_PACKAGE_ADDED;
    private static final String REMOVE_APP = Intent.ACTION_PACKAGE_REMOVED;
    //    public static List<PicksViewCheckHelper> mPicksViewHelpers = new ArrayList<PicksViewCheckHelper>();
    private static final Object lock = new Object();
    // FIXME: 2016/7/12 
/*    public static void addPicksViewCheckHelperObj(PicksViewCheckHelper obj){
        synchronized (lock){
            if(obj != null){
                mPicksViewHelpers.add(obj);
            }
        }
    }
    public static void removePicksViewCheckHelperObj(PicksViewCheckHelper obj){
        synchronized (lock){
            if(obj != null){
                mPicksViewHelpers.remove(obj);
            }
        }
    }*/

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (INSTALL_APP.equals(action)) {
            Uri data = intent.getData();
            String pkgName = data != null ? data.getSchemeSpecificPart() : "";
            PackageManagerWrapper.getInstance().addPkg(pkgName, context);
        } else if (REMOVE_APP.equals(action)) {
            Uri data = intent.getData();
            String pkgName = data != null ? data.getSchemeSpecificPart() : "";
            PackageManagerWrapper.getInstance().deletePkg(pkgName);
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            // FIXME: 2016/7/12
            /*for(PicksViewCheckHelper p: mPicksViewHelpers){
                p.onScreenOn();
            }*/
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            /*for(PicksViewCheckHelper p: mPicksViewHelpers){
                p.onScreenOff();
            }*/
        }
    }

    public static void regist(Context context) {
        if (null == context) {
            return;
        }
        IntentFilter screenOnOrOff = new IntentFilter();
        screenOnOrOff.addAction(Intent.ACTION_SCREEN_OFF);
        screenOnOrOff.addAction(Intent.ACTION_SCREEN_ON);
        IntentFilter addAndRemove = new IntentFilter();
        addAndRemove.addAction(Intent.ACTION_PACKAGE_ADDED);
        addAndRemove.addAction(Intent.ACTION_PACKAGE_REMOVED);
        addAndRemove.addDataScheme("package");    //必须添加这项，否则拦截不到广播
        if (null == mInstance) {
            mInstance = new ReceiverUtils();
        }
        context.registerReceiver(mInstance, addAndRemove);
        context.registerReceiver(mInstance, screenOnOrOff);
    }

    public static void unRegist(Context context) {
        if (null == context || null == mInstance) {
            return;
        }
        context.unregisterReceiver(mInstance);
        mInstance = null;
    }
}
