package com.jiyouliang.fmap.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author YouLiang.Ji
 * 微信平台相关
 */
public class WechatApi {
    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wx8d20b87b8cc47cc5";

    public static final int mTargetScene = SendMessageToWX.Req.WXSceneSession;

    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    /**
     * 注册到微信
     * @param context
     */
    public void regToWx(Context context) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);

        // 将应用的appId注册到微信
        api.registerApp(APP_ID);

        //建议动态监听微信启动广播进行注册到微信
        IntentFilter filter = new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // 将该app注册到微信
                api.registerApp(Constants.APP_ID);
            }
        };
        context.registerReceiver(broadcastReceiver, filter);

    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
