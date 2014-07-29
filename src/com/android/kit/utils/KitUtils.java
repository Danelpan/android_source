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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	 * 根据给定的类型名和字段名，返回R文件中的字段的值
	 * @param typeName 属于哪个类别的属性 （id,layout,drawable,string,color,attr......）
	 * @param fieldName 字段名
	 * @return 字段的值
	 * @throws Exception 
	 */
	public static int getFieldValue(Context context,String typeName,String fieldName){
		int i = -1;
		try {
			Class<?> clazz = Class.forName(context.getPackageName()+".R$"+typeName);
			i = clazz.getField(fieldName).getInt(null);
		} catch (Exception e) {
		}
		return i;
	}
	/**
	 * 反射获取view控件的ID
	 * @param context
	 * @param fieldName
	 * @return
	 */
	public static int getId(Context context,String fieldName){
		return getFieldValue(context,"id", fieldName);
	}
	/**
	 * 反射获取xml布局的id
	 * @param context
	 * @param fieldName
	 * @return
	 */
	public static int getLayout(Context context,String fieldName){
		return getFieldValue(context,"layout", fieldName);
	}
	
	/**
	 * 反射获取图片资源id
	 * @param context
	 * @param fieldName
	 * @return
	 */
	public static int getDrawable(Context context,String fieldName){
		return getFieldValue(context,"drawable", fieldName);
	}
	
	/**
	 * 判断是否支持google地图包
	 * 
	 * @return
	 */
	public static boolean hasGoogleMap() {
		try {
			Class.forName("com.google.android.maps.MapActivity");
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断字符串是否全为数字
	 * 
	 * @param phone
	 * @return
	 */
	public static final boolean isNumeric(String phone) {
		if (phone.matches("\\d*")) {
			return true;
		}
		return false;
	}

	public static final ExecutorService getThreasPools(){
	    ExecutorService	mExecutorService = Executors.newFixedThreadPool(getAvailableProcessors());
		return mExecutorService;
	}
	
	public static int getAvailableProcessors() {
		int size = Runtime.getRuntime().availableProcessors();
		if(size<=0){
		    size = 1;
		}
		return size * 2;
	}
	
	/**
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
    public static int dip2px(Context context, int dpValue) {
        float scale = 0;
        try{
            scale = context.getResources().getDisplayMetrics().density;
        }catch(Exception e){
            return dpValue;
        }
        return (int) (dpValue * scale + 0.5f);
    }
    
    /**
     * 
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * 
     * @param spValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, int spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

}
