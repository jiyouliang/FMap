package com.jiyouliang.fmap;

import android.app.Application;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * @author YouLiang.Ji
 * 应用程序入口
 */
public class MapApplication extends Application {

    private List<View> leakedViews = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        // 检测application
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            //此过程专用于LeakCanary进行堆分析。在此过程中不应初始化应用程序。
            return;
        }
        LeakCanary.install(this);*/
    }



}
