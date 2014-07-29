
package com.android.kit;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.android.kit.utils.KitLog;

/**
 * 项目基本配置
 * 
 * @author Danel
 */

public class KDevice {

    private static KDevice mDevice;
    
    public static int height = 0;

    public static int width = 0;

    public static String deviceId = "";

    public static String packageName = "";

    public static float density;

    public static int densityDip;

    public static String projectVer;

    public static int androidSDKVer;
    
    private KDevice() {
    }
    
    public synchronized static KDevice getInstance(Context context){
        if( null == mDevice ){
            mDevice = new KDevice();
            init(context);
        }
        return mDevice;
    }
    
    private static void init(Context context){
        packageName = context.getPackageName();

        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(packageName, 0);
            projectVer = info.versionName;
        } catch (NameNotFoundException e) {
            KitLog.printStackTrace(e);
        }

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        density = dm.density;
        densityDip = dm.densityDpi;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tm.getDeviceId();

        androidSDKVer = android.os.Build.VERSION.SDK_INT;
        
    }
}
