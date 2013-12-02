package com.android.kit.manager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.android.kit.bitmap.core.DisplayImageOptions;
import com.android.kit.bitmap.core.ImageLoader;
import com.android.kit.bitmap.core.assist.ImageLoadingListener;
/**
 * 图片管理工具
 * @author Danel
 *
 */
public final class BitmapManager {
	/**
	 * 根据自定义宽高获得新的图片位图
	 * @param bitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static final Bitmap getBitmap(Bitmap bitmap, int newWidth,
			int newHeight) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(newWidth / (float) bitmapWidth, newHeight/ (float) bitmapHeight);
		Bitmap resizedBitmap = bitmap;
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight,matrix, true);
		if (bitmap != resizedBitmap) {
			resizedBitmap.recycle();
			resizedBitmap = null;
		}
		return bitmap;
	}
	
	/**
	 * 简单的文图合成方法,从左上角开始合成
	 * @param bmp1 在底下的图片
	 * @param bmp2 在上层的图片
	 * @return 合成后的图片
	 */
	public static final Bitmap merge(Bitmap bmp1, Bitmap bmp2) {
		Paint paint_comm = new Paint(Paint.ANTI_ALIAS_FLAG);
		if (null == bmp1 || null == bmp2){
			throw new NullPointerException("Source Bitmap is empty ……");
		}
		Bitmap bmOverlay = Bitmap.createBitmap(bmp2.getWidth(),bmp2.getHeight(), Bitmap.Config.ARGB_8888);
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
	 * 圆角图片
	 * @param bitmap
	 * @return
	 */
	public static final Bitmap screenshotCycle(Bitmap bitmap,float roundPx) {
		if(null == bitmap){
			throw new NullPointerException("bitmap is emty...");
		}
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(),bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
    /**
     * 截获view图片
     * @param view
     * @return 返回该图片
     */
    public static final Bitmap view2Bitmap(View view) {
        view.setDrawingCacheEnabled(false);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        Bitmap temp = Bitmap.createBitmap(bitmap, 0,90, bitmap.getWidth(), bitmap.getHeight()*68/100);
        if(!bitmap.isRecycled()){
        	bitmap.recycle();
        }
        return temp;
    }
    /**
     * 文件装换成位图
     * @param file
     * @return
     */
    @Deprecated
    public static final void file2Bitmap(File file,ImageLoadingListener listener){
    	if(null == file){
    		throw new NullPointerException("source file is null ...");
    	}
    	if(!file.exists()){
    		return;
    	}
    	ImageLoader loader = ImageLoader.getInstance();
    	DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheInMemory()
		.cacheOnDisc().
		bitmapConfig(Bitmap.Config.ARGB_8888)
		.build();
		loader.loadImage(file.getPath(), options , listener);
    }
    /**
     * 位图转换成流
     * @param bitmap
     * @return
     */
    public static final InputStream bitmap2Stream(Bitmap bitmap) {
        return bitmap2Stream(bitmap,CompressFormat.JPEG,100);
    }
    public static final InputStream bitmap2Stream(Bitmap bitmap,CompressFormat Format,int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Format, quality, baos);
        InputStream stream = new ByteArrayInputStream(baos.toByteArray());
        return stream;
    }
}
