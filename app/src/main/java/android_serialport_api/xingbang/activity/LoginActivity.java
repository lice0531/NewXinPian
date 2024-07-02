package android_serialport_api.xingbang.activity;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.databinding.ActivityLoginBinding;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

public class LoginActivity extends SerialPortActivity implements View.OnClickListener{
    ActivityLoginBinding binding ;

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
    private volatile int firstCount = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initPower();                // 初始化上电方式()
        powerOnDevice(PIN_ADSL);    // 上电

        ziJianThread = new ZiJianThread();
        initHandler();

        TextView title=findViewById(R.id.title_text);
        title.setText("登录");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setVisibility(View.GONE);
        iv_back.setVisibility(View.GONE);
        quanxian();  // 申请权限
    }

    private void initHandler() {
        busHandler = new Handler(message -> {
//            tvZjNum.setText(firstCount + "s");
            return false;
        });
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
                        initView();
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
    private void initView() {
        String add = getFilesDir().getAbsolutePath()+"Xingbang"+ "/mmkv";//android 10版本建议不获取自己包名外的文件夹
        String dir = Environment.getExternalStorageDirectory() +  File.separator +"Xingbang"+ "/mmkv";
        String ren = MMKV.initialize(dir);//替代SharedPreferences
        Logger.e("初始化MMKV"+ "ren: "+ren );
        MmkvUtils.getInstance();

        ziJianThread.start();

        //获取低压高压值
        lowVoltage = Utils.convertToDouble((String) MmkvUtils.getcode("lowVoltage", "7"), 7);
        highVoltage = Utils.convertToDouble((String)MmkvUtils.getcode("highVoltage", "16"), 16);
        lowTiaoZheng=(String) MmkvUtils.getcode("lowTiaoZheng", "0");
        highTiaoZheng=(String) MmkvUtils.getcode("highTiaoZheng", "0");
    }
    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Logger.e("发送命令"+ str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            return;
        }
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

    @Override
    public void onClick(View view) {
        Logger.e("TAG"+ "onClick: " );
        switch (view.getId()){
            case R.id.lg_bt_login:
                Intent intent = new Intent(this,NewMainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }


    private class ZiJianThread extends Thread {
        public volatile boolean exit = false;

        public void run() {
            while (!exit) {
                try {
                    busHandler.sendMessage(busHandler.obtainMessage());

                    Thread.sleep(500);
                    if (firstCount == 1) {
                        test();//检测设备是否正常
                    }
                    if (firstCount == 0) {
                        exit = true;
//                        Intent intent = new Intent(LoginActivity.this, XingbangMain.class);
//                        startActivity(intent);
//                        finish();
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
        Logger.e("高压和低压值"+ "lowVoltage: " + lowVoltage + "  highVoltage: " + highVoltage);
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
        Utils.writeRecord("设置低压" + (c + b) + "--设置高压" + (e + d));
        byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Test((c + b) + (e + d));//14
        sendCmd(powerCmd);
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
//            Logger.e("核心板自检"+ "voltLow: " +voltLow);
//            Logger.e("核心板自检"+ "voltHeigh: " +voltHeigh);
            Utils.writeRecord("单片机返回的设置电压--低压:" + voltLow + "--高压:" + voltHeigh);
            dianya_low = Utils.getFloatToFormat((float) voltLow, 2, 4);
            dianya_high = Utils.getFloatToFormat((float) voltHeigh, 2, 4);
            byte[] powerCmd = OneReisterCmd.send_13("00");//13 退出测试模式
            sendCmd(powerCmd);
        }
    }
}