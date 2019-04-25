package com.cj.record.utils;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
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

    public static RequestBody getRequestBody(ConcurrentHashMap<String, String> map) {
        StringBuffer data = new StringBuffer();
        if (map != null && map.size() > 0) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Object key = entry.getKey();
                Object val = entry.getValue();
                data.append(key).append("=").append(val).append("&");
            }
        }
        String jso = data.substring(0, data.length() - 1);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), jso);
        return requestBody;
    }

    public static MultipartBody.Builder addTextPart(MultipartBody.Builder builder, Map<String, String> map) {
        if (map != null && map.size() > 0) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), entry.getValue());
                builder.addFormDataPart(entry.getKey(), null, requestBody);
            }
        }
        return builder;
    }

    public static MultipartBody.Builder addFilePart(MultipartBody.Builder builder, String fileName, File file) {
        builder.addFormDataPart("file_upload", fileName, RequestBody.create(MediaType.parse("multipart/form-data"), file));
        return builder;
    }
}
