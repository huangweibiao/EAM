package com.eam.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES加密工具类
 * 用于敏感字段的加密和解密
 */
public class AesEncryptUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 默认密钥（实际项目中应从配置文件或环境变量读取）
     * 注意：生产环境请使用更安全的密钥管理方式
     */
    private static final String DEFAULT_KEY = "EAMSystem2024Key";

    /**
     * AES加密
     *
     * @param plainText 明文
     * @param key       密钥（16位）
     * @return 密文（Base64编码）
     */
    public static String encrypt(String plainText, String key) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * AES加密（使用默认密钥）
     *
     * @param plainText 明文
     * @return 密文（Base64编码）
     */
    public static String encrypt(String plainText) {
        return encrypt(plainText, DEFAULT_KEY);
    }

    /**
     * AES解密
     *
     * @param cipherText 密文（Base64编码）
     * @param key        密钥（16位）
     * @return 明文
     */
    public static String decrypt(String cipherText, String key) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }

    /**
     * AES解密（使用默认密钥）
     *
     * @param cipherText 密文（Base64编码）
     * @return 明文
     */
    public static String decrypt(String cipherText) {
        return decrypt(cipherText, DEFAULT_KEY);
    }

    /**
     * 判断字符串是否已加密（简单判断是否为Base64格式）
     *
     * @param text 待判断字符串
     * @return 是否已加密
     */
    public static boolean isEncrypted(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        try {
            Base64.getDecoder().decode(text);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
