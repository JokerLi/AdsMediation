package com.buffalo.ads;

public class NewsDataModel {

    int index;

    public NewsDataModel(int index) {
        this.index = index;
    }

    public String getTitle() {
        return "测试数据Item#" + index;
    }
}
