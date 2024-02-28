package android_serialport_api.xingbang.jilian;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.firingdevice.FiringMainActivity_hf;
import android_serialport_api.xingbang.firingdevice.TestDenatorActivity;
import android_serialport_api.xingbang.firingdevice.VerificationActivity;
import android_serialport_api.xingbang.utils.MmkvUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * WIFI级联控制
 */

public class SyncActivity2WIFI extends BaseActivity {

    @BindView(R.id.btn_test)
    Button btnTest;
    @BindView(R.id.btn_test1)
    Button btnTest1;
    @BindView(R.id.tv_delay)
    TextView tvDelay;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.a_et)
    AutoCompleteTextView aEt;
    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.btn_tongbu_setting)
    Button btnTongbuSetting;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.sync_container)
    LinearLayout syncContainer;
    @BindView(R.id.text_android_ip)
    TextView textAndroidIp;

    private Socket socket = null;
    private boolean socketStatus = false;
    private OutputStream outputStream = null;
    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    private boolean isTongBu = false;
    private int num = 0;

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

    private boolean isConnect;
    private boolean isExit = false;

    private int delay = 0;

    private List<String> ipList = new ArrayList<>();
    private SharedPreferences sp;
    private Set<String> set = new HashSet<>();

    //udp
    private DatagramSocket client;
    private DatagramPacket dpClientReceive;
    private Thread threadClient;

    private static final int CLIENT_PORT = 60000;
    private byte bufClient[] = new byte[1024];
    private static final int BUF_LENGTH = 1024;

    private String Yanzheng = "";//是否验证地理位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync2_net);
        ButterKnife.bind(this);
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);

        mThreadPool = Executors.newCachedThreadPool();

        mBuffer = new byte[50];
        delay = MyTools.getADelay();
        tvDelay.setText("当前延迟(m)：" + delay);

        tvCode.setText(MyTools.getACode().equals("") ? "设置编号" : MyTools.getACode());

        sp = getSharedPreferences("ip", MODE_PRIVATE);

        initView();

        createClient();
        getPropertiesData();
        textAndroidIp.setText("本机IP地址:" + getlocalip());
        new Thread(networkTask).start();
    }
    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = () -> {
        // 在这里进行 http request.网络请求相关操作
        UdpServer();
//            Message msg = new Message();
//            Bundle data = new Bundle();
//            data.putString("value", "请求结果");
//            msg.setData(data);
//            handler.sendMessage(msg);
    };
    /**
     * 实例化UDP客户端
     * */
    private void createClient() {
        try {
            //创建客户端，并且指定端口号，在此端口号侦听信息。
            client = new DatagramSocket(CLIENT_PORT);

            dpClientReceive = new DatagramPacket(bufClient, BUF_LENGTH);

            starClientThread();

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private String createReceiveData(DatagramPacket dp) {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(dp.getData(),
                dp.getOffset(), dp.getLength()));
        try {
            final String msg = stream.readUTF();
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int a = 0;

    private void starClientThread() {
        //创建用来发送的 DatagramPacket 数据报，其中应该包含要发送的信息，以及本地地址，目标端口号。
        threadClient = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        client.receive(dpClientReceive);
                        a++;
                        if (a==1){
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            continue;
                        }
                        final String receiveData = createReceiveData(dpClientReceive);
                        Log.e("级联-UDP接收", "receiveData: "+receiveData );
                        Message msg = new Message();
                        msg.what = 1006;
                        msg.obj = receiveData;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        threadClient.start();
    }

    private void initView() {
        set = sp.getStringSet("keySet", new HashSet<>());
        if (set.size() > 0) {
            ipList.addAll(set);
        }
        if (ipList.size() > 0) {
            aEt.setText(ipList.get(ipList.size()-1));
            btnConnect.setEnabled(true);
        }

        aEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    btnConnect.setEnabled(true);
                } else {
                    btnConnect.setEnabled(false);
                }
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ipList);
        aEt.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        delay = MyTools.getADelay();
        tvDelay.setText("当前延迟(m)：" + delay);
        tvCode.setText(MyTools.getACode().equals("") ? "设置编号" : MyTools.getACode());
    }

    /**
     * 1，子机先拉低，发送数据同步
     */
    String mResponse = "";

//    @Override
//    protected void onDataReceived(byte[] buffer, int size) {
//        String ar = new String(buffer).trim();
//        mResponse = ar;
//        Message msg = Message.obtain();
//        msg.what = 0;
//        msg.obj = ar;
//        handler.sendMessage(msg);
//    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("级联-返回消息", "msg.what: "+msg.what+"-- msg.obj: "+msg.obj);
            switch (msg.what) {
                case 0:
                    String response = (String) msg.obj;

                    if (response == null) {
                        show_Toast("数据为空");
                        break;
                    }
                    if (response.contains("A001" + MyTools.getACode())) {
                        //同步成功
                        //收到服务器的同步确认指令
                        isTongBu = true;
                        handler.sendEmptyMessageDelayed(5, 3600);
                        show_Toast("同步成功");
                        btnTest.setEnabled(false);
                        btnTest.setText("已连接");
                    } else if (response.contains("A002")) {
                        show_Toast("收到起爆测试指令");
                        Log.e("接收消息", "收到起爆测试指令: " );
                        String str5 = "起爆";
                        if (Yanzheng.equals("验证")) {
                            //Intent intent5 = new Intent(XingbangMain.this, XingBangApproveActivity.class);//人脸识别环节
                            Intent intent5 = new Intent(SyncActivity2WIFI.this, VerificationActivity.class);//验证爆破范围页面
                            intent5.putExtra("dataSend", str5);
                            startActivityForResult(intent5, REQUEST_CODE_QIBAO);
                        } else {
                            Intent intent5 = new Intent(SyncActivity2WIFI.this, FiringMainActivity_hf.class);//金建华
                            intent5.putExtra("dataSend", str5);
                            startActivityForResult(intent5, REQUEST_CODE_QIBAO);
                        }
//                        show_Toast("收到网络测试指令");
//                        Intent intent = new Intent(SyncActivity2Net.this, TestDenatorActivity.class);
//                        startActivityForResult(intent, REQUEST_CODE_NET);
                    } else if (response.contains("A003")) {
                        show_Toast("收到准备充电指令");
                        Log.e("接收消息", "收到准备充电指令: " );
                        EventBus.getDefault().post(new FirstEvent("jixu"));
                        //王工的
//                        show_Toast("收到准备充电指令");
//                        Intent intent = new Intent(SyncActivity2Net.this, FiringMainActivity.class);
//                        startActivityForResult(intent, REQUEST_CODE_CHONGDIAN);
                    } else if (response.contains("A004")) {
                        show_Toast("收到起爆指令");
                        Log.e("接收消息", "收到起爆指令: " );
                        EventBus.getDefault().post(new FirstEvent("qibao"));
                        //王工的
//                        show_Toast("收到起爆指令");
//                        Intent intent = new Intent(SyncActivity2Net.this, TempQiBaoActivity.class);
//                        if (response.length() >= 5) {
//                            intent.putExtra("itemId", response.substring(4));
//                        } else {
//                            intent.putExtra("itemId", "");
//                        }
//                        startActivityForResult(intent, REQUEST_CODE_QIBAO);
                    } else if (response.contains("A005")) {
                        EventBus.getDefault().post(new FirstEvent("finish"));
//                        show_Toast("收到退出指令");
                        finish();
                    } else if (response.contains("A006")) {
                        EventBus.getDefault().post(new FirstEvent("qibaoTag"));
                    } else if (response.contains("A008")) {
                        toCheck6();
                    }

                    break;
                case 1:
//                    show_Toast("同步指令错误");
                    break;
                case 2:
                    show_Toast("数据发送成功");
                    break;
                case 3:
                    //心跳数据
                    mNumber = 0;
                    break;
                case 5:
                    /*if (mNumber != 0) {
                        finish();
                    }
                    mNumber++;
                    handler.sendEmptyMessageDelayed(5, 3600);*/
                    break;
                case 6:
                    btnTest.setText("连接中...");
                    btnTest.setEnabled(false);
                    final String data2 = "0001" + MyTools.getACode() + "\n";
                    writeData(data2);
                    break;
                case 10:
//                    btnConnect.setText("已连接");
//                    btnTest.setEnabled(true);
//                    btnTest1.setEnabled(true);
                    break;
                case 1006:
//                    Log.e("UDP协议", "msg.obj: "+msg.obj );
                    a=0;
                    String content = (String) msg.obj;
                    if (content.contains("A0")) {
                        Message msg6 = new Message();
                        msg6.obj = content;
                        msg6.what = 0;
                        handler.sendMessage(msg6);
                    }
                    tvTip.setText(String.valueOf(msg.obj));
                    break;
                /*case 16:
                    //多次同步的哦
                    EMgpio.SetGpioDataLow(94);
                    try {
                        Thread.sleep(12);

                        mOutputStream.write(("0001" + MyTools.getACode()).getBytes());
//                        Thread.sleep(12);
//                        EMgpio.SetGpioDataHigh(94);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessageDelayed(16, 1000);
                    break;*/
                /*case 20:
                    if (!isTongBu) {
                        num++;
                        EMgpio.SetGpioDataLow(94);
                        try {
                            Thread.sleep(12);

                            String a = myInfo();
//                            mOutputStream.write(("0001" + MyTools.getACode() + ",_*").getBytes());
                            mOutputStream.write((a + ",_*").getBytes());
                            show_Toast("发送了");
                            Thread.sleep(20);
                            EMgpio.SetGpioDataHigh(94);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (num < 2) {
                            handler.sendEmptyMessageDelayed(20, 1000);
                        }
                    } else {
                        num = 0;
                    }

                    break;*/
                case 666:
                    IntentBean item = (IntentBean) msg.obj;
                    int resultCode = item.getResultCode();
                    Intent data = item.getData();
                    switch (item.getRequestCode()) {
                        case REQUEST_CODE_NET:
                            if (resultCode == TestDenatorActivity.RESULT_SUCCESS) {
                                //网络测试回调
                                String code = MyTools.getACode();
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
//                            if (resultCode == TempChongdianActivity.RESULT_SUCCESS) {
//                                //充电
//                                String code = MyTools.getACode();
//                                String type = data.getStringExtra("type");
////                    String tip = data.getStringExtra("tip");
//                                String U = data.getStringExtra("U");
//                                String I = data.getStringExtra("I");
//
//                                show_Toast("正在回传数据");
//                                String a = "0003" + code + "," + type + "," + U + "," + I;
//                                writeData(a);
//                            }
                            break;
                        case REQUEST_CODE_QIBAO:
                            if (resultCode == FiringMainActivity_hf.RESULT_SUCCESS) {
                                //起爆
                                String code = MyTools.getACode();
                                String type = data.getStringExtra("type");
                                String tip = data.getStringExtra("tip");

//                                show_Toast("正在回传数据");//退出起爆也换后,返回数据
                                String a = "0004" + code + "," + type + "," + tip;
                                Log.e("级联-返回的数据", "a: "+a );
                                writeData(a);
                            }
                            break;
                    }
                    break;
            }
        }
    };

    private void toCheck6() {
        String code = MyTools.getACode();
        String check = MyTools.getCheck() + "";
        String info = "";
        if (check.equals("1")) {

        } else {
//            List<NXmDataBean> mList = db.getNXM2();
//            for (int i = 0; i < mList.size(); i++) {
//                NXmDataBean item = mList.get(i);
//                if (i == 0) {
//                    info = item.getId() + ";" + item.getName();
//                } else {
//                    info = info + "," + item.getId() + ";" + item.getName();
//                }
//            }
        }
        show_Toast("toCheck6+正在回传数据");
        String a;
        if (check.equals("1")) {
            a = "0008" + code + ",0" + check;
        } else {
            a = "0008" + code + ",0" + check + "," + info;
        }
        writeData(a);
    }

    private int mNumber = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentBean item = new IntentBean();
        item.setRequestCode(requestCode);
        item.setResultCode(resultCode);
        item.setData(data);
        Message msg = new Message();
        msg.obj = item;
        msg.what = 666;
        handler.sendMessageDelayed(msg, delay * 1000);
        /*if (delay > 0) {
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        /*switch (requestCode) {
            case REQUEST_CODE_NET:
                if (resultCode == TempNetActivity.RESULT_SUCCESS) {
                    //网络测试回调
                    String code = MyTools.getACode();
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
                if (resultCode == TempChongdianActivity.RESULT_SUCCESS) {
                    //充电
                    String code = MyTools.getACode();
                    String type = data.getStringExtra("type");
//                    String tip = data.getStringExtra("tip");
                    String U = data.getStringExtra("U");
                    String I = data.getStringExtra("I");

                    show_Toast("正在回传数据");
                    String a = "0003" + code + "," + type + "," + U + "," + I;
                    writeData(a);
                }
                break;
            case REQUEST_CODE_QIBAO:
                if (resultCode == TempQiBaoActivity.RESULT_SUCCESS) {
                    //起爆
                    String code = MyTools.getACode();
                    String type = data.getStringExtra("type");
                    String tip = data.getStringExtra("tip");

                    show_Toast("正在回传数据");
                    String a = "0004" + code + "," + type + "," + tip;
                    writeData(a);
                }
                break;
        }*/
    }

    private void connect() {
// 初始化线程池
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (!socketStatus) {
                    try {
                        socket = new Socket(aEt.getText().toString().trim(), 9003);
//                        socket = new Socket(getWifiRouteIPAddress(), 9002);
//                        socket = new Socket("192.168.43.109", 9002);
                        Log.e("级联-socket连接",socket.isConnected() + " " + getWifiRouteIPAddress());
                        if (socket == null) {
                        } else {
                            socketStatus = true;
                        }
                        if (socket.isConnected()) {
                            handler.sendEmptyMessage(10);
//                            show_Toast("连接成功!");
                            // 步骤1：创建输入流对象InputStream
                            is = socket.getInputStream();
                            // 步骤2：创建输入流读取器对象 并传入输入流对象
                            // 该对象作用：获取服务器返回的数据
                            isr = new InputStreamReader(is);
                            br = new BufferedReader(isr);
                            if (isConnect) {
                                handler.sendEmptyMessage(6);
                                isConnect = false;
                            }

                            // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                            while (!isExit && (response = br.readLine()) != null) {
                                // 步骤4:通知主线程,将接收的消息显示到界面
//                                Utils.print(response + "--");
                                if (response.startsWith("A0")) {
                                    Message msg = Message.obtain();
                                    msg.what = 0;
                                    msg.obj = response;
                                    handler.sendMessage(msg);
                                } else if (response.startsWith("FF")) {
                                    //心跳数据
                                    handler.sendEmptyMessage(3);
                                }

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private String getWifiRouteIPAddress() {
        WifiManager wifi_service = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        //        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        //        System.out.println("Wifi info----->" + wifiinfo.getIpAddress());
        //        System.out.println("DHCP info gateway----->" + Formatter.formatIpAddress(dhcpInfo.gateway));
        //        System.out.println("DHCP info netmask----->" + Formatter.formatIpAddress(dhcpInfo.netmask));
        //DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址
        String routeIp = Formatter.formatIpAddress(dhcpInfo.gateway);
        Log.i("route ip", "wifi route ip：" + routeIp);

        return routeIp;
    }

    /**
     * 头部00，服务器头部A0
     * 01发出同步指令同事携带编码号，服务器收到01返回01则确认同步成功
     * 如果没有收到指令，那么则需要延迟5秒再次发送指令
     * 1.通信协议请求受控指令等待主APP发出指令，收到指令则连接成功
     * 2.主APP发出网络检测的指令，分机APP进行网络监测的功能，检测完成发出指令结果，收到主APP应答
     * 3.充电指令
     * 4.起爆指令，时间同步
     */
    @OnClick({R.id.btn_test, R.id.btn_test1, R.id.btn_tongbu_setting, R.id.btn_connect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test:
                String ip = aEt.getText().toString().trim();
                if (!set.contains(ip)) {
                    set.add(ip);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putStringSet("keySet", set);
                    editor.apply();
                }
                hideInputKeyboard();
                if (MyTools.getACode().equals("")) {
                    show_Toast("设置编号");
                    return;
                }

                btnTest.setText("连接中...");
                btnTest.setEnabled(false);

                if (socket == null) {
                    isConnect = true;
                    connect();
                } else {
                    String a = myInfo();
                    writeData(a + ",_*");
                }

                break;
            case R.id.btn_test1:
                show_Toast("已经断开同步");
                isTongBu = false;
//                closeSocket();
                finish();
//                String a = "0002" + "D0000005" + "," + "1" + "," + "100" + "," + "0" + "," + "200" + "," + "300" + "," + "";
////                writeData(a);
                break;
            case R.id.btn_tongbu_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_connect:
                hideInputKeyboard();
                String ip2 = aEt.getText().toString().trim();
                if (!set.contains(ip2)) {
                    set.add(ip2);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putStringSet("keySet", set);
                    editor.apply();
                }
                btnConnect.setText("连接中...");
                btnConnect.setEnabled(false);
                connect();
                break;
        }
    }

    private String myInfo() {
        String code = MyTools.getACode();
        String check = MyTools.getCheck() + "";
        String info = "";
        if (check.equals("1")) {

        } else {
//            List<NXmDataBean> mList = db.getNXM2();
//            for (int i = 0; i < mList.size(); i++) {
//                NXmDataBean item = mList.get(i);
//                if (i == 0) {
//                    info = item.getId() + ";" + item.getName();
//                } else {
//                    info = info + "," + item.getId() + ";" + item.getName();
//                }
//            }
        }

        show_Toast("myInfo正在回传数据");
        String a;
        if (check.equals("1")) {
            a = "0001" + code + ",0" + check;
        } else {
            a = "0001" + code + ",0" + check + "," + info;
        }

        return a;
    }

    byte[] mBuffer;

    private void closeSocket() {
//        writeData("0005");//退出
        int len = (Protcol.QuitFunc(mBuffer)) & 0xff;
        SendData(mBuffer, len);
        isExit = true;
        handler.removeMessages(3);
        handler.removeMessages(5);
        if (mThreadPool != null) {
            mThreadPool.shutdown();
        }
        /*if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            br = null;
        }*/
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

    private void SendData(byte[] buffer, int size) {
        /*int sendlg = 0;
        byte[] tmpBuffer = new byte[50];
        byte[] sendbuf = new byte[50];

        for (int i = 0; i < size - 4; i++) {
            tmpBuffer[i] = buffer[i + 1];
        }
        sendlg = (MyFunc.exslip_pack(tmpBuffer, size - 4, sendbuf)) & 0xff;

        try {
            mOutputStream.write(sendbuf, 0, sendlg);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /*private void writeData(String data) {
        data = data + ",_*";
        EMgpio.SetGpioDataLow(94);
        try {
//            Thread.sleep(60);
            Thread.sleep(80);
            mOutputStream.write(data.getBytes());
            Thread.sleep(200);
            EMgpio.SetGpioDataHigh(94);
            show_Toast("发送数据了..." + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void writeData(final String data) {
        mThreadPool.execute(() -> {
            if (socketStatus) {
                try {
                    outputStream = socket.getOutputStream();
                    outputStream.write(data.getBytes());
                    // 步骤3：发送数据到服务端
                    outputStream.flush();
                    Log.e("级联", "发送数据: "+data);
//                        handler.sendEmptyMessage(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //eventbus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
//        String msg = event.getMsg();
//        if (msg.equals("qibao")) {
//            String a = "0006";
//            writeData(a);//
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EMgpio.SetGpioDataLow(94);
        closeSocket();
        EventBus.getDefault().unregister(this);
        threadClient.interrupt();
        client.close();
    }

    private void getPropertiesData() {
        Yanzheng = (String) MmkvUtils.getcode("Yanzheng", "验证");
        Log.e("级联", "Yanzheng: "+Yanzheng );
    }

    public void hideInputKeyboard() {
        // 取消焦点
        aEt.clearFocus();

        syncContainer.requestFocus(); // 获取焦点
        hideInputKeyboard();
    }


    /**
     * 或取本机的ip地址
     */
    private String getlocalip() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress == 0) {
            return "请检查是否连接WIFI";
        }
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }


    private void UdpServer(){
        byte[] buffer = new byte[1024];
        /*在这里同样使用约定好的端口*/
        int port = 8091;
        DatagramSocket server = null;
        try {
            server = new DatagramSocket (port);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while(true){
                try {
                    server.receive(packet);
                    String s = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                    Log.e("UDP接收", "address : " + packet.getAddress() + ", port : " + packet.getPort() + ", content : " + s);
                    if (s.startsWith("A0")) {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = s;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }finally{
            if(server != null)
                server.close();
        }
    }
}
