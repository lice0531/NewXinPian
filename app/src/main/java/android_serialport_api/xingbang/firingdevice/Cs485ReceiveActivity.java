package android_serialport_api.xingbang.firingdevice;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kfree.expd.ExpdDevMgr;
import com.kfree.expd.OnOpenSerialPortListener;
import com.kfree.expd.OnSerialPortDataListener;
import com.kfree.expd.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.jilian.IntentBean;
import android_serialport_api.xingbang.jilian.Protcol;
import android_serialport_api.xingbang.jilian.SettingActivity;
import android_serialport_api.xingbang.jilian.SyncActivityYouxian;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Cs485ReceiveActivity extends BaseActivity {
    private Socket socket = null;
    private boolean socketStatus = false;
    private boolean status485 = false;

    private OutputStream outputStream = null;
    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    private boolean isTongBu = false;
    private int num = 0;
    private int mNumber = 0;
    /**
     * 接收服务器消息 变量
     */
    DataInputStream dis;
    // 输入流对象
    InputStream is;
    // 输入流读取器对象
    InputStreamReader isr;
    BufferedReader br;
    // 接收服务器发送过来的消息
    String response;

    /**
     * 界面
     */
    private static final int REQUEST_CODE_NET = 101;
    private static final int REQUEST_CODE_CHONGDIAN = 102;
    private static final int REQUEST_CODE_QIBAO = 103;
    private String TAG = "Cs485ReceiveActivity";
    private boolean isConnect;
    private boolean isExit = false;
    private String Yanzheng = "";//是否验证地理位置

    private boolean A002 = true;
    private String deviceId = "";
    byte[] mBuffer;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.btn_test)
    Button btnTest;
    @BindView(R.id.btn_test1)
    Button btnTest1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cs_wxjl_devices);
        ButterKnife.bind(this);
        mThreadPool = Executors.newCachedThreadPool();
        mBuffer = new byte[50];
        tvCode.setText(!TextUtils.isEmpty(getIntent().getStringExtra("deviceId")) ? getIntent().getStringExtra("deviceId") : "");
        getPropertiesData();
    }

    @OnClick({R.id.btn_test, R.id.btn_test1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test:
                btnTest.setText(getString(R.string.text_sync_tip8));
                btnTest.setEnabled(false);
                openM900Rs485(1,"");
                break;
            case R.id.btn_test1:
                show_Toast(
                        getString(R.string.text_sync_tip11));
                isTongBu = false;
                switch (Build.DEVICE) {
                    case "M900":
                        send485Cmd("0005" + MmkvUtils.getcode("ACode", ""));
                        break;
                    default:
                        closeSocket();
                        break;
                }
                finish();
                break;
        }
    }


    private void getPropertiesData() {
        Yanzheng = (String) MmkvUtils.getcode("Yanzheng", "验证");
        Log.e("级联", "Yanzheng: " + Yanzheng);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    String response = (String) msg.obj;
                    if (response == null) {
                        show_Toast(getString(R.string.text_sync_tip1));
                        break;
                    }
//                    if (response.contains("A001" + MmkvUtils.getcode("ACode", ""))) {
                    if (response.contains("A1" + MmkvUtils.getcode("ACode", ""))) {
                        //同步成功
                        //收到服务器的同步确认指令
                        isTongBu = true;
                        handler.sendEmptyMessageDelayed(5, 1000);
                        show_Toast(getString(R.string.text_sync_tip2));
                        Log.e(TAG,"收到同步A1指令");
                    } else if (response.contains("A2")) {
                        Log.e(TAG,"收到起爆测试A2指令");
                    } else if (response.contains("A3")) {
                        //收到主控轮询的命令
                        if (MmkvUtils.getcode("ACode", "").equals(response.substring(2))) {
                            EventBus.getDefault().post(new FirstEvent("pollMsg"));
                        }
                        Log.e(TAG,"收到轮训子设备数据A3指令");
                        //收到主控的充电指令
                    } else if (response.contains("A4")) {
                        show_Toast(getString(R.string.text_sync_tip5));
                        Log.e(TAG,"收到充电A4指令");
                    } else if (response.contains("A5")) {
                        Log.e("接收到A5指令了",response);
                        //收到主控切换模式的命令  此时通知板子进入起爆模式
                        if (MmkvUtils.getcode("ACode", "").equals(response.substring(2,4))) {
                            //主的子设备
                            show_Toast(getString(R.string.text_sync_tip6));
                            Log.e("主的子设备已接收到切换模式指令",response);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            send485Cmd("B5" + MmkvUtils.getcode("ACode", ""));
                            EventBus.getDefault().post(new FirstEvent("sendCmd83"));
                        } else {
                            //其他子设备
                            Log.e("接收到A5指令了","开始关闭485");
                            show_Toast(getString(R.string.text_sync_tip6));
                            Utils.writeLog("其他子设备：" + MmkvUtils.getcode("ACode", "") + "开始关闭485指令");
                            Log.e("其他子设备已接收到切换模式指令","现在开始关闭485" + response);
                            closeM900Rs485(response);
                        }
                        //此时在起爆页面展示一个文字提示，内容为：时钟校验中，等待起爆，请稍等
                        EventBus.getDefault().post(new FirstEvent("sendWaitQb"));
//                    } else if (response.contains("A003")) {
                        //收到主控的充电指令
                    } else if (response.contains("A6")) {
//                        if (MmkvUtils.getcode("ACode", "").equals(response.substring(2))) {
                        show_Toast(getString(R.string.text_sync_tip6));
                        EventBus.getDefault().post(new FirstEvent("qibao"));
                        send485Cmd("B6" + MmkvUtils.getcode("ACode", ""));
//                        }
//                        Intent intent = new Intent(SyncActivity.this, FiringMainActivity.class);
//                        if (response.length() >= 5) {
//                            intent.putExtra("itemId", response.substring(4));
//                        } else {
//                            intent.putExtra("itemId", "");
//                        }
//                        startActivityForResult(intent, REQUEST_CODE_QIBAO);
                        //收到主控的退出指令
//                    } else if (response.contains("A005")) {
                    } else if (response.contains("A7")) {
                        EventBus.getDefault().post(new FirstEvent("finish"));
                        send485Cmd("B7" + MmkvUtils.getcode("ACode", ""));

//                        show_Toast("收到退出指令");
                        finish();
                    } else if (response.contains("A006")) {
//                        EventBus.getDefault().post(new FirstEvent("qibaoTag"));
                    } else if (response.contains("A008")) {
//                        toCheck6();
                    }

                    break;
                case 1:
//                    show_Toast("同步指令错误");
                    break;
                case 2:
                    show_Toast(getString(R.string.text_sync_tip7));
                    break;
                case 3:
                    //心跳数据
                    mNumber = 0;
                    break;
                case 6:
//                    final String data2 = "0001" + MmkvUtils.getcode("ACode", "") + "\n";
                    final String data2 = "B1" + MmkvUtils.getcode("ACode", "") + "\n";
                    writeData(data2);
                    break;
                case 10:
//                    btnConnect.setText("已连接");
//                    btnTest.setEnabled(true);
//                    btnTest1.setEnabled(true);
                    break;
                case 16:
//                    EMgpio.SetGpioDataLow(94);
                    try {
                        Thread.sleep(12);

//                        mOutputStream.write(("0001" + MmkvUtils.getcode("ACode","")).getBytes());
//                        Thread.sleep(12);
//                        EMgpio.SetGpioDataHigh(94);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessageDelayed(16, 1000);
                    break;
                case 20:
                    if (!isTongBu) {
                        num++;
//                        EMgpio.SetGpioDataLow(94);
                        try {
                            Thread.sleep(12);

                            String a = myInfo();
//                            mOutputStream.write(("0001" + MmkvUtils.getcode("ACode","") + ",_*").getBytes());
//                            mOutputStream.write((a + ",_*").getBytes());
                            show_Toast(getString(R.string.text_sync_tip9));
                            Thread.sleep(20);
//                            EMgpio.SetGpioDataHigh(94);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (num < 2) {
                            handler.sendEmptyMessageDelayed(20, 1000);
                        }
                    } else {
                        num = 0;
                    }

                    break;
                case 666:
                    IntentBean item = (IntentBean) msg.obj;
                    int resultCode = item.getResultCode();
                    Intent data = item.getData();
                    switch (item.getRequestCode()) {
                        case REQUEST_CODE_NET:
                            if (resultCode == TestDenatorActivity.RESULT_SUCCESS) {
                                //网络测试回调
                                String code = (String) MmkvUtils.getcode("ACode", "");
                                String type = data.getStringExtra("type");
                                String tNum = data.getStringExtra("tNum");
                                String faultNum = data.getStringExtra("faultNum");
                                String tU = data.getStringExtra("tU");
                                String tI = data.getStringExtra("tI");
                                String tip = data.getStringExtra("tip");

//                    show_Toast("正在回传数据");
                                String a = "0002" + code + "," + type + "," + tNum + "," + faultNum + "," + tU + "," + tI + "," + tip;
                                show_Toast(a);
                                writeData(a);
                            }
                            break;
                        case REQUEST_CODE_CHONGDIAN:
                            break;
                        case REQUEST_CODE_QIBAO:
                            if (resultCode == FiringMainActivity.RESULT_SUCCESS) {
                                //起爆
                                String code = (String) MmkvUtils.getcode("ACode", "");
                                String type = data.getStringExtra("type");
                                String tip = data.getStringExtra("tip");

                                show_Toast(getString(R.string.text_sync_tip10));
                                String a = "0004" + code + "," + type + "," + tip;
                                writeData(a);
                            }
                            break;
                    }
                    break;
            }

            return false;
        }
    });

    private String myInfo() {
        String code = (String) MmkvUtils.getcode("ACode", "");
        String check = MmkvUtils.getcode("check", 1) + "";
        String info = "";
        if (check.equals("1")) {

        } else {
        }
        show_Toast(getString(R.string.text_sync_tip12));
        String a;
        if (check.equals("1")) {
            a = "0001" + code + ",0" + check;
        } else {
            a = "0001" + code + ",0" + check + "," + info;
        }
        return a;
    }

    /**
     * 发送485命令
     */
    public void send485Cmd(String data) {
        int delay = Integer.parseInt((String) MmkvUtils.getcode("ACode", ""));
        try {
            Thread.sleep(delay * 2L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] powerCmd = Utils.hexStringToBytes(data);
        mExpDevMgr.sendBytesRs485(powerCmd);
//        String str = Utils.bytesToHexFun(powerCmd);
        Log.e("子机485发送-data", data);
    }
    /**
     * socket发送消息
     */
    private void writeData(final String data) {
        mThreadPool.execute(() -> {
            if (socketStatus) {
                try {
                    outputStream = socket.getOutputStream();
                    outputStream.write(data.getBytes());
                    // 步骤3：发送数据到服务端
                    outputStream.flush();
                    Log.e("级联", "发送数据: " + data);
                    handler.sendEmptyMessage(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void closeM900Rs485(String code) {
        switch (Build.DEVICE) {
            case "M900":
                if (mExpDevMgr != null) {
                    mExpDevMgr.closeRs485();
                    mExpDevMgr.set12VEnable(false);
                    Log.e("关闭485，设备是",code);
                    Utils.writeLog("子设备：" + MmkvUtils.getcode("ACode", "") + "已关闭485指令");
                }
                break;
            default:
                closeSocket();
                break;
        }
    }

    private void closeSocket() {
        writeData("0005");//退出
        int len = (Protcol.QuitFunc(mBuffer)) & 0xff;
        isExit = true;
        handler.removeMessages(3);
        handler.removeMessages(5);
        if (mThreadPool != null) {
            mThreadPool.shutdown();
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
        String msg = event.getMsg();
        Log.e("同步页面收到起爆页面485消息了",msg);
        if (msg.equals("qibao")) {
            String a = "0006";
            writeData(a);
        } else if (msg.equals("jcjg")) {//返回测试结果
            String tureNum = Utils.strPaddingZero(event.getTureNum(), 3);
            String errNum = Utils.strPaddingZero(event.getErrNum(), 3);
            String currentPeak = Utils.strPaddingZero(event.getCurrentPeak(), 6);
            Log.e("有线级联页面返回jcjg测试结果", "tureNum: " + tureNum);
            Log.e("有线级联页面返回jcjg测试结果", "errNum: " + errNum);
            Log.e("有线级联页面返回jcjg测试结果", "currentPeak: " + event.getCurrentPeak());
            send485Cmd("B007" + MmkvUtils.getcode("ACode", "") + tureNum + errNum + currentPeak);
//            send485Cmd("B007"+ MmkvUtils.getcode("ACode", "")+tureNum+errNum);
        } else if (msg.equals("ddjc")) {//等待检测
            String tureNum = Utils.strPaddingZero(event.getTureNum(), 3);
            String errNum = Utils.strPaddingZero(event.getErrNum(), 3);
            String currentPeak = Utils.strPaddingZero(event.getCurrentPeak(), 6);
            Log.e("有线级联页面ddjc返回测试结果", "tureNum: " + tureNum);
            Log.e("有线级联页面ddjc返回测试结果", "errNum: " + errNum);
            Log.e("有线级联页面ddjc返回测试结果", "currentPeak: " + event.getCurrentPeak());
            send485Cmd("B008" + MmkvUtils.getcode("ACode", "") + event.getData() + currentPeak);
//            send485Cmd("B008"+ MmkvUtils.getcode("ACode", "")+event.getData());
        } else if (msg.equals("zzcd")) {//正在充电
            String tureNum = Utils.strPaddingZero(event.getTureNum(), 3);
            String errNum = Utils.strPaddingZero(event.getErrNum(), 3);
            String currentPeak = Utils.strPaddingZero(event.getCurrentPeak(), 6);
            Log.e("有线级联页面zzcd返回测试结果", "tureNum: " + tureNum);
            Log.e("有线级联页面zzcd返回测试结果", "errNum: "+errNum);
            Log.e("有线级联页面zzcd返回测试结果", "currentPeak: " + event.getCurrentPeak());
            send485Cmd("B009" + MmkvUtils.getcode("ACode", "") + event.getData() + currentPeak);
//            send485Cmd("B009"+ MmkvUtils.getcode("ACode", "")+event.getData());
        } else if (msg.equals("qbjg")) {//返回起爆结果
            String tureNum = Utils.strPaddingZero(event.getTureNum(), 3);
            String errNum = Utils.strPaddingZero(event.getErrNum(), 3);
            String currentPeak = Utils.strPaddingZero(event.getCurrentPeak(), 6);
            Log.e("有线级联页面返回qbjg测试结果", "tureNum: " + tureNum);
            Log.e("有线级联页面返回qbjg测试结果", "errNum: " + errNum);
            Log.e("有线级联页面返回qbjg测试结果", "currentPeak: " + event.getCurrentPeak());
            send485Cmd("B010" + MmkvUtils.getcode("ACode", "") + event.getData() + currentPeak);
//            send485Cmd("B010"+ MmkvUtils.getcode("ACode", "")+event.getData());
        } else if (msg.contains("B2")) {
            //说明子机已进入起爆页面  此时需给主控发消息告知
            send485Cmd("B2" + MmkvUtils.getcode("ACode", ""));
        } else if (msg.equals("ssjc")) {
            //说明子机已接到轮询指令  此时需给主控发消息告知
//            String tureNum = Utils.strPaddingZero(event.getTureNum(), 3);
//            String errNum = Utils.strPaddingZero(event.getErrNum(), 3);
//            String currentPeak = Utils.strPaddingZero(event.getCurrentPeak(), 6);
            String pollData = "B3" + MmkvUtils.getcode("ACode", "") + event.getData() + event.getTureNum()
                    + event.getErrNum() + event.getCurrentPeak();
            if (pollData.startsWith("B3") && pollData.length() == 18) {
                send485Cmd(pollData);
            }
        } else if (msg.equals("open485")) {
            switch (Build.DEVICE) {
                case "M900":
                    openM900Rs485(2,event.getData());
                    break;
                default:
                    Log.e("执行关闭socket操作","。。。。。");
                    closeSocket();
                    break;
            }

        } else if (msg.equals("close485")) {
            //此时关闭485接收  让板子子机去执行起爆命令
            Utils.writeLog("主的子设备：" + MmkvUtils.getcode("ACode", "") + "开始关闭485指令");
            closeM900Rs485("B5" + MmkvUtils.getcode("ACode", ""));
        } else if (msg.equals("sendA4Data")) {
            int delay = Integer.parseInt((String) MmkvUtils.getcode("ACode", ""));
            try {
                Thread.sleep(delay * 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (event.getData().startsWith("B4") && event.getData().length() == 18) {
                Log.e("收到充电指令后发送的数据正常",event.getData());
                send485Cmd(event.getData());
            } else {
                Log.e("收到充电指令后发送的数据有误",event.getData());
            }
        }
    }

    private void openM900Rs485(int type,String qbResult){
        mExpDevMgr = new ExpdDevMgr(this);
        //串口打开监听
        OnOpenSerialPortListener listener = new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File file) {
                Log.e("485接口-串口状态监听", "打开成功-file: " + file.toString());
            }

            @Override
            public void onFail(File file, Status status) {
                Log.e("485接口-串口状态监听", "打开失败-status: " + status);
            }
        };
        //串口数据监听
        OnSerialPortDataListener listener2 = new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                String fromCommad = Utils.bytesToHexFun(bytes);//将数组转化为16进制字符串
                Log.e("485接口-接收数据", "onDataReceived: " + fromCommad);
//                                if (fromCommad.startsWith("A0")) {
                if (fromCommad.startsWith("A")) {
                    if(fromCommad.equals("A6")) {
                        EventBus.getDefault().post(new FirstEvent("qibao"));
                    } else {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = fromCommad;
                        handler.sendMessage(msg);
                    }
                } else if (fromCommad.startsWith("FF")) {
                    //心跳数据
                    handler.sendEmptyMessage(3);
                }
            }

            @Override
            public void onDataSent(byte[] bytes) {
                String fromCommad = Utils.bytesToHexFun(bytes);//将数组转化为16进制字符串
                Log.e("485接口-发送数据", "onDataSent: " + fromCommad);
            }
        };
        mExpDevMgr.set12VEnable(true);
        mExpDevMgr.openRs485(listener, listener2, 115200);
        if (type == 1) {
            final String data = "B1" + deviceId;
            send485Cmd(data);
        } else {
            int delay = Integer.parseInt((String) MmkvUtils.getcode("ACode", ""));
            try {
                Thread.sleep(delay * 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (qbResult.startsWith("B005") && qbResult.length() == 20) {
                Log.e("起爆结束后发送的数据正常", qbResult);
                send485Cmd(qbResult);
            } else {
                Log.e("起爆结束后发送的数据有误", qbResult);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EMgpio.SetGpioDataLow(94);//下电
//        closeM900Rs485("页面销毁时正常关闭485");
//        Utils.writeLog("子设备：" + MmkvUtils.getcode("ACode", "") + "页面退出时开始关闭485指令");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
