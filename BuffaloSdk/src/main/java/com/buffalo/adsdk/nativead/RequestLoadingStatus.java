package com.buffalo.adsdk.nativead;


import java.util.Vector;

/**
 * Created by shimiaolei on 16/1/2.
 */
class RequestLoadingStatus {
    int mSize = 0;
    final private Vector<Boolean> mLoadingStatus = new Vector<Boolean>();

    public void resetLoadingStatus(int size) {
        mSize = size;
        mLoadingStatus.clear();
        for (int i = 0; i < mSize; ++i) {
            mLoadingStatus.add(false);
        }
    }

    public boolean isBeanLoading(int i) {

        if (i >= 0 && i < mLoadingStatus.size()) {
            return mLoadingStatus.get(i);
        }
        return true;
    }

    public boolean setBeanLoading(int i, boolean value) {

        if (i >= 0 && i < mLoadingStatus.size()) {
            mLoadingStatus.set(i, value);
            return true;
        }
        return false;
    }

    public int getWaitingBeansNumber() {

        if (mLoadingStatus.size() != mSize)
            return 0;

        int waitNum = 0;
        for (Boolean isLoading : mLoadingStatus) {
            if (!isLoading) {
                waitNum += 1;
            }
        }

        return waitNum;
    }
}
