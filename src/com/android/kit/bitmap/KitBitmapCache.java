package com.android.kit.bitmap;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.webkit.URLUtil;

import com.android.kit.http.AsyncHttpClient;
import com.android.kit.http.BinaryHttpResponseHandler;

/**
 * 缓存处理公共入口，该入口可以配置缓存的一些基础信息，同时触发使用缓存，通过配置可以
 * <br>做到缓存处理，达到一些必要效果
 * <br>同时给出了几个入口展示，从缓存获取信息，从网络获取信息
 * 
 * @author Danel
 * 
 */
public final class KitBitmapCache {
	private final Object object = new Object();
	private SoftReference<Context> wrContext = null;
	private ExecutorService mServiceFile = null;
	private ExecutorService mServiceNetWork = null;
	private ExecutorService director = null;
	private final CacheConfig baseConfig = new CacheConfig();
	
	private LruCache<String, Bitmap> mCache = null;
	private CacheLoaderListener displayListener;
	
	private volatile boolean isPause = false;
	final SparseArray<String> mSparseArray = new SparseArray<String>();
	public KitBitmapCache(Context context) {
		this(context, 5);
	}

	public KitBitmapCache(Context context, int poolSize) {
		this(context, poolSize, CacheUtils.defaultMMSize(context));
	}
	
	public KitBitmapCache(Context context, int poolSize,int cacheSize){
		wrContext = new SoftReference<Context>(context);
		poolsFile(2*poolSize);
		poolsNetwork(poolSize);
		mCache = new LruCache<String, Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return CacheUtils.getBitmapSize(bitmap);
			}
		};
		displayListener = new SimpleDisplayer(SimpleDisplayer.NORMAL);
		initBaseConfig(context);
		director = Executors.newCachedThreadPool();
	}
		
	/**
	 * 实例化文件线程池
	 * @param poolSize
	 */
	public void poolsFile(int poolSize){
		mServiceFile = initPools(poolSize);
	}
	/**
	 * 实例化文件线程池
	 * @param poolSize
	 */
	public void poolsNetwork(int poolSize){
		mServiceNetWork = initPools(poolSize);
	}
	
	private ExecutorService initPools(int poolSize){
		return Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setPriority(Thread.NORM_PRIORITY);
				return thread;
			}
		});
	}
	/**
	 * 初始化一些常用
	 * @param context
	 */
	private void initBaseConfig(Context context){
		baseConfig.setSuffix(".pic");
		baseConfig.setCachePath(CacheUtils.getExternalCacheDir(context).getAbsolutePath());
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int defaultWidth = (int)Math.floor(displayMetrics.widthPixels/3);
		baseConfig.setBitmapHeight(defaultWidth);
		baseConfig.setBitmapWidth(defaultWidth);
		baseConfig.setMapCache(mCache);
	}
	/**
	 * 获取当前使用的缓存的配置设置
	 * @return
	 */
	public CacheConfig getCacheConfig(){
		return baseConfig;
	}
	/**
	 * 获得网络请求的方式，分post和get
	 * @return
	 */
	public String getNetMethod(){
		return baseConfig.getNetMethod();
	}
	/**
	 * 设置网络请求方式，可以直接从枚举中获取请求方式，也必须从枚举中获取：
	 * 如：<pre>HttpMethod.POST
	 *HttpMethod.GET
	 * </pre>
	 * @param netMethod 
	 * @return
	 */
	public KitBitmapCache setNetMethod(String netMethod){
		baseConfig.setNetMethod(netMethod);
		return this;
	}
	
	/**
	 * 设置是否支持内存缓存
	 * @param supportMemoryCache
	 */
	public KitBitmapCache setSupportMemoryCache(boolean supportMemoryCache){
		baseConfig.setSupportMemoryCache(supportMemoryCache);
		return this;
	}
	
	/**
	 * 设置是否支持文件
	 * @param supportMemoryCache
	 */
	public KitBitmapCache setSupportDiskCache(boolean supportMemoryCache){
		baseConfig.setSupportDiskCache(supportMemoryCache);
		return this;
	}
	
	
	/**
	 * 设置基类配置属性模型，该配置以最后修改为准，它将是最后修改的属性信息
	 * @param suffix
	 */
	public KitBitmapCache setSuffix(String suffix) {
		baseConfig.setSuffix(suffix);
		return this;
	}
	/**
	 * 获取当前配置的基础属性文件的后缀名称
	 * @return
	 */
	public String getSunffix(){
		return baseConfig.getSuffix();
	}

	/**
	 * 获取图片缓存全局保存缓存路径，也就是图片缓存的所在路径
	 * @return
	 */
	public String getCachePath() {
		return baseConfig.getCachePath();
	}

	/**
	 * 设置图片缓存全局保存缓存路径，也就是图片缓存的所在路径
	 * @param cachePath
	 */
	public KitBitmapCache setCachePath(String cachePath) {
		CacheUtils.createFile(cachePath);
		baseConfig.setCachePath(cachePath);
		return this;
	}
	/**
	 * 设置默认图片，当正在加载图片的时候
	 * @return
	 */
	public KitBitmapCache setBaseViewBackground(Bitmap bitmap){
		baseConfig.setLoadingBitmap(bitmap);
		return this;
	}
	/**
	 * 以ID形式设置加载默认图片
	 * @param res
	 * @return
	 */
	public KitBitmapCache setBaseViewBackground(int res){
		setBaseViewBackground(
				CacheUtils.decodeSampledBitmapFromResource(getContext().getResources(), 
				res,
				baseConfig.getBitmapWidth(), 
				baseConfig.getBitmapHeight()));
		return this;
	}
	/**
	 * 设置加载失败之后的默认图片
	 * @param res
	 * @return
	 */
	public KitBitmapCache setBaseViewErrorBackground(Bitmap bitmap){
		baseConfig.setErrorBitmap(bitmap);
		return this;
	}
	/**
	 * 设置加载失败之后的默认图片
	 * @param res
	 * @return
	 */
	public KitBitmapCache setBaseViewErrorBackground(int res){
		setBaseViewErrorBackground(
				CacheUtils.decodeSampledBitmapFromResource(getContext().getResources(),
				res,
				baseConfig.getBitmapWidth(),
				baseConfig.getBitmapHeight()));
		return this;
	}
	
	/**
	 * 获得显示监听的接口实例
	 * @return
	 */
	public CacheLoaderListener getDisplayListener() {
		return displayListener;
	}

	/**
	 * 设置显示监听听器
	 * @param displayListener
	 */
	public KitBitmapCache setDisplayListener(CacheLoaderListener displayListener) {
		this.displayListener = displayListener;
		return this;
	}

	/**
	 * 获得当前缓存的上下文,如果实例对象没有传入context
	 * <br>那么将会获得一个null值
	 * @return
	 */
	public Context getContext() {
		if (null == wrContext) {
			return null;
		}
		return wrContext.get();
	}
	
	/**
	 * 挂起正在处理图片缓存的线程
	 */
	public void pause(){
		synchronized (object) {
			isPause = true;
		}
	}
	/**
	 * 重新唤起正在处理缓存的线程
	 */
	public void resume(){
		synchronized (object) {
			isPause = false;
			object.notifyAll();
		}
	}
	/**
	 * 根据URL获取相应的信息
	 * <h3>注意:</h3>该方法只暂时只针对单个线程,单个图片缓存实例
	 * @param url
	 * @param networkTaskListener
	 */
	public void getResultFromNetwork(final String url,final CacheLoaderListener loaderListener){
		if(null == url || !URLUtil.isNetworkUrl(url)){
			return;
		}
		this.displayListener = loaderListener;
		final CacheConfig config = copyBaseConfig(baseConfig);
		config.setUrl(url);
		NetworkTask task = new NetworkTask(config);
		mServiceNetWork.execute(task);
	}
	
	/**
	 * 从缓存内存中那图片，具体结果将会体现在在回调函数中，
	 * <br>该方法只暂时只针对单个线程,单个图片缓存实例
	 * @param url
	 * @param loaderListener
	 */
	public void getResultFromCache(final String url,final CacheLoaderListener loaderListener){
		if(null == url ){
			return;
		}
		this.displayListener = loaderListener;
		final CacheConfig config = copyBaseConfig(baseConfig);
		config.setUrl(url);
		FileTask fileTask = new FileTask(config);
		mServiceFile.execute(fileTask);
	}
	
	/**
	 * 关闭线程池,这里包含了两个线程池，
	 * 一个是网络加载线程池，一个是文件加载线程池
	 */
	public void shutdownPools(){
		if(!mServiceFile.isShutdown()){
			mServiceFile.shutdown();
		}
		if(!mServiceNetWork.isShutdown()){
			mServiceNetWork.shutdown();
		}
		
		if(!director.isShutdown()){
			director.shutdown();
		}
	}

	/**
	 * 根据url从网络获取相应缓存数据，图片
	 * @param url 缓存数据的url
	 */
	public void display(String url){
		CacheConfig config = copyBaseConfig(baseConfig);
		config.setUrl(url);
		_doDisplay(config);
	}

	/**
	 * 获取相应信息同时关联控件veiw，一般是图片关联控件
	 * @param view
	 * @param url
	 */
	public void display(View view,String url){
		CacheConfig config = copyBaseConfig(baseConfig);
		config.setUrl(url);
		config.setView(view);
		display(view,url,config);
	}
	/**
	 * 获取相应信息同时关联控件veiw，一般是图片关联控件,可配置类型
	 * <h3>注意:</h3>该方法暂不适用在大量图片中，如网格，列表等
	 * @param view
	 * @param url
	 * @param config
	 */
	public void display(View view,String url,CacheConfig mconfig){
		CacheConfig config = mconfig;
		config.setUrl(url);
		config.setView(view);
		_doDisplay(config);
	}
	/**
	 * 以监听形式获取相应信息，获取信息情况参见{@link CacheConfig}
	 * @param view
	 * @param url
	 * @param cacheLoaderListener
	 */
	public void display(View view,String url,CacheLoaderListener cacheLoaderListener){
		this.displayListener = cacheLoaderListener;
		display(view,url);
	}
	
	/**
	 * 通过宽高获取图片
	 * @param view
	 * @param url
	 * @param width
	 * @param height
	 */
	public void display(View view,String url,int width,int height){
		CacheConfig config = copyBaseConfig(baseConfig);
		config.setBitmapWidth(width);
		config.setBitmapHeight(height);
		config.setUrl(url);
		config.setView(view);
		_doDisplay(config);
	}
	private void _doDisplay(final CacheConfig config) {
		final String url = config.getUrl();
		config.setMapKey(url);
		displayListener.onCacheLoaderStart(config);
		if(TextUtils.isEmpty(url)){
			this.displayListener.onCacheLoaderFinish(config,false);
			return;
		}
		prepareDisplayTaskFor(config.getView(), url);
		if(mCache.snapshot().containsKey(url)){
			config.setBitmap(mCache.get(url));
			this.displayListener.onCacheLoaderFinish(config,true);
		}else{
			director.execute(new Runnable() {
				
				@Override
				public void run() {
					String key = CacheUtils.createKey(url);
					File file = new File(config.getCachePath(), 
							key+(TextUtils.isEmpty(config.getSuffix())?"":config.getSuffix()));
					if(file.exists()){ //判断文件是否存在，存在就去取文件中的图片
						FileTask fileTask = new FileTask(config);
						mServiceFile.execute(fileTask);
					}else{ //那么直接从网络获取图片
						NetworkTask task = new NetworkTask(config);
						mServiceNetWork.execute(task);
					}
				}
			});
			
		}
	}
	
	private CacheConfig copyBaseConfig(CacheConfig temp){
		CacheConfig config = new CacheConfig();
		config.setAnimation(temp.getAnimation());
		config.setBitmapHeight(temp.getBitmapHeight());
		config.setBitmapWidth(temp.getBitmapWidth());
		config.setCachePath(temp.getCachePath());
		config.setErrorBitmap(temp.getErrorBitmap());
		config.setLoadingBitmap(temp.getLoadingBitmap());
		config.setSuffix(temp.getSuffix());
		config.setSupportDiskCache(temp.isSupportDiskCache());
		config.setSupportMemoryCache(temp.isSupportMemoryCache());
		config.setMapCache(temp.getMapCache());
		config.setNetMethod(temp.getNetMethod());
		return config;
	}
	
	/**
	 * 释放内存缓存，在界面的时候调用该方法
	 */
	public void destroy(){
		clearCache();
		shutdownPools();
		mSparseArray.clear();
	}
	/**
	 * 清空缓存内容
	 */
	public void clearCache(){
		if(null != mCache && mCache.size() > 0){
			Iterator<Entry<String, Bitmap>> mIterator = mCache.snapshot().entrySet().iterator();
			while(mIterator.hasNext()){
				Entry<String, Bitmap> enry = mIterator.next();
				Bitmap bm = enry.getValue();
				if(bm != null && !bm.isRecycled()){
					bm.recycle();
				}
				
				Bitmap loading = baseConfig.getLoadingBitmap();
				if(loading != null && !loading.isRecycled()){
					loading.recycle();
				}
				Bitmap failure = baseConfig.getErrorBitmap();
				if(failure != null && !failure.isRecycled()){
					failure.recycle();
				}
			}
			mCache.snapshot().clear();
		}
	}
	/**
	 * 清空当前硬盘缓存中的数据信息
	 */
	public void clearCurrentCachePath(){
		mServiceFile.submit(new Runnable() {
			@Override
			public void run() {
				CacheUtils.clearCurrentCachePath(baseConfig.getCachePath());
			}
		});
	}

	private final class FileTask implements Runnable{
		private SoftReference<CacheConfig>reference;
		public FileTask(CacheConfig taskConfig) {
			this.reference = new SoftReference<CacheConfig>(taskConfig);
		}
		@Override
		public void run() {
			synchronized (object) {
				while (isPause) {
					try {
						object.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			CacheConfig cacheConfig = reference.get();
			if(cacheConfig ==null){
				return;
			}
			Bitmap bitmap = getMMCacheBitmap(cacheConfig);
			if(bitmap == null){
				bitmap = getSDCacheBitmap(cacheConfig);
			}
			sayResult(bitmap,cacheConfig);
		}
	}
	
	private final class NetworkTask implements Runnable {
		private CacheConfig cacheConfig;
		public NetworkTask(CacheConfig taskConfig) {
			this.cacheConfig = taskConfig;
		}

		@Override
		public void run() {

			synchronized (object) {
				while (isPause) {
					try {
						object.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			if(null == cacheConfig){
				return;
			}
			if(isRunChaos(cacheConfig.getView(), cacheConfig.getUrl())){
				Bitmap bitmap = getMMCacheBitmap(cacheConfig);

				if(bitmap == null){
					bitmap = getSDCacheBitmap(cacheConfig);
				}
				
				if(null == bitmap){
					if(displayListener.onCacheLoaderLoading(cacheConfig)){
						
					}else{
						AsyncHttpClient client = new AsyncHttpClient();
						String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
						
						if(cacheConfig.getNetMethod().equalsIgnoreCase("post")){
							client.post(cacheConfig.getUrl(), new BinaryHttpResponseHandler(allowedContentTypes) {
							    @Override
							    public void onSuccess(byte[] fileData) {
							    	httpResult(fileData,cacheConfig);
							    }
							    @Override
								public void onFailure(Throwable error,
										byte[] binaryData) {
									httpResult(null,cacheConfig);
									super.onFailure(error, binaryData);
								}
							});
						}else{
							client.get(cacheConfig.getUrl(), new BinaryHttpResponseHandler(allowedContentTypes) {
							    @Override
							    public void onSuccess(byte[] fileData) {
							    	httpResult(fileData,cacheConfig);
							    }

								@Override
								public void onFailure(Throwable error,
										byte[] binaryData) {
									httpResult(null,cacheConfig);
									super.onFailure(error, binaryData);
								}
								
								
							    
							});
							
						}
					}
					
				}else{
					sayResult(bitmap,cacheConfig);
				}
			}
		}
	}
	
	private void httpResult(byte[]buffer,CacheConfig cacheConfig){
		Bitmap bitmap = null;
		if(null != buffer){
			String key = CacheUtils.createKey(cacheConfig.getUrl());
			CacheUtils.createFile(cacheConfig.getCachePath());
			File file = new File(cacheConfig.getCachePath(), 
					key+(TextUtils.isEmpty(cacheConfig.getSuffix())?"":cacheConfig.getSuffix()));
			//防止覆盖图片情况，这种情况解决图片重复覆盖导致失真问题
			if(file.exists()){ //如果文件存在了那么就不覆盖文件
			}else{
				CacheUtils.saveBitmapToFile(buffer,file);
			}
			bitmap = CacheUtils.getBitmapFromFile(file, cacheConfig);
			if(!cacheConfig.isSupportDiskCache()){ //如果不支持硬盘缓存，那么就把文件删除
				if(file.exists()){
					file.delete();
				}
			}
			if(null == bitmap){
				file.delete();
			}
		}
		sayResult(bitmap,cacheConfig);
	}
	
	private final  Handler mhander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			CacheConfig config = (CacheConfig) msg.obj;
			if(null == config){
				return;
			}
			if(isRunChaos(config.getView(), config.getUrl())){
				cancelDisplayTaskFor(config.getView());
				displayListener.onCacheLoaderFinish(config,config.getBitmap()!=null);
			}
		}
	};
	
	//结果触发
	private void sayResult(Bitmap bitmap,CacheConfig cacheConfig){
		if(null!= bitmap && !mCache.snapshot().containsKey(cacheConfig.getUrl())){
			mCache.put(cacheConfig.getMapKey(), bitmap);
		}
		if(!isRunChaos(cacheConfig.getView(), cacheConfig.getUrl())){ //如果为true，那么直接不发消息不改变
			return;
		}
		cacheConfig.setBitmap(mCache.get(cacheConfig.getMapKey()));
		if(mCache.get(cacheConfig.getMapKey())!=null&&!cacheConfig.isSupportMemoryCache()){
			mCache.remove(cacheConfig.getMapKey());
		}
		Message message = new Message();
		message.obj = cacheConfig;
		mhander.sendMessage(message);
	}
	
	private Bitmap getMMCacheBitmap(CacheConfig mConfig){
		return mCache.get(mConfig.getMapKey());
	}
	
	private Bitmap getSDCacheBitmap(CacheConfig mConfig){
		String key = CacheUtils.createKey(mConfig.getUrl());
		File file = new File(baseConfig.getCachePath(), 
				key+(TextUtils.isEmpty(baseConfig.getSuffix())?"":baseConfig.getSuffix()));
		
		Bitmap bitmap = null;
		if(file.exists()){
			bitmap  = CacheUtils.getBitmapFromFile(file,baseConfig);
			if(null==bitmap || !baseConfig.isSupportDiskCache() || !CacheUtils.isMounted()){
				file.delete();
			}
		}
		
		if(null == bitmap && URLUtil.isFileUrl(mConfig.getUrl())){
			file = new File(mConfig.getUrl());
			if(file.exists()){
				bitmap = CacheUtils.getBitmapFromFile(file,baseConfig);
			}
		}
		return bitmap;
	}
	
	private synchronized String getLoadingUriForView(View view) {
		if(view == null){
			return "";
		}
		return mSparseArray.get(view.hashCode());
	}

	private synchronized void prepareDisplayTaskFor(View view, String memoryCacheKey) {
		if(view == null){
			return ;
		}
		mSparseArray.put(view.hashCode(), memoryCacheKey);
	}

	private synchronized void cancelDisplayTaskFor(View view) {
		if(view == null){
			return ;
		}
		mSparseArray.remove(view.hashCode());
	}
	
	/**
	 * 判断是不是已经篡改了标记对应的视图
	 * @return
	 */
	private synchronized boolean isRunChaos(View view,String url){
		if(view == null){
			return true;
		}
		if(mSparseArray.get(view.hashCode())!=null){
			return getLoadingUriForView(view).equals(url);
		}else {
			return false;
		}
	}
	
	
}
