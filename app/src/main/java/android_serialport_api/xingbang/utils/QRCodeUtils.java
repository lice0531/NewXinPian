package android_serialport_api.xingbang.utils;
import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;
public class QRCodeUtils {
    public static Bitmap generateQRCodeJm(String content) {
        try {
            // 加密文字内容
            String encryptedContent = EncryptionUtils.encrypt(content);

            // 设置二维码的参数
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // 生成二维码
            Bitmap bitmap = toBitmap(qrCodeWriter.encode(encryptedContent, BarcodeFormat.QR_CODE, 500, 500, hints));
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将二维码转换为 Bitmap
    private static Bitmap toBitmap(com.google.zxing.common.BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bitmap;
    }

    // 生成二维码
    public static Bitmap generateQRCode(String content) {
        try {
            // 设置二维码参数
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            // 生成二维码
            Bitmap bitmap = toBitmap(qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 500, 500, hints));
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
