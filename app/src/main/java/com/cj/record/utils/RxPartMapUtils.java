package com.cj.record.utils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2019/4/18.
 */

public class RxPartMapUtils {

    public static RequestBody toRequestBodyOfText(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), value);
        return body;
    }

    public static RequestBody toRequestBodyOfImage(File pFile) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), pFile);
        return fileBody;
    }
}
