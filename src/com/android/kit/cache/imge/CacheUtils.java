
package com.android.kit.cache.imge;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
public final class CacheUtils {

    private static String imageCachePath = "";

    private CacheUtils() {
    }

    /**
     * 判断SDcard是否可用
     * 
     * @return
     */
    public static final boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
    
    public static final String getSDCard(){
        String path = "";
        if(isMounted()){
            path = Environment.getExternalStorageDirectory().getPath();
        }
        return path;
    }
    
    public static final String getDefaultImageCachePath(Context context){
        String path = "";
        if(isMounted()){
            path = getSDCard() + File.separator + "Android"+ File.separator +"data" + File.separator + context.getPackageName() + File.separator + "imgCache" + File.separator;
        }else {
            path = context.getCacheDir().getPath()+File.separator+"imgCache"+File.separator;
        }
        return path;
    }
    
    /**
     * 获取程序外部的缓存目录
     * 
     * @param context
     * @return
     */
    public static final File getCacheDir(Context context) {
        String cacheDir = getDefaultImageCachePath(context);
        
        if(isMounted()){
            
            if(!TextUtils.isEmpty(imageCachePath)){
                cacheDir = imageCachePath;
                if (!cacheDir.startsWith(getSDCard())) {
                    
                    if(!cacheDir.startsWith(File.separator)){
                        cacheDir = File.separator + cacheDir;
                    }
                    
                    cacheDir =  getSDCard() + cacheDir;
                }
            }
            
        }
        KitLog.e("CacheDir", cacheDir);
        return KitFileUtils.createFile(cacheDir);
    }
    
    /**
     * 设置图片SDcard存储路径
     * 
     * @param cachePath
     */
    public static final void setImageCachePath(String cachePath) {
        CacheUtils.imageCachePath = cachePath;
    }
    
    /**
     * 获取自定义的图片目录
     * @return
     */
    public static final String getImageCachePath(){
        return CacheUtils.imageCachePath;
    }

    /**
     * 默认缓存大小
     * 
     * @param context
     * @return
     */
    public static final int defaultMMSize(Context context) {
        return Math.round(getMemoryClass(context) * 1024 * 1024 / 4);
    }

    /**
     * 获得类内存大小
     * 
     * @param context
     * @return
     */
    public static final int getMemoryClass(Context context) {
        return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

    /**
     * 获取文件路径空间大小
     * 
     * @param path
     * @return
     */
    public static final long getUsableSpace(File path) {
        StatFs stats = new StatFs(path.getPath());
        return stats.getBlockSizeLong() * stats.getAvailableBlocksLong();
    }

    /**
     * 根据key获得一个唯一的字符串，相当于提出特殊字符
     * 
     * @param key
     * @return
     */
    public static final String generator(String key) {
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

    public static final String createKey(String value) {
        String key = "";
        key = CacheUtils.generator(value);
        return key;
    }

    private static final String bytesToHexString(byte[] bytes) {
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
    public static final File getDiskCacheFile(String dir, String name, String suffix) {
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
    public static final File getDiskCacheFile(Context context, String name, String suffix) {
        return getDiskCacheFile(getCacheDir(context).getPath(), createKey(name), suffix);
    }

    /**
     * 获取文件的相应位图信息
     * 
     * @param file
     * @param config
     * @return
     */
    public static final synchronized Bitmap getBitmapFromFile(File file, CacheConfig config) {
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
    public static final void clearCurrentCachePath(String path) {
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
    public static final boolean removeExpiredCache(Context context, String path,
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
