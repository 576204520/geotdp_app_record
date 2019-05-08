package com.cj.record.mvp.base;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;


import com.cj.record.utils.Urls;
import com.mabeijianxi.smallvideorecord2.DeviceUtils;
import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2017/8/25.
 */
public class App extends Application implements Thread.UncaughtExceptionHandler {
    public static String userID;

    @Override
    public void onCreate() {
        super.onCreate();
        //异常
        Thread.setDefaultUncaughtExceptionHandler(this);
        //初始化视频录制压缩
        initSmallVideo();
        //二维码扫描
        ZXingLibrary.initDisplayOpinion(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //分包
        MultiDex.install(this);
    }


    public static void initSmallVideo() {
        File dcim = new File(Urls.VIDEO_PATH);
        // 设置拍摄视频缓存路径
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                JianXiCamera.setVideoCachePath(dcim + "/picvideo/");
            } else {
                JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/picvideo/");
            }
        } else {
            JianXiCamera.setVideoCachePath(dcim + "/picvideo/");
        }
        // 初始化拍摄，遇到问题可选择开启此标记，以方便生成日志
        JianXiCamera.initialize(false, null);
    }

    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        sb.append("Hoast time is  ");
        sb.append(time + " //t");
        sb.append("Version code is  ");
        sb.append(Build.VERSION.SDK_INT + "//t");
        sb.append("Model is  ");
        sb.append(Build.MODEL + "//t");
        sb.append(ex.toString() + "//t");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        sb.append(sw.toString());

        File file = new File(Urls.APP_PATH + "log.log");
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] bytes = sb.toString().getBytes();
            os.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
