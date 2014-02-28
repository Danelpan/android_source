package com.android.kit.cache.imge;


/**
 * 缓存监听
 * @author Danel
 *
 */
public interface CacheLoaderListener {
	/**
	 * 处理缓存开始,在这里可以配置一些相应的信息，
	 * <br>以及初始化一些必要信息
	 */
	void onCacheLoaderStart(CacheConfig baseConfig);
	/**
	 * 处理缓存过程中,该方法很大程度是在线程中执行的，不能在此方法中更改UI
	 * <br>当然可以在这里处理一些耗时间的需求操作，但是也有情况该回调是
	 * <br>不可能被触发的，也就是当不读硬盘缓存，不读网络的时候是不会被回调的
	 * <br>当返回true的话，那么将不会执行下一步操作
	 */
	boolean onCacheLoaderLoading(CacheConfig baseConfig);
	/**
	 * 处理缓存结束，改方法的回调是以消息的形式触发的，所以不必要处理过多东西
	 * @param ccf 返回相应的缓存配置文件结果
	 * @param isSuccess 缓存处理是否成功
	 */
	void onCacheLoaderFinish(CacheConfig baseConfig,boolean isSuccess);
}
