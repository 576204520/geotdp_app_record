package com.cj.record.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonUtils {

    private static Gson gson = null;

    public static synchronized Gson getInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    //判定是否是json格式
    public static boolean isGoodJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            System.out.println("bad json: " + json);
            return false;
        }
    }
}
