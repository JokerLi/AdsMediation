package com.buffalo.ads.utils.imageloader;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    private static final String DEFAULT_SUB_PICTURE_DIRECTORY = "buffalo-demo";

    public static File getDefaultSubPictureDirectory() {
        return getSubPictureDirectory(DEFAULT_SUB_PICTURE_DIRECTORY);
    }

    public static File getSubPictureDirectory(String dir) {
        File picturesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File subCache = new File(picturesFolder, dir);
        if (!subCache.exists()) {
            subCache.mkdir();
        }
        return subCache;
    }

    /**
     * 确保目录存在,没有则创建
     */
    public static boolean confirmFolderExist(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            return file.mkdirs();
        }

        return false;
    }

    public static File getSubCacheDirectory(Context context, String dir) {
        File cache = getCacheDirectory(context);
        File subCache = new File(cache, dir);
        if (!subCache.exists()) {
            subCache.mkdir();
        }
        return subCache;
    }

    public static File getSubFileDirectory(Context context, String dir) {
        File files = getFilesDirectory(context);
        File subFiles = new File(files, dir);
        if (!subFiles.exists()) {
            subFiles.mkdir();
        }
        return subFiles;
    }

    public static File getCacheDirectory(Context context) {
        return getAppExternalStoragePath(context, "cache", true);
    }

    public static File getFilesDirectory(Context context) {
        return getAppExternalStoragePath(context, "files", true);
    }

    private static File getAppExternalStoragePath(Context context, String type, boolean preferExternal) {
        File appDir = null;

        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException var5) {
            externalStorageState = "";
        }

        if (preferExternal && "mounted".equals(externalStorageState)) {
            appDir = getExternalDirWithName(context, type);
        }

        if (appDir == null) {
            if (TextUtils.equals(type, "files")) {
                appDir = context.getFilesDir();
            } else {
                appDir = context.getCacheDir();
            }
        }

        if (appDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + File.separator + type + File.separator;
            appDir = new File(cacheDirPath);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
        }
        return appDir;
    }

    private static File getExternalDirWithName(Context context, String folderName) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), folderName);
        if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
            return null;
        } else {
            return appCacheDir;
        }
    }

    /**
     * 删除文件夹所有内容
     */
    public static void deleteFile(File file) {
        if (file != null && file.exists()) { // 判断文件是否存在
            if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                if (files != null) {
                    for (File childFile : files) { // 遍历目录下所有的文件
                        deleteFile(childFile); // 把每个文件 用这个方法进行迭代
                    }
                }
            }
            //安全删除文件
            deleteFileSafely(file);
        }
    }

    /**
     * 重命名
     */
    public static File renameFile(File srcFile, String newName) {
        File destFile = new File(newName);
        srcFile.renameTo(destFile);
        return destFile;
    }

    public static boolean checkFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        return new File(filePath).exists();
    }

    /**
     * 安全删除文件.防止删除后重新创建文件，报错 open failed: EBUSY (Device or resource busy)
     */
    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
