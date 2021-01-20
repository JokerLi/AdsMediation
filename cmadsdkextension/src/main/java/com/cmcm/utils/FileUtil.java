package com.cmcm.utils;

import android.os.Build;
import android.os.StatFs;

import java.io.File;

/**
 * Created by Chu Zongxiang on 2016/5/31.
 */
public class FileUtil {

    public static void removeExpiredFilesInDirectory(File dir, long expireTime) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return;
        }

        File[] fileArray = dir.listFiles();
        if (fileArray != null) {
            for (File file : fileArray) {
                if (System.currentTimeMillis() - file.lastModified() > expireTime) {
                    file.delete();
                }
            }
        }
    }

    public static long diskFreeSize(File dir) {
        long freeSize = 0;
        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long blockSize;
            long blocks;
            if (Build.VERSION.SDK_INT >= 18) {
                blockSize = statFs.getBlockSizeLong();
                blocks = statFs.getAvailableBlocksLong();
            } else {
                blockSize = statFs.getBlockSize();
                blocks = statFs.getAvailableBlocks();
            }
            freeSize = blocks * blockSize;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return freeSize;
    }
}
