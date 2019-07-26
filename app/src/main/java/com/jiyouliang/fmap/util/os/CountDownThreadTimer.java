package com.jiyouliang.fmap.util.os;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;


/**
 * @author YouLiang.Ji
 * <p>
 * 倒计时任务,参考os.CountDownTimer,并新增子线程执行任务,避免占用UI线程。
 * 通过start() (see {@link #start()})方法开启倒计时,通过cancel() (see {@link #cancel()})结束倒计时。
 * 页面关闭建议调用cancel()方法取消倒计时
 */
public abstract class CountDownThreadTimer {

    private static final String TAG = "CountDownThreadTimer";

    /**
     * Millis since epoch when alarm should stop.
     */
    private long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    /**
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = false;
    private final InternalHanlder mHandler;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownThreadTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
        mHandler = new InternalHanlder(this);
    }

    /**
     * 取消倒计时,由于该方法在子线程中调用,页面关闭（比如{@link Activity#onDestroy()}方法）请调用该方法取消倒计时任务
     */
    public synchronized final void cancel() {
        mCancelled = true;
        //避免结束时还有任务在执行
        mHandler.setmCancelled(mCancelled);
        mHandler.removeMessages(MSG_START);
        Message.obtain(mHandler, MSG_CANCEL).sendToTarget();
        mHandler.removeMessages(MSG_CANCEL);
    }

    /**
     * 启动倒计时,为了避免占用过多UI线程，该方法默认在在子线程中执行
     */
    public synchronized final void start() {
        mCancelled = false;
        //开始倒计时才能计算结束时间
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.setmCancelled(mCancelled);
        mHandler.setmStopTimeInFuture(mStopTimeInFuture);
        // 开发中建议改为线程池
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (!mCancelled) {
                    if (mMillisInFuture <= 0) {
                        //设置为当前时间,让倒计时结束
                        Message.obtain(mHandler, MSG_CANCEL).sendToTarget();
                    }
                    Message.obtain(mHandler, MSG_START).sendToTarget();
                    try {
                        Thread.sleep(mCountdownInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    /**
     * 倒计时回调,该回调在主线程中调用
     *
     * @param mills 倒计时剩余毫秒值
     */
    abstract public void onTick(long mills);

    /**
     * 结束倒计时,该回调在主线程中调用
     */
    abstract public void onFinish();

    /**
     * 开始任务消息
     */
    private static final int MSG_START = 1;
    /**
     * 结束任务消息
     */
    private static final int MSG_CANCEL = 0;


    private static class InternalHanlder extends Handler {
        private long mStopTimeInFuture;
        private CountDownThreadTimer mTimer;
        private boolean mCancelled;

        private InternalHanlder(CountDownThreadTimer timer) {
            this.mTimer = timer;
        }

        void setmStopTimeInFuture(long stopTimeInFuture) {
            this.mStopTimeInFuture = stopTimeInFuture;
        }

        void setmCancelled(boolean cancelled) {
            this.mCancelled = cancelled;
        }

        @Override
        public void handleMessage(Message msg) {

            synchronized (mTimer) {
                switch (msg.what) {
                    case MSG_CANCEL:
                        mTimer.onFinish();
                        break;
                    case MSG_START:
                        if (mCancelled) {
                            return;
                        }

                        final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();
                        if (millisLeft <= 0) {
                            mTimer.onFinish();
                        } else {
                            mTimer.onTick(millisLeft);
                        }
                        break;
                    default:
                        // 谷歌建议switch语句要保留default,即使没有任何操作
                }

            }
        }
    }


}
