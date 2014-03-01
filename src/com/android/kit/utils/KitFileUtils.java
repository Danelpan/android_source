
package com.android.kit.utils;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import android.content.Context;
import android.os.Environment;

/**
 * 文件工具
 * 
 * @author Danel
 */
public final class KitFileUtils {
    
    private static final String INDIVIDUAL_DIR_NAME = "images";
    private static final String CLASS_DIR_NAME = "class";
    
    public static String PROJECT_ROOT_DIR = "";
    
    private KitFileUtils() {
    }

    /**
     * 级连创建文件，通过一个分解字符串的形式循环创建目录
     * 
     * @param path
     */
    public static File createFile(String path) {
        StringTokenizer st = new StringTokenizer(path, File.separator);
        String rootPath = st.nextToken() + File.separator;
        String tempPath = rootPath;
        File boxFile = null;
        while (st.hasMoreTokens()) {
            rootPath = st.nextToken() + File.separator;
            tempPath += rootPath;
            boxFile = new File(tempPath);
            if (!boxFile.exists()) {
                boxFile.mkdirs();
            }
        }
        return boxFile;
    }
    
    /**
     * 获取缓存的目录，
     * @param context
     * @return
     */
    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }


    public static File getImageCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(cacheDir, INDIVIDUAL_DIR_NAME);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = cacheDir;
            }
        }
        return individualCacheDir;
    }
    
    public static File getClassCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(cacheDir, CLASS_DIR_NAME);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = cacheDir;
            }
        }
        return individualCacheDir;
    }


    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                KitLog.out("Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                KitLog.out("Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }
}
