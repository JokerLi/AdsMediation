package com.cmcm.adsdk.report;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.cmcm.adsdk.CMAdManager;
import com.cmcm.utils.Networking;

import java.util.ArrayList;
import java.util.List;

/**
 * 商业数据上报
 * */
public class BuinessDataReporter extends AsyncTask<Void, Void, Void> {

	private static final String CHINA_REPORT_HOST = "http://rcv.mobad.ijinshan.com/rp/";
	static final String WORLD_REPORT_HOST = "https://ssdk.adkmob.com/rp/";

	private BuinessPublicData mPublic;
	private List<BuinessDataItem> mItems;

	// ---------------------功能接口---------------------------
	
	/**
	 * 单条上报调用
	 * */
	public void setData(BuinessDataItem item, BuinessPublicData publicData) {
		mPublic = publicData;
		mItems = new ArrayList<BuinessDataItem>();
		mItems.add(item);
	}
	
	/**
	 * 批量上报调用
	 * */
	public void setData(List<BuinessDataItem> items, BuinessPublicData publicData) {
		mPublic = publicData;
		mItems = items;
	}
	
	private String getUploadUrl() {
		if(CMAdManager.sIsCnVersion){
			return CHINA_REPORT_HOST;
		}
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
			return ;
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
		for (BuinessDataItem idx : mItems) {
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
