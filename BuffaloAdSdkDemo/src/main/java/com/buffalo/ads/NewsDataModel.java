package com.buffalo.ads;

/**
 * Created by chenhao on 16/5/26.
 */
public class NewsDataModel {

    int index;
    public NewsDataModel(int index){
        this.index = index;
    }

    public String getTitle(){
        return "测试数据Item#"+index;
    }


}
