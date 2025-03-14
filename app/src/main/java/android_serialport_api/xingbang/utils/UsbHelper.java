package android_serialport_api.xingbang.utils;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;

public class UsbHelper {
    private static final String TAG = "UsbHelper";
    private static final String ACTION_USB_PERMISSION = "com.example.USB_PERMISSION";

    private UsbManager usbManager;
    private UsbMassStorageDevice[] storageDevices;
    private PendingIntent usbPermissionIntent;
    private UsbDevice usbDevice;
    private Context context;
    private UsbEventListener usbEventListener;

    public UsbHelper(Context context) {
        this.context = context;
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    /**
     * 初始化 USB 设备
     */
    public void initUsb() {
        // 获取所有连接的 USB 设备
        storageDevices = UsbMassStorageDevice.getMassStorageDevices(context);
        // 注册 USB 权限广播接收器
        registerUSBReceiver();
        // 请求 USB 设备权限
        requestUSBPermission();
    }

    /**
     * 请求 USB 设备权限
     */
    private void requestUSBPermission() {
        // 检查是否有连接的 USB 设备
        if (storageDevices != null && storageDevices.length > 0) {
            for (UsbMassStorageDevice device : storageDevices) {
                usbDevice = device.getUsbDevice();
                if (!usbManager.hasPermission(usbDevice)) {
                    usbPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(usbDevice, usbPermissionIntent);
                } else {
                    // 如果有权限，初始化设备
                    initDevice(device);
                }
            }
        } else {
            Log.e(TAG, "没有连接的 USB 设备");
            if (usbEventListener != null) {
                usbEventListener.onNoUsbDeviceConnected();
            }
        }
    }

    /**
     * 注册 USB 权限广播接收器
     */
    private void registerUSBReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbReceiver, filter);
    }

    /**
     * 初始化 USB 设备并读取文件
     */
    private void initDevice(UsbMassStorageDevice device) {
        try {
            // 生成 U 盘唯一标识
            String vpnUUid = generateUsbUuid(device);
            Log.e(TAG, "usbuuid：" + vpnUUid);
            MmkvUtils.savecode("upUuid", vpnUUid);
            // 初始化设备
            device.init();
            FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
            Log.e(TAG, "U 盘文件系统: " + currentFs.getVolumeLabel());
            // 读取 CSV 文件内容
            String readContent = UsbUtils.readFileFromUSB(currentFs.getRootDirectory(), "updata.csv");
            String upContent = new String(MyUtils.decryptMode("jadl12345678912345678912".getBytes(), Base64.decode(readContent, Base64.DEFAULT)));
            Log.e(TAG, "读取的 CSV 文件内容: \n" + upContent);
            // 回调读取结果
            if (usbEventListener != null) {
                usbEventListener.onUsbFileRead(upContent);
            }
        } catch (Exception e) {
            Log.e(TAG, "初始化 U 盘设备失败: " + e.getMessage());
            if (usbEventListener != null) {
                usbEventListener.onUsbError(e.getMessage());
            }
        }
    }

    /**
     * 生成 U 盘唯一标识
     */
    @SuppressLint("HardwareIds")
    private String generateUsbUuid(UsbMassStorageDevice device) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return device.getUsbDevice().getVendorId() + "W" +
                    device.getUsbDevice().getProductId() + "N" + androidId;
        }
        return androidId; // 如果无法获取 USB 信息，返回 Android ID
    }

    /**
     * USB 权限广播接收器
     */
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_USB_PERMISSION:
                        synchronized (this) {
                            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                // 用户授予权限
                                if (device != null) {
                                    // 初始化设备
                                    for (UsbMassStorageDevice storageDevice : storageDevices) {
                                        if (storageDevice.getUsbDevice().equals(device)) {
                                            initDevice(storageDevice);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                // 用户拒绝权限
                                Log.e(TAG, "用户拒绝 USB 权限");
                                if (usbEventListener != null) {
                                    usbEventListener.onUsbPermissionDenied();
                                }
                            }
                        }
                        break;

                    case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                        // USB 设备插入
                        UsbDevice attachedDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (attachedDevice != null) {
                            usbDevice = attachedDevice;
                            Log.e(TAG, "U盘已插入,设备信息: " + attachedDevice.getDeviceName());
                            if (usbEventListener != null) {
                                usbEventListener.onUsbDeviceAttached(attachedDevice);
                            }
                        }
                        break;

                    case UsbManager.ACTION_USB_DEVICE_DETACHED:
                        // USB 设备拔出
                        Log.e(TAG, "U盘已拔出");
                        usbDevice = null;
                        if (usbEventListener != null) {
                            usbEventListener.onUsbDeviceDetached();
                        }
                        break;
                }
            }
        }
    };

    /**
     * 注销广播接收器
     */
    public void unregisterReceiver() {
        context.unregisterReceiver(usbReceiver);
    }

    /**
     * 设置 USB 事件监听器
     */
    public void setUsbEventListener(UsbEventListener listener) {
        this.usbEventListener = listener;
    }

    /**
     * USB 事件监听接口
     */
    public interface UsbEventListener {
        void onUsbFileRead(String content); // 读取文件内容
        void onUsbDeviceAttached(UsbDevice device); // USB 设备插入
        void onUsbDeviceDetached(); // USB 设备拔出
        void onUsbPermissionDenied(); // USB 权限被拒绝
        void onUsbError(String errorMessage); // USB 操作错误
        void onNoUsbDeviceConnected(); // 没有连接的 USB 设备
    }
}
