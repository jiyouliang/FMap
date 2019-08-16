package com.jiyouliang.fmap.ui.navi;

/**  
 * 创建时间：2017-9-20 下午3:37:55  
 * 项目名称：IndexActivity  
 * @author liqi  
 * @version 1.0   
 * @since JDK 1.6.0_21  
 * 文件名称：TTS.java  
 * 类说明：  
 */
public interface TTS {
	public void init();
	public void playText(String playText);
	public void stopSpeak();
	public void destroy();
	public boolean isPlaying();
	public void setCallback(ICallBack callback);
}
