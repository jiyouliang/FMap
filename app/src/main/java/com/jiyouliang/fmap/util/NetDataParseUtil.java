package com.jiyouliang.fmap.util;

import com.alibaba.fastjson.JSON;

/**
 * @author YouLiang.Ji
 * 将服务器返回数据解析为bean对象
 */
public class NetDataParseUtil {

    private static NetDataParseUtil instance;

    private NetDataParseUtil() {
    }

    public static synchronized NetDataParseUtil getInstance() {
        if (instance == null) {
            instance = new NetDataParseUtil();
        }
        return instance;
    }


    /**
     * json字符串转成对于类型对象
     * @param data
     * @param clazz
     * @return
     */
    public <T> T getparseToObject(String data, Class<T> clazz){
        return JSON.parseObject(data, clazz);
    }


}
