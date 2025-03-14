package android_serialport_api.xingbang.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public class UsbUtils {
    private static final String TAG = "USB页面USBUtil";

    // 获取 U 盘的唯一标识方法
    public static String getUsbDeviceIdentifier(Context context) {
        String uuid = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7及以上使用 StorageManager 获取 UUID
            uuid = getUsbDeviceUuid(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6 使用 UsbManager 获取 Vendor ID 和 Product ID
            uuid = getUsbDeviceIds(context);
        }
//        else {
//            // 对于 Android 5 以下版本，可以使用 SAF 来访问文件（没有唯一标识符）
//            openFilePicker(context);
//        }
        return uuid;
    }

    // 获取 U 盘的 UUID（适用于 Android 7及以上）
    public static String getUsbDeviceUuid(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> volumes = null;
        String uuid = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            volumes = storageManager.getStorageVolumes();
            for (StorageVolume volume : volumes) {
                if (volume.isRemovable()) {
                    uuid = volume.getUuid();
                    if (uuid != null) {
                        Log.e(TAG, "UUID: " + uuid); // U盘的唯一标识符
                        return uuid;
                    } else {
                        Log.e(TAG, "获取UUID出错: " + uuid); // U盘的唯一标识符
                    }
                } else {
                    Log.e(TAG, "获取UUID失败: " + uuid); // U盘的唯一标识符
                }
            }
        }
        return uuid;
    }

    // 获取 U 盘的 Vendor ID 和 Product ID（适用于 Android 6）
    private static String getUsbDeviceIds(Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            int vendorId = device.getVendorId();
            int productId = device.getProductId();
            Log.e(TAG, "Vendor ID: " + vendorId + ", Product ID: " + productId);
            return "Vendor ID: " + vendorId + ", Product ID: " + productId;
        }
        return "";
    }

    // 读取文件内容
    public static void readTextFile(File file) {
        if (file.exists() && file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(fis);
                 BufferedReader reader = new BufferedReader(isr)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.e(TAG,"文件内容: " + line);
                }
            } catch (IOException e) {
                Log.e(TAG, "读取文件失败" + e.getMessage().toString());
            }
        } else {
            Log.e(TAG, "文件不存在或不是有效的文件:" + file.getPath());
        }
    }

    /**
     * 获取U盘的挂载路径
     */
    public static String getUSBMountPath(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        if (storageManager == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0及以上使用StorageVolume API
            List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
            for (StorageVolume volume : storageVolumes) {
                if (volume.isRemovable()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // Android 11及以上
                        File directory = volume.getDirectory();
                        if (directory != null) {
                            return directory.getAbsolutePath();
                        }
                    } else {
                        // Android 7.0到10.0
                        try {
                            return (String) volume.getClass().getMethod("getPath").invoke(volume);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            // Android 6.0及以下
            String[] possiblePaths = {"/storage/usb", "/mnt/usb", "/mnt/media_rw"};
            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists() && file.isDirectory()) {
                    return path;
                }
            }
        }
        return null;
    }

    /**
     * 请求 USB 设备权限
     *
     * @param context 上下文
     * @param device  USB 设备
     */
    public static void requestUSBPermission(Context context, UsbDevice device) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (usbManager != null && !usbManager.hasPermission(device)) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0,
                    new Intent("com.example.USB_PERMISSION"), 0);
            usbManager.requestPermission(device, permissionIntent);
        }
    }

    /**
     * 释放 USB 设备控制权
     *
     * @param device USB 设备
     */
    public static void releaseUSBDevice(UsbMassStorageDevice device) {
        if (device != null) {
            device.close(); // 关闭设备，释放控制权
            Log.d("USBUtils", "USB 设备控制权已释放");
        }
    }

    /**
     * 检查 USB 设备是否仍然连接
     *
     * @param context 上下文
     * @param device  要检查的 USB 设备
     * @return 是否仍然连接
     */
    public static boolean isUSBDeviceConnected(Context context, UsbDevice device) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            return false;
        }

        // 获取当前连接的 USB 设备列表
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        if (deviceList.size() > 0) {
            Log.e("usb列表",deviceList.toString());
            for (UsbDevice usbDevice : deviceList.values()) {
                if (usbDevice.getDeviceName().equals(device.getDeviceName())) {
                    return true; // 设备仍然连接
                }
            }
        } else {
            return false; // 设备未连接
        }
        return false; // 设备未连接
    }

    /**
     * 检查 USB 设备是否仍然连接
     *
     * @param context   上下文
     * @param vendorId  设备的厂商 ID
     * @param productId 设备的产品 ID
     * @return 是否仍然连接
     */
    public static boolean isUSBDeviceConnected(Context context, int vendorId, int productId) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            return false;
        }
        // 获取当前连接的 USB 设备列表
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Log.e(TAG,"usbdata：" + deviceList.values());
        for (UsbDevice usbDevice : deviceList.values()) {
            if (usbDevice.getVendorId() == vendorId && usbDevice.getProductId() == productId) {
                return true; // 设备仍然连接
            }
        }
        return false; // 设备未连接
    }

    // 比对 U盘UUID是否正确
    public static boolean isUuidValid(String str, String inputUuid) {
        // 提取字符串中的 UUID 部分
        String[] parts = str.split(",");
        for (String part : parts) {
            if (part.startsWith("uuid:")) {
                String uuid = part.substring(5);  // 获取 uuid 后的值
                return uuid.equals(inputUuid);  // 比较输入的 UUID 和提取的 UUID
            }
        }
        return false;  // 如果没有找到匹配的 UUID
    }

    // 比对用户输入的数字密码是否正确
    public static boolean isPasswordValid(String str, String inputPwd) {
        // 提取密码部分
        String[] parts = str.split(",");
        for (String part : parts) {
            if (part.startsWith("pwd")) {
                String[] pwdParts = part.split(":");
                if (pwdParts.length == 2 && pwdParts[1].equals(inputPwd)) {
                    return true;  // 找到密码并验证
                }
            }
        }
        return false;  // 如果没有找到匹配的密码
    }

    /**
     * 写入文件到 U 盘
     *
     * @param root     U 盘根目录
     * @param fileName 文件名
     * @param content  文件内容
     * @return 是否写入成功
     */
    public static boolean writeFileToUSB(UsbFile root, String fileName, String content) {
        try {
            // 创建文件
            UsbFile file = root.createFile(fileName);
            // 写入内容
            try (OutputStream outputStream = new UsbFileOutputStream(file)) {
                outputStream.write(content.getBytes());
                Log.d(TAG, "文件写入成功: " + file.getName());
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, "写入文件失败: " + e.getMessage());
            return false;
        }
    }


    /**
     * 读取 U 盘中的文件内容
     *
     * @param root     U 盘根目录
     * @param fileName 文件名
     * @return 文件内容
     */
    public static String readFileFromUSB(UsbFile root, String fileName) {
        StringBuilder content = new StringBuilder();
        try {
            // 查找文件
            UsbFile file = root.search(fileName);
            if (file != null) {
                // 读取内容
                try (InputStream inputStream = new UsbFileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        content.append(new String(buffer, 0, length));
                    }
                }
            } else {
                Log.e(TAG, "文件不存在: " + fileName);
            }
        } catch (IOException e) {
            Log.e(TAG, "读取文件失败: " + e.getMessage());
        }
        return content.toString();
    }

    /**
     * 读取文件内容
     */
    public String readFileContent(UsbFile file) {
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = new UsbFileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                content.append(new String(buffer, 0, length));
            }
        } catch (IOException e) {
            Log.e(TAG, "读取文件失败: " + e.getMessage());
        }
        return content.toString();
    }
}
