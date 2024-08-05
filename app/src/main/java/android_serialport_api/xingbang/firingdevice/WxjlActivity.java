package android_serialport_api.xingbang.firingdevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.IOException;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class WxjlActivity extends SerialPortActivity {
    /**
     * 界面
     */
    private String TAG = "无线级联页面";
    private String wxjlDeviceId = "01";//目前只有一台设备  所以设备地址先固定01
    private boolean isRemote = false;//是否可以进行远距离无线级联
    @BindView(R.id.btn_near)
    RelativeLayout btnNear;
    @BindView(R.id.btn_remote)
    RelativeLayout btnRemote;
    private String dataLength82 = "", data82 = "";
    private boolean receive82 = false;//发出82命令是否返回
    private Handler handler_msg = new Handler();
    private EnterJcms enterJcms;
    Handler openHandler = new Handler();//重新打开串口
    private boolean isRestarted = false;
    private int errorNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxjl);
        ButterKnife.bind(this);
        initHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onRestart() {
        isRestarted = true;
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        openHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openSerial();
            }
        }, 2000);
//        cale();
    }
    private void cale() {
        String d1 = "03";
        String d2 = "05";
        String d3 = "0D";
        int str1 = Integer.parseInt(d1, 16);
        int str2 = Integer.parseInt(d2, 16);
        int str3 = Integer.parseInt(d3, 16);
        Log.e(TAG,"10进制数是str1：" + str1 + "--str2:" + str2 + "--str3:" + str3);

        String st1 = "C0018403030005000D00C6DDC0";
        String st2 = "C00184020100FE36C0";
        String st3 = "C00184050108DC65C0";
        String e1 = st1.substring(8, st1.length() - 6);
        String e2 = st2.substring(8, st2.length() - 6);
        String e3 = st3.substring(8, st3.length() - 6);
        Log.e(TAG,"错误雷管cmd是:" + e1 + "--" + e2 + "--" + e3);
    }
    private void openSerial(){
        if (isRestarted) {
            initSerialPort(115200);
            Log.e(TAG,"重新开启打开串口");
        }
    }
    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        Log.e(TAG + "-返回命令", fromCommad);
        Utils.writeLog("<-:" + fromCommad);
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                return;
            } else {
                String cmd = DefCommand.getCmd2(fromCommad);
                Log.e(TAG,TAG + "--返回命令" + cmd);
                if (cmd != null) {
                    int localSize = fromCommad.length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                    doWithReceivData(cmd, localBuf);//处理cmd命令
                }
            }
        }
    }

    /***
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, byte[] locatBuf) {
        if (DefCommand.CMD_5_TRANSLATE_82.equals(cmd)) {//82 无线级联：进入检测模式
            Log.e(TAG, "收到82指令了");
            receive82 = true;
            Message message = new Message();
            message.what = 2;
            message.obj = "true";
            handler_msg.sendMessage(message);
        } else {
            Log.e(TAG, TAG + "-返回命令没有匹配对应的命令-cmd: " + cmd);
        }
    }

    private void initHandler() {
        handler_msg = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0:
                        String jcmsResult = (String) msg.obj;
                        closeThread();
                        if ("true".equals(jcmsResult)) {
                            enterRemotePage();
                        } else {
                            show_Toast("芯片未返回，请退出APP后再重新级联");
                        }
                        break;
                    case 2:
                        enterRemotePage();
                        break;
                }
                return false;
            }
        });
    }

    private void enterRemotePage() {
        closeSerial();
        Intent intent = new Intent(WxjlActivity.this, WxjlRemoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("wxjlDeviceId", wxjlDeviceId);
        startActivity(intent);
        finish();
    }

    private void closeThread(){
        if (enterJcms != null) {
            enterJcms.exit = true;
            enterJcms.interrupt();
        }
    }

    private class EnterJcms extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;
            while (!exit) {
                try {
                    if (receive82) {
                        exit = true;
                        break;
                    }
                    if (zeroCount > 0 && zeroCount <= 5 && !receive82) {
                        String b = Utils.intToHex(1);
                        dataLength82 = Utils.addZero(b, 2);
                        data82 = "01";
                        sendCmd(ThreeFiringCmd.sendWxjl82(wxjlDeviceId, dataLength82, data82));
                        Log.e(TAG, "发送82进入检测模式指令");
                        Thread.sleep(1500);
                    } else if (zeroCount > 5){
                        Log.e(TAG,"82指令未返回已发送5次，停止发送82指令");
                        exit = true;
                        Message message = new Message();
                        message.what = 0;
                        message.obj = "error";
                        handler_msg.sendMessage(message);
                        break;
                    }
                    zeroCount++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Log.e("发送命令", str);
                Utils.writeLog("->:" + str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void closeSerial() {
        isRestarted = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApplication.closeSerialPort();
                Log.e(TAG,"调用mApplication.closeSerialPort()开始关闭串口了。。");
                mSerialPort = null;
            }
        }).start();
    }

    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 1000; // 快速点击间隔
    private String Yanzheng_sq = "";//是否验雷管已经授权
    @OnClick({R.id.btn_near, R.id.btn_remote})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_near:
                closeSerial();
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
                Intent intent = new Intent(WxjlActivity.this, WxjlNearActivity.class);
//                Intent intent = new Intent(WxjlActivity.this, NewNearJlActivity.class);
                intent.putExtra("errorNum",errorNum);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.btn_remote:
                if (isRemote) {
                    enterJcms = new EnterJcms();
                    enterJcms.start();
                    Log.e(TAG,"开启发送82命令的线程了");
                } else {
                    show_Toast("请先近距离级联");
                    return;
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
        String msg = event.getMsg();
        Log.e(TAG,"eventBus收到消息了" + msg);
        if (event.getMsg().equals("is81End")) {
            //说明此时近距离级联81指令已结束  并未执行82指令   此时点击远距离级联需要先发送82指令  等82成功再远距离级联
            isRemote = true;
        } else if (event.getMsg().equals("errorLgNum")) {
            errorNum = Integer.parseInt(event.getData());
            Log.e(TAG,"eventBus收到errorLgNum了:" + errorNum);
        }
    }

    @Override
    protected void onDestroy() {
        sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
        closeSerial();
        super.onDestroy();
        closeThread();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
