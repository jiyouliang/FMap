package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.jiyouliang.fmap.util.DeviceUtils;
import com.jiyouliang.fmap.util.security.RSACrypt;

import java.util.Map;

/**
 * @author YouLiang.Ji
 * Model基类:建议所有Model继承该类,该类的作用是将公共mode提取出来,比如组装签名json参数
 */

public class BaseModel {

    /**
     * 组装网络请求通用json对象,该对象存储sign和timestame两个字段,避免每次发起网络请求都需要重复添加
     * @return
     */
    private JSONObject getCommonJsonObject(Context context) throws Exception {
        JSONObject json = new JSONObject();
        String timestamp = DeviceUtils.getTimestampStr();
        String sign = RSACrypt.genSign(context, timestamp);
        json.put("timestamp", timestamp);
        json.put("sign", sign);
        return json;
    }


    /**
     * 组装网络请求参数,通过参数timestamp和sign请勿传递进来
     * @return
     */
    protected String assembleReqParams(Context context, Map<String, String> map) throws Exception {
        //遍历key和value,存储到json作为请求参数
        JSONObject json = getCommonJsonObject(context);
        for(Map.Entry<String, String> entry : map.entrySet()){
            if(entry == null){
                continue;
            }
            String key = entry.getKey();
            String value = entry.getValue();
            if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)){
                continue;
            }

            json.put(key, value);
        }
        return json.toString();
    }
}
