package com.cmcm.adsdk.config;

import android.text.TextUtils;

import com.cmcm.adsdk.Const;


public class PosBean implements Comparable<PosBean> {
    private static final String TAG = "PosBean";

    final public int adtype;
    final public String placeid;
    final public String parameter;
    final public String name;
    final public Integer weight;

	public boolean isValidInfo(){
		return weight > 0;
	}

	public PosBean(String name, String placeid, Integer weight, int adType, String parameter) {
		this.adtype = adType;
		if(!TextUtils.isEmpty(name)){
			name.trim();
		}
		this.name = name;
		this.placeid = placeid;
		this.weight = weight;
		this.parameter = parameter;
	}

	@Override
	public int compareTo(PosBean bean) {
		return bean.weight.compareTo(this.weight);
	}

    public String getAdName(){
        return name;
    }
}
