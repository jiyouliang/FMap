package com.jiyouliang.fmap.util;

/**
 * @author YouLiang.Ji
 */
public class UserUtils {

    /**
     * 根据手机号生成用户名
     * @param phone
     * @return
     */
    public static String getUserName(String phone){
        return String.format("map_%s", phone.substring(0, 6));
    }
}
