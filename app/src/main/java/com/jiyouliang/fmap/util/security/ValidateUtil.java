package com.jiyouliang.fmap.util.security;

/**
 * 校验相关
 */
public class ValidateUtil {

    /**
     * 手机号匹配
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone){
        if(null == phone){
            return false;
        }
        return phone.matches("^1[356789]\\d{9}$");
    }
}
