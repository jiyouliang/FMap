package com.jiyouliang.fmap.server.task;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author YouLiang.Ji
 * <p>
 * SharedPreferences任务
 */
public class SharedPreferencesTask  extends BaseTask{

    private static final String KEY_PHONE = "phone";

    /**
     * 用户相关配置信息
     */
    private static final String FILE_NAME_USER = "sp_user";
    private Runnable runnable;
    private Context mContext;
    private SharedPreferences mSp;

    @Deprecated
    public SharedPreferencesTask() {
    }

    public SharedPreferencesTask(Context context) {
        this.mContext = context;
        mSp = getSharePreferences(mContext);
    }



    @Override
    protected void callOnThreadPool() {
        super.callOnThreadPool();
    }

    @Override
    Runnable getRunnable() {
        return this.runnable;
    }

    private void setRunnable(Runnable r) {
        this.runnable = r;
    }


    /**
     * 异步存储用户手机号
     *
     * @param phone
     */
    public void saveUserPhone(final Context context, final String phone) {
       /* synchronized (this) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
                    sp.edit().putString(KEY_PHONE, phone).apply();
                }
            };
           setRunnable(r);
        }*/
        synchronized (this) {
            SharedPreferences sp = getSharePreferences(context);
            // apply默认已经异步存储磁盘,所以我们不需要再处理异步
            sp.edit().putString(KEY_PHONE, phone).apply();
        }
    }

    /**
     * 获取存储的用户手机号
     * @param context
     */
    public String getUserPhone(Context context) {
       /* synchronized (this) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    SharedPreferences sp = getSharePreferences(mContext);
                    sp.getString(KEY_PHONE, null);
                }
            };
            setRunnable(r);
        }*/
        SharedPreferences sp = getSharePreferences(context);
        return sp.getString(KEY_PHONE, null);
    }

    private SharedPreferences getSharePreferences(Context context) {
        return context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
    }

    /**
     * 清空存储的手机号
     */
    public void clearPhone(){
        if(mSp == null){
            throw new NullPointerException("mSp对象不能为空,请调用构造方法SharedPreferencesTask(Context context)初始化");
        }
        mSp.edit().putString(KEY_PHONE, "").apply();
    }
}
