package com.cj.record.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;


import com.cj.record.BuildConfig;
import com.cj.record.net.RetrofitClient;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;

import java.io.File;

/**
 * Created by Administrator on 2017/2/8.
 */
public class DownloadService extends Service {
    /**
     * 安卓系统下载类
     **/
    DownloadManager manager;
    /**
     * 接收下载完的广播
     **/
    DownloadCompleteReceiver receiver;
    private long requestId;
    //百度音乐测试地址
    String testLink = "http://gdown.baidu.com/data/wisegame/fd84b7f6746f0b18/baiduyinyue_4802.apk";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        initDownManager(testLink);
        initDownManager(BuildConfig.URL + "gcdz.apk");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化下载器
     **/
    private void initDownManager(String downloadLink) {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(downloadLink));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 设置为可被媒体扫描器找到
        down.allowScanningByMediaScanner();
        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载后文件存放的位置
        down.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "record.apk");
//        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "member.apk");
        // 将下载请求放入队列
        requestId = manager.enqueue(down);
        SPUtils.put(this, "requestId", requestId + "");
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    // 接受下载完成后的intent
    public class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (requestId == downId) {
                    //自动安装apk
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    File apkFile = queryDownloadedApk(context);
                    if (apkFile != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri contentUri = FileProvider.getUriForFile(context, "com.cj.reocrd.fileprovider", apkFile);
                            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
                        } else {
                            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        }
                        context.startActivity(install);
                    }
                } else {
                    ToastUtil.showToastS(context, "下载失败");
                }
            }
        }

        public File queryDownloadedApk(Context context) {
            File targetApkFile = null;
            DownloadManager downloader = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (requestId != -1) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(requestId);
                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                Cursor cur = downloader.query(query);
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        if (!TextUtils.isEmpty(uriString)) {
                            targetApkFile = new File(Uri.parse(uriString).getPath());
                        }
                    }
                    cur.close();
                }
            }
            return targetApkFile;
        }
    }

    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

}
