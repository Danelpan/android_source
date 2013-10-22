package com.android.kit.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

/**
 * 缓存工具类
 * @author Danel
 *
 */
public final class CacheUtils {
    /**
     * 保存图片到文件,默认{@link CompressFormat}为JPEG，和quality is max
     * @param bitmap
     * @param file
     */
    public static final void bitmap2File(Bitmap bitmap, File file) {
    	bitmap2File(bitmap,file,CompressFormat.JPEG,100);
    }
    /**
     * 保存图片到文件
     * @param bitmap
     * @param file
     */
    public static final void bitmap2File(Bitmap bitmap, File file,CompressFormat format,int quality ) {
        if(bitmap == null){
        	throw new NullPointerException("source bitmap is null...");
        }
        if(file == null){
        	throw new NullPointerException("targe file is null...");
        }
    	if (file.exists()) {
            file.delete();
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
        	KitLog.printStackTrace(e);
        }
        if (outputStream == null) {
            return;
        }
        bitmap.compress(CompressFormat.JPEG, 100, outputStream);
        try {
            outputStream.flush();
        } catch (IOException e) {
        	KitLog.printStackTrace(e);
        }finally{
        	try {
                outputStream.close();
            } catch (IOException e) {
            	KitLog.printStackTrace(e);
            }
        }
    }
    
    /**
     * 获取存储的class
     * @param name
     * @return
     */
    public static final Object getClass(File file) {
    	if(null == file){
    		throw new NullPointerException("file is null ...");
    	}
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        if (file.exists()) {
            try {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                return ois.readObject();
            } catch (FileNotFoundException e) {
            	KitLog.printStackTrace(e);
            } catch (IOException e) {
            	KitLog.printStackTrace(e);
            } catch (ClassNotFoundException e) {
            	KitLog.printStackTrace(e);
            } finally {
                try {
                    ois.close();
                    fis.close();
                } catch (IOException e) {}
            }
        }
        return null;
    }
    
    /**
     * 保存类到存储,必须是实现了序列化之后的操作
     * @param name
     * @param obj
     */
    public static final void saveClass(File file, Object obj) {
    	if(null == obj){
    		throw new NullPointerException("Object is null ...");
    	}
    	if(null == file){
    		throw new NullPointerException("file is null ...");
    	}
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        if (file.exists()) {
            file.delete();
        }
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
            fos.flush();
        } catch (FileNotFoundException e) {
        	KitLog.printStackTrace(e);
        } catch (IOException e) {
        	KitLog.printStackTrace(e);
        } finally {
            try {
                oos.close();
                fos.close();
            } catch (IOException e) {}
        }
    }
}
