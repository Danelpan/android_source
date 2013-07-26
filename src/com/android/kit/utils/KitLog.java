package com.android.kit.utils;

import com.android.kit.VersionInfo;

import android.util.Log;

/**
 * LOG统一输出工具类,该类会根据版本信息中debug的设置而定是否要打印日志的
 * <br>{@link VersionInfo}
 * @author Danel
 *
 */
public final class KitLog {
	private KitLog(){}
	public static void e(String tag,String logMsg){
		if(VersionInfo.isDebug()){
			Log.e(tag, logMsg);
		}
	}
	public static void e(String tag,String logMsg,Throwable throwable){
		if(VersionInfo.isDebug()){
			Log.e(tag, logMsg,throwable);
		}
	}
	public static void d(String tag,String logMsg){
		if(VersionInfo.isDebug()){
			Log.d(tag, logMsg);
		}
	}
	public static void d(String tag,String logMsg,Throwable throwable){
		if(VersionInfo.isDebug()){
			Log.d(tag, logMsg,throwable);
		}
	}
	public static void i(String tag,String logMsg){
		if(VersionInfo.isDebug()){
			Log.i(tag, logMsg);
		}
	}
	public static void i(String tag,String logMsg,Throwable throwable){
		if(VersionInfo.isDebug()){
			Log.i(tag, logMsg,throwable);
		}
	}
	public static void w(String tag,String logMsg){
		if(VersionInfo.isDebug()){
			Log.w(tag, logMsg);
		}
	}
	public static void w(String tag,String logMsg,Throwable throwable){
		if(VersionInfo.isDebug()){
			Log.w(tag, logMsg,throwable);
		}
	}
	public static void v(String tag,String logMsg){
		if(VersionInfo.isDebug()){
			Log.v(tag, logMsg);
		}
	}
	public static void v(String tag,String logMsg,Throwable throwable){
		if(VersionInfo.isDebug()){
			Log.v(tag, logMsg,throwable);
		}
	}
	public static void out(Object mObject){
		if(VersionInfo.isDebug()){
			System.out.println(mObject);
		}
	}
	public static void err(Object mObject){
		if(VersionInfo.isDebug()){
			System.err.println(mObject);
		}
	}
	
	public static void printStackTrace(Throwable throwable){
		if(VersionInfo.isDebug()){
			throwable.printStackTrace();
		}
	}
}
