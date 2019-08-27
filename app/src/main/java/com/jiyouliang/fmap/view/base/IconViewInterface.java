package com.jiyouliang.fmap.view.base;

/**
 * IconView相关接口
 */
public interface IconViewInterface {

    /**
     * 初始化背景图片
     * @return true:设置背景图片，false：未设置背景图片，则通过读取自定义属性设置背景
     */
    boolean createBackground();

    /**
     * 初始化icon图片
     *  @return true:设置icon背景图片，false：未设置icon背景图片，则通过读取自定义属性设置背景
     */
    boolean createIcon();

}
