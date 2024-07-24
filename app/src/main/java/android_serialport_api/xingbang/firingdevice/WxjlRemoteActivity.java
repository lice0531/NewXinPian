package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.custom.DeviceAdapter;
import android_serialport_api.xingbang.custom.ListViewForScrollView;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.models.DeviceBean;
import android_serialport_api.xingbang.server.MySocketServer;
import android_serialport_api.xingbang.server.PollingReceiver;
import android_serialport_api.xingbang.server.PollingUtils;
import android_serialport_api.xingbang.server.WebConfig;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.upload.InitConst;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WxjlRemoteActivity extends SerialPortActivity {

    @BindView(R.id.lv)
    ListViewForScrollView lv;
    MySocketServer server;
    @BindView(R.id.btn_net_test)
    Button btnNetTest;
    @BindView(R.id.btn_prepare_charge)
    Button btnPrepareCharge;
    @BindView(R.id.btn_qibao)
    Button btnQibao;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.lv_net)
    ListViewForScrollView lvNet;
    @BindView(R.id.lv_qibao)
    ListViewForScrollView lvQibao;
    @BindView(R.id.lv_chongdian)
    ListViewForScrollView lvChongdian;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_msg)
    TextView tvMsg;

    private List<DeviceBean> list_device = new ArrayList<>();
    private List<DeviceBean> cmdList = new ArrayList<>();
    private List<DeviceBean> pollingList = new ArrayList<>();
    private DeviceAdapter adapter;
    private boolean isDeviceConnet;

    private int qibaoNum = 0;
    public boolean cs = true;
    public boolean cd = false;
    public boolean qb = false;
    public boolean qh = false;
    private ConcurrentHashMap<String, String> lastReceivedMessages = new ConcurrentHashMap<>();
    //起爆按钮点击时，值为qh:发送A6命令（切换模式）   值为qb:发送A4命令（起爆）
    private String qbFlag = "qb";
    private String TAG = "远距离串口无线级联页面";
    private String wxjlDeviceId = "";
    private SyncDevices syncDevices;
    private int firstCmdReFlag = 0;//发出同步命令是否返回

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxjl_remote);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
//        initSocket();
        initPower();                // 初始化上电方式()
        powerOnDevice(PIN_ADSL);
        initSerRate();
    }

    int rate = 2400;
    private void initSerRate() {
        //通讯速率需从115200降到2400
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mApplication.closeSerialPort();
//                Log.e(TAG, "调用mApplication.closeSerialPort()开始关闭串口了。。");
//                mSerialPort = null;
//            }
//        }).start();
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        initSerialPort(rate);
//        Log.e(TAG, "3秒后重新打开串口，将波特率降为" + rate);
        initData();
    }

    private void initData() {
        wxjlDeviceId = !TextUtils.isEmpty(getIntent().getStringExtra("wxjlDeviceId")) ?
                getIntent().getStringExtra("wxjlDeviceId") : "01";
        syncDevices = new SyncDevices();
        syncDevices.start();
    }

    public synchronized void sendCmd(byte[] mBuffer) {
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

    private class SyncDevices extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;
            while (!exit) {
                try {
                    if (firstCmdReFlag == 1) {
                        exit = true;
                        break;
                    }
                    if (zeroCount > 0 && zeroCount <= 10 && firstCmdReFlag == 0) {
                        Log.e(TAG,"发送A0同步指令");
                        sendCmd(ThreeFiringCmd.setWxjlA0(wxjlDeviceId));
                        Thread.sleep(1500);
                    } else if (zeroCount > 10){
                        Log.e(TAG,"A0指令未返回已发送10次，停止发送A0指令");
                        exit = true;
                        break;
                    }
                    zeroCount++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理芯片返回命令
     */
    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        byte[] localBuf = Utils.hexStringToBytes(fromCommad);
        Log.e("返回命令", fromCommad);
        Utils.writeLog("<-:" + fromCommad);
        doWithReceivData(localBuf);//处理cmd命令
    }

    /**
     * 处理芯片返回命令
     */
    private void doWithReceivData(byte[] cmdBuf) {
        String res = Utils.bytesToHexFun(cmdBuf);
        Message msg = new Message();
        if (res.contains(DefCommand.CMD_MC_SEND_B0)) {
            //已收到同步B0指令
            firstCmdReFlag = 1;
            //同步
            msg.what = 0;
            DeviceBean bean = new DeviceBean();
            bean.setRes(res);
            bean.setCurrentPeak("0");
            bean.setCode(res.substring(res.length() - 2));
            bean.setInfo("在线");
            msg.obj = bean;
        } else if (res.contains(DefCommand.CMD_MC_SEND_B1)) {
            // 起爆检测
            msg.what = InitConst.QIBAO_CESHI;
            msg.obj = receiveMsg(res, "在线");
            //开启轮询  10秒轮询一次接收到的消息  来更新当前设备列表信息
            sendCmd(ThreeFiringCmd.setWxjlA5(wxjlDeviceId,"13324160"));
            PollingUtils.startPollingService(WxjlRemoteActivity.this, InitConst.POLLING_TIME,
                    PollingReceiver.class, PollingUtils.ACTION);
        } else if (res.contains(DefCommand.CMD_MC_SEND_B2)) {
            // 充电
            DeviceBean bean = new DeviceBean();
            bean.setRes(res);
            bean.setCurrentPeak("0");
            bean.setCode(res.substring(res.length() - 2));
            bean.setInfo("正在充电");
            msg.what = 8;
            msg.obj = bean;
        } else if (res.contains(DefCommand.CMD_MC_SEND_B3)) {
            // 充电升到高压
            DeviceBean bean = new DeviceBean();
            bean.setRes(res);
            bean.setCurrentPeak("0");
            bean.setCode(res.substring(res.length() - 2));
            bean.setInfo("正在充电");
            msg.what = 8;
            msg.obj = bean;
        } else if (res.contains(DefCommand.CMD_MC_SEND_B4)) {
            // 单个设备起爆
            msg.what = InitConst.CODE_UPDAE_STATUS;
            msg.obj = receiveMsg(res, "正在起爆");
        } else if (res.contains(DefCommand.CMD_MC_SEND_B5)) {
            Log.e(TAG,"得到的B5消息是：" + res);
            DeviceBean bean = new DeviceBean();
            bean.setRes(res);
            bean.setCurrentPeak("0");
            bean.setCode(res.substring(2,4));
            // 1:检测状态   3：电流数据   4：雷管总数   6：错误数量
            int aa = res.length() / 4;
            for (int i = 1; i < aa; i++) {
                String value = res.substring(4 * i, 4 * (i + 1));
                Log.e(TAG,"B5消息中拿到的值是：" + value);
                switch (value.substring(0, 1)){
                    case "1":
                        //根据不同的值展示状态
                        bean.setInfo("检测中");
                        Log.e(TAG,"得到的B5消息中拿到检测状态了");
                        break;
                    case "3":
                        //问清楚值的单位后再换算展示电流信息
//                        bean.setCurrentPeak("0");
                        Log.e(TAG,"得到的B5消息中拿到电流信息了");
                        String sb = value.substring(value.length() - 3);
                        String cp = Utils.addZero(sb, 4);
                        Log.e(TAG,"得到的电流是" + sb + "--补零后：" + cp);

                        break;
                    case "4":
                        bean.setTrueNum(value.substring(value.length() - 3));
                        Log.e(TAG,"得到的B5消息中拿到全部数量了" + value.substring(value.length() - 3));
                        break;
                    case "6":
                        bean.setErrNum(value.substring(value.length() - 3));
                        Log.e(TAG,"得到的B5消息中拿到错误数量了" + value.substring(value.length() - 3));
                        break;
                }
            }
            msg.what = 8;
            msg.obj = bean;
        } else if (res.contains(DefCommand.CMD_MC_SEND_B6)) {
            // 切换模式起爆
            msg.what = InitConst.CODE_TRANSLATE;
            msg.obj = receiveMsg(res, "正在起爆");
        } else if (res.contains(DefCommand.CMD_MC_SEND_B7)) {
            // 退出
            msg.what = InitConst.CODE_EXIT;
            msg.obj = receiveMsg(res, "");
        }
        handler_msg.sendMessage(msg);
    }


    public static String formatCurrentPeak(String str) {
        // Step 1: 截取最后6位字符
        String lastSixCharacters = str.substring(str.length() - 6);
        // Step 2: 去除前面零
        String numericPart = lastSixCharacters.replaceFirst("^0+", "");
        // Step 3: 如果去除前导零后为空字符串，则返回 "0"
        if (numericPart.isEmpty()) {
            return "0";
        }
        return numericPart;
    }

    private DeviceBean receiveMsg(String res, String desc) {
        //接收到485消息后处理
        DeviceBean bean = new DeviceBean();
        bean.setRes(res);
        bean.setCurrentPeak("0");
        bean.setCode(res.substring(2));
        bean.setInfo(desc);
        return bean;
    }

    private void initView() {
        adapter = new DeviceAdapter(this, list_device, true);
        lv.setAdapter(adapter);
    }

    private void initSocket() {
        WebConfig webConfig = new WebConfig();
        webConfig.setPort(9002);
        webConfig.setMaxParallels(10);
        server = new MySocketServer(webConfig, handler_msg);
        server.startServerAsync();
        server.heart();//心跳监听
    }

    private Handler handler_msg = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.obj != null) {
                switch (msg.what) {
//                    case 2000:
//                        tvMsg.setText("收到的指令是：" + msg.obj);
//                        break;
                    case 0:
                        DeviceBean bean = (DeviceBean) msg.obj;
                        if (bean.getRes() != null && msg.obj != null) {
                            Log.e("判断", "bean.getRes(): " + bean.getRes());
                            String res = bean.getRes();//进页面为空

                            if (res.contains("B0")) {
                                //受控指令
                                if (!isDeviceConnet) {
                                    isDeviceConnet = true;
                                }
                                Log.e("判断", "list_device.contains(bean): " + list_device.contains(bean));
                                Log.e("判断", "list_device: " + list_device.toString());
                                Log.e("判断", "bean: " + bean.toString());
                                Boolean chongfu = true;
                                for (DeviceBean a : list_device) {
                                    if (a.getCode().equals(bean.getCode())) {
                                        chongfu = false;
                                    }
                                }
                                if (chongfu) {
                                    list_device.add(bean);
                                    adapter.notifyDataSetChanged();
                                    //同步成功
                                    sendCmd(ThreeFiringCmd.setWxjlA0("01"));
                                    //更新设备个数
                                    Message message = handler_msg.obtainMessage();
                                    int collectionSize = list_device.size();
                                    message.what = 15;
                                    message.arg1 = collectionSize;
                                    handler_msg.sendMessage(message);
                                } else {
                                    show_Toast(bean.getCode() + "已注册");
                                }
                            } else {
                                show_Toast("未识别的设备在连接");
                            }
                        }
                        break;
                    case 15:
                        //根据设备个数  超过1个时，点起爆按钮发送切换模式命令  1个就还是发送起爆命令
                        if (msg.arg1 <= 1) {
                            qbFlag = "qb";
                        } else {
                            qbFlag = "qh";
                        }
                        break;
                    case InitConst.CODE_EXIT:
                        PollingUtils.stopPollingService(WxjlRemoteActivity.this, PollingReceiver.class, PollingUtils.ACTION);
                        Log.e("轮询已关闭", "....");
                        break;
                    case InitConst.CODE_TRANSLATE:
                        //接收到了子机切换模式的消息
                        DeviceBean dt = (DeviceBean) msg.obj;
                        if (dt.getRes() != null && msg.obj != null) {
                            Log.e("接收到了子机切换模式的消息", dt.getRes());
                        }
                        break;
                    case InitConst.CODE_UPDAE_STATUS:
                        //接收到子机发送的起爆不同状态消息  此时需更新每个设备的轮询状态
                        if (msg.obj != null) {
                            DeviceBean dc = (DeviceBean) msg.obj;
                            if (dc.getRes() != null && msg.obj != null && dc.getRes().length() == 18) {
                                Log.e("map是", lastReceivedMessages.toString());
                                //得到当前的消息
                                String currentMsg = dc.getRes();
                                String currentCode = dc.getCode();
                                if (lastReceivedMessages.get(currentCode) != null &&
                                        currentMsg.length() >= 4 &&
                                        lastReceivedMessages.get(currentCode).length() >= 4 &&
                                        currentMsg.substring(0, 4).equals(lastReceivedMessages.get(currentCode).substring(0, 4))) {
                                    // 如果上一条接收的消息不为null，并且与当前消息的前四个字符相同，则执行相应的逻辑
                                    Log.e("接受的还是旧消息", "不做处理");
                                } else {
                                    //说明已经处理过当前设备的消息   此时就不再处理
                                    if (dc.getRes().length() > 1) {
                                        String value = dc.getRes().substring(1, 2);
                                        if ("3".equals(value)) {
                                            String lastMsg = lastReceivedMessages.get(currentMsg.substring(0, 4));
                                            lastReceivedMessages.put(currentMsg.substring(0, 4), currentMsg);
                                            for (DeviceBean reBean : list_device) {
                                                if (reBean.getCode().equals(dc.getCode())) {
                                                    reBean.setRes(dc.getRes());
                                                    reBean.setSend(dc.getIsSend());
                                                    reBean.setInfo(dc.getInfo());
                                                    reBean.setErrNum(dc.getErrNum());
                                                    reBean.setCurrentPeak(dc.getCurrentPeak());
                                                    reBean.setTrueNum(dc.getTrueNum());
                                                }
                                            }
//                                            int a = 0;
//                                            for (int i = 0; i < list_device.size(); i++) {
//                                                if (list_device.get(i).getCode().equals(dc.getCode())) {
//                                                    a = i + 1;
//                                                }
//                                            }
//                                            if (list_device.size() != 1 && a < list_device.size()) {
//                                                send485Cmd("A5" + list_device.get(a).getCode());
//                                                // 说明接收到了子机的轮询消息  点击按钮单独接收子机响应的消息时，将list_device作为参数循环去给子机发消息
//                                                Log.e("轮询发送下一条子机消息了，指令", "A5" + list_device.get(a).getCode() + "index是：" + a +
//                                                        list_device.toString());
//                                            }
                                        } else {
                                            lastReceivedMessages.put(currentMsg, currentMsg);
                                            String lastMsg = lastReceivedMessages.get(currentMsg);
                                            Log.e("按钮：旧消息：" + lastMsg + "新消息", currentMsg + "消息已去重");
                                            for (DeviceBean reBean : list_device) {
                                                if (reBean.getCode().equals(dc.getCode())) {
                                                    reBean.setRes(dc.getRes());
                                                    reBean.setSend(dc.getIsSend());
                                                }
                                            }
                                            int a = 0;
                                            for (int i = 0; i < list_device.size(); i++) {
                                                if (list_device.get(i).getCode().equals(dc.getCode())) {
                                                    a = i + 1;
                                                }
                                            }
//                                            if (!"2".equals(value) && list_device.size() != 1 && a < list_device.size()) {
//                                                send485Cmd("A" + dc.getRes().substring(1, 2) + list_device.get(a).getCode());
//                                                // 说明接收到了子机的轮询消息  点击按钮单独接收子机响应的消息时，将list_device作为参数循环去给子机发消息
//                                                Log.e("按钮点击开始发送下一条子机消息了，指令", "A" + dc.getRes().substring(1, 2)
//                                                        + list_device.get(a).getCode() + "index是：" + a + list_device.toString());
//                                            }
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case 8:
                        DeviceBean bean4 = (DeviceBean) msg.obj;
                        //受控指令
                        if (!isDeviceConnet) {
                            isDeviceConnet = true;
                        }
                        Log.e(TAG,list_device.toString() + "--res:" + bean4.getRes() +
                                "--code:" +bean4.getCode());
                        if (!list_device.contains(bean4)) {
                            for (int a = 0; a < list_device.size(); a++) {
                                if (list_device.get(a).getCode().equals(bean4.getCode())) {
                                    list_device.get(a).setRes(bean4.getRes());
                                    list_device.get(a).setInfo(bean4.getInfo());
                                    list_device.get(a).setCurrentPeak(bean4.getCurrentPeak());
                                    Log.e(TAG,"info:" + bean4.getInfo());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case 99:
                        DeviceBean bean2 = (DeviceBean) msg.obj;
                        String res2 = bean2.getRes();
                        Log.e("handler信息", "list_device.size(): " + list_device.size());
                        if (list_device.size() >= 0) {
                            show_Toast("设备：" + bean2.getCode() + "已断线");
                            for (int i = 0; i < list_device.size(); i++) {
                                if (bean2.getCode().equals(list_device.get(i).getCode())) {
                                    list_device.remove(i);
                                }
                            }
                            for (int i = 0; i < cmdList.size(); i++) {
                                if (bean2.getCode().equals(cmdList.get(i).getCode())) {
                                    cmdList.remove(i);
                                }
                            }
                            for (int i = 0; i < pollingList.size(); i++) {
                                if (bean2.getCode().equals(pollingList.get(i).getCode())) {
                                    pollingList.remove(i);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            //根据设备个数更新起爆和切换模式按钮
                            Message message = handler_msg.obtainMessage();
                            int collectionSize = list_device.size();
                            message.what = 15;
                            message.arg1 = collectionSize;
                            handler_msg.sendMessage(message);
                        }
                        break;
                }
            }
            return false;
        }
    });


    @OnClick({R.id.btn_net_test, R.id.btn_prepare_charge, R.id.btn_qibao, R.id.btn_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_net_test:
                if (cs) {
                    //准备测试
                    Log.e(TAG, "发送起爆测试A2指令");
                    writeData("A1");//网络测试指令
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_prepare_charge:
                if (cd) {
                    writeData("A2");//准备充电指令
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_qibao:
                if (cd) {
                    if (qbFlag.equals("qh")) {
                        //切换模式
                        if (qh) {
                            writeData("A6");//切换模式指令
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    } else {
                        //起爆
                        if (qb) {
                            writeData("A4");//准备起爆指令
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    }
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_exit:
                writeData("A7");//准备退出指令
                break;
        }
    }

    private void writeData(final String data) {
        String tip = "";
        switch (data) {
            case "A1":
                tip = "起爆测试";
                break;
            case "A2":
                tip = "准备充电";
                break;
            case "A4":
                tip = "起爆";
                break;
            case "A6":
                tip = "起爆";
                break;
            case "A7":
                tip = "退出流程";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确定进行" + tip + "吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    show_Toast("正在执行" + data);
                    switch (Build.DEVICE) {
                        case "M900":
                            setTipText(data);
                            break;
                        default:
                            if (server != null) {
                                server.writeData(data, list_device);
                            }
                            break;
                    }

                })
                .setNeutralButton("取消", null)
                .show();
    }

    private void setTipText(String data) {
        switch (data) {
            case "A1":
                tvTip.setText("正在起爆测试...");
                cs = false;
                cd = true;
                sendCmd(ThreeFiringCmd.setWxjlA1("01"));
                break;
            case "A2":
                tvTip.setText("正在充电...");
                qb = true;
                qh = true;
                sendCmd(ThreeFiringCmd.setWxjlA2("01"));
                break;
            case "A6":
                tvTip.setText("正在执行起爆...");
                sendCmd(ThreeFiringCmd.setWxjlA6("01", "01"));
                fuwei();
                break;
            case "A4":
                tvTip.setText("正在执行起爆...");
                sendCmd(ThreeFiringCmd.setWxjlA4("01"));
                fuwei();
                break;
            case "A7":
                tvTip.setText("执行退出指令...");
                sendCmd(ThreeFiringCmd.setWxjlA7("01"));
                list_device.clear();
                lastReceivedMessages.clear();
                adapter.notifyDataSetChanged();
                cs = true;
                cd = false;
                qb = false;
                qh = false;
                break;
        }
    }

    private void fuwei() {
        cs = true;
        cd = false;
        qb = false;
        qh = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            sendCmd(ThreeFiringCmd.setWxjlA7("01"));
            finish();
        }
        return true;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
        Log.e(TAG,"远距离无线级联页面收到: " + event.getMsg());
        if (event.getMsg().equals("pollingService")) {
            // 到时间了  发起轮询消息
            Iterator<Map.Entry<String, String>> iterator = lastReceivedMessages.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if (next.getKey().startsWith("B5")) {
                    iterator.remove();
                }
            }
            //询问检测状态（15）  电流信息（33） 全部雷管数量（42）   错误雷管数量（60）
            sendCmd(ThreeFiringCmd.setWxjlA5(wxjlDeviceId,"13324160"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendCmd(ThreeFiringCmd.setWxjlA7("01"));
//        EMgpio.SetGpioDataLow(94);
        try {
            if (server != null) {
                server.stopServerAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (syncDevices != null) {
            syncDevices.exit = true;  // 终止线程thread
            syncDevices.interrupt();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
