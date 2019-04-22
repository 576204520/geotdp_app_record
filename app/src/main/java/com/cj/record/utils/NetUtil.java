package com.cj.record.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.cj.record.R;

/**
 * @author cj 判断网络工具类
 */
public class NetUtil {
    /**
     * 没有连接网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 1;

    public static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    /**
     * 检查网络
     */
    public static boolean haveNet(Context context) {
        if (NetUtil.getNetWorkState(context.getApplicationContext()) < 0) {
            new AlertDialog.Builder(context.getApplicationContext())
                    .setTitle(R.string.hint)
                    .setMessage(R.string.net_no_work)
                    .setNegativeButton(R.string.go_setting,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                                }
                            })
                    .setPositiveButton(R.string.disagree, null)
                    .setCancelable(false)
                    .show();
            return false;
        } else {
            return true;
        }
    }
}