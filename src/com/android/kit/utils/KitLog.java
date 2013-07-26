package com.android.kit.utils;

import com.android.kit.VersionInfo;

import android.util.Log;

/**
 * LOG统一输出工具类
 * @author Danel
 *
 */
public final class KitLog {
	private KitLog(){}
	public static final void e(String tag,String logMsg){
		if(VersionInfo.isDebug()){
			Log.e(tag, logMsg);
		}
	}
	public static final void e(String tag,String logMsg,Throwable throwable){
		if(VersionInfo.isDebug()){
			Log.e(tag, logMsg,throwable);
		}
	}
	public static final void d(String tag,String logMsg){
		if(VersionInfo.isDebug()){
			Log.d(tag, logMsg);
		}
	}
	public static final void d(String tag,String logMsg,Throwable throwable){
		if(VersionInfo.isDebug()){
			Log.d(tag, logMsg,throwable);
		}
	}
	public static final void i(String tag,String logMsg){
		if(VersionInfo.isDebug()){
			Log.i(tag, logMsg);
		}
	}
	public static final void i(String tag,String logMsg,Throwable throwable){
		if(VersionInfo.isDebug()){
			Log.i(tag, logMsg,throwable);
		}
	}
	public static final void w(String tag,String logMsg){
		if(VersionInfo.isDebug()){
			Log.w(tag, logMsg);
		}
	}
	public static final void w(String tag,String logMsg,Throwable throwable){
		if(VersionInfo.isDebug()){
			Log.w(tag, logMsg,throwable);
		}
	}
	public static final void v(String tag,String logMsg){
		if(VersionInfo.isDebug()){
			Log.v(tag, logMsg);
		}
	}
	public static final void v(String tag,String logMsg,Throwable throwable){
		if(VersionInfo.isDebug()){
			Log.v(tag, logMsg,throwable);
		}
	}
	public static final void out(Object mObject){
		if(VersionInfo.isDebug()){
			System.out.println(mObject);
		}
	}
	public static final void err(Object mObject){
		if(VersionInfo.isDebug()){
			System.err.println(mObject);
		}
	}
	
	public static final void printStackTrace(Throwable throwable){
		if(VersionInfo.isDebug()){
			throwable.printStackTrace();
		}
	}
}
