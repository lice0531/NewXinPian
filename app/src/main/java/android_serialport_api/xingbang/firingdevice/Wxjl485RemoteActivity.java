package android_serialport_api.xingbang.firingdevice;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.kfree.expd.ExpdDevMgr;
import com.kfree.expd.OnOpenSerialPortListener;
import com.kfree.expd.OnSerialPortDataListener;
import com.kfree.expd.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
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
public class Wxjl485RemoteActivity extends BaseActivity {

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
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.llCurrentPeak)
    LinearLayout llCurrentPeak;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    public static final int QIBAO_CESHI = 4;
    //M900有线级联页面轮询时间
    private List<DeviceBean> list_device = new ArrayList<>();
    // 轮询时所使用的子机列表，每次轮询时将list_device给pollingList赋值
    private List<DeviceBean> pollingList = new ArrayList<>();
    private List<DeviceBean> cmdList = new ArrayList<>();

    private DeviceAdapter adapter;

    private boolean isDeviceConnet;

    private int qibaoNum = 0;
    public ExpdDevMgr mExpDevMgr;
    public boolean cs = true;
    public boolean cd = false;
    //是否点击了切换模式按钮
    public boolean qh = false;
    public boolean qb = false;
    private ConcurrentHashMap<String, String> lastReceivedMessages = new ConcurrentHashMap<>();
    //起爆按钮点击时，值为qh:发送A5命令（切换模式）   值为qb:发送A6命令（起爆）
    private String qbFlag = "qb";

    private String TAG = "远距离无线级联485页面";
    private String wxjlDeviceId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxjl_remote);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        initSocket();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mExpDevMgr = new ExpdDevMgr(this);
        //串口打开监听
        OnOpenSerialPortListener listener = new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File file) {
                Log.e("485接口", "远距离无线级联页面打开成功 ");
            }

            @Override
            public void onFail(File file, Status status) {
                Log.e("485接口", "打开失败");
            }
        };
        //485接口数据监听
        OnSerialPortDataListener listener2 = new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                String res = Utils.bytesToHexFun(bytes);
                Log.e("485接口", "数据接收: " + res);
                Utils.writeRecord("485接口-数据接收: " + res);
                if (res.length() > 2) {
                    Message msg = new Message();
                    if (res.startsWith("B1")) {
                        //同步
                        msg.what = 0;
                        DeviceBean bean = new DeviceBean();
//                        bean.setRes(res.substring(0, 4));
//                        bean.setCode(res.substring(4));
                        bean.setRes(res.substring(0, 2));
                        bean.setErrNum("000");
                        bean.setTrueNum("000");
                        bean.setCode(res.substring(2));
                        bean.setCurrentPeak("0");
                        bean.setInfo("在线");
                        msg.obj = bean;
                    } else if (res.startsWith("B2")) {
                        //说明一台子机已进入起爆页面  此时需让下一台设备进入起爆页面
                        msg.what = InitConst.QIBAO_CESHI;
                        msg.obj = receiveMsg(res, "在线");
                        //开启轮询  10秒轮询一次接收到的消息  来更新当前设备列表信息
                        PollingUtils.startPollingService(Wxjl485RemoteActivity.this, InitConst.POLLING_TIME,
                                PollingReceiver.class, PollingUtils.ACTION);
                    } else if (res.startsWith("B3")) {
                        //接收到轮训消息
                        DeviceBean bean = new DeviceBean();
                        bean.setRes(res);
                        if (res.length() == 18) {
                            bean.setCode(res.substring(2, 4));
                            //子机发送过来的数据是18位
                            bean.setTrueNum(res.substring(6, 9));
                            bean.setErrNum(res.substring(9, 12));
                            bean.setCurrentPeak(formatCurrentPeak(res));
                            String status = res.substring(4, 6);
                            if ("01".equals(status)) {
                                bean.setInfo("在线");
                            } else if ("02".equals(status)) {
                                bean.setInfo("等待检测");
                            } else if ("03".equals(status)) {
                                bean.setInfo("检测结束");
                            } else if ("04".equals(status)) {
                                bean.setInfo("正在充电");
                            } else if ("05".equals(status)) {
                                bean.setInfo("起爆结束");
                            }
                        }
                        msg.what = InitConst.CODE_UPDAE_STATUS;
                        msg.obj = bean;
                    } else if (res.startsWith("B005")) {
                        //接收到起爆结束消息
                        DeviceBean bean = new DeviceBean();
                        bean.setRes(res);
                        if (res.length() == 20) {
                            bean.setCode(res.substring(4, 6));
                            //子机发送过来的数据是20位
                            bean.setTrueNum(res.substring(8, 11));
                            bean.setErrNum(res.substring(11, 14));
                            bean.setCurrentPeak(formatCurrentPeak(res));
                            bean.setInfo("起爆结束");
                        }
                        msg.what = 8;
                        msg.obj = bean;
                    } else if (res.contains("B4")) {
                        //接收到充电消息
                        DeviceBean bean = new DeviceBean();
                        bean.setRes(res);
                        if (res.length() == 18) {
                            bean.setCode(res.substring(2, 4));
                            //子机发送过来的数据是18位
                            bean.setTrueNum(res.substring(6, 9));
                            bean.setErrNum(res.substring(9, 12));
                            bean.setCurrentPeak(formatCurrentPeak(res));
                            bean.setInfo("正在充电");
                        }
                        //准备充电
                        msg.what = 8;
                        msg.obj = bean;
                    } else if (res.startsWith("B5")) {
                        //接收到子机切换模式指令
                        msg.what = InitConst.CODE_TRANSLATE;
                        msg.obj = receiveMsg(res, "正在起爆");
                    } else if (res.startsWith("B6")) {
                        //准备起爆
                        msg.what = InitConst.CODE_UPDAE_STATUS;
                        msg.obj = receiveMsg(res, "正在起爆");
                    } else if (res.startsWith("B7")) {
                        //准备退出
                        msg.what = InitConst.CODE_EXIT;
                        msg.obj = receiveMsg(res, "");
                    }
                    handler_msg.sendMessage(msg);
                }

            }

            @Override
            public void onDataSent(byte[] bytes) {
                String str = Utils.bytesToHexFun(bytes);
                Log.e("485发送", str);
            }
        };
        mExpDevMgr.set12VEnable(true);
        mExpDevMgr.openRs485(listener, listener2, 2400);
        wxjlDeviceId = !TextUtils.isEmpty(getIntent().getStringExtra("wxjlDeviceId")) ?
                getIntent().getStringExtra("wxjlDeviceId") : "";
        send485Cmd("A1" + wxjlDeviceId);
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
        bean.setCode(res.substring(2));
        bean.setInfo(desc);
        return bean;
    }

    /**
     * 发送485命令
     */
    public void send485Cmd(String data) {
        Log.e("发送命令", "data: " + data);
        Utils.writeRecord("485接口-数据发送: " + data);
        byte[] powerCmd = Utils.hexStringToBytes(data);
        mExpDevMgr.sendBytesRs485(powerCmd);
    }

    private void initView() {
        /*DeviceBean bean = new DeviceBean();
        bean.setCode("1");
        bean.setName("2");
        bean.setRes("6");
        bean.setSocket(new Socket());
        for (int i = 0; i < 20; i++) {
            list.add(bean);
        }*/
        tvStatus.setVisibility(View.GONE);
        llCurrentPeak.setVisibility(View.VISIBLE);
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
            switch (msg.what) {

                case 0:
                    if (msg.obj != null) {
                        DeviceBean bean = (DeviceBean) msg.obj;
                        if (!TextUtils.isEmpty(bean.getRes())) {
                            String res = bean.getRes();//进页面为空
                            Log.e("判断", "bean.getRes(): " + bean.getRes() + "   case0进来了。。。");

//                        if (res.equals("0001")) {
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
                                    cmdList.clear();
                                    cmdList.addAll(list_device);
                                    if (server != null) {
                                        switch (Build.DEVICE) {
                                            case "T-QBZD-Z6":
                                            case "M900":
//                                            send485Cmd("A001" + bean.getCode());
                                                send485Cmd("A1" + bean.getCode());
                                                break;
                                            default:
//                                            server.writeData("A001");//同步指令，告诉客户端连接成功
                                                server.writeData("A1" + bean.getCode());//同步指令，告诉客户端连接成功
                                                server.setList(list_device);
                                                if (list_device.size() == 1) {
                                                    //只有一个子机时，显示起爆按钮
                                                    server.heart();
                                                }
                                                break;
                                        }
                                    }
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
                    PollingUtils.stopPollingService(Wxjl485RemoteActivity.this, PollingReceiver.class, PollingUtils.ACTION);
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

//                            if (lastReceivedMessages.get(currentCode) != null && currentMsg.substring(0,4)
//                                    .equals(lastReceivedMessages.get(currentCode).substring(0,4))){
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
                                        int a = 0;
                                        for (int i = 0; i < list_device.size(); i++) {
                                            if (list_device.get(i).getCode().equals(dc.getCode())) {
                                                a = i + 1;
                                            }
                                        }
                                        if (list_device.size() != 1 && a < list_device.size()) {
                                            send485Cmd("A3" + list_device.get(a).getCode());
                                            // 说明接收到了子机的轮询消息  点击按钮单独接收子机响应的消息时，将list_device作为参数循环去给子机发消息
                                            Log.e("轮询发送下一条子机消息了，指令", "A3" + list_device.get(a).getCode() + "index是：" + a +
                                                    list_device.toString());
                                        }
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
                                        if (!"2".equals(value) && list_device.size() != 1 && a < list_device.size()) {
                                            send485Cmd("A" + dc.getRes().substring(1, 2) + list_device.get(a).getCode());
                                            // 说明接收到了子机的轮询消息  点击按钮单独接收子机响应的消息时，将list_device作为参数循环去给子机发消息
                                            Log.e("按钮点击开始发送下一条子机消息了，指令", "A" + dc.getRes().substring(1, 2)
                                                    + list_device.get(a).getCode() + "index是：" + a + list_device.toString());
                                        }
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
                    if (!list_device.contains(bean4)) {
                        for (int a = 0; a < list_device.size(); a++) {
                            if (list_device.get(a).getCode().equals(bean4.getCode())) {
                                list_device.get(a).setRes(bean4.getRes());
                                list_device.get(a).setInfo(bean4.getInfo());
                                list_device.get(a).setCurrentPeak(bean4.getCurrentPeak());
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
            return false;
        }
    });


    private void setTipText(String data) {
        switch (data) {
            case "A2":
                tvTip.setText("正在起爆测试...");
                cs = false;
                cd = true;
                break;
            case "A4":
                tvTip.setText("正在充电...");
                qb = true;
                qh = true;
                break;
            case "A5":
                tvTip.setText("正在执行起爆...");
                fuwei();
                break;
            case "A6":
                tvTip.setText("正在执行起爆...");
                fuwei();
                break;
            case "A7":
                tvTip.setText("执行退出指令...");
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

    @OnClick({R.id.btn_net_test, R.id.btn_prepare_charge, R.id.btn_qibao, R.id.btn_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_net_test:
                if (cs) {
                    writeData("A2");//网络测试指令
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_prepare_charge:
                if (cd) {
                    writeData("A4");//准备充电指令
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            //起爆
            case R.id.btn_qibao:
                if (cd) {
                    if (qbFlag.equals("qh")) {
                        //切换模式
                        if (qh) {
                            writeData("A5");//切换模式指令
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    } else {
                        //起爆
                        if (qb) {
                            writeData("A6");//准备起爆指令
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

    private void fuwei() {
        cs = true;
        cd = false;
        qb = false;
        qh = false;
    }

    private void writeData(final String data) {
        String tip = "";
        switch (data) {
            case "A2":
                tip = "起爆测试";
                break;
            case "A4":
                tip = "准备充电";
                break;
            case "A5":
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
                        case "T-QBZD-Z6":
                        case "M900":
//                            send485Cmd(data);
//                            if (list_device != null && list_device.size() > 0) {
                                setTipText(data);
                                if ("A2".equals(data) || "A7".equals(data) || "A4".equals(data) || "A6".equals(data)) {
                                    Log.e("当前指令", data);
                                    send485Cmd(data);
                                } else if ("A5".equals(data)) {
                                    //切换模式
                                    PollingUtils.stopPollingService(Wxjl485RemoteActivity.this, PollingReceiver.class, PollingUtils.ACTION);
                                    Log.e("轮询已关闭，指令是", data);
                                    //只给第一台同步过来的子机发: A5+自己编号+01
                                    send485Cmd(data + list_device.get(0).getCode() + "01");
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                    //其他子机发: A5AA00
                                    send485Cmd(data + "AA00");
                                } else {
                                    Log.e("当前指令" + data, "按钮点击  开始循环发消息了。。。");
                                    send485Cmd(data + list_device.get(0).getCode());
                                }
//                            }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
        Log.e(TAG,"远距离无线级联页面收到: " + event.getMsg());
        if (event.getMsg().equals("pollingService")) {
            // 到时间了  发起轮询消息
            Iterator<Map.Entry<String, String>> iterator = lastReceivedMessages.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if (next.getKey().startsWith("B3")) {
                    iterator.remove();
                }
            }
            send485Cmd("A3" + list_device.get(0).getCode());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (server != null) {
                server.stopServerAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.DEVICE.equals("M900")) {
            mExpDevMgr.closeRs485();
            mExpDevMgr.set12VEnable(false);
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
