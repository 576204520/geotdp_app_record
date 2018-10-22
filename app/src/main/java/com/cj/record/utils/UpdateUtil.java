package com.cj.record.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.cj.record.R;
import com.cj.record.activity.MainActivity;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.VersionVo;
import com.cj.record.service.DownloadService;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 */

public class UpdateUtil {
    public static final String DOWNLOAD_FILE_NAME = "record.apk";
    private static final String TAG = "UpdateUtil";

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

    /**
     * 检查app版本
     */
    public static void checkVersion(final Activity activity, final boolean isSetting) {
        Map<String, String> params = new HashMap<>();
        params.put("userID", BaseActivity.userID);
        OkGo.<String>post(Urls.GET_APP_CHECK_VERSION)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //注意这里已经是在主线程了
                        String data = response.body();//这个就是返回来的结果
                        Gson gson = new Gson();
                        JsonResult jsonResult = gson.fromJson(data, JsonResult.class);
                        //如果登陆成功，保存用户名和密码到数据库,并保存到baen
                        if (jsonResult.getStatus()) {
                            String result = jsonResult.getResult();
                            VersionVo version = gson.fromJson(result.toString(), VersionVo.class);
                            //0-不用更新  1-可以更新 2必须更新
                            String type = version.getType();
                            String content = "版本:" + version.getName() + "\n更新内容:" + version.getDescription() + "\n大小:" + version.getSize();
                            int code = Integer.parseInt(version.getCode());
                            int localCode = UpdateUtil.getVerCode(activity);
                            if (localCode < code) {
                                if ("2".equals(type)) {
                                    showViesionDialog(content, true, activity);

                                } else {
                                    showViesionDialog(content, false, activity);
                                }
                            }else {
                                //即使已经是最新，设置页面也要提示出来
                                if (isSetting) {
                                    ToastUtil.showToastS(activity, "已是最新版本");
                                }
                            }
                        } else {
                            ToastUtil.showToastS(activity, jsonResult.getMessage());
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtil.showToastS(activity, "网络连接错误");
                    }
                });
    }

    private static void showViesionDialog(String con, final boolean isMust, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.hint);
        builder.setMessage(con);
        builder.setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //更新
                        activity.startService(new Intent(activity, DownloadService.class));
                    }
                });
        builder.setPositiveButton(R.string.record_camera_cancel_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //强制更新，取消了会关闭程序
                if (isMust) {
                    activity.finish();
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
