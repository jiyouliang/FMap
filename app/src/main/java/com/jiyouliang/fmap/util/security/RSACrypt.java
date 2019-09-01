package com.jiyouliang.fmap.util.security;

import android.content.Context;
import android.text.TextUtils;

import com.jiyouliang.fmap.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


/**
 * 非对称加密RSA解密和解密
 */
@SuppressWarnings("Duplicates")
public class RSACrypt {

    private static final String KEY_ALGORITHM = "RSA";
    //签名算法
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * 分段加密，如果生成秘钥长度为1024字节每次最大加密长度117字节，如果秘钥长度为2048字节则每次加密长度245字节
     */
    private static final int ENCRYPT_MAX_SIZE = 245;
    /**
     * 每次最大解密长度：如果生成秘钥长度为1024字节每次最大加密长度128字节，如果秘钥长度为2048字节则每次解密长度256字节
     */
    private static final int DECRYPT_MAX_SIZE = 256;

    private static final String PUBLIC_KEY = "为了安全已存储在服务器";
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQD5zz7ISzfJ3GCorNuRo6F0r5NgV3KOHO6ufEElS4RkiYC2bUquHXWslIo7vG8BwWYIiny3dujL0fdsy4H8HPAwkRQMYtuGiVVkiXnenlwk0l5WqfUVZHQGAyCheLBclN3JkV1qTidZ0YDhsPYtfYBePnzHuZuFXuE6prPlFvviE9T0YDSjLQ4oMp1cHGsMDQAEqpe4iVyXJTJl0WsLp5ZtCndDp8wQJAT+Ery6fhAjUgcM9rIseRCorG7MQnCkeUmFQkDo8OJQptAIGNe4DmQlspFHVun9TgJIznNIxqprGyN/aZyq2OmAubeCIEVYidM/cbiW4wd917SZSY4UE3ctAgMBAAECggEAUf6DRezib+Bk3Zh3LVN/IDSs8/dgA2NduGnSXDJwTyuO0tIGrCXBRRcwVDEiYnofSnhiPTnLaXMOemhFzwGUmOxVn0cndP5IyAoTtU1XQ0LFcj6w0ge5yWtnX3ikso6uSYXsGoNyiRmp6upRs1a5NrXd8RRsBpfA56Zl77DeljJ0NZk47b/vHRJZ2qtjatHeoxcQ0c6ZYsjqYDp7G84lmh7w7jSAoSr3HOLXB7fld2K2EKxdRMlSHiQbXrwdeWAQEALn3LBZsKpkhXlteo+4Yn4lWpaRC1HqGlg8HvXDAvRWdNC5jen1KZQtNqmOnd5xRYcTZMeQn7jySCAY8w5t5QKBgQD92RcAd+2BbmNDDgXdHeTOsqop4HK1lvygq5pxY7G/mO+kuAkJk9rAsAwsf79SU+UbDLFTqP0eLcx0tBSUh9sRJrUK1Wny1RcDpmrbDGLvEKsLbeWK1fbF2x0+BPxRyQ4FqACStYUxHBffRjB9/TzUtCcHPf0L5KN6N/Ry8w0KawKBgQD77WQX8Luzju/8YgsCcAERqhh4TnYtsLdp58Lar8Ol9dZbJFzm9Cgho6cw0DumpdRrJUQGa+O8aKPcBdWHaEcKfZPdhs73kCxOAfMSfCvC72qQWWDbjhTlPU+jmHqA5afxNhd0M6tw2biT7Mlb/mC/hHHS5cgVPc9wexTaKQSaxwKBgQCNqf9aNZOILfCbVVIUakBs0eyA3k9l49MsX1gbe4WQ5WqnxETW4XqtGhJRKyG8cjFjfRvhEmff1bNKVc0+burkkXNrjlpIOH9VfCQaBrmXrPenszbs2ieTl4qkN+gQRGHc6jsXpNGl7IWuBt+9D/xIwBkfw4pafsh0xpFXLSjT7QKBgG9taXc5UBw0XatYxhvW5HImtNoJgjXlUsqx6rqFQVkXai833yt71IBEfT+W6rtj35dgjTz13W3JLqizn+VbVwip0IwMA2D2UNJUBGopp8V51b1w+ulBZ7aZLq9iUtTOP0DuNuN7sxmgAwbSlaFt2ub6ssmgPqY+h5Dtr6RqvhGjAoGAZbabVKIgXH6Oe4sm+9ljSFtZBmtOqwCz9Z0bU7UXHPzLetEH2ZZs2R3zn4ZGOZnfOwupvjl4D8gvfJdapnhW3mzm9b8R2iM5Z3IC4WGQsZFkjWqH2VTKJBPbbA6Z6R8Xjop19BVM2m67vti6GKGGbtG8Dyb6lY64RdmOYANo4Uo=";

    public static String getPublicKeyStr() {
        return PUBLIC_KEY;
    }

    public static String getPrivateKeyStr() {
        return PRIVATE_KEY;
    }


    public static PublicKey getPublicKey() {
        try {
            KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
            return kf.generatePublic(new X509EncodedKeySpec(Base64.decode(PUBLIC_KEY)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey getPrivateKey() {
        try {
            KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(PRIVATE_KEY)));
            return privateKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥加密
     */
    public static String encryptByPrivateKey(PrivateKey privateKey, String input) {
        try {
            // 1.创建Cipher对象
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 2.初始化模式：加密/解密
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            // 3.加密/解密
            int offset = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baso = new ByteArrayOutputStream();
            while (input.getBytes().length - offset > 0) {
                if (input.getBytes().length - offset >= ENCRYPT_MAX_SIZE) {
                    // 加密完整117长度
                    buffer = cipher.doFinal(input.getBytes(), offset, ENCRYPT_MAX_SIZE);
                    // 重新计算偏移量
                    offset += ENCRYPT_MAX_SIZE;
                } else {
                    // 剩余最后一块
                    buffer = cipher.doFinal(input.getBytes(), offset, input.getBytes().length - offset);
                    offset = input.getBytes().length;
                }
                baso.write(buffer);
            }
            // byte[] privateEncrypt = cipher.doFinal(input.getBytes());
            String result = Base64.encode(baso.toByteArray());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥加密
     *
     * @param input
     * @return
     */
    public static String encryptByPrivateKey(String input) {
        try {
            // 1.创建Cipher对象
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 2.初始化模式：加密/解密
            PrivateKey privateKey = getPrivateKey();
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            // 3.加密/解密
            int offset = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baso = new ByteArrayOutputStream();
            while (input.getBytes().length - offset > 0) {
                if (input.getBytes().length - offset >= ENCRYPT_MAX_SIZE) {
                    // 加密完整117长度
                    buffer = cipher.doFinal(input.getBytes(), offset, ENCRYPT_MAX_SIZE);
                    // 重新计算偏移量
                    offset += ENCRYPT_MAX_SIZE;
                } else {
                    // 剩余最后一块
                    buffer = cipher.doFinal(input.getBytes(), offset, input.getBytes().length - offset);
                    offset = input.getBytes().length;
                }
                baso.write(buffer);
            }
            // byte[] privateEncrypt = cipher.doFinal(input.getBytes());
            String result = Base64.encode(baso.toByteArray());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 分段加密，每次最大加密长度117字节 公钥加密
     */
    public static String encryptByPublicKey(PublicKey publicKey, String input) {
        try {
            // 私钥加密
            // Cipher加密解密三部曲
            // 1.创建Cipher对象
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 2.初始化模式：加密/解密
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 3.加密/解密
            int offset = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baso = new ByteArrayOutputStream();
            while (input.getBytes().length - offset > 0) {
                if (input.getBytes().length - offset >= ENCRYPT_MAX_SIZE) {
                    // 加密完整117长度
                    buffer = cipher.doFinal(input.getBytes(), offset, ENCRYPT_MAX_SIZE);
                    // 重新计算偏移量
                    offset += ENCRYPT_MAX_SIZE;
                } else {
                    // 剩余最后一块
                    buffer = cipher.doFinal(input.getBytes(), offset, input.getBytes().length - offset);
                    offset = input.getBytes().length;
                }
                baso.write(buffer);
            }
            // byte[] privateEncrypt = cipher.doFinal(input.getBytes());
            String result = Base64.encode(baso.toByteArray());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥加密
     *
     * @param input
     * @return
     */
    public static String encryptByPublicKey(String input) {
        try {
            // 私钥加密
            // Cipher加密解密三部曲
            // 1.创建Cipher对象
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 2.初始化模式：加密/解密
            PublicKey publicKey = getPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 3.加密/解密
            int offset = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baso = new ByteArrayOutputStream();
            while (input.getBytes().length - offset > 0) {
                if (input.getBytes().length - offset >= ENCRYPT_MAX_SIZE) {
                    // 加密完整117长度
                    buffer = cipher.doFinal(input.getBytes(), offset, ENCRYPT_MAX_SIZE);
                    // 重新计算偏移量
                    offset += ENCRYPT_MAX_SIZE;
                } else {
                    // 剩余最后一块
                    buffer = cipher.doFinal(input.getBytes(), offset, input.getBytes().length - offset);
                    offset = input.getBytes().length;
                }
                baso.write(buffer);
            }
            // byte[] privateEncrypt = cipher.doFinal(input.getBytes());
            String result = Base64.encode(baso.toByteArray());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥解密
     *
     * @param input 密文（base64编码）
     */
    public static String decryptByPublicKey(PublicKey publicKey, String input) {
        try {
            byte[] inputDecode = Base64.decode(input);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 2.初始化模式：加密/解密
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            // 3.加密/解密
            int offset = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baso = new ByteArrayOutputStream();
            while (inputDecode.length - offset > 0) {
                if (inputDecode.length - offset >= DECRYPT_MAX_SIZE) {
                    // 加密完整117长度
                    buffer = cipher.doFinal(inputDecode, offset, DECRYPT_MAX_SIZE);
                    // 重新计算偏移量
                    offset += DECRYPT_MAX_SIZE;
                } else {
                    // 剩余最后一块
                    buffer = cipher.doFinal(inputDecode, offset, inputDecode.length - offset);
                    offset = inputDecode.length;
                }
                baso.write(buffer);
            }
            return baso.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥解密
     *
     * @param input
     * @return
     */
    public static String decryptByPublicKey(String input) {
        try {
            byte[] inputDecode = Base64.decode(input);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = getPublicKey();
            // 2.初始化模式：加密/解密
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            // 3.加密/解密
            int offset = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baso = new ByteArrayOutputStream();
            while (inputDecode.length - offset > 0) {
                if (inputDecode.length - offset >= DECRYPT_MAX_SIZE) {
                    // 加密完整117长度
                    buffer = cipher.doFinal(inputDecode, offset, DECRYPT_MAX_SIZE);
                    // 重新计算偏移量
                    offset += DECRYPT_MAX_SIZE;
                } else {
                    // 剩余最后一块
                    buffer = cipher.doFinal(inputDecode, offset, inputDecode.length - offset);
                    offset = inputDecode.length;
                }
                baso.write(buffer);
            }
            //byte[] result = cipher.doFinal(inputDecode);
            return baso.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥解密
     *
     * @param privateKey
     * @param input
     * @return
     */
    public static String decryptByPrivateKey(PrivateKey privateKey, String input) {
        try {
            byte[] inputDecode = Base64.decode(input);
            // 私钥加密
            // Cipher加密解密三部曲
            // 1.创建Cipher对象
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 2.初始化模式：加密/解密
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            // 3.加密/解密
            int offset = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baso = new ByteArrayOutputStream();
            while (inputDecode.length - offset > 0) {
                if (inputDecode.length - offset >= DECRYPT_MAX_SIZE) {
                    // 加密完整117长度
                    buffer = cipher.doFinal(inputDecode, offset, DECRYPT_MAX_SIZE);
                    // 重新计算偏移量
                    offset += DECRYPT_MAX_SIZE;
                } else {
                    // 剩余最后一块
                    buffer = cipher.doFinal(inputDecode, offset, inputDecode.length - offset);
                    offset = inputDecode.length;
                }
                baso.write(buffer);
            }
            //byte[] result = cipher.doFinal(inputDecode);
            return baso.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥解密
     *
     * @param input
     * @return
     */
    public static String decryptByPrivateKey(String input) {
        try {
            byte[] inputDecode = Base64.decode(input);
            // 私钥加密
            // Cipher加密解密三部曲
            // 1.创建Cipher对象
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = getPrivateKey();
            // 2.初始化模式：加密/解密
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            // 3.加密/解密
            int offset = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baso = new ByteArrayOutputStream();
            while (inputDecode.length - offset > 0) {
                if (inputDecode.length - offset >= DECRYPT_MAX_SIZE) {
                    // 加密完整117长度
                    buffer = cipher.doFinal(inputDecode, offset, DECRYPT_MAX_SIZE);
                    // 重新计算偏移量
                    offset += DECRYPT_MAX_SIZE;
                } else {
                    // 剩余最后一块
                    buffer = cipher.doFinal(inputDecode, offset, inputDecode.length - offset);
                    offset = inputDecode.length;
                }
                baso.write(buffer);
            }
            //byte[] result = cipher.doFinal(inputDecode);
            return baso.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对已加密数据进行签名
     *
     * @param data       已加密的数据
     * @param privateKey 私钥
     * @return 对已加密数据生成的签名
     * @throws Exception
     */

    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encode(signature.sign());
    }

    /**
     * 对已加密数据进行签名
     *
     * @param data 已加密的数据
     * @return 对已加密数据生成的签名
     * @throws Exception
     */

    public static String sign(byte[] data) throws Exception {
        byte[] keyBytes = Base64.decode(PRIVATE_KEY);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encode(signature.sign());
    }


    /**
     * 验签
     *
     * @param data      签名之前的数据
     * @param publicKey 公钥
     * @param sign      签名之后的数据
     * @return 验签是否成功
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decode(sign));
    }

    /**
     * 验签
     *
     * @param data 签名之前的数据
     * @param sign 签名之后的数据
     * @return 验签是否成功
     * @throws Exception
     */
    public static boolean verify(byte[] data, String sign) throws Exception {
        byte[] keyBytes = Base64.decode(PUBLIC_KEY);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decode(sign));
    }


    public static String genSign(Context context, String timestamp) throws Exception {
        String keystoreMD5 = KeystoreUtil.getMD5Signatures(context.getPackageManager(), context.getPackageName());
        if (TextUtils.isEmpty(keystoreMD5)) {
            return null;
        }
        StringBuilder data = new StringBuilder();
        data.append(keystoreMD5).append(timestamp);
        //生成RSA 签名
        return sign(data.toString().getBytes());
    }


}
