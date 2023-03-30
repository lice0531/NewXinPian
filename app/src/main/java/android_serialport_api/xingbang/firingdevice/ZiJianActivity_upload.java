package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.NetUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.upload.FTP;
import android_serialport_api.xingbang.utils.upload.IntervalUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

public class ZiJianActivity_upload extends SerialPortActivity {

    @BindView(R.id.tv_zj_num)
    TextView tvZjNum;
    @BindView(R.id.tv_zj_dy)
    TextView tvZjDy;
    @BindView(R.id.tv_zj_gy)
    TextView tvZjGy;
    SharedPreferences.Editor edit;
    private float dianya_low = 0;//低压信息
    private float dianya_high = 0;//高压信息
    private double lowVoltage;
    private String lowTiaoZheng;
    private String highTiaoZheng;
    private String version;
    private String version_cloud;
    private double highVoltage;
    private From42Power busInfo;
    private Handler busHandler = null;//总线信息
    private int flag = 0;
    private ZiJianThread ziJianThread;
    private volatile int firstCount = 4;

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

    public volatile String mDownLoadFilePath;   // 下载文件路径 3
    public volatile long mDownLoadFileSize;     // 下载文件大小
    public String CJ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zi_jian);
        ButterKnife.bind(this);
        initPower();                // 初始化上电方式()
        powerOnDevice(PIN_ADSL);    // 上电
    // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        //获取偏好设置的编辑器
        SharedPreferences sp = getSharedPreferences("config", 0);
        edit = sp.edit();
        //获取低压高压值
        lowVoltage = Utils.convertToDouble(sp.getString("lowVoltage", "8.5"), 8.5);
        highVoltage = Utils.convertToDouble(sp.getString("highVoltage", "16"), 16);
        lowTiaoZheng = sp.getString("lowTiaoZheng", "0");
        highTiaoZheng = sp.getString("highTiaoZheng", "0");
        initHandler();
        initFTP();              // 初始化FTP
        ziJianThread = new ZiJianThread();
//        ziJianThread.start();
        Utils.writeRecord("--进入起爆器--");
        quanxian();//申请权限
        CJ="NM_";//SC-四川 NM-内蒙(不同的版本需要修改)
        if (IntervalUtil.isFastClick_2()) {
            //有三个版本,16V-普通板子 16V-11000版子  17V-11000板子
            //UpgradeActivity里面的对应值也要改
            GetFileName(CJ+"KT50_V1.3_17V", ".bin");//17V是电流11000,16V是改变前的
        }
        deleteRiZhi();
    }

    /**
     * 初始化FTP
     */
    private void initFTP() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mFTP = new FTP(mIP, mUserName, mPassWord);
    }


    private void GetFileName(String name, String type) {

        // 如果是Bin文件
        if (type.equals(".bin")) {
            mGetFrom = 3;    // 从xb获得
        }
        Log.e("是否有网", NetUtils.haveNetWork(this) + "");
        // 网络判断
        if (!NetUtils.haveNetWork(this)) {
            return;
        }

        try {
            String ftpFileName = null;  // 所需文件在 服务器文件名称
            String version_ftp;         // 所需文件在 服务器的版本号
//            mPath_Local = "";           // 所需文件在 本地文件路径
            String version_self;        // 当前使用 软件版本(apk升级参数)
            String time_0 = null;       // bin文件日期最新日期

            // 如果登录成功
            if (mFTP.openConnect()) {
                // 获取服务器文件列表
                mList_FtpFileName.clear();
                mList_FtpFileName = mFTP.listFiles("/");
//                Log.e("下载目录", mList_FtpFileName.toString());

                for (int i = 0; i < mList_FtpFileName.size(); i++) {
                    String fileName = mList_FtpFileName.get(i).getName();
                    long fileSize = mList_FtpFileName.get(i).getSize();

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
                                version_cloud = ftpFileName;
                                Log.e("Download_Bin_1", "需下载文件名称: " + ftpFileName + " 需下载文件大小: " + mDownLoadFileSize + " 需下载文件路径: " + mDownLoadFilePath);
                            }
                            // 如果 这是第n个符合条件文件
//                            else {
//
//                                // 例如: permit_20210131.bin 比对 permit_20210319.bin
//                                Log.e("Download_Bin_2", "time_0: " + time_0);
//                                Log.e("Download_Bin_2", "time_1: " + time_1);
//
//                                if (TimeUtils.isDate2Bigger(time_0, time_1)) {
//                                    Log.e("Download_Bin_2", "boolean: " + TimeUtils.isDate2Bigger(time_0, time_1));
//                                    ftpFileName = fileName;
//                                    mDownLoadFilePath = mSaveDirPath + "/" + ftpFileName;
//                                    mDownLoadFileSize = fileSize;
//                                    Log.e("Download_Bin_2", "需下载文件名称: " + ftpFileName + " 需下载文件大小: " + mDownLoadFileSize + " 需下载文件路径: " + mDownLoadFilePath);
//                                }
//
//
//                            }

                        }


                    }


                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void quanxian() {
        final String TAG = "权限";
        List<PermissonItem> permissonItems = new ArrayList<>();
        permissonItems.add(new PermissonItem(Manifest.permission.CAMERA, "照相机", R.drawable.permission_ic_memory));
        permissonItems.add(new PermissonItem(Manifest.permission.ACCESS_FINE_LOCATION, "定位", R.drawable.permission_ic_location));
        permissonItems.add(new PermissonItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写", R.drawable.permission_ic_memory));
        permissonItems.add(new PermissonItem(Manifest.permission.READ_EXTERNAL_STORAGE, "读", R.drawable.permission_ic_memory));
        permissonItems.add(new PermissonItem(Manifest.permission.INTERNET, "网络", R.drawable.permission_ic_memory));
        permissonItems.add(new PermissonItem(Manifest.permission.RECEIVE_BOOT_COMPLETED, "网络", R.drawable.permission_ic_memory));
        HiPermission.create(this)
                .permissions(permissonItems)
                .title("用户您好")
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        show_Toast("用户关闭权限申请");
                    }

                    @Override
                    public void onFinish() {
//                        show_Toast("所有权限申请完成");
                        ziJianThread.start();
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                        Log.i(TAG, "onDeny");
                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                    }
                });
    }

    //退出方法
    private void exit() {
        powerOffDevice(PIN_ADSL);//主板下电
        //点击在两秒以内
        removeALLActivity();//执行移除所以Activity方法
    }

    private class ZiJianThread extends Thread {
        public volatile boolean exit = false;

        public void run() {
            while (!exit) {
                try {
                    busHandler.sendMessage(busHandler.obtainMessage(1));

                    Thread.sleep(1000);
                    if (firstCount == 3) {
                        test();//检测设备是否正常
//                        sendCmd(FourStatusCmd.getSoftVersion("00"));//43
                    }
                    if (firstCount == 0) {
                        exit = true;
                        Intent intent = new Intent(ZiJianActivity_upload.this, XingbangMain.class);
                        startActivity(intent);
                        finish();//如果不结束当前页面的话,会和后面的页面抢命令
                        break;
                    }
                    firstCount--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void test() {
        Log.e("高压和低压值", "lowVoltage: " + lowVoltage + "  highVoltage: " + highVoltage);
        int a = Double.valueOf(lowVoltage * 10).intValue();
        byte[] delayBye = Utils.shortToByte((short) a);
        String delayStr = Utils.bytesToHexFun(delayBye);
        String b = delayStr.substring(0, 2);
        String c = delayStr.substring(2);

        int a2 = Double.valueOf(highVoltage * 10).intValue();
        byte[] delayBye2 = Utils.shortToByte((short) a2);
        String delayStr1 = Utils.bytesToHexFun(delayBye2);
        String d = delayStr1.substring(0, 2);
        String e = delayStr1.substring(2);
        Log.e("低压", "c+b: " + c + b);
        Log.e("高压", "e+d: " + e + d);
        Utils.writeRecord("设置低压" + (c + b) + "--设置高压" + (e + d));
        byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Test((c + b) + (e + d));//14
        sendCmd(powerCmd);
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    private void initHandler() {
        busHandler = new Handler(message -> {
            switch (message.what) {
                case 1:
                    tvZjNum.setText(firstCount + "s");
                    break;
                case 2:

                    Log.e("自检", "version: " + version);
                    Log.e("自检", "version_cloud: " + version_cloud);
                    if (version_cloud != null && !version_cloud.contains(version)) {
                        ziJianThread.exit = true;
                        createDialog();


                    }
                    break;
            }

            return false;
        });
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);
//        Log.e("自检收到", "fromCommad: "+fromCommad );
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                return;
            } else {
                String cmd = DefCommand.getCmd(fromCommad);
                if (cmd != null) {
                    int localSize = fromCommad.length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                    doWithReceivData(cmd, localBuf, localSize);
                }
            }
        }
    }

    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Utils.writeLog("自检发送:" + str);
                Log.e("发送命令", str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            return;
        }
    }

    /**
     * 处理接收到的cmd命令
     */
    private void doWithReceivData(String cmd, byte[] cmdBuf, int size) {
        byte[] locatBuf = new byte[size];
        System.arraycopy(cmdBuf, 0, locatBuf, 0, size);//将cmdBuf数组复制到locatBuf数组
        if (DefCommand.CMD_1_REISTER_4.equals(cmd)) {//13 关闭电源
//            busHandler.sendMessage(busHandler.obtainMessage());
            sendCmd(FourStatusCmd.getSoftVersion("00"));//43
        } else if (DefCommand.CMD_1_REISTER_5.equals(cmd)) {//14 核心板自检

            String fromCommad = Utils.bytesToHexFun(locatBuf);
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            String a = realyCmd1.substring(6, 8);
            String a1 = realyCmd1.substring(8, 10);
            String b = realyCmd1.substring(10, 12);
            String b1 = realyCmd1.substring(12, 14);
            String c = realyCmd1.substring(14, 16);//总线电流
            String c1 = realyCmd1.substring(16);
            double voltLow = (Integer.parseInt(a1, 16) * 256 + Integer.parseInt(a, 16)) / 4.095 * 3.0 * 0.006;
            double voltHeigh = (Integer.parseInt(b1, 16) * 256 + Integer.parseInt(b, 16)) / 4.095 * 3.0 * 0.006;
//            Log.e("核心板自检", "voltLow: " +voltLow);
//            Log.e("核心板自检", "voltHeigh: " +voltHeigh);
            Utils.writeRecord("单片机返回的设置电压--低压:" + voltLow + "--高压:" + voltHeigh);
            dianya_low = Utils.getFloatToFormat((float) voltLow, 2, 4);
            dianya_high = Utils.getFloatToFormat((float) voltHeigh, 2, 4);
            byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");//13 退出测试模式
            sendCmd(powerCmd);
        }
        if (DefCommand.CMD_4_XBSTATUS_4.equals(cmd)) {//获取软件版本号 43
            String realyCmd1 = DefCommand.decodeCommand(Utils.bytesToHexFun(locatBuf));
            String a = realyCmd1.substring(6);//2020031201
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < a.length(); i += 2) {
                String str = a.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
            }
            Log.e("软件版本返回的命令", "output: " + output);
            version = CJ+output;
            MmkvUtils.savecode("yj_version", output.toString());
            Message msg = new Message();
            msg.what = 2;
            msg.obj = output.toString();
            busHandler.sendMessage(msg);
        }
    }

    /***
     * 建立对话框
     */
    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("升级提醒");//"说明"
        builder.setMessage("检测到有新的硬件程序版本,请确定您当前的网络环境稳定,建议在WIFI环境或者稳定的4G网络热点下再进行更新,是否进行更新?");
        builder.setPositiveButton("进行更新", (dialog, which) -> {
//            show_Toast("当前系统程序有新版本,正在升级,请稍等!");
            finish();
            Intent intent = new Intent(this, UpgradeActivity.class);
            intent.putExtra("dataSend", "升级");
            startActivity(intent);
            dialog.dismiss();
        });
//        builder.setNeutralButton("退出", (dialog, which) -> {
//            dialog.dismiss();
//            finish();
//        });
        builder.setNegativeButton("进入程序", (dialog, which) -> {
            finish();
            Intent intent = new Intent(this, XingbangMain.class);
            startActivity(intent);
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void deleteRiZhi(){
        String filePath;
        String filePath2;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "/程序运行日志/" ;
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/程序运行日志/" ;
        }
        if (hasSDCard) {
            filePath2 = Environment.getExternalStorageDirectory().toString() + File.separator + "/XB程序日志/" ;
        } else {
            filePath2 = Environment.getDownloadCacheDirectory().toString() + File.separator + "/XB程序日志/" ;
        }

        File dir = new File(filePath);
        Utils.deleteRiZhi(dir);
        File dir2 = new File(filePath2);
        Utils.deleteRiZhi(dir2);
//                Utils.deleteDirWihtFile("/程序运行日志/");
//                Utils.deleteDirWihtFile("/XB程序日志/");
//        show_Toast("删除成功");
    }
}
