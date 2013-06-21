/**
 * Copyright (c) 2012-2013, Danel(E-mail:danel.pan@sohu.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.kit.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

public class SimpleDisplayer implements CacheLoaderListener {
	private long duration = 100;
	private int type = 1;
	private int roundPixels = 10;
	/**
	 * 淡入淡出
	 */
	public static final int TYPE_FADE_IN = 1;
	/**
	 * 圆角
	 */
	public static final int TYPE_ROUNDED = 2;
	/**
	 * 正常
	 */
	public static final int NORMAL = 3;
	
	public static final int DURATION_MILLIS = 200;
	public SimpleDisplayer() {
		this(NORMAL);
	}

	public SimpleDisplayer(int type) {
		this.type = type;
	}
	
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getRoundPixels() {
		return roundPixels;
	}

	public void setRoundPixels(int roundPixels) {
		this.roundPixels = roundPixels;
	}

	@Override
	public void onCacheLoaderStart(CacheConfig ccf) {
		View view = ccf.getView();
		Bitmap defBitmap = ccf.getLoadingBitmap();
		if (null != view && null != defBitmap) {
			if ((view instanceof ImageView)) {
				((ImageView) view).setImageBitmap(defBitmap);
			} else if (view instanceof ImageButton) {
				((ImageButton) view).setImageBitmap(defBitmap);
			} else {
				view.setBackgroundDrawable(null);
				Drawable drawable = new BitmapDrawable(defBitmap);
				view.setBackgroundDrawable(drawable);
			}
		}
	}

	@Override
	public boolean onCacheLoaderLoading(CacheConfig ccf) {
		return false;
	}

	@Override
	public void onCacheLoaderFinish(CacheConfig ccf, boolean isSuccess) {
		View view = ccf.getView();
		if (null == view) {
			return;
		}
		if(type == TYPE_FADE_IN){
			typeOfFade(ccf,isSuccess);
		}else if(type == TYPE_ROUNDED){
			typeOfRounded(ccf,isSuccess);
		}else {
			typeOfDef(ccf,isSuccess);
		}
	}

	private void animateFade(View imageView, long durationMillis) {
		AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
		fadeImage.setDuration(durationMillis);
		fadeImage.setInterpolator(new DecelerateInterpolator());
		imageView.startAnimation(fadeImage);
	}
	
	private void typeOfFade(CacheConfig ccf, boolean isSuccess){
		typeOfDef(ccf,isSuccess);
		if(isSuccess){
			animateFade(ccf.getView(), duration);
		}
	}
	
	private void typeOfRounded(CacheConfig ccf, boolean isSuccess){
		View view = ccf.getView();
		if (isSuccess) {
			Bitmap defBitmap = ccf.getBitmap();
			if (null != defBitmap) {
				if ((view instanceof ImageView)) {
					((ImageView) view).setImageBitmap(roundCorners(defBitmap,(ImageView) view,10));
				} else if (view instanceof ImageButton) {
					((ImageButton) view).setImageBitmap(roundCorners(defBitmap,(ImageButton) view,10));
				} else {
					view.setBackgroundDrawable(null);
					Drawable drawable = new BitmapDrawable(defBitmap);
					view.setBackgroundDrawable(drawable);
				}
			}
		} else {
			Bitmap defBitmap = ccf.getLoadfailBitmap();
			if (null != defBitmap) {
				if ((view instanceof ImageView)) {
					((ImageView) view).setImageBitmap(defBitmap);
				} else if (view instanceof ImageButton) {
					((ImageButton) view).setImageBitmap(defBitmap);
				} else {
					view.setBackgroundDrawable(null);
					Drawable drawable = new BitmapDrawable(defBitmap);
					view.setBackgroundDrawable(drawable);
				}
			}
		}
//		animateFade(view, duration);
	}
	private void typeOfDef(CacheConfig ccf, boolean isSuccess){
		View view = ccf.getView();
		if (isSuccess) {
			Bitmap defBitmap = ccf.getBitmap();
			if (null != defBitmap) {
				if ((view instanceof ImageView)) {
					((ImageView) view).setImageBitmap(defBitmap);
				} else if (view instanceof ImageButton) {
					((ImageButton) view).setImageBitmap(defBitmap);
				} else {
					view.setBackgroundDrawable(null);
					Drawable drawable = new BitmapDrawable(defBitmap);
					view.setBackgroundDrawable(drawable);
				}
			}
		} else {
			Bitmap defBitmap = ccf.getLoadfailBitmap();
			if (null != defBitmap) {
				if ((view instanceof ImageView)) {
					((ImageView) view).setImageBitmap(defBitmap);
				} else if (view instanceof ImageButton) {
					((ImageButton) view).setImageBitmap(defBitmap);
				} else {
					view.setBackgroundDrawable(null);
					Drawable drawable = new BitmapDrawable(defBitmap);
					view.setBackgroundDrawable(drawable);
				}
			}
		}
	}
	
	public static Bitmap roundCorners(Bitmap bitmap, ImageView imageView, int roundPixels) {
		Bitmap roundBitmap;

		int bw = bitmap.getWidth();
		int bh = bitmap.getHeight();
		int vw = imageView.getWidth();
		int vh = imageView.getHeight();
		if (vw <= 0) vw = bw;
		if (vh <= 0) vh = bh;

		int width, height;
		Rect srcRect;
		Rect destRect;
		switch (imageView.getScaleType()) {
			case CENTER_INSIDE:
				float vRation = (float) vw / vh;
				float bRation = (float) bw / bh;
				int destWidth;
				int destHeight;
				if (vRation > bRation) {
					destHeight = Math.min(vh, bh);
					destWidth = (int) (bw / ((float) bh / destHeight));
				} else {
					destWidth = Math.min(vw, bw);
					destHeight = (int) (bh / ((float) bw / destWidth));
				}
				int x = (vw - destWidth) / 2;
				int y = (vh - destHeight) / 2;
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(x, y, x + destWidth, y + destHeight);
				width = vw;
				height = vh;
				break;
			case FIT_CENTER:
			case FIT_START:
			case FIT_END:
			default:
				vRation = (float) vw / vh;
				bRation = (float) bw / bh;
				if (vRation > bRation) {
					width = (int) (bw / ((float) bh / vh));
					height = vh;
				} else {
					width = vw;
					height = (int) (bh / ((float) bw / vw));
				}
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(0, 0, width, height);
				break;
			case CENTER_CROP:
				vRation = (float) vw / vh;
				bRation = (float) bw / bh;
				int srcWidth;
				int srcHeight;
				if (vRation > bRation) {
					srcWidth = bw;
					srcHeight = (int) (vh * ((float) bw / vw));
					x = 0;
					y = (bh - srcHeight) / 2;
				} else {
					srcWidth = (int) (vw * ((float) bh / vh));
					srcHeight = bh;
					x = (bw - srcWidth) / 2;
					y = 0;
				}
				width = Math.min(vw, bw);
				height = Math.min(vh, bh);
				srcRect = new Rect(x, y, x + srcWidth, y + srcHeight);
				destRect = new Rect(0, 0, width, height);
				break;
			case FIT_XY:
				width = vw;
				height = vh;
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(0, 0, width, height);
				break;
			case CENTER:
			case MATRIX:
				width = Math.min(vw, bw);
				height = Math.min(vh, bh);
				x = (bw - width) / 2;
				y = (bh - height) / 2;
				srcRect = new Rect(x, y, x + width, y + height);
				destRect = new Rect(0, 0, width, height);
				break;
		}

		try {
			roundBitmap = getRoundedCornerBitmap(bitmap, roundPixels, srcRect, destRect, width, height);
		} catch (OutOfMemoryError e) {
			roundBitmap = bitmap;
		}

		return roundBitmap;
	}

	private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixels, Rect srcRect, Rect destRect, int width, int height) {
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final RectF destRectF = new RectF(destRect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xFF000000);
		canvas.drawRoundRect(destRectF, roundPixels, roundPixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, srcRect, destRectF, paint);

		return output;
	}
}
