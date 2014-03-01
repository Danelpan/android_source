
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
    
    public int height = 0;

    public int width = 0;

    public String deviceId = "";

    public String packageName = "";

    public float density;

    public int densityDip;

    public String projectVer;

    public int androidSDKVer;
    
    private KDevice() {
    }
    
    public static KDevice getInstance(Context context){
        if( null == mDevice ){
            mDevice = new KDevice();
        }
        return mDevice;
    }
    
    public void init(Context context){
        this.packageName = context.getPackageName();

        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(this.packageName, 0);
            this.projectVer = info.versionName;
        } catch (NameNotFoundException e) {
            KitLog.printStackTrace(e);
        }

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        this.width = dm.widthPixels;
        this.height = dm.heightPixels;
        this.density = dm.density;
        this.densityDip = dm.densityDpi;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.deviceId = tm.getDeviceId();

        this.androidSDKVer = android.os.Build.VERSION.SDK_INT;
        
    }
}
