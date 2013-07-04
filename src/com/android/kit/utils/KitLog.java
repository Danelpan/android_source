package com.android.kit.utils;

import android.util.Log;

import com.android.kit.Constant;

/**
 * LOG统一输出工具类
 * @author Danel
 *
 */
public final class KitLog {
	private KitLog(){}
	public static final void e(String tag,String logMsg){
		if(Constant.DEBUG){
			Log.e(tag, logMsg);
		}
	}
	public static final void e(String tag,String logMsg,Throwable throwable){
		if(Constant.DEBUG){
			Log.e(tag, logMsg,throwable);
		}
	}
	public static final void d(String tag,String logMsg){
		if(Constant.DEBUG){
			Log.d(tag, logMsg);
		}
	}
	public static final void d(String tag,String logMsg,Throwable throwable){
		if(Constant.DEBUG){
			Log.d(tag, logMsg,throwable);
		}
	}
	public static final void i(String tag,String logMsg){
		if(Constant.DEBUG){
			Log.i(tag, logMsg);
		}
	}
	public static final void i(String tag,String logMsg,Throwable throwable){
		if(Constant.DEBUG){
			Log.i(tag, logMsg,throwable);
		}
	}
	public static final void w(String tag,String logMsg){
		if(Constant.DEBUG){
			Log.w(tag, logMsg);
		}
	}
	public static final void w(String tag,String logMsg,Throwable throwable){
		if(Constant.DEBUG){
			Log.w(tag, logMsg,throwable);
		}
	}
	public static final void v(String tag,String logMsg){
		if(Constant.DEBUG){
			Log.v(tag, logMsg);
		}
	}
	public static final void v(String tag,String logMsg,Throwable throwable){
		if(Constant.DEBUG){
			Log.v(tag, logMsg,throwable);
		}
	}
	public static final void out(Object mObject){
		if(Constant.DEBUG){
			System.out.println(mObject);
		}
	}
	public static final void err(Object mObject){
		if(Constant.DEBUG){
			System.err.println(mObject);
		}
	}
}
