package com.android.kit.net;

import java.util.Map;
/**
 * Http 请求配置模型类
 * @author Danel
 *
 */
public final class HttpModel {
	
	/**连接网络超时*/
	public int connTimeout = 30*1000;
	
	/**读网络超时*/
	public int readTime = 30*1000;
	
	/**网络编码方式*/
	public String charset = "UTF-8";
	
	/** 网络连接方式*/
	public String netMethod = "POST";
	
	/**设置联网配置信息*/
	public Map<String, Object> requestProperty;
	
	/**
	 * 是否使用网络运营商缓存
	 */
	public boolean useNetCaches = false;
	
	/**
	 * 重定向
	 */
	public boolean followRedirects = true;
	
}
