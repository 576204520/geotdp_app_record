package com.cj.record.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017/8/25.
 */
public class Urls {
//    public static final String SERVER_PATH = "http://192.168.11.247:8089/";//广西测试
//    public static final String SERVER_PATH = "http://test.geotdp.com/";//测试
//    public static final String SERVER_PATH = "http://192.168.11.220/";//内部测试

//    public static final String SERVER_PATH = "http://gx.geotdp.com:8083/";//广西
//    public static final String SERVER_PATH = "http://jx.geotdp.com:8083/";//江西
//    public static final String SERVER_PATH = "http://xj.geotdp.com/";//新疆
//    public static final String SERVER_PATH = "http://ln.geotdp.com:8081/";//辽宁
    public static final String SERVER_PATH = "http://bj.geotdp.com:8081/";//北京
    // 登录请求
    public static final String LOGIN_POST = SERVER_PATH + "geotdp/compileUser/login";
    //获取版本信息
    public static final String GET_APP_CHECK_VERSION = SERVER_PATH + "geotdp/version/check";
    //下载apk地址
    public static final String Download_APK = SERVER_PATH + "gcdz.apk";
    // 关联项目 获取项目信息请求
    public static final String GET_PROJECT_INFO_BY_KEY_POST = SERVER_PATH + "geotdp/project/getProjectInfoByKey";
    //获取需要关联的勘察点列表
    public static final String GET_RELATE_HOLE = SERVER_PATH + "geotdp/hole/getRelateList";
    //关联勘察点
    public static final String DO_RELATE_HOLE = SERVER_PATH + "geotdp/hole/relate";
    //获取下载数据列表 getHoleListWithRecord
    public static final String GET_RELATE_HOLEWITHRECORD = SERVER_PATH + "geotdp/hole/getHoleListWithRecord";
    //下载服务器的数据
    public static final String DOWNLOAD_RELATE_HOLE = SERVER_PATH + "geotdp/hole/download";
    //上传字典库
    public static final String DICTIONARY_UPLOAD = SERVER_PATH + "geotdp/dictionary/upload";
    //下载字典库
    public static final String DICTIONARY_DOWNLOAD = SERVER_PATH + "geotdp/dictionary/download";
    // 勘探点上传所有
    public static final String UPLOAD_HOLE_NEW = SERVER_PATH + "geotdp/hole/uploadNew";
    //模板下载
    public static final String TEMPLATE_DOWNLOAD = SERVER_PATH + "geotdp/template/download";
    //模板上传
    public static final String TEMPLATE_UPLOAD = SERVER_PATH + "geotdp/template/upload";

    public static final String APP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com.geotdb.compile" + File.separator + "files";
    public static final String APP_PATH_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com.geotdb.compile" + File.separator + "chche";
    // 存放数据库的路径
    public static final String DATABASE_PATH = APP_PATH + File.separator + "database";
    // 本地orm框架控制的数据库名
    public static final String DATABASE_NAME = "gcdz.db";
    // 本地orm框架控制的数据库完整的地址
    public static final String DATABASE_BASE = DATABASE_PATH + File.separator + DATABASE_NAME;
    // 存放图片的路径
    public static final String PIC_PATH = APP_PATH + File.separator + "pic";
    // 存放VIDEO的路径
    public static final String VIDEO_PATH = APP_PATH + File.separator + "video";
    public static class SPKey {
        //登录成功的保存user信息
        public static final String USER_ID = "userId";
        public static final String USER_EMAIL = "userEmail";
        public static final String USER_REALNAME = "userRealName";
    }
}
