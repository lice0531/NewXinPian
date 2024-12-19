package android_serialport_api.xingbang.utils;
import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {
    // 密钥 (16 字节)
    private static final String SECRET_KEY = "1234567890123456";
    // 初始化向量 (16 字节)
    private static final String IV = "1234567890123456";

    // 加密
    public static String encrypt(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes()); // 使用 IV
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  // 指定 CBC 模式和 PKCS5 填充
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    // 解密
    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes()); // 使用 IV
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  // 指定 CBC 模式和 PKCS5 填充
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decodedData = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] originalData = cipher.doFinal(decodedData);
        return new String(originalData);
    }
}
