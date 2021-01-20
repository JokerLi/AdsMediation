package com.cmcm.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.cmcm.adsdk.CMAdManager;

import java.util.ArrayList;
import java.util.List;

public class PackageManagerWrapper {
	private static PackageManagerWrapper instanceManagerWrapper = new PackageManagerWrapper();
	private Context        mCtxContext 	= CMAdManager.getContext();
	private PackageManager mPM = mCtxContext.getPackageManager();
	public static synchronized PackageManagerWrapper getInstance() {
		return instanceManagerWrapper;
	}

	/**
	 * package name only
	 * */
	public List<String> getPkgNameList(boolean isContainSystemApp) {
		List<PackageInfo> allPkgs = getInstalledPkgNoThrow(0);
		List<String> list = new ArrayList<String>();
		if (null != allPkgs && allPkgs.size() > 0) {
			for (PackageInfo packageInfo : allPkgs) {
				if (isContainSystemApp || Commons.isUserApp(packageInfo.applicationInfo)) {
					list.add(packageInfo.packageName);
				}
			}
		}
		return list;
	}

    private List<PackageInfo> pkgList ;
	private Object            mDataLock = new Object();
    private List<PackageInfo> getInstalledPkgNoThrow(int flags) {
        try {
			synchronized (mDataLock) {
				if (null == pkgList) {
					pkgList = mPM.getInstalledPackages(flags);
				}
			}
        } catch (Exception e) {
        }

		return pkgList;
    }

    public void deletePkg(String pkg) {
		synchronized (mDataLock) {
			if (null != pkgList) {
				for (int i = 0; i < pkgList.size(); i++) {
					if (pkgList.get(i).packageName.equals(pkg)) {
						pkgList.remove(i);
						break;
					}
				}
			}
		}
    }

    public void addPkg(String pkg,Context context) {
        try {
			synchronized (mDataLock) {
				if (null != pkgList) {
					PackageInfo info = context.getPackageManager().getPackageInfo(pkg, 0);
					for (int i = 0; i < pkgList.size(); i++) {
						if (pkgList.get(i).packageName.equals(pkg)) {
							pkgList.remove(i);
							break;
						}
					}
					pkgList.add(info);
				}
			}
        } catch (Exception e) {
        }
    }

}

