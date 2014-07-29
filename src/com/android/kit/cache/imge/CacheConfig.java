package com.android.kit.cache.imge;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;

import com.android.kit.net.HttpMethod;
/**
 * 缓存模型配置文件，配置了一些缓存的信息,用以回调给用户
 * <br>通过这些模型大致可以得到，当前操作缓存数据的一些基本信息
 * <br>是否硬盘缓存,以及硬盘缓存的路径，以及图片的后缀是什么
 * @author Danel
 *
 */
public class CacheConfig {
	/**
	 * 当前模型的标志
	 */
	private Object tag;
	
	/**
	 * 网络连接方式
	 */
	private HttpMethod httpMethod = HttpMethod.GET;
	
	/**
	 * 相应的位图
	 */
	private Bitmap bitmap;
	/**
	 * 位图在网络中的url
	 */
	private String url;
	/**
	 * 位图所要显示到的视图中<br>
	 * 一般情况下都是imageview视图控件
	 */
	private View view;
	/**
	 * 位图的宽度
	 */
	private int reqWidth;
	/**
	 * 位图的高度
	 */
	private int reqHeight;
	
	/**
	 * 图片旋转的角度
	 */
	private int imageRot;
	/**
	 * 显示位图时候的动画
	 */
	private Animation animation;
	/**
	 * 加载图片时候的默认图片
	 */
	private Bitmap loadingBitmap;
	/**
	 * 图片加载失败之后设置的默认图片
	 */
	private Bitmap loadFailureBitmap;
	/**
	 * 图片的硬盘存储路径
	 */
	private String cachePath ;
	/**
	 * 图片的后缀名称，通常以".xx的形式"
	 */
	private String suffix;
	/**
	 * 是否支持硬盘缓存
	 */
	private boolean isSupportDiskCache = true;
	/**
	 * 是否支持内存缓存
	 */
	private boolean isSupportMemoryCache = true;
	
	private String mapKey;
	
	private CacheLoaderListener loaderListener;
	

	public String getMapKey() {
		return mapKey;
	}

	public void setMapKey(String mapKey) {
		this.mapKey = mapKey;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public boolean isSupportDiskCache() {
		return isSupportDiskCache;
	}

	public void setSupportDiskCache(boolean isSupportDiskCache) {
		this.isSupportDiskCache = isSupportDiskCache;
	}

	public boolean isSupportMemoryCache() {
		return isSupportMemoryCache;
	}

	public void setSupportMemoryCache(boolean isSupportMemoryCache) {
		this.isSupportMemoryCache = isSupportMemoryCache;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getCachePath() {
		return cachePath;
	}

	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public Bitmap getLoadingBitmap() {
		return loadingBitmap;
	}

	public void setLoadingBitmap(Bitmap loadingBitmap) {
		this.loadingBitmap = loadingBitmap;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public int getReqWidth() {
        return reqWidth;
    }

    public void setReqWidth(int reqWidth) {
        this.reqWidth = reqWidth;
    }

    public int getReqHeight() {
        return reqHeight;
    }

    public void setReqHeight(int reqHeight) {
        this.reqHeight = reqHeight;
    }

    public Bitmap getLoadFailureBitmap() {
        return loadFailureBitmap;
    }

    public void setLoadFailureBitmap(Bitmap loadFailureBitmap) {
        this.loadFailureBitmap = loadFailureBitmap;
    }

    public CacheLoaderListener getLoaderListener() {
        return loaderListener;
    }

    public void setLoaderListener(CacheLoaderListener loaderListener) {
        this.loaderListener = loaderListener;
    }

	public int getImageRot() {
		return imageRot;
	}

	public void setImageRot(int imageRot) {
		this.imageRot = imageRot;
	}

}
