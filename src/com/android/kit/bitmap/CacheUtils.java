package com.android.kit.bitmap;

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
import java.util.StringTokenizer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

/**
 * @author Danel
 * @summary 图片缓存工具类，该类提供了简单的图片信息配置，和一些文件的基本操作
 */
public final class CacheUtils {

	private static String cachePath = "";

	private CacheUtils() {
	}

	/**
	 * 判断SDcard是否可用
	 * 
	 * @return
	 */
	public static boolean isMounted() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * 获取可以使用的缓存目录
	 * 
	 * @param context
	 * @param uniqueName
	 *            目录名称
	 * @return
	 */
	public static File getDiskCacheDir(Context context) {
		String cachePath = isMounted() ? getExternalCacheDir(context).getPath()
				: context.getCacheDir().getPath();
		return new File(cachePath + File.separator);
	}

	/**
	 * 获取bitmap的字节大小
	 * 
	 * @param bitmap
	 * @return
	 */
	public static int getBitmapSize(Bitmap bitmap) {
		if (bitmap == null) {
			return 0;
		}
		return bitmap.getRowBytes() * bitmap.getHeight();
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
			cacheDir = "/Android/data/" + context.getPackageName()+ "/imgCache/";
		} else {
			cacheDir = cachePath;
		}
		return createFile(cacheDir.startsWith(getExternalStorageDirectory())?cacheDir:getExternalStorageDirectory()+cacheDir);
	}
	
	/**
	 * 获取SD的路径
	 * @return
	 */
	public static final String getExternalStorageDirectory(){
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 获取文件路径空间大小
	 * 
	 * @param path
	 * @return
	 */
	public static long getUsableSpace(File path) {
		StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
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
		String key = CacheUtils.generator(value);
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

	public static File getDiskCacheFile(String dir, String name, String suffix) {
		name += TextUtils.isEmpty(suffix) ? "" : "." + suffix;
		File file = new File(createFile(dir), name);
		return file;
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
						bitmap = decodeSampledBitmapFromDescriptor(
								fileDescriptor, config.getBitmapWidth(),
								config.getBitmapHeight());
					}
				} catch (IOException e) {
				}
			} catch (FileNotFoundException e) {
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
	 * 保存图片到硬盘缓存
	 */
	public static synchronized void saveBitmapToFile(InputStream is, File file) {
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

	/**
	 * 更具提供的path路径清空当前的文件
	 */
	public static void clearCurrentCachePath(String path) {
		if (CacheUtils.isMounted()) {
			try {
				File file = CacheUtils.createFile(path);
				if (file.isDirectory()) {
					File subFile[] = file.listFiles();
					for (int i = 0; i < subFile.length; i++) {
						subFile[i].delete();
					}
				} else if (file.isFile()) {
					file.delete();
				}
			} catch (Exception e) {
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
				File file = createFile(path);
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

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * Decode and sample down a bitmap from a file to the requested width and
	 * height.
	 * 
	 * @param filename
	 *            The full path of the file to decode
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
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

	/**
	 * Decode and sample down a bitmap from a file input stream to the requested
	 * width and height.
	 * 
	 * @param fileDescriptor
	 *            The file descriptor to read from
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
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

	/**
	 * Calculate an inSampleSize for use in a {@link BitmapFactory.Options}
	 * object when decoding bitmaps using the decode* methods from
	 * {@link BitmapFactory}. This implementation calculates the closest
	 * inSampleSize that will result in the final decoded bitmap having a width
	 * and height equal to or larger than the requested width and height. This
	 * implementation does not ensure a power of 2 is returned for inSampleSize
	 * which can be faster when decoding but results in a larger bitmap which
	 * isn't as useful for caching purposes.
	 * 
	 * @param options
	 *            An options object with out* params already populated (run
	 *            through a decode* method with inJustDecodeBounds==true
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
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
