package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import android.DeviceControl;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.serialport.DeviceControlSpd;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.senter.pda.iam.libgpiot.Gpiot1;

import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.Cmd_EX;
import android_serialport_api.xingbang.cmd.DefCmd;
import android_serialport_api.xingbang.utils.NetUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.upload.FTP;
import android_serialport_api.xingbang.utils.upload.IntervalUtil;
import android_serialport_api.xingbang.utils.upload.Result;
import android_serialport_api.xingbang.utils.upload.XbUtils;
import android_serialport_api.xingbang.utils.uploadUtils.ByteUtil;
import android_serialport_api.xingbang.utils.uploadUtils.EmptyUtils;
import android_serialport_api.xingbang.utils.uploadUtils.FileInfoUtils;
import android_serialport_api.xingbang.utils.uploadUtils.TimeUtils;
import android_serialport_api.xingbang.utils.uploadUtils.ToastUtils;

/**
 * @ClassName: UpgradeActivity
 * @Description: 升级程序
 * @Date: 11/30/20 10:47 AM
 * @Author: kalinaji
 */
public class UpgradeActivity extends SerialPortActivity {

    private static final String TAG = "升级页面";
    // 控件
    private Button mBtnAssets, mBtnUp, mBtnDown;
    private Button mBtnOpenFile;
    private Button mBtnNewCurrency;
    private Button mBtnNewCurrency2;
    private Button mBtnNewPermit;
    private TextView mTvCmd;
    private TextView mTvVersion;
    private Button mBtnAPK;

    private static Handler mHandler = new Handler();
//    private SerialHelper mSerialHelper;     // 串口帮助类

    // FTP参数
    private FTP mFTP;
    private String mIP = "182.92.61.78";
    private String mUserName = "xingbang";
    private String mPassWord = "xingbang666";
    private String mSaveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xb";
    private List<FTPFile> mList_FtpFileName = new ArrayList<>();

    // E2
    private int mGetFrom;                       // 1:从Assets获得 2:从文件管理器获得 3:从xb获得
    private String mFileName = "micro.bin";     // 1
    private Uri mUri;                           // 2
    private List<byte[]> mList_Byte = new ArrayList<>();
    private int mNumber_E2 = 0;
    private int mIndex_E2 = 0;
    private int mFormat = 1024;
    private String mTip = "";


    private String mPath_Local;                 // 本地已下载安装路径
    private ReadThread mReadThread_upload;             // IAP检测 线程
    private ProgressThread mProgressThread;     // 下载百分比 线程
    public volatile String mDownLoadFilePath;   // 下载文件路径 3
    public volatile long mDownLoadFileSize;     // 下载文件大小
    public volatile Result mResult;             // 下载完成返回结果
    private String shengji = "升级";
    private String BinPath = "/nm/";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        shengji = (String) bundle.get("dataSend");
        Log.e(TAG, "传递-dataSend: " + shengji);
        mContext = this;
        initPower();
        // xb文件夹不存在则创建
        XbUtils.isFileExistence(mSaveDirPath);

        mArr_Permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

//        initPowerMode();        // 初始化上电方式
        initFTP();              // 初始化FTP
//        initSerialHelper();     // 初始化串口类
        initHandler();          // 初始化Handler
        initView();             // 初始化控件
        if (shengji.length()>0) {
            if (IntervalUtil.isFastClick_2()) {
                Download_File(shengji,BinPath, ".bin");
            }
        }
    }


    /**
     * 实例化上电方式
     */
    public void initPower() {
        mPowerOnMode = mApplication.getPowerIndex();
        Log.e("上电", "mPowerOnMode: " + mPowerOnMode);
        if (mPowerOnMode == 0) {
            try {
                mDeviceControl = new DeviceControl(DeviceControl.PowerType.MAIN, 94, 93);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("BaseActivity", "实例化 DeviceControl");
        } else if (mPowerOnMode == 1) {
            mGpiot1 = new Gpiot1();
            Log.e("BaseActivity", "实例化 Gpiot1");
        } else if (mPowerOnMode == 2) {
            try {
                mDeviceControlSpd = new DeviceControlSpd("NEW_MAIN_FG", 108);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("BaseActivity", "实例化 DeviceControl");
        } else if (mPowerOnMode == 3) {
            try {
                mDeviceControlSpd = new DeviceControlSpd("NEW_MAIN_FG", 156, 170, 7, 9);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("BaseActivity", "实例化 DeviceControl");
        } else {
            Log.e("BaseActivity", "实例化 空");
        }
    }

    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Utils.writeLog("->:" + str);
//                Log.e("发送命令", str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //处理芯片返回
    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);
        Log.e(TAG, "收到命令: " + fromCommad);
        mHandler.sendMessage(mHandler.obtainMessage(1100, fromCommad));

    }

    /**
     * 初始化上电方式
     */
//    private void initPowerMode() {
//        String device = Build.DEVICE;
//        Log.e("Build.DEVICE", device);
//
//        switch (device) {
//
//            // KT50 起爆器设备
//            case "KT50_B2": {
//                Log.e("liyi_device", "device: KT50_B2");
//                mSportName = "/dev/ttyMT1";   // 串口号
//                mPowerOnMode = 0;             // 上电方式
//                mIsPortrait = true;           // true 竖屏
//                break;
//            }
//            // ST327 起爆器设备
//            case "ST327": {
//                Log.e("liyi_device", "device: ST327");
//                mSportName = "/dev/ttyMSM0";   // 串口号
//                mPowerOnMode = 1;              // 上电方式(1: Gpio包上电)
//                mIsPortrait = true;            // true 竖屏
//                break;
//            }
//            // S337 起爆器设备
//            case "S337": {
//                Log.e("liyi_device", "device: S337");
//                mSportName = "/dev/ttyMSM0";   // 串口号
//                mPowerOnMode = 1;              // 上电方式(1: Gpio包上电)
//                mIsPortrait = true;            // true 竖屏
//                break;
//            }
//
//            // 级联设备 平板
//            case "astar-y3": {
//                Log.e("liyi_device", "device: astar-y3");
//                mSportName = "/dev/ttyS3";     // 串口号
//                mPowerOnMode = 0;              // 上电方式
//                mIsPortrait = false;           // true 竖屏
//                break;
//            }
//            default:
//
//                break;
//        }
//    }

    /**
     * 初始化FTP
     */
    private void initFTP() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mFTP = new FTP(mIP, mUserName, mPassWord);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        powerOffDevice(PIN_ADSL);// 下电
        // 退出 IAP检测线程
        if (mReadThread_upload != null) {
            mReadThread_upload.mExit = true;
            mReadThread_upload.interrupt();
        }
    }


    /**
     * 初始化串口类
     */
//    private void initSerialHelper() {
//        Log.e("liyi_initSerialHelper", "mSportName: " + mSportName);
//        mSerialHelper = new SerialHelper(mSportName, InitConst.BAUD_RATE) {
//
//            @Override
//            protected void onDataReceived(final ComBean comBean) {
//                Log.e("UpgradeActivity", "接收指令: " + ByteUtil.ByteArrToHex(comBean.bRec));
//                mHandler.sendMessage(mHandler.obtainMessage(1100, comBean));
//
//                mSerialHelper.mTimeThread.mExit = true;
//                mSerialHelper.mTimeThread.interrupt();
//                mSerialHelper.mTimeThread = null;
//            }
//
//            @Override
//            protected void onDataTimeOut(int maxTime) {
//                Log.e("UpgradeActivity", "最大超时: " + maxTime);
//                mHandler.sendMessage(mHandler.obtainMessage(1300, maxTime));
//            }
//
//        };
//
//    }

    /**
     * 初始化控件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    private void initView() {
        mBtnAssets = findViewById(R.id.btn_assets);
        mBtnAssets.setOnClickListener(v -> {
            Toast.makeText(this, "待开发", Toast.LENGTH_SHORT).show();
        });

        mBtnOpenFile = findViewById(R.id.btn_open_file);
        mBtnOpenFile.setOnClickListener(v -> {
            // 判断用户是否已经授权，未授权则向用户申请授权，已授权则直接进行呼叫操作
            if (ContextCompat.checkSelfPermission(this, mArr_Permissions[0]) != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(this, mArr_Permissions[1]) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, mArr_Permissions, 9001);
            } else {
                if (IntervalUtil.isFastClick_2()) {
                    selectFile_2();
                }
            }
        });

        mBtnNewCurrency = findViewById(R.id.btn_new_currency);

        mBtnNewCurrency.setOnClickListener(v -> {
            // 判断用户是否已经授权，未授权则向用户申请授权，已授权则直接进行呼叫操作
            if (ContextCompat.checkSelfPermission(this, mArr_Permissions[0]) != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(this, mArr_Permissions[1]) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, mArr_Permissions, 9002);
            } else {
                if (IntervalUtil.isFastClick_2()) {
                    Download_File("currency",BinPath, ".bin");
                }
            }
        });
        mBtnNewCurrency2 = findViewById(R.id.btn_new_currency2);
        mBtnNewCurrency2.setOnClickListener(v -> {
            // 判断用户是否已经授权，未授权则向用户申请授权，已授权则直接进行呼叫操作
            if (ContextCompat.checkSelfPermission(this, mArr_Permissions[0]) != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(this, mArr_Permissions[1]) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, mArr_Permissions, 9002);
            } else {
                if (IntervalUtil.isFastClick_2()) {
                    Download_File("second",BinPath, ".bin");
                }
            }
        });
        mBtnNewPermit = findViewById(R.id.btn_new_permit);
        mBtnNewPermit.setOnClickListener(v -> {
            // 判断用户是否已经授权，未授权则向用户申请授权，已授权则直接进行呼叫操作
            if (ContextCompat.checkSelfPermission(this, mArr_Permissions[0]) != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(this, mArr_Permissions[1]) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, mArr_Permissions, 9003);
            } else {
                if (IntervalUtil.isFastClick_2()) {
                    Download_File("permit",BinPath, ".bin");
                }
            }
        });

        mTvVersion = findViewById(R.id.tv_version);
        mTvVersion.setText("版本号: " + XbUtils.getVersionName(mContext));
        mTvVersion.setOnLongClickListener(v -> {

            // xb文件夹不存在则创建
            XbUtils.isFileExistence(mSaveDirPath);

            // 判断用户是否已经授权，未授权则向用户申请授权，已授权则直接进行呼叫操作
            if (ContextCompat.checkSelfPermission(this, mArr_Permissions[0]) != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(this, mArr_Permissions[1]) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, mArr_Permissions, 9004);
            } else {
                if (IntervalUtil.isFastClick_2()) {
                    Download_File("KT50UpgradeProgram",BinPath, ".apk");
                }
            }
            return true;
        });

        mTvCmd = findViewById(R.id.tv_cmd);
        mTvCmd.setText("注意！使用前请关闭起爆器程序");
    }


    @SuppressLint("DefaultLocale")
    private void initHandler() {

        mHandler = new Handler(msg -> {

            switch (msg.what) {

                // 处理返回数据
                case 1100:
                    String cmd = (String) msg.obj;
                    // 执行 后续操作
                    doWithReceiveData(Utils.hexStringToBytes(cmd));
                    break;

                // 升级失败
                case 1200:
                    mDialogPlus.dismiss();
                    mTip = "使用 " + mDownLoadFilePath +
                            " 路径下bin文件升级失败 " +
                            "\n\n失败原因: 没有IAP硬件升级程序";
                    mTvCmd.setText(mTip);
                    mTvCmd.setBackgroundResource(R.color.color_red);
                    break;

                // 超时
                case 1300:
                    Log.e("UpgradeActivity", "串口超时 " + msg.obj + "秒 没有返回");
                    mTip = "升级超时 串口没有返回";
                    mTvCmd.setText(mTip);
                    mTvCmd.setBackgroundResource(R.color.color_red);
                    break;

                // 下载开始
                case 1400:
                    String fileName = (String) msg.obj;
                    Log.e("liyi", "线程开启 开始下载文件: " + fileName);

                    // 开始下载
                    new Thread(() -> {
                        try {
                            mResult = mFTP.download(BinPath, fileName, mSaveDirPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();


                    // 进度条线程
                    mProgressThread = new ProgressThread(mDownLoadFileSize, mDownLoadFilePath);
                    mProgressThread.start();
                    break;

                // 下载进度条
                case 1410:
                    long percentage = XbUtils.getPercent((long) msg.obj, mDownLoadFileSize);
                    Log.e("liyi", "下载百分比: " + percentage + "%");

                    mTip = mTip + percentage + "%... ";
                    mTvCmd.setText(mTip);
                    break;

                // 下载成功
                case 1420:
                    // storage/emulated/0/xb/currency_20201121.bin
                    String path = (String) msg.obj;
                    //  currency
                    String name = path.substring(path.indexOf("xb/") + 1, path.indexOf("."));
                    //  .bin
                    String type = path.substring(path.lastIndexOf("."));

                    if (mResult != null) {

                        if (mResult.isSucceed()) {

                            // apk文件
                            if (type.equals(".apk")) {
                                mTip = mTip + "\n升级程序下载成功";
                                mTvCmd.setBackgroundResource(R.color.green);
                                mDialogPlus.dismiss();

                                // 打开APK
                                XbUtils.openAPK(this, mResult.getPath());    // 升级程序下载成功
                            }
                            // bin文件
                            else if (type.equals(".bin")) {

                                if (name.equals("currency")) {
                                    mTip = "通用版升级文件 下载成功\n\n";
                                } else if (name.equals("permit")) {
                                    mTip = "煤许版升级文件 下载成功\n\n";
                                }
                                startUpdate(path);
                            }

                        } else {
                            mTip = mTip + "升级程序下载失败\n";
                            mTvCmd.setBackgroundResource(R.color.color_red);
                        }
                    }

                    mTvCmd.setText(mTip);
                    Log.e("liyi", "响应结果: " + mResult.isSucceed() + " 响应内容: " + mResult.getResponse());
                    break;


            }

            return false;
        });

    }


    /**
     * 后续操作
     */
    private void doWithReceiveData(byte[] cmdBuf) {
        String formCmd = ByteUtil.ByteArrToHex(cmdBuf);
        String cmd = DefCmd.getCmdUp(formCmd);
        String address = DefCmd.getAddress(formCmd);
        Log.e("liyi_cmdBuf", "串口返回: cmd: " + cmd + " address: " + address + " formCmd: " + formCmd);
        if (formCmd.equals("4849")) {
            return;
        }
        switch (cmd) {

            case "E0":
                Log.e("返回E0", "E0 -> E1");
                mTip = mTip + "\nE0 强制升级指令完成";
                mTvCmd.setText(mTip);
                mReadThread_upload.mIsReturnCmd = true;
                E1();
                break;

            case "E1":
                Log.e("返回E1", "开始升级指令完成");
                mTip = mTip + "\nE1 开始升级指令完成";
                mTvCmd.setText(mTip);
                Log.e("E1后续操作", "E1 -> E2");
                E2();
                break;

            case "E2":
                // 下一次
                mIndex_E2 = mIndex_E2 + 1;
                Log.e("返回E2", "bin文件发送开始 mIndex_E2: " + mIndex_E2 + "--mNumber_E2:" + mNumber_E2);

                // 下一次 例如: 30 < 31
                if (mIndex_E2 < mNumber_E2) {
                    Log.e("返回E2 下一次", "bin文件发送中 E2 -> E2");
                    byte[] mE2 = Cmd_EX.sendE2(ByteUtil.ByteArrToHex(mList_Byte.get(mIndex_E2)));
//                    mSerialHelper.send(mE2, true);
                    sendCmd(mE2);
//                    Log.e("UpgradeActivity", "mIndex_E2: " + mIndex_E2 + " E2发送指令: " + ByteUtil.ByteArrToHex(mE2));

                    // 最后一次传输 例如: 31 < 31
                } else {
                    Log.e("返回E2 最后一次", "bin文件发送完成 E2 -> E4");
                    mTip = mTip + "\nE2 固件发送和接收指令完成";
                    mTvCmd.setText(mTip);

                    E4();
                }
                break;

            case "E4":
                Log.e("返回E4", "E4 -> 固件升级完成");
                mTip = mTip + "\nE4 验证分割块数一致指令完成";
                mTvCmd.setText(mTip);
                // E4返回命令
                String Cmd_E4 = ByteUtil.ByteArrToHex(cmdBuf);
                // 16进制字符串
                String str_E4_16 = DefCmd.getE4AgreementNumber(Cmd_E4);
                // 16进制字符串 转换 10进制字符串
                int int_E4_10_Number = Integer.parseInt(str_E4_16, 16);
                // bin文件名称
                String fileName = mDownLoadFilePath.substring(mDownLoadFilePath.lastIndexOf("/") + 1);

                // 如果 文件分割块数一致
                if (int_E4_10_Number == mNumber_E2) {

                    String version;
                    if (fileName.contains("currency")) {
                        version = "通用版";
                    } else if (fileName.contains("permit")) {
                        version = "煤许版";
                    } else {
                        version = "";
                    }

                    mTip = mTip + "\n" + version + "升级成功";
                    mTvCmd.setText(mTip);
                    mTvCmd.setBackgroundResource(R.color.green);
                    Log.e("UpgradeActivity", "int_E4_10_Number == mNumber_E2: " + int_E4_10_Number + " == " + mNumber_E2);
                    dialog();
                } else {
                    mTip = mTip + "\n使用\n" + mDownLoadFilePath + "\n路径下 bin文件升级失败";
                    mTvCmd.setText(mTip);
                    mTvCmd.setBackgroundResource(R.color.color_red);

                }

                // 重置E2发送次数,发送指数,清空List,关闭进度条
                mNumber_E2 = 0;
                mIndex_E2 = 0;
                mList_Byte.clear();
                mDialogPlus.dismiss();

//                // 下电
//                powerOffDevice();
//                // 上电
//                powerOnDevice();
                break;

            default:
                Log.e("UpgradeActivity", "cmd指令没有做处理->" + cmd);
                break;
        }
    }

    private void dialog() {
        AlertDialog dialog = new AlertDialog.Builder(UpgradeActivity.this)
                .setTitle("系统程序升级成功")//设置对话框的标题//"成功起爆"
                .setMessage("系统程序升级成功,请点击确认后,重新进入程序!")//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
                .setNegativeButton("确认", (dialog13, which) -> {
                    dialog13.dismiss();
                    finish();
                })
//                .setNeutralButton("确认", (dialog2, which) -> {
//                    dialog2.dismiss();
//                })
                .create();
        dialog.show();
    }

    private void E0() {
        // 下电
        powerOffDevice(PIN_ADSL);
        // 上电
        powerOnDevice(PIN_ADSL);
        // E0 强制升级指令
        byte[] mE0 = Cmd_EX.sendE0();
//            mSerialHelper.send(mE0, true);
        sendCmd(mE0);
        Log.e("UpgradeActivity E0()", "E0发送指令: " + ByteUtil.ByteArrToHex(mE0));
    }

    private void E1() {

        // 获取 固件分割的块数
        mNumber_E2 = XbUtils.getDivisionNumber(mContext, mFormat, mGetFrom, mFileName, mUri, mDownLoadFilePath);
        // E1 开始升级指令
        byte[] mE1 = Cmd_EX.sendE1(mNumber_E2, mFormat);
//            mSerialHelper.send(mE1, true);
        sendCmd(mE1);
        Log.e("UpgradeActivity E1()", "E1发送指令: " + ByteUtil.ByteArrToHex(mE1));
        Log.e(TAG, "mNumber_E2: " + mNumber_E2);
    }

    private void E2() {


        // 如果 已选择了文件 或者 从xb获得标记(固定路径)
        if (!EmptyUtils.isEmpty(mUri) || mGetFrom == 3) {

            // E2 固件发送和接收
            // byte源数组
            byte[] mBinFile = XbUtils.getBinFileByte(mContext, mGetFrom, mFileName, mUri, mDownLoadFilePath);
            // 固件分割的块数
            mNumber_E2 = XbUtils.getDivisionNumber(mContext, mFormat, mGetFrom, mFileName, mUri, mDownLoadFilePath);
            // 循环
            for (int i = 0; i < mNumber_E2; i++) {

                // 新数组
                byte[] mBinNew = new byte[mFormat];
                // 起始位置
                int mSrcPos = (mFormat * i);
                // 余数
                int mRemainder = mBinFile.length - mSrcPos;

                // mFormat整组
                if (mRemainder > mFormat) {

                    System.arraycopy(
                            mBinFile,       // byte源数组
                            mSrcPos,        // 截取源byte数组起始位置（0位置有效）
                            mBinNew,        // byte目的数组（截取后存放的数组）
                            0,      // 截取后存放的数组起始位置（0位置有效）
                            mFormat         // 截取的数据长度
                    );

                    // 不满组
                } else {
                    // 余数数组
                    byte[] mArray_remainder = new byte[mRemainder];
                    // 补F数组
                    byte[] mArray_F = new byte[mFormat - mRemainder];
                    for (int j = 0; j < mFormat - mRemainder; j++) {
                        mArray_F[j] = (byte) 0xFF;
                    }
                    System.arraycopy(
                            mBinFile,        // byte源数组
                            mSrcPos,         // 截取源byte数组起始位置（0位置有效）
                            mArray_remainder,// byte目的数组（截取后存放的数组）
                            0,        // 截取后存放的数组起始位置（0位置有效）
                            mRemainder);     // 截取的数据长度

                    mBinNew = XbUtils.byteMerger(mArray_remainder, mArray_F);
                }

                mList_Byte.add(mBinNew);

//                        Log.e("UpgradeActivity", "i: " + i);
//                        Log.e("UpgradeActivity", "mList_Byte.get(i): " + ByteUtil.ByteArrToHex(Cmd_EX.sendE2(ByteUtil.ByteArrToHex(mList_Byte.get(i)))));
//                        Log.e("UpgradeActivity", "srcPos: " + mSrcPos);
//                        Log.e("UpgradeActivity", "mBinNew.length: " + mBinNew.length);
            }

            mIndex_E2 = 0;
            // E2 固件发送和接收
            byte[] mE2 = Cmd_EX.sendE2(ByteUtil.ByteArrToHex(mList_Byte.get(mIndex_E2)));
            sendCmd(mE2);
            Log.e(TAG, "mNumber_E2: " + mNumber_E2);
//                Log.e("UpgradeActivity", "E2 mIndex_E2: " + mIndex_E2 + " E2发送指令: " + ByteUtil.ByteArrToHex(mE2));

        } else {
            Log.e("UpgradeActivity", " E2 请选择文件");
        }

    }

    private void E4() {
//        if (mSerialHelper.isOpen()) {
        // E4 固件发送结束后，验证发送的分割块数与接收的块数是否一致
        byte[] mE4 = Cmd_EX.sendE4();
//            mSerialHelper.send(mE4, true);
        sendCmd(mE4);
//            Log.e("UpgradeActivity", "E4发送指令: " + ByteUtil.ByteArrToHex(mE4));
//        } else {
//            Log.e("UpgradeActivity E4()", "串口没打开");
//        }
    }


    /**
     * 选择文件
     */
    private void selectFile_2() {
        mGetFrom = 2;
//        OpenSerialHelper();  // 打开串口

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // 不限制
        intent.addCategory(Intent.CATEGORY_OPENABLE); // 只有设置了这个，返回的uri才能使用
        startActivityForResult(intent, 1500);
    }


    /**
     * 下载文件
     */
    private void Download_File(String name,String remotePath, String type) {
        Log.e("Download_File", "下载文件名称: " + name + " 下载文件类型: " + type);
        showDialog();        // 进度条

        // 如果是Bin文件
        if (type.equals(".bin")) {
            mGetFrom = 3;    // 从xb获得
        }
        // 重置提示
        mTip = "";
        mTvCmd.setBackgroundResource(R.color.color_red);

        // 网络判断
        if (!NetUtils.haveNetWork(mContext)) {
            mTip = "访问服务器失败\n(可能是没有连接网络)";
            mTvCmd.setText(mTip);
            mTvCmd.setBackgroundResource(R.color.color_red);
            if (mDialogPlus != null) {
                mDialogPlus.dismiss();
            }
            return;
        }

        try {

            String ftpFileName = null;  // 所需文件在 服务器文件名称
            String version_ftp;         // 所需文件在 服务器的版本号
            mPath_Local = "";           // 所需文件在 本地文件路径
            String version_self;        // 当前使用 软件版本(apk升级参数)
            String time_0 = null;       // bin文件日期最新日期

            // 如果登录成功
            if (mFTP.openConnect()) {
                Log.e("Download_File", "FTP服务器登录成功");

                // 获取服务器文件列表
                mList_FtpFileName.clear();
                mList_FtpFileName = mFTP.listFiles(remotePath);
                Log.e("下载目录", mList_FtpFileName.toString());

                for (int i = 0; i < mList_FtpFileName.size(); i++) {
                    String fileName = mList_FtpFileName.get(i).getName();
                    long fileSize = mList_FtpFileName.get(i).getSize();
                    Log.e("Download_File", "服务器 文件序号: " + i + " 文件名称: " + fileName + " 文件大小: " + fileSize);

                    // 符合所需名称
                    if (fileName.contains(name)) {

                        // 如果是Bin文件
                        if (type.equals(".bin")) {

                            // 截取bin文件日期
                            String time_1 = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf(".bin"));

                            // 如果 这是第一个符合条件文件
                            if (time_0 == null) {
                                ftpFileName = fileName;
                                time_0 = time_1;
                                mDownLoadFilePath = mSaveDirPath + "/" + ftpFileName;
                                mDownLoadFileSize = fileSize;
                                Log.e("Download_Bin_1", "需下载文件名称: " + ftpFileName + " 需下载文件大小: " + mDownLoadFileSize + " 需下载文件路径: " + mDownLoadFilePath);
                            }
                            // 如果 这是第n个符合条件文件
                            else {

                                // 例如: permit_20210131.bin 比对 permit_20210319.bin
                                Log.e("Download_Bin_2", "time_0: " + time_0);
                                Log.e("Download_Bin_2", "time_1: " + time_1);

                                if (TimeUtils.isDate2Bigger(time_0, time_1)) {
                                    Log.e("Download_Bin_2", "boolean: " + TimeUtils.isDate2Bigger(time_0, time_1));
                                    ftpFileName = fileName;
                                    mDownLoadFilePath = mSaveDirPath + "/" + ftpFileName;
                                    mDownLoadFileSize = fileSize;
                                    Log.e("Download_Bin_2", "需下载文件名称: " + ftpFileName + " 需下载文件大小: " + mDownLoadFileSize + " 需下载文件路径: " + mDownLoadFilePath);
                                }


                            }

                        }
                        // 如果是APK文件
                        else if (type.equals(".apk")) {
                            ftpFileName = fileName;
                            mDownLoadFilePath = mSaveDirPath + "/" + ftpFileName;
                            mDownLoadFileSize = fileSize;
                            Log.e("Download_APK", "需下载文件名称: " + ftpFileName + " 需下载文件大小: " + mDownLoadFileSize + " 需下载文件路径: " + mDownLoadFilePath);
                        }


                    }
                }


                // 如果服务器没有所需文件
                if (ftpFileName == null) {
                    if (type.equals(".apk")) {
                        mTip = "服务器上没有\n升级程序安装包(请联系管理员上传安装包)";
                    } else if (type.equals(".bin")) {
                        mTip = "服务器上没有\n升级程序文件(请联系管理员上传升级文件)";
                    }
                    mTvCmd.setText(mTip);
                    mTvCmd.setBackgroundResource(R.color.color_red);
                    return;
                }

                // 如果下载的是安装包
                if (type.equals(".apk")) {
                    // 当前使用Apk版本号
                    version_self = XbUtils.getVersionName(mContext);
                    // 截取服务器Apk版本号
                    version_ftp = XbUtils.InterceptedVersion(ftpFileName);
                    // 0:等于 1:大于 -1:小于
                    int index = XbUtils.compareVersion(version_self, version_ftp);

                    Log.e("Download_APK", "index: " + index);
                    Log.e("Download_APK", "服务器Apk版本号: " + version_ftp);
                    Log.e("Download_APK", "当前使用 应用程序 版本号: " + version_self);

                    // 服务器版本 和 当前版本一致
                    if (index == 0) {
                        mDialogPlus.dismiss();
                        ToastUtils.longs("已经是最新版本了");
                        Log.e("Download_APK", "index == 0 已经是最新版本了");
                    }
                    // 不变
                    else if (index == 1) {
                        mDialogPlus.dismiss();
                        ToastUtils.longs("本地程序版本 高于 服务器版本");
                        Log.e("Download_APK", "index == 1 本地程序版本 高于 服务器版本");
                    }
                    // 需要更新软件
                    else if (index == -1) {
                        // 获取本地APK文件列表
                        List<String> list_localFileName = XbUtils.getFileNameList(mSaveDirPath, ".apk");

                        // 本地没有APK文件列表
                        if (list_localFileName == null) {
                            // 本地已下载安装路径
                            mPath_Local = "";
                        }
                        // 本地有APK文件列表
                        else {
                            // 本地已下载安装路径
                            mPath_Local = XbUtils.isHaveApk(ftpFileName, list_localFileName);

                            for (int i = 0; i < list_localFileName.size(); i++) {
                                Log.e("file_local", "本地 文件序号: " + i + " 文件路径: " + list_localFileName.get(i));
                            }
                        }
                        Log.e("file_local", "本地已下载安装路径: " + mPath_Local);

                        // 本地没有文件
                        if (mPath_Local.isEmpty()) {
                            // FTP服务器 开始下载 所需文件
                            mHandler.sendMessage(mHandler.obtainMessage(1400, ftpFileName));
                            mTip = "FTP服务器登录成功\n正在下载最新程序...\n" + ftpFileName + " 文件\n\n";
                            mTvCmd.setText(mTip);
                            Log.e("file_local", "index == -1 开始下载");

                        }
                        // 本地有文件
                        else {
                            if (mDialogPlus.isShowing()) {
                                mDialogPlus.dismiss();
                            }
                            // 打开APK
                            XbUtils.openAPK(this, mPath_Local);
                        }

                    }
                } else if (type.equals(".bin")) {
                    // FTP服务器 开始下载 所需文件
                    mHandler.sendMessage(mHandler.obtainMessage(1400, ftpFileName));
                    mTip = "FTP服务器登录成功\n正在下载最新bin文件...\n";
                    mTvCmd.setText(mTip);
                }

            } else {
                Log.e("Download_APK", "FTP服务器登录失败");
                mTip = "FTP服务器登录失败";
                mTvCmd.setText(mTip);
                mTvCmd.setBackgroundResource(R.color.color_red);
                mDialogPlus.dismiss();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 开始升级
     */
    private void startUpdate(String path) {
        Log.e("开始升级-startUpdate", "path: " + path);
        initSerialPort(115200);
        // 如果是bin文件
        if (path.substring(path.length() - 3).equals("bin")) {
//            OpenSerialHelper();  // 打开串口

            mTip = mTip + "使用 " + path + " 路径下的bin文件\n\n开始升级...";
            mTvCmd.setText(mTip);
            E0();

            // IAP检测线程
            mReadThread_upload = new ReadThread(mHandler);
            mReadThread_upload.start();
            // 重置 返回指令
            mReadThread_upload.mIsReturnCmd = false;

        } else {
            Log.e("startUpdate", "请选择bin文件升级");
        }


    }


    /**
     * IAP检测线程
     */
    private static class ReadThread extends Thread {

        public boolean mExit = false;            // true 退出循环
        public boolean mIsReturnCmd = false;     // false 没有异常
        public Handler mHandler;

        public ReadThread(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void run() {
            super.run();

            try {
                while (!isInterrupted() && !mExit) {
                    Thread.sleep(1000);

                    if (mExit) {
                        return;
                    }

                    if (!mIsReturnCmd) {
                        mHandler.sendMessage(mHandler.obtainMessage(1200));
                        return;
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {

        switch (requestCode) {

            case 9001:
                Log.e("liyi", "9001");
                if (grantResults[0] == -1) {
                    ToastUtils.longs("需要读权限,请点击允许");
                }
//                if (IntervalUtil.isFastClick_2()) {
//                    selectFile_2();
//                }
                break;


            case 9002:
                Log.e("liyi", "9002");
                if (grantResults[0] == -1) {
                    ToastUtils.longs("需要读权限,请点击允许");
                }
//                if (IntervalUtil.isFastClick_2()) {
//                    DownLoadAndUpdate_BIN(0);
//                }
                break;


            case 9003:
                Log.e("liyi", "9003");
                if (grantResults[0] == -1) {
                    ToastUtils.longs("需要读权限,请点击允许");
                }
//                if (IntervalUtil.isFastClick_2()) {
//                    DownLoadAndUpdate_BIN(1);
//                }
                break;

            case 9004:
                Log.e("liyi", "9004");
                if (grantResults[0] == -1) {
                    ToastUtils.longs("需要读权限,请点击允许");
                }
//                if (IntervalUtil.isFastClick_2()) {
//                    Download_APK("KT50UpgradeProgram");
//                }
                break;

            case 9005:
                Log.e("liyi", "9005");
                if (grantResults[0] == -1) {
                    ToastUtils.longs("需要读权限,请点击允许");
                }
//                if (IntervalUtil.isFastClick_2()) {
//                    startActivity(new Intent(mContext, DownLoadActivity.class));
//                }
                break;

            default:
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    /**
     * 进度条线程
     */
    private static class ProgressThread extends Thread {

        private long max_size;
        private long curr_size;
        private String path;

        public int mIndex;
        public boolean mExit = false;

        public ProgressThread(long mDownLoadFileSize, String mDownLoadFilePath) {
            this.max_size = mDownLoadFileSize;
            this.path = mDownLoadFilePath;
        }

        @Override
        public void run() {
            super.run();

            try {
                while (!isInterrupted() && !mExit) {
                    Thread.sleep(2500);

                    if (mExit) {
                        return;
                    }

                    // 文件大小
                    curr_size = new File(path).length();
                    Log.e("liyi_Progress", mIndex + " 正在下载 " + curr_size + " KB");

                    // 更新进度条
                    mHandler.sendMessage(mHandler.obtainMessage(1410, curr_size));

                    // 下载完成
                    if (max_size == curr_size) {
                        Log.e("liyi_Progress", "下载完成: " + path);
                        mHandler.sendMessage(mHandler.obtainMessage(1420, path));
                        mExit = true;
                    }

                    mIndex++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // 打开文件管理器
            if (requestCode == 1500) {
                mUri = data.getData();

                // 4.4以上
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    mDownLoadFilePath = FileInfoUtils.getPath(this, mUri);
                }
                // 4.4以下
                else {
                    mDownLoadFilePath = FileInfoUtils.getRealFilePath(this, mUri);
                }

                // 选择文件路径
                Log.e("onActivityResult", "mDownLoadFilePath: " + mDownLoadFilePath);

                // 进度条
                showDialog();
                // 重置提示
                mTip = "";
                mTvCmd.setBackgroundResource(R.color.white);
                // 开始升级
                startUpdate(mDownLoadFilePath);
            }
            // 未知安装权限页面
            else if (requestCode == 2500) {
                XbUtils.openAPKFile_8(this, mPath_Local);
            }

        } else {

            if (requestCode == 2500) {
                // 8.0手机位置来源安装权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!getPackageManager().canRequestPackageInstalls()) {
                        Log.e("liyi", "没有赋予 未知来源安装权限");
                        ToastUtils.longs("没有赋予 未知来源安装权限");
                    }
                }
            } else if (requestCode == 3500) {
                Log.e("liyi", "安装新程序，请长按版本号");
                ToastUtils.longs("安装新程序，请长按版本号");
            }

        }

    }


}
