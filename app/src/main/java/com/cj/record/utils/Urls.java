package com.cj.record.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017/8/25.
 */
public class Urls {
    //老版本地址
    public static final String APP_PATH_OLD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com.geotdb.compile" + File.separator + "files";
    public static final String APP_PATH_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com.geotdbj.compile" + File.separator + "chche";
    //新版本3.+地址
    public static final String APP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com.geotdbj.compile" + File.separator + "files";
    // 存放数据库的路径
    public static final String DATABASE_PATH_OLD = APP_PATH_OLD + File.separator + "database";
    // 存放数据库的路径
    public static final String DATABASE_PATH = APP_PATH + File.separator + "database";
    // 本地orm框架控制的数据库名
    public static final String DATABASE_NAME = "gcdz.db";
    // 本地orm框架控制的数据库完整的地址old
    public static final String DATABASE_BASE_OLD = DATABASE_PATH_OLD + File.separator + DATABASE_NAME;
    // 本地orm框架控制的数据库完整的地址
    public static final String DATABASE_BASE = DATABASE_PATH + File.separator + DATABASE_NAME;
    // 存放图片的路径
    public static final String PIC_PATH = APP_PATH + File.separator + "pic";
    // 存放VIDEO的路径
    public static final String VIDEO_PATH = APP_PATH + File.separator + "video";

    public static final String APP_KEY = "geotdp";

    public static class SPKey {
        //登录成功的保存user信息
        public static final String USER_ID = "userId";
        public static final String USER_EMAIL = "userEmail";
        public static final String USER_REALNAME = "userRealName";
        public static final String USER_PWD = "userPassword";
        public static final String USER_AUTO = "userAuto";
        public static final String USER_IDCARD = "userIdCard";
        public static final String USER_CERTIFICATENUMBER3 = "userCertificateNuCmber3";
        //判断是否整理过数据
        public static final String DATA_INIT = "initData";
        public static final String DATA_INIT2 = "initData2";
    }

    //各企业自己的key
//    public static final String C_KEY = "tZ4FzkhObZ";//北京正式
    public static final String C_KEY = "nQvvTKE504";//测试

}
