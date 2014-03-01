
package com.android.kit.cache.imge;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.android.kit.utils.KitCacheUtils;
import com.android.kit.utils.KitFileUtils;
import com.android.kit.utils.KitLog;

/**
 * @author Danel
 * @summary 图片缓存工具类，该类提供了简单的图片信息配置，和一些文件的基本操作
 */
public class CacheUtils {

    private static String cachePath = "";

    private CacheUtils() {
    }

    /**
     * 判断SDcard是否可用
     * 
     * @return
     */
    public static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 获取可以使用的缓存目录
     * 
     * @param context
     * @param uniqueName 目录名称
     * @return
     */
    public static File getDiskCacheDir(Context context) {
        String cachePath = isMounted() ? getExternalCacheDir(context).getPath()
                : context.getCacheDir().getPath();
        return new File(cachePath + File.separator);
    }

    /**
     * 默认缓存大小
     * 
     * @param context
     * @return
     */
    public static int defaultMMSize(Context context) {
        return Math.round(getMemoryClass(context) * 1024 * 1024 / 4);
    }

    /**
     * 获得类内存大小
     * 
     * @param context
     * @return
     */
    public static int getMemoryClass(Context context) {
        return ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

    /**
     * 获取程序外部的缓存目录
     * 
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        String cacheDir = "";
        if (TextUtils.isEmpty(cachePath)) {
            cacheDir = "/Android/data/" + context.getPackageName()
                    + "/imgCache/";
        } else {
            cacheDir = cachePath;
            if (cacheDir.startsWith(Environment.getExternalStorageDirectory().getPath())) {
                return KitFileUtils.createFile(cacheDir);
            }
        }
        return KitFileUtils.createFile(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    /**
     * 获取文件路径空间大小
     * 
     * @param path
     * @return
     */
    public static long getUsableSpace(File path) {
        StatFs stats = new StatFs(path.getPath());
        return stats.getBlockSizeLong() * stats.getAvailableBlocksLong();
    }

    /**
     * 设置图片SDcard存储路径
     * 
     * @param cachePath
     */
    public static void setExternalCachePath(String cachePath) {
        CacheUtils.cachePath = cachePath;
    }

    /**
     * 根据key获得一个唯一的字符串，相当于提出特殊字符
     * 
     * @param key
     * @return
     */
    public static String generator(String key) {
        String cacheKey;
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    public static String createKey(String value) {
        String key = "";
        key = CacheUtils.generator(value);
        return key;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 获取缓存文件
     * 
     * @param dir
     * @param name
     * @param suffix
     * @return
     */
    public static File getDiskCacheFile(String dir, String name, String suffix) {
        name += TextUtils.isEmpty(suffix) ? "" : "." + suffix;
        File file = new File(KitFileUtils.createFile(dir), name);
        return file;
    }

    /**
     * 获取缓存文件
     * 
     * @param context
     * @param name
     * @param suffix
     * @return
     */
    public static File getDiskCacheFile(Context context, String name, String suffix) {
        return getDiskCacheFile(getExternalCacheDir(context).getPath(), createKey(name), suffix);
    }

    /**
     * 获取文件的相应位图信息
     * 
     * @param file
     * @param config
     * @return
     */
    public static synchronized Bitmap getBitmapFromFile(File file, CacheConfig config) {
        Bitmap bitmap = null;
        if (file.exists()) {
            FileInputStream inputStream = null;
            FileDescriptor fileDescriptor = null;
            try {
                inputStream = new FileInputStream(file);
                try {
                    fileDescriptor = inputStream.getFD();
                    if (fileDescriptor != null) {
                        file.setLastModified(System.currentTimeMillis());
                        bitmap = KitCacheUtils.decodeSampledBitmapFromDescriptor(
                                fileDescriptor, config.getReqWidth(),
                                config.getReqHeight());
                    }
                } catch (IOException e) {
                    KitLog.e("Read io error", e.getMessage());
                }
            } catch (FileNotFoundException e) {
                KitLog.e("Read file error", e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return bitmap;
    }

    /**
     * 更具提供的path路径清空当前的文件
     */
    public static void clearCurrentCachePath(String path) {
        if (CacheUtils.isMounted()) {
            try {
                File file = KitFileUtils.createFile(path);
                if (file.isDirectory()) {
                    File subFile[] = file.listFiles();
                    for (int i = 0; i < subFile.length; i++) {
                        subFile[i].delete();
                    }
                } else if (file.isFile()) {
                    file.delete();
                }
            } catch (Exception e) {
                KitLog.e("clearCurrentCachePath", "删除文件失败" + e.getMessage());
            }
        }
    }

    /**
     * 过期删除的图片
     * 
     * @param context
     * @param path
     * @param expired
     * @return
     */
    public static boolean removeExpiredCache(Context context, String path,
            int expired) {
        if (isMounted()) {
            try {
                SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = new Date(System.currentTimeMillis());
                File file = KitFileUtils.createFile(path);
                if (file.isDirectory()) {
                    File subFile[] = file.listFiles();
                    for (int i = 0; i < subFile.length; i++) {
                        int currentTime = Integer.valueOf(dataFormat
                                .format(date));
                        int subFileLastModified = Integer.valueOf(dataFormat
                                .format(new Date(subFile[i].lastModified()))
                                .toString());
                        int day = currentTime - subFileLastModified;
                        if (day > expired) {
                            subFile[i].delete();
                        }
                    }
                } else if (file.isFile()) {
                    int currentTime = Integer.valueOf(dataFormat.format(date));
                    int subFileLastModified = Integer.valueOf(dataFormat
                            .format(new Date(file.lastModified())).toString());
                    int day = currentTime - subFileLastModified;
                    if (day > expired) {
                        file.delete();
                    }
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

}
