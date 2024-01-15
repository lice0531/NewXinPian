package android_serialport_api.xingbang.jilian;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.firingdevice.FiringMainActivity;
import android_serialport_api.xingbang.firingdevice.TestDenatorActivity;
import android_serialport_api.xingbang.firingdevice.VerificationActivity;
import android_serialport_api.xingbang.utils.MmkvUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 热点级联设置
 */
public class SyncActivity extends BaseActivity {

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
    private String Yanzheng = "";//是否验证地理位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);
        mThreadPool = Executors.newCachedThreadPool();

        mBuffer = new byte[50];
        delay = MyTools.getADelay();
        tvDelay.setText(getString(R.string.text_sync_dqyc) + delay);

        tvCode.setText(MyTools.getACode().equals("")?getString(R.string.text_sync_szbh):MyTools.getACode());
        getPropertiesData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        delay = MyTools.getADelay();
        tvDelay.setText(getString(R.string.text_sync_dqyc)  + delay);
        tvCode.setText(MyTools.getACode().equals("")?getString(R.string.text_sync_szbh):MyTools.getACode());
    }

    private void getPropertiesData() {
        Yanzheng = (String) MmkvUtils.getcode("Yanzheng", "验证");
        Log.e("级联", "Yanzheng: "+Yanzheng );
    }



    /**
     * 1，子机先拉低，发送数据同步
     */
    String mResponse = "";

//    @Override
//    protected void onDataReceived(byte[] buffer, int size) {
//        String ar = new String(buffer).trim();
//        Log.e("消息返回", "ar: "+ar );
//        mResponse = ar;
//        Message msg = Message.obtain();
//        msg.what = 0;
//        msg.obj = ar;
//        handler.sendMessage(msg);
//    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String response = (String) msg.obj;
                    if (response == null) {
                        show_Toast(getString(R.string.text_sync_tip1));
                        break;
                    }
                    if (response.contains("A001" + MyTools.getACode())) {
                        //同步成功
                        //收到服务器的同步确认指令
                        isTongBu = true;
                        handler.sendEmptyMessageDelayed(5, 3600);
                        show_Toast(getString(R.string.text_sync_tip2));
                        btnTest.setEnabled(false);
                        btnTest.setText(R.string.text_sync_tip3);
                    } else if (response.contains("A002")) {
                        show_Toast(getString(R.string.text_sync_tip4));
                        String str5 = "级联起爆";
                        if (Yanzheng.equals("验证")) {
                            //Intent intent5 = new Intent(XingbangMain.this, XingBangApproveActivity.class);//人脸识别环节
                            Intent intent5 = new Intent(SyncActivity.this, VerificationActivity.class);//验证爆破范围页面
                            intent5.putExtra("dataSend", str5);
                            startActivityForResult(intent5, REQUEST_CODE_QIBAO);
                        } else {
                            Intent intent5 = new Intent(SyncActivity.this, FiringMainActivity.class);//金建华
                            intent5.putExtra("dataSend", str5);
                            startActivityForResult(intent5, REQUEST_CODE_QIBAO);
                        }
                    } else if (response.contains("A003")) {
                        show_Toast(getString(R.string.text_sync_tip5));
                        EventBus.getDefault().post(new FirstEvent("jixu"));
//                        Intent intent = new Intent(SyncActivity.this, FiringMainActivity.class);
//                        startActivityForResult(intent, REQUEST_CODE_CHONGDIAN);
                    } else if (response.contains("A004")) {
                        show_Toast(getString(R.string.text_sync_tip6));
                        EventBus.getDefault().post(new FirstEvent("qibao"));
//                        Intent intent = new Intent(SyncActivity.this, FiringMainActivity.class);
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
                case 5:
                    /*if (mNumber != 0) {
                        finish();
                    }
                    mNumber++;
                    handler.sendEmptyMessageDelayed(5, 3600);*/
                    break;
                case 6:
                    btnTest.setText(R.string.text_sync_tip8);
                    btnTest.setEnabled(false);
                    final String data2 = "0001" + MyTools.getACode() + "\n";
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

//                        mOutputStream.write(("0001" + MyTools.getACode()).getBytes());
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
//                            mOutputStream.write(("0001" + MyTools.getACode() + ",_*").getBytes());
//                            mOutputStream.write((a + ",_*").getBytes());
                            show_Toast("发送了");
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
//                                show_Toast("正在回传数据");
//                                String a = "0003" + code + "," + type + "," + U + "," + I;
//                                writeData(a);
//                            }
                            break;
                        case REQUEST_CODE_QIBAO:
                            if (resultCode == FiringMainActivity.RESULT_SUCCESS) {
                                //起爆
                                String code = MyTools.getACode();
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
        show_Toast("正在回传数据");
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
        handler.sendMessageDelayed(msg,delay * 1000);
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
        mThreadPool.execute(() -> {
            if (!socketStatus) {
                try {
//                        socket = new Socket(aEt.getText().toString().trim(), 9002);
                    socket = new Socket(getWifiRouteIPAddress(), 9002);
//                        socket = new Socket("192.168.43.109", 9002);
                    Log.e("socket输出",socket.isConnected() + " " + getWifiRouteIPAddress());
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
//                                log.e(response + "--");
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
        Log.e("网络route ip", "wifi route ip：" + routeIp);

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
    @OnClick({R.id.btn_test, R.id.btn_test1, R.id.btn_tongbu_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test:
                if (MyTools.getACode().equals("")){
                    show_Toast(getString(R.string.text_sync_szbh));
                    return;
                }
                btnTest.setText(getString(R.string.text_sync_tip8));
                btnTest.setEnabled(false);

//                handler.sendEmptyMessageDelayed(16,1000);

//                EMgpio.SetGpioDataLow(94);//gpio下电操作
//                try {
//                    Thread.sleep(12);
//
//                    String a = myInfo();
//                    Log.e("级联", "a: "+a );
////                    mOutputStream.write(("0001" + MyTools.getACode() + ",_*").getBytes());
//                    mOutputStream.write((a + ",_*").getBytes());
//                    Log.e("级联", "(a + ,_*): "+(a + ",_*"));
//                    show_Toast("发送了");
//                    Thread.sleep(20);
////                    EMgpio.SetGpioDataHigh(94);////gpio下电操作
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                handler.sendEmptyMessageDelayed(20, 1000);


                if (socket == null) {
                    isConnect = true;
//                    show_Toast("请重新打开APP，确保服务端已经在运行");
                    btnTest.setText(getString(R.string.text_sync_tip8));
                    btnTest.setEnabled(false);
                    connect();
                } else {
                    btnTest.setText(getString(R.string.text_sync_tip8));
                    btnTest.setEnabled(false);
                    final String data = "0001" + MyTools.getACode() + "\n";
                    writeData(data);
                }

                break;
            case R.id.btn_test1:
                show_Toast(getString(R.string.text_sync_tip11));
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

        show_Toast(getString(R.string.text_sync_tip12));
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
    //串口发送消息
//    private void writeData(String data) {
////        data = data + ",_*";
////        EMgpio.SetGpioDataLow(94);
//        try {
////            Thread.sleep(60);
//            Thread.sleep(80);
//            mOutputStream.write(data.getBytes());
//            Thread.sleep(200);
////            EMgpio.SetGpioDataHigh(94);
//            show_Toast("发送数据了..." + data);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    /**
     * socket发送消息
     * */
    private void writeData(final String data) {
        mThreadPool.execute(() -> {
            if (socketStatus) {
                try {
                    outputStream = socket.getOutputStream();
                    outputStream.write(data.getBytes());
                    // 步骤3：发送数据到服务端
                    outputStream.flush();
                    Log.e("级联", "发送数据: "+data);
                    handler.sendEmptyMessage(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
        String msg = event.getMsg();
        if (msg.equals("qibao")) {
            String a = "0006";
            writeData(a);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EMgpio.SetGpioDataLow(94);//下电
        closeSocket();
        EventBus.getDefault().unregister(this);
    }

}
