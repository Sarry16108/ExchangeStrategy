package com.example.finance.tradestrategy.utils;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/23.
 */

public class ToolGson {
    private static Gson mGson = new Gson();


    public static String castObjectJson(Object object) {
        return mGson.toJson(object);
    }

    public static <T> T castJsonObject(String json, Class<T> type) {
        return mGson.fromJson(json, type);
    }

    /**
     * List<String>测试可以
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> castJsonObjList(String json, Class<T> type) {
        JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
        ArrayList<T> arrayList = new ArrayList<>(jsonArray.size());
        for (final JsonElement element : jsonArray) {
            arrayList.add(mGson.fromJson(element, type));
        }
        return arrayList;
    }

    /**
     * List<String>测试可以
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> castJsonObjList2(String json, Class<T> type) {
        return mGson.fromJson(json, new TypeToken<List<T>>() {}.getType());
    }

    /**
     * List<String>测试可以
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> Set<T> castJsonObjSet(String json, Class<T> type) {
        return mGson.fromJson(json, new TypeToken<Set<T>>() {}.getType());
    }

    public static <T> String castMapToJson(Map<String, T> map) {
        String jsonStr = mGson.toJson(map);
        return jsonStr;
    }

    public static <T> Map<String, T> castJsonToMap(String json, Class<T> type) {
        Type inType = new TypeToken<Map<String, T>>() {}.getType();
        return mGson.fromJson(json, inType);
    }

//    public static Map<String, UserSimpleInfo> castJsonToMapUserSimpleInfo(String json) {
//        Type inType = new TypeToken<Map<String, UserSimpleInfo>>() {}.getType();
//        return mGson.fromJson(json, inType);
//    }
//
//    public static Map<String, UserImg> castJsonToMapUserImg(String json) {
//        Type inType = new TypeToken<Map<String, UserImg>>() {}.getType();
//        return mGson.fromJson(json, inType);
//    }
}
