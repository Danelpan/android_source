package com.android.kit.cache.imge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.webkit.URLUtil;

import com.android.kit.net.HttpMethod;
import com.android.kit.net.KitHttpClient;
import com.android.kit.utils.KitBitmapUtils;
import com.android.kit.utils.KitCacheUtils;
import com.android.kit.utils.KitFileUtils;
import com.android.kit.utils.KitLog;
import com.android.kit.utils.KitUtils;

/**
 * 缓存处理公共入口，该入口可以配置缓存的一些基础信息，同时触发使用缓存，通过配置可以
 * <br>做到缓存处理，达到一些必要效果
 * <br>同时给出了几个入口展示，从缓存获取信息，从网络获取信息
 * 
 * @author Danel
 * 
 */
public final class CacheBitmapLoader {
	private final Object object = new Object();
	private WeakReference<Context> wrContext = null;
	private ExecutorService mExecutorService = null;
	private final CacheConfig baseConfig = new CacheConfig();
	
	private static LruCache<String, Bitmap> mCache = null;
	
	private volatile boolean isPause = false;
	
	private final SparseArray<String> cacheKeysForViews = new SparseArray<String>();
	
	public CacheBitmapLoader(Context context) {
		this(context, CacheUtils.defaultMMSize(context));
	}
	
	public CacheBitmapLoader(Context context,int cacheSize){
		wrContext = new WeakReference<Context>(context);
		setThreadPoolsSize(KitUtils.getAvailableProcessors());
		if(mCache == null){
		    mCache = new LruCache<String, Bitmap>(cacheSize){
		        @Override
		        protected int sizeOf(String key, Bitmap bitmap) {
		            return KitBitmapUtils.getBitmapSize(bitmap);
		        }
		    };
		}
		buildBaseConfig(context);
	}
		
	/**
	 * 实例化文件线程池
	 * @param poolSize
	 */
	public void setThreadPoolsSize(int poolSize){
	    mExecutorService = Executors.newFixedThreadPool(poolSize);
	}
	
	/**
	 * 初始化一些常用
	 * @param context
	 */
	private void buildBaseConfig(Context context){
		baseConfig.setSuffix(".pic");
		baseConfig.setCachePath(CacheUtils.getCacheDir(context).getAbsolutePath());
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int defaultWidth = (int)Math.floor((displayMetrics.widthPixels*5)/6);
		baseConfig.setReqHeight(defaultWidth);
		baseConfig.setReqWidth(defaultWidth);
		baseConfig.setMapCache(mCache);
		baseConfig.setLoaderListener(new SimpleDisplayer());
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
	public HttpMethod getHttpMethod(){
		return baseConfig.getHttpMethod();
	}
	/**
	 * 设置网络请求方式
	 * @param netMethod
	 * @return
	 */
	public CacheBitmapLoader setHttpMethod(HttpMethod method){
		baseConfig.setHttpMethod(method);
		return this;
	}
	
	/**
	 * 设置是否支持内存缓存
	 * @param supportMemoryCache
	 */
	public CacheBitmapLoader setSupportMemoryCache(boolean supportMemoryCache){
		baseConfig.setSupportMemoryCache(supportMemoryCache);
		return this;
	}
	
	/**
	 * 设置是否支持文件
	 * @param supportMemoryCache
	 */
	public CacheBitmapLoader setSupportDiskCache(boolean supportMemoryCache){
		baseConfig.setSupportDiskCache(supportMemoryCache);
		return this;
	}
	
	
	/**
	 * 设置基类配置属性模型，该配置以最后修改为准，它将是最后修改的属性信息
	 * @param suffix
	 */
	public CacheBitmapLoader setSuffix(String suffix) {
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
	public CacheBitmapLoader setCachePath(String cachePath) {
	    KitLog.e("setCachePath", "当前自定义图片缓存路径:"+cachePath);
	    KitFileUtils.createFile(cachePath);
		baseConfig.setCachePath(cachePath);
		return this;
	}
	/**
	 * 设置默认图片，当正在加载图片的时候
	 * @return
	 */
	public CacheBitmapLoader setLoadingBitmap(Bitmap bitmap){
		baseConfig.setLoadingBitmap(bitmap);
		return this;
	}
	/**
	 * 以ID形式设置加载默认图片
	 * @param res
	 * @return
	 */
	public CacheBitmapLoader setLoadingBitmap(int res){
	    setLoadingBitmap(
				KitCacheUtils.decodeSampledBitmapFromResource(getContext().getResources(), 
				res,
				baseConfig.getReqWidth(), 
				baseConfig.getReqHeight()));
		return this;
	}
	/**
	 * 设置加载失败之后的默认图片
	 * @param res
	 * @return
	 */
	public CacheBitmapLoader setLoadFailureBitmap(Bitmap bitmap){
		baseConfig.setLoadFailureBitmap(bitmap);
		return this;
	}
	/**
	 * 设置加载失败之后的默认图片
	 * @param res
	 * @return
	 */
	public CacheBitmapLoader setLoadFailureBitmap(int res){
	    setLoadFailureBitmap(
	            KitCacheUtils.decodeSampledBitmapFromResource(getContext().getResources(),
				res,
				baseConfig.getReqWidth(),
				baseConfig.getReqHeight()));
		return this;
	}
	
	/**
	 * 获得显示监听的接口实例
	 * @return
	 */
	public CacheLoaderListener getDisplayListener() {
		return baseConfig.getLoaderListener();
	}

	/**
	 * 设置显示监听听器
	 * @param displayListener
	 */
	public CacheBitmapLoader setDisplayListener(CacheLoaderListener displayListener) {
	    baseConfig.setLoaderListener(displayListener);
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
	 * 从缓存内存中那图片，具体结果将会体现在在回调函数中，
	 * <br>该方法只暂时只针对单个线程,单个图片缓存实例
	 * @param url
	 * @param loaderListener
	 */
	public void getBitmapFromCache(final String url,final CacheLoaderListener loaderListener){
	    getBitmapFromCache(baseConfig,url,loaderListener);
	}
	
	public void getBitmapFromCache(View view,final String url,final CacheLoaderListener loaderListener){
	    baseConfig.setView(view);
	    getBitmapFromCache(baseConfig,url,loaderListener);
	}
	
	public void getBitmapFromCache(CacheConfig cacheConfig,String url,final CacheLoaderListener loaderListener){
        final CacheConfig config = copyBaseConfig(cacheConfig);
        config.setLoaderListener(loaderListener);
        config.setUrl(url);
        _getBitmapFromCache(config);
	}
	
	private void _getBitmapFromCache(CacheConfig cacheConfig){
	    String url = cacheConfig.getUrl();
	    if(TextUtils.isEmpty(url) || !URLUtil.isNetworkUrl(url)){
            KitLog.err("当前传入的url不是一个网络连接");
            return;
        }
	    AsyBitmapTask task = new AsyBitmapTask(cacheConfig);
        mExecutorService.submit(task);
	}
	
	/**
	 * 关闭线程池,这里包含了两个线程池，
	 * 一个是网络加载线程池，一个是文件加载线程池
	 */
	public void shutdownPools(){
		if(!mExecutorService.isShutdown()){
		    mExecutorService.shutdown();
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
		baseConfig.setLoaderListener(cacheLoaderListener);
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
		config.setReqWidth(width);
		config.setReqHeight(height);
		config.setUrl(url);
		config.setView(view);
		_doDisplay(config);
	}
	private void _doDisplay(CacheConfig config) {
		String url = config.getUrl();
		config.setMapKey(url);
		config.getLoaderListener().onCacheLoaderStart(config);
		if(TextUtils.isEmpty(url)){
			KitLog.err("Bitmap of url is null,please check the url is ready...");
			config.getLoaderListener().onCacheLoaderFinish(config,false);
			return;
		}
		prepareDisplayTaskFor(config.getView(), url);
		Bitmap mBitmap = mCache.get(url);
		if(mBitmap != null && !mBitmap.isRecycled()){
			config.setBitmap(mCache.get(url));
			config.getLoaderListener().onCacheLoaderFinish(config,true);
		}else{
			AsyBitmapTask task = new AsyBitmapTask(config);
			mExecutorService.submit(task);
		}
	}
	
	private CacheConfig copyBaseConfig(CacheConfig temp){
		CacheConfig config = new CacheConfig();
		config.setAnimation(temp.getAnimation());
		config.setReqHeight(temp.getReqHeight());
		config.setReqWidth(temp.getReqWidth());
		config.setCachePath(temp.getCachePath());
		config.setLoadFailureBitmap(temp.getLoadFailureBitmap());
		config.setLoadingBitmap(temp.getLoadingBitmap());
		config.setSuffix(temp.getSuffix());
		config.setSupportDiskCache(temp.isSupportDiskCache());
		config.setSupportMemoryCache(temp.isSupportMemoryCache());
		config.setMapCache(temp.getMapCache());
		config.setHttpMethod(temp.getHttpMethod());
		config.setLoaderListener(temp.getLoaderListener());
		return config;
	}
	
	/**
	 * 释放内存缓存，在界面的时候调用该方法
	 */
	public void destroy(){
		clearCache();
		shutdownPools();
		cacheKeysForViews.clear();
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
				Bitmap failure = baseConfig.getLoadFailureBitmap();
				if(failure != null && !failure.isRecycled()){
					failure.recycle();
				}
			}
			mCache.snapshot().clear();
			mCache = null;
		}
	}
	/**
	 * 清空当前硬盘缓存中的数据信息
	 */
	public void clearCurrentCachePath(){
	    mExecutorService.submit(new Runnable() {
			@Override
			public void run() {
				CacheUtils.clearCurrentCachePath(baseConfig.getCachePath());
			}
		});
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
				config.getLoaderListener().onCacheLoaderFinish(config,config.getBitmap()!=null);
			}else{
				KitLog.err("不会加载图片");
			}
		}
	};
	
	private Bitmap getBitmapFromFile(CacheConfig cacheConfig){
	    Bitmap bitmap = null;
        String key = CacheUtils.createKey(cacheConfig.getUrl());
        File file = new File(cacheConfig.getCachePath(), 
                key+(TextUtils.isEmpty(cacheConfig.getSuffix())?"":cacheConfig.getSuffix()));

        if(file.exists()){
            bitmap = CacheUtils.getBitmapFromFile(file,cacheConfig);
            if(!cacheConfig.isSupportDiskCache() || !CacheUtils.isMounted()){
                file.delete();
            }
            if(bitmap == null){
                file.delete();
            }
        }
        
        if(null == bitmap && URLUtil.isFileUrl(cacheConfig.getUrl())){
            file = new File(cacheConfig.getUrl());
            if(file.exists()){
                bitmap = CacheUtils.getBitmapFromFile(file,cacheConfig);
                if(bitmap == null){
                    file.delete();
                }
            }
        }
        
        if(!cacheConfig.isSupportDiskCache()){ //如果不支持硬盘缓存，那么就把文件删除
            if(file.exists()){
                file.delete();
            }
        }
        
        return bitmap;
	}
	
	private Bitmap getBitmapFromHttp(CacheConfig cacheConfig){
	    Bitmap bitmap = null;
        
        if(cacheConfig.getLoaderListener().onCacheLoaderLoading(cacheConfig)){
            
        }else{
            InputStream is = null;
            
            KitHttpClient mHttpClient = new KitHttpClient();
            try {
                switch (cacheConfig.getHttpMethod()) {
                    case GET:
                        is = mHttpClient.getInputStream(cacheConfig.getUrl());
                        break;
                    case POST:
                        is = mHttpClient.postInputStream(cacheConfig.getUrl());
                        break;
                    default:
                        break;
                }
            } catch (ClientProtocolException e) {
                KitLog.printStackTrace(e);
            } catch (IOException e) {
                KitLog.printStackTrace(e);
            }
            
            if(is != null){ //正常从网络中获取流
                String key = CacheUtils.createKey(cacheConfig.getUrl());
                KitFileUtils.createFile(cacheConfig.getCachePath());
                File file = new File(cacheConfig.getCachePath(), 
                        key+(TextUtils.isEmpty(cacheConfig.getSuffix())?"":cacheConfig.getSuffix()));
                //防止覆盖图片情况，这种情况解决图片重复覆盖导致失真问题
                if(file.exists()){ //如果文件存在了那么就不覆盖文件
                    file.delete();
                }
                KitBitmapUtils.stream2File(is, file);
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
        }
            
        return bitmap;
	}
	
	public final class AsyBitmapTask implements Runnable{

	    private CacheConfig mCacheConfig;
	    
        public AsyBitmapTask(CacheConfig cacheConfig) {
            this.mCacheConfig = cacheConfig;
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
            if(null == mCacheConfig){
                return;
            }
            
            if(isRunChaos(mCacheConfig.getView(), mCacheConfig.getUrl())){
                Bitmap bitmap = mCache.get(mCacheConfig.getUrl());
                if(null == bitmap){ //从文件中获取
                    KitLog.err("try to get bitmap from disk....");
                    bitmap = getBitmapFromFile(mCacheConfig);
                }
                
                if(null == bitmap){ //从网络中获取
                    KitLog.err("try to get bitmap from http....");
                    bitmap = getBitmapFromHttp(mCacheConfig);
                }
                
                if(null !=bitmap && !mCache.snapshot().containsKey(mCacheConfig.getUrl())){
                    mCache.put(mCacheConfig.getMapKey(), bitmap);
                }
                if(!isRunChaos(mCacheConfig.getView(), mCacheConfig.getUrl())){
                    return;
                }
                mCacheConfig.setBitmap(mCache.get(mCacheConfig.getMapKey()));
                if(mCache.snapshot().containsKey(mCacheConfig.getUrl()) && !mCacheConfig.isSupportMemoryCache()){
                    mCache.remove(mCacheConfig.getUrl());
                }
                Message message = new Message();
                message.obj = mCacheConfig;
                mhander.sendMessage(message);
            }
        }
	    
	}
	
	private synchronized String getLoadingUriForView(View view) {
		if(view == null){
			return "";
		}
		return cacheKeysForViews.get(view.hashCode());
	}

	private synchronized void prepareDisplayTaskFor(View view, String memoryCacheKey) {
		if(view == null){
			return ;
		}
		cacheKeysForViews.put(view.hashCode(), memoryCacheKey);
	}

	private synchronized void cancelDisplayTaskFor(View view) {
		if(view == null){
			return ;
		}
		cacheKeysForViews.remove(view.hashCode());
	}
	
	/**
	 * 判断是不是已经篡改了标记对应的视图
	 * @return
	 */
	private synchronized boolean isRunChaos(View view,String url){
		if(view == null){
			return true;
		}
		if(null != cacheKeysForViews.get(view.hashCode())){
			return getLoadingUriForView(view).equals(url);
		}else {
			return false;
		}
	}
	
}
