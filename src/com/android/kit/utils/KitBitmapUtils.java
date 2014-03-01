
package com.android.kit.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.android.kit.cache.imge.FlushedInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

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
        Bitmap BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(), resId);
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = newWidth * height / width;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0,
                width, height, matrix, true);

        return new BitmapDrawable(ctx.getResources(), resizedBitmap);
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
     * 保存流到文件
     * @param is
     * @param file
     */
    public static final synchronized void stream2File(InputStream is, File file) {
        BufferedOutputStream out = null;
        FlushedInputStream in = null;
        FileOutputStream outputStream = null;
        try {
            in = new FlushedInputStream(new BufferedInputStream(is, 8 * 1024));
            outputStream = new FileOutputStream(file);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        } catch (Exception e) {
            if (file != null && file.exists()) {
                file.delete();
            }
            file = null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
                if (in != null) {
                    in.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (final IOException e) {
            }
        }
    }
}
