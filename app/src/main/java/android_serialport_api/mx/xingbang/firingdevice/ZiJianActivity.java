package android_serialport_api.mx.xingbang.firingdevice;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.mx.xingbang.R;
import android_serialport_api.mx.xingbang.SerialPortActivity;
import android_serialport_api.mx.xingbang.cmd.DefCommand;
import android_serialport_api.mx.xingbang.cmd.OneReisterCmd;
import android_serialport_api.mx.xingbang.cmd.vo.From42Power;
import android_serialport_api.mx.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;//主板上电

public class ZiJianActivity extends SerialPortActivity {

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
    private double highVoltage;
    private From42Power busInfo;
    private Handler busHandler = null;//总线信息
    private int flag = 0;
    private ZiJianThread ziJianThread;
    private volatile int firstCount = 4;

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
                    busHandler.sendMessage(busHandler.obtainMessage());

                    Thread.sleep(1000);
                    if (firstCount == 2) {
                        test();//检测设备是否正常
                    }
                    if (firstCount == 0) {
                        exit = true;
                        Intent intent = new Intent(ZiJianActivity.this, XingbangMain.class);
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
            tvZjNum.setText(firstCount + "s");
//            if (dianya_low != 0) {
//                Log.e("低压", "电压: " + dianya_low);
//                tvZjDy.setText(dianya_low + "V");
//            }
//            if (dianya_high != 0) {
//                tvZjGy.setText(dianya_high + "V");
//                Log.e("高压", "电压: " + dianya_high);
//            }
//            if (firstCount == 0) {
//                ziJianThread.exit = true;
//                if(lowTiaoZheng.equals("0")&&highTiaoZheng.equals("0")){
//                    AlertDialog dialog = new AlertDialog.Builder(ZiJianActivity.this)
//                            .setTitle("当前设备还未进行电压调整")//设置对话框的标题//"成功起爆"
//                            .setMessage("请先到设置中设置电压,再进行操作")//设置对话框的内容"本次任务成功起爆！"
//                            //设置对话框的按钮
//                            .setNeutralButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    Intent intent = new Intent(ZiJianActivity.this, XingbangMain.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            })
//                            .setNegativeButton("退出", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    exit();
//                                    finish();
//                                }
//                            })
//                            .create();
//                    dialog.show();
//                }else if ((lowVoltage+0.5) > dianya_low && dianya_low > (lowVoltage-0.5) && dianya_high < (highVoltage+0.5) && dianya_high > (highVoltage-0.5)) {
//                    show_Toast("设备运行正常");
//                    Log.e("自检页面", "跳转到主页面");
//                    Intent intent = new Intent(ZiJianActivity.this, XingbangMain.class);
//                    startActivity(intent);
//                    finish();
//                } else if((lowVoltage+0.5) < dianya_low || dianya_low < (lowVoltage-0.5)){
//                    AlertDialog dialog = new AlertDialog.Builder(ZiJianActivity.this)
//                            .setTitle("当前设备运行异常")//设置对话框的标题//"成功起爆"
//                            .setMessage("起爆器低压异常:输出低压数值不正常,请检测设备后再进行操作")//设置对话框的内容"本次任务成功起爆！"
//                            //设置对话框的按钮
//                            .setNegativeButton("退出", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    exit();
//                                    finish();
//                                }
//                            })
//                            .create();
//                    dialog.show();
//                }else if(dianya_high > (highVoltage+0.5) || dianya_high < (highVoltage-0.5)){
//                    AlertDialog dialog = new AlertDialog.Builder(ZiJianActivity.this)
//                            .setTitle("当前设备运行异常")//设置对话框的标题//"成功起爆"
//                            .setMessage("起爆器高压异常:设备输出高压数值不正常,请检测设备后再进行操作")//设置对话框的内容"本次任务成功起爆！"
//                            //设置对话框的按钮
//                            .setNegativeButton("退出", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    exit();
//                                    finish();
//                                }
//                            })
//                            .create();
//                    dialog.show();
//                }else {
//                    Intent intent = new Intent(ZiJianActivity.this, XingbangMain.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
            return false;
        });
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);
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
    }
}
