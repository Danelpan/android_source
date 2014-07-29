
package com.android.kit.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Base64;
import android.view.View;

public final class KitBitmapUtils {
    private KitBitmapUtils() {
    }

    /**
     * 根据宽高重新生成一张图片
     * 
     * @param ctx
     * @param resId
     * @param w
     * @param h
     * @return
     */
    public static final Drawable resizeImage(Context ctx, int resId, int w, int h) {
        
        Bitmap resizedBitmap = resizeBitmap(ctx, resId, w, h);

        return new BitmapDrawable(ctx.getResources(), resizedBitmap);
    }
    
    public static final Bitmap resizeBitmap(Context ctx, int resId, int w, int h){
    	Bitmap BitmapOrg = decodeSampledBitmapFromResource(ctx.getResources(), resId, w, h);
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = newWidth * height / width;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0,width, height, matrix, true);
        return resizedBitmap;
    }

    /**
     * 两张图片合并成一张图片,从左上角开始合成
     * 
     * @param bmp1 在底下的图片
     * @param bmp2 在上层的图片
     * @return 合成后的图片
     */
    public static final Bitmap merge(Bitmap bmp1, Bitmap bmp2) {
        if (null == bmp1 || null == bmp2)
            return null;
        Paint paint_comm = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap bmOverlay = Bitmap.createBitmap(bmp2.getWidth(),
                bmp2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOverlay);

        float x = (bmp2.getWidth() - bmp1.getWidth()) / 2f;
        float y = (bmp2.getHeight() - bmp1.getHeight()) / 2f;

        x = x > 0 ? x : 0;
        y = y > 0 ? y : 0;

        canvas.drawBitmap(bmp1, x, y, paint_comm);

        canvas.drawBitmap(bmp2, 0, 0, paint_comm);
        return bmOverlay;
    }

    /**
     * 获取bitmap的字节大小
     * 
     * @param bitmap
     * @return
     */
    public static final int getBitmapSize(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * 位图和流的转换
     * 
     * @param bitmap
     * @return
     */
    public static final InputStream bitmap2Stream(Bitmap bitmap) {
        return bitmap2Stream(bitmap, CompressFormat.JPEG, 100);
    }

    /**
     * 位图和流的转换
     * 
     * @param bitmap
     * @param format
     * @param quality
     * @return
     */
    public static final InputStream bitmap2Stream(Bitmap bitmap, CompressFormat format, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, quality, baos);
        InputStream stream = new ByteArrayInputStream(baos.toByteArray());
        return stream;
    }

    /**
     * 截获view图片
     * 
     * @param view
     * @return 返回该图片
     */
    public static final Bitmap view2Bitmap(View view) {
        view.setDrawingCacheEnabled(false);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 保存图片到文件,默认{@link CompressFormat}为JPEG，和quality is max
     * 
     * @param bitmap
     * @param file
     */
    public static final void bitmap2File(Bitmap bitmap, File file) {
        bitmap2File(bitmap, file, CompressFormat.JPEG, 100);
    }
    
    public static final String base64(Bitmap bitmap){
    	ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            byte[] imgBytes = out.toByteArray();
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            KitLog.printStackTrace(e);
        } finally {
            KitStreamUtils.closeStream(out);
        }
        return "";
    }

    public static final String base64(File file){
    	return base64(BitmapFactory.decodeFile(file.getPath()));
    }
    
    /**
     * 保存图片到文件
     * 
     * @param bitmap
     * @param file
     */
    public static final void bitmap2File(Bitmap bitmap, File file, CompressFormat format,
            int quality) {
        if (bitmap == null) {
            throw new NullPointerException("source bitmap is null...");
        }
        if (file == null) {
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
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                KitLog.printStackTrace(e);
            }
        }
    }

    /**
     * 根据字节转换成位图
     * 
     * @param bs
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static final Bitmap decodeSampledBitmapFromBytes(byte[] bs, int reqWidth, int reqHeight) {
        if (null == bs) {
            return null;
        }

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap = null;

        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bs, 0, bs.length, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;

        int desiredWidth = getResizedDimension(reqWidth, reqHeight,
                actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(reqHeight, reqWidth,
                actualHeight, actualWidth);

        decodeOptions.inJustDecodeBounds = false;

        decodeOptions.inSampleSize =
                calculateInSampleSize(decodeOptions, desiredWidth, desiredHeight);
        Bitmap tempBitmap =
                BitmapFactory.decodeByteArray(bs, 0, bs.length, decodeOptions);

        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                tempBitmap.getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap,
                    desiredWidth, desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }
        return bitmap;
    }

    /**
     * 根据图片ID获取相应的位图信息
     * 
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static final Bitmap decodeSampledBitmapFromResource(Resources res,
            int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    
    public static final Bitmap decodeBitmapFromResource(Resources res,int resId){
    	return BitmapFactory.decodeResource(res, resId);
    }

    /**
     * 根据文件解码位图
     * 
     * @param fileDescriptor
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static final Bitmap decodeSampledBitmapFromDescriptor(
            FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        Bitmap bitmap;
        try{
        	bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        }catch(OutOfMemoryError error){
        	options.inSampleSize = options.inSampleSize * 2;
        	bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        }
        
        return bitmap;
    }

    public static final Bitmap decodeSampledBitmapFromFile(String filename) {
        return decodeSampledBitmapFromFile(filename, 0, 0);
    }

    public static final Bitmap decodeSampledBitmapFromFile(String filename,
            int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    /**
     * 根据宽高计算出一个合理的压缩比例
     * 
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        KitLog.err("inSampleSize---->"+inSampleSize);
        return inSampleSize;
    }

    static final int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
            int actualSecondary) {
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }
    
    /**
     * 获取文件图片的旋转角度
     * @param filepath
     * @return
     */
    public final static int getRotationAngle(String filepath){
        int degree = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(filepath);
        } catch (IOException e) {
            KitLog.printStackTrace(e);
        }
        if (exifInterface != null) {
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                }
            }
        }
        return degree;
    }
    
    /**
     * 根据角度旋转位图，同时生成一张旋转后的位图，原图片将被释放掉
     * @param bitmapSource
     * @param degrees
     * @return
     */
    public final static Bitmap getRotationBitmap(Bitmap bitmapSource , int degrees){
        if(null==bitmapSource || bitmapSource.isRecycled()){
            return null;
        }
        
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmapSource, 0, 0, bitmapSource.getWidth(), bitmapSource.getHeight(), matrix, true);   
        if(rotateBitmap != null) {   
            bitmapSource.recycle();   
        } 
        return rotateBitmap;
    }
    
}
