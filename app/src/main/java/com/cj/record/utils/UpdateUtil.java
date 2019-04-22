package com.cj.record.utils;

import android.content.Context;
import android.content.pm.PackageManager;


/**
 */

public class UpdateUtil {

    /**
     * 获取版本名称
     */
    public static String getVerName(Context context) {
        String verName = null;
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 获取版本号
     */
    public static int getVerCode(Context context) {
        int verName = 0;
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
