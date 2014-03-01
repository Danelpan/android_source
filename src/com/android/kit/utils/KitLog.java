
package com.android.kit.utils;

import android.util.Log;

import com.android.kit.KSDK;

/**
 * LOG统一输出工具类,该类会根据版本信息中debug的设置而定是否要打印日志的 <br>
 * {@link KSDK}
 * 
 * @author Danel
 */
public final class KitLog {
    private KitLog() {
    }

    public static void e(String tag, String logMsg) {
        if (KSDK.isDebug() && logMsg != null) {
            Log.e(tag, logMsg);
        }
    }

    public static void e(String tag, String logMsg, Throwable throwable) {
        if (KSDK.isDebug() && throwable != null) {
            Log.e(tag, logMsg, throwable);
        }
    }

    public static void d(String tag, String logMsg) {
        if (KSDK.isDebug() && logMsg != null) {
            Log.d(tag, logMsg);
        }
    }

    public static void d(String tag, String logMsg, Throwable throwable) {
        if (KSDK.isDebug() && throwable != null) {
            Log.d(tag, logMsg, throwable);
        }
    }

    public static void i(String tag, String logMsg) {
        if (KSDK.isDebug() && logMsg != null) {
            Log.i(tag, logMsg);
        }
    }

    public static void i(String tag, String logMsg, Throwable throwable) {
        if (KSDK.isDebug() && throwable != null) {
            Log.i(tag, logMsg, throwable);
        }
    }

    public static void w(String tag, String logMsg) {
        if (KSDK.isDebug() && logMsg != null) {
            Log.w(tag, logMsg);
        }
    }

    public static void w(String tag, String logMsg, Throwable throwable) {
        if (KSDK.isDebug() && throwable != null) {
            Log.w(tag, logMsg, throwable);
        }
    }

    public static void v(String tag, String logMsg) {
        if (KSDK.isDebug() && logMsg != null) {
            Log.v(tag, logMsg);
        }
    }

    public static void v(String tag, String logMsg, Throwable throwable) {
        if (KSDK.isDebug() && throwable != null) {
            Log.v(tag, logMsg, throwable);
        }
    }

    public static void out(Object mObject) {
        if (KSDK.isDebug() && mObject != null) {
            System.out.println(mObject);
        }
    }

    public static void err(Object mObject) {
        if (KSDK.isDebug() && mObject != null) {
            System.err.println(mObject);
        }
    }

    public static void printStackTrace(Throwable throwable) {
        if (KSDK.isDebug() && throwable != null) {
            throwable.printStackTrace();
        }
    }
}
