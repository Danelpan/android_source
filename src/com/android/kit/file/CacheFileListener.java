package com.android.kit.file;
/**
 * 缓存文件接口，通过缓存文件接口，我们可以追踪文件存储和取文件的监听
 * <br>
 * @author Danel
 *
 */
public interface CacheFileListener {
	void onCacheFileStart(FileConfig baseConfig);
	boolean onCacheFileLoading(FileConfig baseConfig);
}
