package com.android.kit.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

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
        bitmap.compress(format, quality, outputStream);
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
    

	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	public static Bitmap decodeSampledBitmapFromFile(String filename) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	public static Bitmap decodeSampledBitmapFromDescriptor(
			FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory
				.decodeFileDescriptor(fileDescriptor, null, options);
	}


	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further.
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}
}

