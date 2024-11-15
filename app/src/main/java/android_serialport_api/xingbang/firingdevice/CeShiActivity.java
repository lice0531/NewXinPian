package android_serialport_api.xingbang.firingdevice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.sdk.devicemanager.ICcon;

import android_serialport_api.xingbang.LxrSerialPortActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//public class CeShiActivity extends Activity implements ICcon.OnSerialPortDataListener, ICcon.OnOpenSerialPortListener {
public class CeShiActivity extends LxrSerialPortActivity {

    @BindView(R.id.btn_1)
    Button btn1;
    @BindView(R.id.btn_2)
    Button btn2;
    @BindView(R.id.btn_3)
    Button btn3;
    @BindView(R.id.btn_4)
    Button btn4;
    @BindView(R.id.btn_5)
    Button btn5;
    @BindView(R.id.btn_6)
    Button btn6;
    @BindView(R.id.btn_7)
    Button btn7;
    @BindView(R.id.btn_8)
    Button btn8;
    @BindView(R.id.btn_9)
    Button btn9;
    @BindView(R.id.btn_10)
    Button btn10;
    @BindView(R.id.btn_11)
    Button btn11;
    @BindView(R.id.btn_12)
    Button btn12;
    @BindView(R.id.tv_1)
    TextView tv1;
    private Handler Handler_tip = null;//提示
    public ICcon iCcon;
    private String TAG = "测试页面--";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ce_shi);
        ButterKnife.bind(this);
        iCcon = ICcon.getInstance();
        iCcon.debugMode(false);
        Handler_tip = new Handler(msg -> {
            if (msg.what == 1) {
                String receData = (String) msg.obj;
                tv1.setText("接收到的数据:" + receData);
            }
            return false;
        });
    }

    @Override
    protected void onLxrDataReceived(byte[] buffer) {
        String fromCommad = Utils.bytesToHexFun(buffer);
//        Log.e(TAG, "收到串口数据" + fromCommad);
        Message msg = new Message();
        msg.what = 1;
        Log.e(TAG + "收到串口数据",fromCommad);
        msg.obj = fromCommad;
        Handler_tip.sendMessage(msg);
    }

//    @Override
//    protected void onDataReceived(byte[] buffer, int size) {
//        String fromCommad = Utils.bytesToHexFun(buffer);
//        Log.e("串口返回", "fromCommad: "+fromCommad );
//        Message msg = new Message();
//        msg.what = 1;
//        Log.e(TAG + "收到串口数据",fromCommad);
//        msg.obj = fromCommad;
//        Handler_tip.sendMessage(msg);
//    }

    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_10, R.id.btn_11, R.id.btn_12})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1://PCIE上电
                boolean a = iCcon.setPciePwr_EnOn();
                Log.e(TAG + "PCIE上电", "上电: " + a);
                break;
            case R.id.btn_2://PCIE下电
                boolean b = iCcon.setPciePwr_EnOff();
                Log.e("PCIE下电", "下电: " + b);
                break;
            case R.id.btn_3://mcu上电并打开串口
                int aa = iCcon.mcu_powerAndoPenOrCloseSerialPort(1);
                Log.e(TAG + "mcu上电并打开串口", "上电: " + aa);
                boolean rateResult = iCcon.setPCIBandrate(115200);
                Log.e(TAG + "设置波特率", rateResult + "");
                break;
            case R.id.btn_4://mcu下电并关闭串口
                int bb = iCcon.mcu_powerAndoPenOrCloseSerialPort(0);
                Log.e(TAG + "mcu下电并打开串口", "下电: " + bb);
                break;
            case R.id.btn_5://打开读取数据线程  mcu上电
                int a1 = iCcon.MCU_interface_power(1);
                Log.e(TAG,"mcu上电: " + a1);
                break;
            case R.id.btn_6://关闭读取数据线程  mcu下电
                int a2 = iCcon.MCU_interface_power(0);
                Log.e(TAG + "mcu下电", "下电: " + a2);
                break;
            case R.id.btn_7://不包含MCU 打开串口
                //接口回调打开串口方法  包含MCU上电
                iCcon.openSerialPort(this, this);
                break;
            case R.id.btn_8://包含MCU下电 关闭串口
                //关闭读串口数据
                iCcon.closeReadThead();
                //接口回调关闭串口
                iCcon.closeSerialPort();
                break;
            case R.id.btn_9://控制卡上电
//                int c = iCcon.exPowerOn();
                powerOn();
//                Log.e(TAG + "控制卡上电", "返回: " + c);
//                byte[] cmd =FourStatusCmd.setToXbCommon_OpenPower_42_2("00");
//                iCcon.PCI_CommandWrite(cmd.length,cmd,100);
                break;
            case R.id.btn_10://控制卡上电
                int d = iCcon.exPowerOff();
//                int gpio93=iCcon.setExGpio(93,1);
//                int gpio94=iCcon.setExGpio(94,1);
                Log.e(TAG + "控制卡下电", "返回: " + d);
//                Log.e("控制卡上电", "gpio93: "+gpio93 );
//                Log.e("控制卡上电", "gpio94: "+gpio94 );
                break;
            case R.id.btn_11://打开串口回调
//                sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));
                //此方法是接口回调的写串口数据
                byte[] sendData1 = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");
                String sendDataStr = Utils.bytesToHexFun(sendData1);
                int write = iCcon.onDataSent(sendData1);
                if (write == 0) {
                    mTips = "串口发送成功,写入数据：" + sendDataStr + "--长度：" + sendDataStr.length();
                    tv1.setText(mTips);
                    Log.e(TAG, "串口发送数据:" + sendDataStr);
                } else {
                    mTips = "发送失败";
                    tv1.setText(mTips);
                }
                break;
            case R.id.btn_12://退出
                exitPage();
                finish();
                break;
        }
    }

    //发送命令
//    public void sendCmd(byte[] mBuffer) {
//        if (mSerialPort != null && mOutputStream != null) {
//            try {
//                String str = Utils.bytesToHexFun(mBuffer);
//                Utils.writeLog("自检发送:" + str);
//                Log.e("发送命令", str);
//                mOutputStream.write(mBuffer);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        } else {
//            return;
//        }
//    }

    public static String byte2Hex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        // 遍历byte[]数组，将每个byte数字转换成16进制字符，再拼接起来成字符串
        for (int i = 0; i < bytes.length; i++) {
            // 每个byte转换成16进制字符时，bytes[i] & 0xff如果高位是0，输出将会去掉，所以+0x100(在更高位加1)，再截取后两位字符
            builder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return builder.toString();
    }

//    @Override
//    public void onSuccess(String s) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                tv1.setText("串口打开成功");
//                //设置波特率
//                if (setBaund(115200)) {
//                    tv1.append("\n波特率设置成功：115200");
//                    Log.e(TAG, "设置波特率成功:115200");
//                } else {
//                    Log.e(TAG, "设置波特率失败");
//                    tv1.append("\n波特率设置失败：115200");
//                }
//                //回调方式  开启回调数据接受
//                iCcon.openReadThead();
//            }
//        });
//        Log.e(TAG, "串口打开open success");
//    }
//
//    @Override
//    public void onFail(String s, Status status) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                tv1.setText("串口打开失败");
//            }
//        });
//        Log.e(TAG, "串口打开open failed");
//    }
//
//    @Override
//    public void onDataReceived(byte[] bytes) {
//        String fromCommad = Utils.bytesToHexFun(bytes);
////        Log.e(TAG, "串口返回fromCommad: " + fromCommad);
//        Message msg = new Message();
//        msg.what = 1;
//        Log.e(TAG, "收到串口数据" + fromCommad);
//        msg.obj = fromCommad;
//        Handler_tip.sendMessage(msg);
//    }

    private String mTips;

    private boolean setBaund(int value) {
        boolean baund = iCcon.setPCIBandrate(value);//设置波特率
        if (baund) {
            mTips = "波特率设置成功";
            tv1.setText(mTips);
            return true;
        } else {
            mTips = "波特率设置失败";
            tv1.setText(mTips);
            return false;
        }
    }

    class mThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (swithState) {
                int switchStatus = iCcon.get_switchStatus();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv1.setText("请连续波动安全开关，观察状态变化\n开关状态：" + switchStatus);
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private mThread thread;
    private boolean swithState = false;

    private void getSwithStatus() {
        if (thread == null) {
            thread = new mThread();
        }
        if (swithState) {
            swithState = false;
            thread.interrupt();
            thread = null;
            mTips = "安全开关状态检测已关闭";

        } else {
            mTips = "请连续波动安全开关，观察状态变化";
            swithState = true;
            thread.start();
        }
    }

    private boolean powerOff() {
        int powerOff = iCcon.exPowerOff();
        if (powerOff == 0) {
            Log.e(TAG, "板卡下电成功");
            return true;
        } else {
            Log.e(TAG, "板卡下电失败");
            return false;
        }
    }


    private boolean powerOn() {
        int exPowerOn = iCcon.exPowerOn();
        Log.e(TAG, "exPowerOn=======" + exPowerOn);
        if (exPowerOn == 0) {
            Log.e(TAG, "板卡上电成功");
            return true;
        } else {
            Log.e(TAG, "板卡上电失败");
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        powerOff();
    }

    private void exitPage() {
        iCcon.closeReadThead();
        iCcon.closeSerialPort();
//        powerOff();
        iCcon.MCU_interface_power(0);
        Log.e(TAG,"关闭串口下电");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        swithState = false;
//        if (thread != null && thread.isAlive()) {
//            thread.interrupt();
//        }
        exitPage();
    }
}