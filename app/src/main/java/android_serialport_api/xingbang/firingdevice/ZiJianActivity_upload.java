package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.db.GreenDaoMaster;
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
    public String CJ = "";
    public String binName = "";

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
        ziJianThread = new ZiJianThread();
//        ziJianThread.start();
        Utils.writeRecord("--进入起爆器--");
        quanxian();//申请权限
        CJ = "SC_";//SC-四川 NM-内蒙(不同的版本需要修改)
//        CJ="XB_";//实验用
        binName = CJ + "    KT50_V1.3_MX";
        deleteRiZhi();
        // 保存区域参数
        SPUtils.put(this, Constants_SP.RegionCode, "1");

        deletaBeian();
        if (NetUtils.haveNetWork(this)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format1 = simpleDateFormat.format(new Date(System.currentTimeMillis() ));
            Log.e("记录时间", "format1: "+format1 );
            MmkvUtils.savecode("time",format1);
        }
    }

    private void deletaBeian() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format1 = simpleDateFormat.format(new Date(System.currentTimeMillis() ));
        GreenDaoMaster master = new GreenDaoMaster();
        master.deleteTypeLeiGuan(format1);
        master.deleteShouQuan(format1);

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
                        //对比时间
                        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String format1 = sd.format(new Date(System.currentTimeMillis() ));//当前时间
                        String time =(String) MmkvUtils.getcode("time","2024-11-20 12:00:00");
                            try {
                                Date date1 = sd.parse(format1);//当前日期
                                Date date2 = sd.parse(time);//有网记录时间
                                if(date1.compareTo(date2)>0){
                                    //过期
                                    ziJianThread.start();
                                    Log.e(TAG, "当前时间大于记录时间: " );
                                }else {
                                    createDialog();
                                    //大于
                                    Log.e(TAG, "当前时间小于记录时间: " );
                                }
                            } catch (ParseException | java.text.ParseException e) {
                                e.printStackTrace();
                            }


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
                    if (firstCount == 2) {
                        test();//检测设备是否正常
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
//        Utils.writeRecord("设置低压" + (c + b) + "--设置高压" + (e + d));
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
                        createDialog();//把判断的名字传过去(没改)


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
                Utils.writeLog("->:" + str);
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
            version = output.toString();
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
        builder.setTitle("系统提醒");//"说明"
        builder.setMessage("检测系统时间被修改,联网更新时间后,再进入程序");
//        builder.setPositiveButton("进行更新", (dialog, which) -> {
//
//            finish();
//            dialog.dismiss();
//        });
        builder.setNeutralButton("退出", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });

        builder.create().show();
    }

    private void deleteRiZhi() {
        String filePath;
        String filePath2;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "/程序运行日志/";
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/程序运行日志/";
        }
        if (hasSDCard) {
            filePath2 = Environment.getExternalStorageDirectory().toString() + File.separator + "/XB程序日志/";
        } else {
            filePath2 = Environment.getDownloadCacheDirectory().toString() + File.separator + "/XB程序日志/";
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
