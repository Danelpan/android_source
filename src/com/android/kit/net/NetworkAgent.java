/*
 * Copyright (C) 2012-2013 Author:Danel Email:danel.pan@sohu.com
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
package com.android.kit.net;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import com.android.kit.bitmap.FlushedInputStream;
import com.android.kit.exception.KitConnNullPointterException;
import com.android.kit.utils.KitStreamUtils;

/**
 * Create on 2012-12-1 下午2:13:36 
 * @author Danel 
 * @Email danel.pan@sohu.com
 * @description Http请求配置类，通过该对象，可以获得连接服务器的情况成功，或者失败
 * <br>该类未单例类，具体的使用如下：
 * <pre>
 * NetworkAgent mNetworkAgent = NetworkAgent.getInstance();
 * mNetworkAgent.....;
 * </pre>
 */
public class NetworkAgent {
	private static final String TAG = "HttpUtils";
	/**
	 * @description 客户端连接服务器超时时间
	 */
	private int timeout_conn = 30*1000;
	/**
	 * @description 客户端读取服务器超时时间
	 */
	private int timeout_read = 30*1000;
	
	private String charset = "UTF-8";
	
	/**
	 * @description 连接网络请求方式，POST请求
	 */
	public final String POST = "POST";
	/**
	 * @description 连接网络请求方式GET请求
	 */
	public final String GET = "GET";
	
	private static NetworkAgent na = null;
	
	public synchronized static  NetworkAgent getInstance(){
		if(null == na){
			na = new NetworkAgent();
		} 
		return na;
	}
	
	
	/**
	 * 获得连接编码方式
	 * @return
	 */
	public String getCharset() {
		return charset;
	}
	/**
	 * 设置连接编码方式
	 * @param charset 如果不设置的话默认为UTF-8格式
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	/**
	 * 设置连接服务器超时的时间
	 * @param timeout 超时的时间，默认为30*1000毫秒
	 */
	public void setConnTimeout(int timeout) {
		this.timeout_conn = timeout;
	}
	/**
	 * 获得设置的连接服务器是的超时时间
	 * @return
	 */
	public long getConnTimeout() {
		return timeout_conn;
	}
	
	/**
	 * 设置客户端连接服务器超时时间
	 * @param timeout 默认为30*1000毫秒
	 */
	public void setReadTimeout(int timeout){
		this.timeout_read = timeout;
	}
	/**
	 * 获得客户端读取服务器数据时的超时时间
	 * @return
	 */
	public long getReadTimeout(){
		return timeout_read;
	}
	
	/**
	 * 通过URL获得一个联网的连接，用{@link HttpStatusListener}操作该连接
	 * @param url
	 * @param params
	 * @param method
	 * @param statusListener
	 * @throws IOException
	 */
	public void doConnection(String url,Map<String,Object>params,String method, HttpStatusListener statusListener) throws IOException{
		HttpURLConnection conn = doConnection(url, params, method);
		if(statusListener.onInstance(conn)){
			return;
		}
		InputStream is = conn.getInputStream();
		int responseCode = conn.getResponseCode();
		statusListener.onResponse(responseCode,is);
	}
	
	/**
	 * 从连接中，获得一个输入流
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStream(String url,Map<String,Object>params,String method) throws IOException{
		HttpURLConnection connection = doConnection(url,params,method);
		if(connection==null){
			throw new KitConnNullPointterException();
		}
		return connection.getInputStream();
	}
	
	/**
	 * 通过一个连接获得一个字符串
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	public String getString(String url,Map<String,Object>params,String method) throws IOException{
		InputStream is = getInputStream(url,params,method);
		FlushedInputStream in = new FlushedInputStream(new BufferedInputStream(is, 8*1024));
		return KitStreamUtils.readAsciiLine(in);
	}
	
	/**
	 * 通过URL获得一个联网的连接
	 * @param url
	 * @param params
	 * @param method
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection doConnection(String url,Map<String,Object>params,String method) throws IOException{
		if(URLUtil.isHttpsUrl(url)){
			HttpsVerify();
		}
		HttpURLConnection conn;
		if("POST".equalsIgnoreCase(method)){
			conn = httpPost(url, params);
		}else if("GET".equalsIgnoreCase(method)){
			conn = httpGet(url, params);
		}else{
			 throw new ProtocolException("Unknown method '" + method);
		}
		return conn;
	}
	
	/**
	 * 通过一个连接获得一个HttpURLConnection的实例，详情请见{@link URL}
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection httpInstance(String path) throws IOException{
		URL url =new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setDoInput(true);
		
		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(true);
		conn.setConnectTimeout(timeout_conn);
		conn.setReadTimeout(timeout_read);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", charset);
		return conn;
	}
	
	/**
	 * POST请求连接网络，请求的参数同意存在一个MAP中做处理，如果有特殊情况，如添加统计参数之类的
	 * @param urlPath
	 * @param params
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection httpPost(String url, Map<String, Object> params) throws IOException{
		Log.d(TAG, "URL-->"+url);
		String data = paramsStr(params);
		
		HttpURLConnection conn = httpInstance(url);
		conn.setRequestMethod("POST");
		if(!TextUtils.isEmpty(data)){
			Log.d(TAG, "URL ALL-->"+url+"?"+data);
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(data);
			dos.flush();
			dos.close();
		}
		return conn;
	}
	
	private HttpURLConnection httpGet(String url, Map<String, Object> params) throws IOException{
		String data = paramsStr(params);
		url = url+"?"+data;
		Log.d(TAG, "URL-->"+url);
		HttpURLConnection conn = httpInstance(url);
		conn.setRequestMethod("GET");
		return conn;
	}
	/**
	 * 接口参数Map转换成拼接参数
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private StringBuffer paramsToString(Map<String, Object> params) throws UnsupportedEncodingException{
		StringBuffer sb = new StringBuffer();
		for (@SuppressWarnings("rawtypes") Map.Entry entry : params.entrySet()) {
		sb.append((String)entry.getKey()).append("=").append(URLEncoder.encode((String)entry.getValue(), charset));
			sb.append("&");
		}
		return sb;
	}
	
	/**
	 * 检查连接参数map状态，如果为null或者size为0的话，那么返回false，否则返回true
	 * @param params
	 * @return
	 */
	private boolean checkParams(Map<String, Object> params){
		if(params==null||params.size()<=0){
			return false;
		}
		return true;
	}
	
	/**
	 * 接口参数拼接字符串
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String paramsStr(Map<String, Object> params) throws UnsupportedEncodingException{
		String data = "";
		if(checkParams(params)){
			StringBuffer sb = paramsToString(params);
			data += TextUtils.isEmpty(data)?"":"&"+sb.deleteCharAt(sb.length() - 1).toString();
			Log.d(TAG, "PARMAS-->"+sb.deleteCharAt(sb.length() - 1).toString());
		}
		return data;
	}
	
	/**
	 * Https证书处理方法
	 */
	private void HttpsVerify(){
		X509TrustManager xtm = new X509TrustManager(){
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
				Log.d(TAG, "---cert: " + chain[0].toString() + ", 认证方式: " + authType);
			}
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		HostnameVerifier hnv = new HostnameVerifier(){
			@Override
			public boolean verify(String hostname, SSLSession session) {
				Log.d(TAG, "hostname:" + hostname);
				return false;
			}
		};
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			X509TrustManager[] xtmArray = { xtm };
			sslContext.init(null, xtmArray, new SecureRandom());
		}catch (GeneralSecurityException localGeneralSecurityException){
			Log.d(TAG,"GeneralSecurityException",localGeneralSecurityException);
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(hnv);
	}
}
