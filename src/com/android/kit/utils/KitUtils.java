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
package com.android.kit.utils;

import java.io.File;
import java.util.StringTokenizer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



/**
 * android_source
 * 2012-12-11 下午5:26:30
 * @author Danel
 * @summary 提供一些公用的方法工具
 */
public final class KitUtils {
	private KitUtils(){}
	/**
	 * 判断设备是否已经连接网络,true为当前设备已经连接了网络，false那么
	 * <br>设备未连接网络
	 * @param context
	 * @return
	 */
	public static final boolean isNetworkOnline(Context context){
		if(null == context){
			throw new NullPointerException();
		}
		ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);   
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;   
	}
	/**
	 * 获取网络信息
	 * @param context
	 * @return
	 */
	public static final NetworkInfo getNetworkInfo(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo;
	}
	
	/**
	 * 判断设备wifi是否可用，true为wifi可用，false不可用
	 * @param context
	 * @return
	 */
	public static final boolean isWifiOnline(Context context){
		if(null == context){
			throw new NullPointerException();
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(null == info){
			return false;
		}else{
			if(info.isAvailable()){
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断设备手机网络是否可用，true为网络可用，false为网络不可用
	 * @param context
	 * @return
	 */
	public static final boolean isMobileNetworkOnline(Context context){
		if(null == context){
			throw new NullPointerException();
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(null == info){
			return false;
		}else{
			if(info.isAvailable()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 级连创建文件，通过一个分解字符串的形式循环创建目录
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

}
