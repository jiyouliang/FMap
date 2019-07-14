package com.jiyouliang.fmap.util.security;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * keystore相关
 */
public class KeystoreUtil {

    /**
     * 获取md5签名信息
     *
     * @param pm
     * @param package_name 包名
     */

    public static String getMD5Signatures(PackageManager pm, String package_name) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(package_name, PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(signatures[0].toByteArray());
            byte[] digest = md5.digest();

            for (int i = 0; i < digest.length; i++) {
                //转16进制
                if (Integer.toHexString(0xFF & digest[i]).length() == 1) {
                    //1位数，前面补0
                    stringBuffer.append("0").append(Integer.toHexString(0xFF & digest[i]));
                } else {
                    stringBuffer.append(Integer.toHexString(0xFF & digest[i]));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString().toUpperCase();
    }
}
