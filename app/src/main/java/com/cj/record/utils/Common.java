package com.cj.record.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.UUID;

import static com.cj.record.activity.base.BaseActivity.userID;

/**
 * Created by Administrator on 2018/5/25.
 */

public class Common {
    public static String getUUID() {
        UUID uid = UUID.randomUUID();
        String str = uid.toString().replace("-", "");
        return str;
    }


    //找文件夹下的图片
    public static String getPicByDir(String dirPath) {
        int start = dirPath.lastIndexOf("/");
        if (start != -1) {
            return dirPath + "/" + dirPath.substring(start + 1, dirPath.length()) + ".jpg";
        } else {
            return null;
        }
    }

    //找文件夹下的视频
    public static String getVideoByDir(String dirPath) {
        int start = dirPath.lastIndexOf("/");
        if (start != -1) {
            return dirPath + "/" + dirPath.substring(start + 1, dirPath.length()) + ".mp4";
        } else {
            return null;
        }
    }

    //计算两个坐标位置
    public static String GetDistance(double l1, double b1, double l2, double b2) {
        double n, t, t2, m, m2, ng2;
        double c, x0, p, e12, c0, c1, c2, c3, pai;
        double x1, y1, x2, y2;

        double y0 = 500000;
        pai = 3.1415926535898;
        p = 206264.8062471;
        c = 6399698.90178271;
        e12 = 6.7385254146835E-03;
        c0 = 6367558.49687;
        c1 = 32005.7801;
        c2 = 133.9213;
        c3 = 0.7032;

        b1 = b1 * pai / 180;
        x0 = c0 * b1 - Math.cos(b1) * (c1 * Math.sin(b1) + c2 * Math.pow(Math.sin(b1), 3) + c3 * Math.pow(Math.sin(b1), 5));//子午线弧长
        double i = (l1 - l2) * 3600; //经差"
        t = Math.tan(b1);
        t2 = t * t;
        ng2 = e12 * Math.pow((Math.cos(b1)), 2);
        n = c / Math.sqrt(1 + ng2);  //卯酉圈曲率半径
        m = i * Math.cos(b1) / p;
        m2 = m * m;
        x1 = x0 + n * t * ((0.5 + ((5 - t2 + 9 * ng2 + 4 * ng2 * ng2) / 24 + (61 - 58 * t2 + t2 * t2) * m2 / 720) * m2) * m2);
        y1 = n * m * (1 + m2 * ((1 - t2 + ng2) / 6 + m2 * (5 - 18 * t2 + t2 * t2 + 14 * ng2 - 58 * ng2 * t2) / 120));
        y1 = y1 + y0;

        b2 = b2 * pai / 180;
        x2 = c0 * b2 - Math.cos(b2) * (c1 * Math.sin(b2) + c2 * Math.pow(Math.sin(b2), 3) + c3 * Math.pow(Math.sin(b2), 5));//子午线弧长
        y2 = 500000;

        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        double value = Math.sqrt(Math.pow((y2 - y1), 2) + Math.pow((x2 - x1), 2));
        String s = String.valueOf(dcmFmt.format(value));
        return s;
    }


    //检查gps是否开启
    public static boolean gPSIsOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps) {
            return true;
        }
        return false;
    }

    /**
     * 检查gps
     */
    public static boolean haveGps(Context context) {
        if (!Common.gPSIsOPen(context)) {
            new AlertDialog.Builder(context)
                    .setTitle("定位服务提示")
                    .setMessage("GPS未开启,是否进行设置？")
                    .setNegativeButton("去设置",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setPositiveButton("取消", null)
                    .setCancelable(false)
                    .show();
            return false;
        } else {
            return true;
        }
    }

    public static String replaceAll(String str) {
        return str.replaceAll("&mdash;", "—")
                .replaceAll("&ldquo;", "“")
                .replaceAll("&rdquo;", "”")
                .replaceAll("&lsquo;", "‘")
                .replaceAll("&rsquo;", "’")
                .replaceAll("&amp;", "&")
                .replaceAll("&hellip;", "…");
    }

    public static void showMessage(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.hint)
                .setMessage(message)
                .setNegativeButton(R.string.agree,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setCancelable(false)
                .show();
    }

    public static final String getDataBaseKey(Context context) {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}